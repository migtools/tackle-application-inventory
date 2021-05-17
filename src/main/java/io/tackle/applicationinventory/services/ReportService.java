package io.tackle.applicationinventory.services;

import io.quarkus.panache.common.Sort;
import io.tackle.applicationinventory.dto.AdoptionPlanAppDto;
import io.tackle.applicationinventory.dto.EffortEstimate;
import io.tackle.applicationinventory.entities.Application;
import io.tackle.applicationinventory.entities.ApplicationsDependency;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.EdgeReversedGraph;
import org.jgrapht.graph.builder.GraphTypeBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class ReportService {
    /*
    Considering ALL the applications in the system , it will create a Graph
    and traverse it considering the parent and child dependencies
    It considers the graph doesn't have a cycle relationship
     */
    @Transactional
    public List<AdoptionPlanAppDto> getAdoptionPlanAppDtos(List<Long> applicationIds) {
        Graph<Application, DefaultEdge> graph = getApplicationsGraphFromDependencies();

        EdgeReversedGraph<Application, DefaultEdge> reversedGraph = new EdgeReversedGraph<>(graph);
        return applicationIds.stream()
            .map(e -> buildAdoptionPlanAppDto(applicationIds, reversedGraph, e))
            .sorted(Comparator.comparing(e -> e.positionY))
            .collect(Collectors.toList());
    }

    private Graph<Application, DefaultEdge> getApplicationsGraphFromDependencies() {
        // create the graph
        Graph<Application, DefaultEdge> graph = GraphTypeBuilder.<Application, DefaultEdge>
            directed()
            .edgeClass(DefaultEdge.class)
            .allowingSelfLoops(false)
            .allowingMultipleEdges(false)
            .weighted(false)
            .buildGraph();

        // load current data from DB that must be fine (i.e. without cycles) by definition
        try (Stream<ApplicationsDependency> dependencies = ApplicationsDependency.streamAll(Sort.by("id"))) {
            dependencies.forEach(applicationsDependency -> Graphs.addEdgeWithVertices(graph, applicationsDependency.from, applicationsDependency.to));
        }
        try (Stream<Application> applicationList = Application.streamAll(Sort.by("id"))) {
            applicationList.forEach(application -> Graphs.addAllVertices(graph, List.of(application)));
        }
        return graph;
    }

    @Transactional
    private AdoptionPlanAppDto buildAdoptionPlanAppDto(List<Long> applicationIds, EdgeReversedGraph<Application, DefaultEdge> graph, Long e) {
        Application application = Application.findById(e);

        AdoptionPlanAppDto planAppDto = new AdoptionPlanAppDto();
        planAppDto.applicationName = application.name;
        planAppDto.decision = application.review.proposedAction;
        planAppDto.applicationId = application.id;
        planAppDto.effortEstimate = application.review.effortEstimate;
        planAppDto.positionY = application.review.workPriority;

        // Obtain the numeric effort from the String stored
        planAppDto.effort = EffortEstimate.getEnum(application.review.effortEstimate).getEffort();

        // Calculate recursively which is the start X position for this Application depending on its dependencies
        planAppDto.positionX = startPositionNode(graph, application, applicationIds);

        return planAppDto;
    }

    private Integer startPositionNode(EdgeReversedGraph<Application, DefaultEdge> graph, Application application, List<Long> selectedApps) {
        List<Application> parents = Graphs.successorListOf(graph, application);
        if (parents.size() > 0) {
            return parents.stream().map(e -> {
                // recursively we'll find the position calculating previous positions + lengths of the ancestors
                Integer pos = startPositionNode(graph, e, selectedApps);
                // if the parent is an application selected to appear in the list then we use its effort, otherwise is like we are hiding it with effort = 0
                Integer effort = (selectedApps.contains(e.id)) ? EffortEstimate.getEnum(application.review.effortEstimate).getEffort() : 0;
                return pos + effort;
            })
                .max(Integer::compare).get();
            // Selecting the largest position from its ancestors
        } else {
            // point of return for the recursivity on Top nodes
            return 0;
        }
    }
}
