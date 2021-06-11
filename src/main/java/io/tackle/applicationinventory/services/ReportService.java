package io.tackle.applicationinventory.services;

import io.quarkus.panache.common.Sort;
import io.tackle.applicationinventory.dto.AdoptionPlanAppDto;
import io.tackle.applicationinventory.dto.EffortEstimate;
import io.tackle.applicationinventory.entities.Application;
import io.tackle.applicationinventory.entities.ApplicationsDependency;
import org.jboss.logging.Logger;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.EdgeReversedGraph;
import org.jgrapht.graph.builder.GraphTypeBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class ReportService {
    private static final Logger LOGGER = Logger.getLogger(ReportService.class);

    /*
        Considering ALL the applications in the system , it will create a Graph
        and traverse it considering the parent and child dependencies
        It considers the graph doesn't have a cycle relationship
         */
    @Transactional
    public List<AdoptionPlanAppDto> getAdoptionPlanAppDtos(List<Long> applicationIds) {
        Graph<Application, DefaultEdge> graph = getApplicationsGraphFromDependencies();

        EdgeReversedGraph<Application, DefaultEdge> reversedGraph = new EdgeReversedGraph<>(graph);
        List<AdoptionPlanAppDto> sortedList = applicationIds.stream()
            .map(a -> Application.findById(a))
            .filter(Objects::nonNull) // Application exists
            .filter(a -> ((Application) a).review != null) // Application has a review
            .peek(a -> {
                if (!EffortEstimate.isExists(((Application) a).review.effortEstimate)) {
                    LOGGER.warn("Application discarded by missing Effort mapping for : " + ((Application) a).review.effortEstimate);
                }
            })
            .filter(a -> EffortEstimate.isExists(((Application) a).review.effortEstimate)) // The review has a valid Effort
            .map(e -> buildAdoptionPlanAppDto(applicationIds, reversedGraph, (Application) e))
            .sorted(Comparator.comparing(e -> String.format("%06d", ((AdoptionPlanAppDto) e).positionY) + ((AdoptionPlanAppDto) e).applicationName).reversed())
            .collect(Collectors.toList());

        // adjusting the positionY value to it's real order
        sortedList.forEach(e -> e.positionY = sortedList.indexOf(e));

        return sortedList;
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
    private AdoptionPlanAppDto buildAdoptionPlanAppDto(List<Long> applicationIds, EdgeReversedGraph<Application, DefaultEdge> graph, Application application) {
        if (application.review == null) return null;

        AdoptionPlanAppDto planAppDto = new AdoptionPlanAppDto();
        planAppDto.applicationName = application.name;
        planAppDto.decision = application.review.proposedAction;
        planAppDto.applicationId = application.id;
        planAppDto.effortEstimate = application.review.effortEstimate;
        planAppDto.positionY = application.review.workPriority;

        // Obtain the numeric effort from the String stored
        if (EffortEstimate.isExists(application.review.effortEstimate)) {
            planAppDto.effort = EffortEstimate.getEnum(application.review.effortEstimate).getEffort();
        } else {
            LOGGER.warn("Effort mapping not found for : " + application.review.effortEstimate);
        }

        // Calculate recursively which is the start X position for this Application depending on its dependencies
        planAppDto.positionX = startPositionNode(graph, application, applicationIds);

        return planAppDto;
    }

    private Integer startPositionNode(EdgeReversedGraph<Application, DefaultEdge> graph, Application application, List<Long> selectedApps) {
        List<Application> parents = Graphs.predecessorListOf(graph, application);
        if (parents.size() > 0) {
            return parents.stream().map(e -> {
                // recursively we'll find the position calculating previous positions + lengths of the ancestors
                Integer pos = startPositionNode(graph, e, selectedApps);
                // if the parent is an application selected to appear in the list then we use its effort, otherwise is like we are hiding it with effort = 0
                Integer effort = (e.review != null && EffortEstimate.isExists(e.review.effortEstimate) && selectedApps.contains(e.id)) ? EffortEstimate.getEnum(e.review.effortEstimate).getEffort() : 0;
                return pos + effort;
            })
            .max(Integer::compare).get();
            // Selecting the largest position from its predecessors
        } else {
            // point of return for the recursivity on Top nodes
            return 0;
        }
    }
}
