package io.tackle.applicationinventory.entities;

import io.quarkus.panache.common.Sort;
import io.tackle.applicationinventory.exceptions.ApplicationsInventoryException;
import io.tackle.commons.annotations.Filterable;
import io.tackle.commons.entities.AbstractEntity;
import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.cycle.CycleDetector;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.builder.GraphTypeBuilder;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.ws.rs.core.Response;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
@Table(
        name = "applications_dependency"
)
@SQLDelete(sql = "UPDATE applications_dependency SET deleted = true WHERE id = ?", check = ResultCheckStyle.COUNT)
@Where(clause = "deleted = false")
public class ApplicationsDependency extends AbstractEntity {
    @ManyToOne
    // in this bean, since the 'uniqueConstraints' has been specified,
    // it's safer to ensure the constraint column names are declared
    @JoinColumn(name = "from_id")
    @Filterable(filterName = "from.id")
    // it's the caller, the dependant, the northbound side
    public Application from;

    @ManyToOne
    // in this bean, since the 'uniqueConstraints' has been specified,
    // it's safer to ensure the constraint column names are declared
    @JoinColumn(name = "to_id")
    @Filterable(filterName = "to.id")
    // it's the responder, the dependency, the southbound side
    public Application to;
    
    @PrePersist
    @PreUpdate
    public void preChangesCheck() {
        validate(from, to);
    }

    public static void validate(Application from, Application to) throws ApplicationsInventoryException {
        // validate input data
        if (from == null || to == null) throw new ApplicationsInventoryException("Not valid application reference provided", Response.Status.BAD_REQUEST);
        // "self-loop" for dependencies is not allowed
        if (from.equals(to)) throw new ApplicationsInventoryException("'from' and 'to' values are the same: an application can not be a dependency of itself", Response.Status.CONFLICT);
        // applications must be already in the DB
        Application fromApplication = Application.findById(from.id);
        if (fromApplication == null) throw new ApplicationsInventoryException(String.format("Not found the application with id %s", from.id), Response.Status.NOT_FOUND);
        Application toApplication = Application.findById(to.id);
        if (toApplication == null) throw new ApplicationsInventoryException(String.format("Not found the application with id %s", to.id), Response.Status.NOT_FOUND);

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
        // add the dependency
        Graphs.addEdgeWithVertices(graph, fromApplication, toApplication);

        // check if it creates an cycle
        CycleDetector<Application, DefaultEdge> cycleDetector = new CycleDetector<>(graph);
        if (cycleDetector.detectCycles()) {
            final String message = cycleDetector.findCyclesContainingVertex(fromApplication)
                    .stream()
                    .map(application -> application.name)
                    .collect(Collectors.joining("', '", "Dependencies cycle created from applications '", "'"));
            throw new ApplicationsInventoryException(message, Response.Status.CONFLICT);
        }
    }

}
