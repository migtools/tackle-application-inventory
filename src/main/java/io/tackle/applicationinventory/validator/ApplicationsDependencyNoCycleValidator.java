package io.tackle.applicationinventory.validator;

import io.quarkus.panache.common.Sort;
import io.tackle.applicationinventory.entities.Application;
import io.tackle.applicationinventory.entities.ApplicationsDependency;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.cycle.CycleDetector;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.builder.GraphTypeBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.stream.Stream;

@ApplicationScoped
public class ApplicationsDependencyNoCycleValidator implements ConstraintValidator<ApplicationsDependencyNoCycle, ApplicationsDependency> {

    static Graph<Application, DefaultEdge> graph;

    @Override
    public void initialize(ApplicationsDependencyNoCycle constraintAnnotation) {

    }

    @Override
    public boolean isValid(ApplicationsDependency dependency, ConstraintValidatorContext context) {
        if ( dependency == null ) {
            return true;
        }

        if (graph == null)
        {
            graph = GraphTypeBuilder.<Application, DefaultEdge>
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
        }

        // add the dependency
        DefaultEdge edge = Graphs.addEdgeWithVertices(graph, dependency.from, dependency.to);
        // check if it creates an cycle
        CycleDetector<Application, DefaultEdge> cycleDetector = new CycleDetector<>(graph);

        if(cycleDetector.detectCycles())
        {
           graph.removeEdge(edge);
           return false;
        }
        else
        {
           return true;
        }
    }
}
