/*
 * Copyright © 2021 the Konveyor Contributors (https://konveyor.io/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.tackle.applicationinventory.services;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.ResourceArg;
import io.quarkus.test.junit.QuarkusTest;
import io.tackle.applicationinventory.dto.AdoptionPlanAppDto;
import io.tackle.applicationinventory.entities.Application;
import io.tackle.applicationinventory.entities.ApplicationsDependency;
import io.tackle.applicationinventory.entities.Review;
import io.tackle.commons.testcontainers.PostgreSQLDatabaseTestResource;
import org.junit.jupiter.api.*;

import javax.inject.Inject;
import javax.transaction.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;

@QuarkusTest
@QuarkusTestResource(value = PostgreSQLDatabaseTestResource.class,
    initArgs = {
        @ResourceArg(name = PostgreSQLDatabaseTestResource.DB_NAME, value = "application_inventory_db"),
        @ResourceArg(name = PostgreSQLDatabaseTestResource.USER, value = "application_inventory"),
        @ResourceArg(name = PostgreSQLDatabaseTestResource.PASSWORD, value = "application_inventory")
    }
)
class ReportServiceTest extends ReportTestUtil {
    @Inject
    ReportService reportService;

    @Inject
    UserTransaction transaction;

    @Test
    public void given_FullListOfApplications_when_getAdoptionPlan_then_resultIsTheExpected() {
        List<Long> applicationList = new ArrayList<>();
        for (int i=10; i < 20; i++) {
            applicationList.add(((Application) Application.find("name", "App" + i).firstResult()).id);
        }

        List<AdoptionPlanAppDto> applicationPlanDtoList = reportService.getAdoptionPlanAppDtos(applicationList);
        assertThat(applicationPlanDtoList).hasSize(10);

        // checking first app
        assertPlanDto(applicationPlanDtoList, "App10", 23, 1, "Rehost", 4);
        assertPlanDto(applicationPlanDtoList, "App14", 0, 1, "Rehost", 5);
        // checking intermediate app
        assertPlanDto(applicationPlanDtoList, "App15", 16, 4, "Replatform", 3);
        assertPlanDto(applicationPlanDtoList, "App13", 21, 2, "Refactor", 9);

        // checking last app
        assertPlanDto(applicationPlanDtoList, "App19", 0, 8, "Refactor", 0);
    }

    @Test
    public void given_ReducedListOfApplications_when_getAdoptionPlan_then_resultIsTheExpectedWithAppsFillingEmptySpaceOfNonSelectedApps() {
        // Not selecting APP 12
        List<Long> applicationList = new ArrayList<>();
        for (int i=10; i < 20; i++) {
            if (i != 12) {
                applicationList.add(((Application) Application.find("name", "App" + i).firstResult()).id);
            }
        }

        List<AdoptionPlanAppDto> applicationPlanDtoList = reportService.getAdoptionPlanAppDtos(applicationList);
        assertThat(applicationPlanDtoList).hasSize(9);

        // checking first apps
        assertPlanDto(applicationPlanDtoList, "App10", 22, 1, "Rehost", 4);
        assertPlanDto(applicationPlanDtoList, "App11", 0, 8, "Refactor", 7);
        assertPlanDto(applicationPlanDtoList, "App13", 20, 2, "Refactor", 8);
        assertPlanDto(applicationPlanDtoList, "App14", 0, 1, "Rehost", 5);
        // checking intermediate app
        assertPlanDto(applicationPlanDtoList, "App15", 16, 4, "Replatform", 3);
        // checking last app
        assertPlanDto(applicationPlanDtoList, "App19", 0, 8, "Refactor", 0);
    }

    @Test
    public void give_ThousandsOfApplicationsAndDependencies_when_getAdoptionPlan_then_resultIsTheExpectedAndTimeIsLowerThanTimeoutValue() throws HeuristicRollbackException, SystemException, HeuristicMixedException, RollbackException, NotSupportedException {
        List<Long> appList = new ArrayList<>();
        transaction.begin();
        transaction.setTransactionTimeout(200);
        // Add first ones
        List<Long> initialApps = Application.find("name like 'App%'").stream()
            .map(e -> ((Application) e).id)
            .collect(Collectors.toList());
        appList.addAll(initialApps);

        Application app10 = Application.find("name", "App10").firstResult();
        int totalApplications = 2000;
        for (int i = 20; i < totalApplications; i++) {
            Application app = new Application();
            app.name = "App" + i;
            app.businessService = "";
            app.comments = "";
            app.description = "";
            app.tags = Set.of("tag1", "tag2");
            app.persistAndFlush();

            Review review = new Review();
            review.effortEstimate = "Small";
            review.businessCriticality = 1;
            review.comments = "";
            review.proposedAction = "Replatform";
            review.workPriority = 0;
            review.application = app;
            review.persistAndFlush();

            app.review = review;

            ApplicationsDependency dependency = new ApplicationsDependency();
            dependency.to = app10;
            dependency.from = app;
            dependency.persistAndFlush();
            appList.add(app.id);
        }
        transaction.commit();

        List<AdoptionPlanAppDto> applicationPlanDtoList = new ArrayList<>();
        Assertions.assertTimeout(Duration.ofSeconds(15), () -> {
            applicationPlanDtoList.addAll(reportService.getAdoptionPlanAppDtos(appList));
        });

        assertThat(applicationPlanDtoList).hasSize(totalApplications-10);

        // checking 10,14 apps
        assertPlanDto(applicationPlanDtoList, "App10", 23, 1, "Rehost", 4);
        assertPlanDto(applicationPlanDtoList, "App14", 0, 1, "Rehost", 5);

        // checking 15 app
        assertPlanDto(applicationPlanDtoList, "App15", 16, 4, "Replatform", 3);
        // checking 19 app
        assertPlanDto(applicationPlanDtoList, "App19", 0, 8, "Refactor", 0);

        // checking 20 app
        assertPlanDto(applicationPlanDtoList, "App20", 24, 1, "Replatform", null);

        // checking 100 app
        if (totalApplications > 100) {
            assertPlanDto(applicationPlanDtoList, "App100", 24, 1, "Replatform", null);
        }

        // checking 500 app
        if (totalApplications > 500) {
            assertPlanDto(applicationPlanDtoList, "App500", 24, 1, "Replatform", null);
        }

        // checking 1000 app
        if (totalApplications > 1000) {
            assertPlanDto(applicationPlanDtoList, "App1000", 24, 1, "Replatform", null);
        }

        // checking 1500 app
        if (totalApplications > 1500) {
            assertPlanDto(applicationPlanDtoList, "App1500", 24, 1, "Replatform", null);
        }
    }

    @Test
    public void given_SeveralApplicationsWithReviewButOneWithout_when_AdoptionPlan_then_ItDoesNotCrashAndThatAppIsNotIncluded() throws HeuristicRollbackException, SystemException, HeuristicMixedException, RollbackException, NotSupportedException {
        List<Long> applicationList = new ArrayList<>();

        transaction.begin();
        Application app50 = new Application(); // application without review
        app50.name = "App" + 50;
        app50.businessService = "";
        app50.comments = "";
        app50.description = "";
        app50.persistAndFlush();
        transaction.commit();

        for (int i=10; i < 20; i++) {
           applicationList.add(((Application) Application.find("name", "App" + i).firstResult()).id);
        }
        applicationList.add(app50.id);

        List<AdoptionPlanAppDto> applicationPlanDtoList = reportService.getAdoptionPlanAppDtos(applicationList);
        assertThat(applicationPlanDtoList).hasSize(10);
    }

    @Test
    @Transactional
    public void given_SeveralApplicationsWithReviewButOneWithWrongEffortValue_when_AdoptionPlan_then_ItDoesNotCrashAndThatAppIsNotIncluded() {
        List<Long> applicationList = new ArrayList<>();
        for (int i=10; i < 20; i++) {
           applicationList.add(((Application) Application.find("name", "App" + i).firstResult()).id);
        }
        Application app = Application.findById(applicationList.get(0));
        app.review.effortEstimate="NotExistingValue";

        List<AdoptionPlanAppDto> applicationPlanDtoList = reportService.getAdoptionPlanAppDtos(applicationList);
        assertThat(applicationPlanDtoList).hasSize(9);
    }

    @Test
    public void given_SeveralApplicationsWithReviewButOneParentWithoutReview_when_AdoptionPlan_then_ItDoesNotCrashAndThatAppIsNotIncluded() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        transaction.begin();
        Application application15 = Application.find("name", "App15").firstResult();
        application15.review.delete();
        application15.review = null;
        transaction.commit();

        List<Long> applicationList = new ArrayList<>();
        for (int i=10; i < 20; i++) {
            applicationList.add(((Application) Application.find("name", "App" + i).firstResult()).id);
        }

        List<AdoptionPlanAppDto> applicationPlanDtoList = reportService.getAdoptionPlanAppDtos(applicationList);
        assertThat(applicationPlanDtoList).hasSize(9);

        // checking first app App15 has not sum its effort as it doesnt have review
        assertPlanDto(applicationPlanDtoList, "App10", 19, 1, "Rehost", 3);
        assertPlanDto(applicationPlanDtoList, "App14", 0, 1, "Rehost", 4);

        // App15 doesnt appear in the output as it doesnt have review
        assertThat(applicationPlanDtoList.stream().filter(a -> a.applicationName.equalsIgnoreCase("App15")).count()).isEqualTo(0);

        // checking last app
        assertPlanDto(applicationPlanDtoList, "App19", 0, 8, "Refactor", 0);
    }

    private void assertPlanDto(List<AdoptionPlanAppDto> applicationPlanDtoList, String app, Integer expected_positionX, Integer expected_effort, String expected_decision, Integer expected_posy) {
        if (expected_positionX != null ) {
            assertThat(applicationPlanDtoList.stream().filter(e -> e.applicationName.equalsIgnoreCase(app)).findFirst().get().positionX).isEqualTo(expected_positionX);
        }
        if (expected_effort != null) {
            assertThat(applicationPlanDtoList.stream().filter(e -> e.applicationName.equalsIgnoreCase(app)).findFirst().get().effort).isEqualTo(expected_effort);
        }
        if (expected_decision != null) {
            assertThat(applicationPlanDtoList.stream().filter(e -> e.applicationName.equalsIgnoreCase(app)).findFirst().get().decision).isEqualTo(expected_decision);
        }
        if (expected_posy != null) {
            assertThat(applicationPlanDtoList.stream().filter(e -> e.applicationName.equalsIgnoreCase(app)).findFirst().get().positionY).isEqualTo(expected_posy);
        }
    }
}