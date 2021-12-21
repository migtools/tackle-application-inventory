package io.tackle.applicationinventory.services;

import io.tackle.applicationinventory.entities.Application;
import io.tackle.applicationinventory.entities.ApplicationsDependency;
import io.tackle.applicationinventory.entities.Review;
import io.tackle.commons.tests.SecuredResourceTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import javax.transaction.Transactional;
import java.util.List;

public abstract class ReportTestUtil extends SecuredResourceTest {
    @BeforeEach
    @Transactional
    public void insertDataForTests() {
        for (int i=10; i < 20; i++) {
            createApplication(i);
        }
        createDependency(10, 13);
        createDependency(10, 12);
        createDependency(10, 16);
        createDependency(12, 15);
        createDependency(13, 12);
        createDependency(13, 15);
        createDependency(16, 18);
        createDependency(16, 19);
        createDependency(15, 17);
        createDependency(15, 18);
        createDependency(17, 19);
    }

    @AfterEach
    @Transactional
    public void deleteDataForTests() {
        Application.find("name like 'App%'").stream().forEach(e -> e.delete());
    }

    @Transactional
    public void createDependency(int from, int to) {
        ApplicationsDependency dependency = new ApplicationsDependency();
        dependency.from = (Application) Application.find("name", "App" + from).firstResultOptional().orElseThrow();
        dependency.to = (Application) Application.find("name", "App" + to).firstResultOptional().orElseThrow();
        dependency.persist();
    }

    @Transactional
    public static void createApplication(int i) {
        Application app = new Application();
        app.name = "App" + i;
        app.businessService = "";
        app.comments = "";
        app.description = "";
        //app.tags = Set.of("tag1", "tag2");
        app.persistAndFlush();

        Review review = new Review();

        // 10 Small , 11, XLarge, 12 Small, 13 Medium , 14 Small, 15 Large, 16 Large, 17 XLarge, 18 XLarge, 19 XLarge
        review.effortEstimate = ( List.of(10,12,14).contains(i) ? "Small" : (i == 13 ? "Medium" : (List.of(15,16).contains(i) ? "Large" : "Extra_Large")));

        // 10 Rehost, 11 Refactor, 12 Replatform, 13 Refactor, 14 Rehost, 15 Replatform, 16 Refactor, 17 Rehost, 18 Replatform, 19 Refactor
        review.proposedAction = ( List.of(10,14,17).contains(i) ? "Rehost" : (List.of(15,12, 18).contains(i) ? "Replatform" : "Refactor"));

        // 10-2 , 11-1, 12-1, 13-1, 14-2, 15-5, 16-6, 17-2, 18-8, 19-9
        review.workPriority = ( List.of(10,14,17).contains(i) ? 2 : (i == 15 ? 5 : (List.of(13,12,11).contains(i) ? 1 : i - 10)));

        review.businessCriticality = 1;
        review.comments = "";
        review.application = app;
        review.persistAndFlush();

        app.review = review;
    }
}
