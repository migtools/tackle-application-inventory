/*
 * Copyright © 2021 Konveyor (https://konveyor.io/)
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
package io.tackle.applicationinventory.flyway;

import io.quarkus.test.junit.QuarkusTestProfile;
import io.tackle.commons.testcontainers.PostgreSQLDatabaseTestResource;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FlywayMigrationProfile implements QuarkusTestProfile {

    @Override
    public Map<String, String> getConfigOverrides() {
        return Collections.singletonMap("quarkus.oidc.enabled","false");
    }

    @Override
    public Set<Class<?>> getEnabledAlternatives() {
        return Collections.emptySet();
    }

    @Override
    public String getConfigProfile() {
        return "flyway";
    }

    @Override
    public List<TestResourceEntry> testResources() {
        Map<String, String> args = new HashMap<>(3);
        args.put(PostgreSQLDatabaseTestResource.DB_NAME,"application_inventory_db");
        args.put(PostgreSQLDatabaseTestResource.USER,"application_inventory");
        args.put(PostgreSQLDatabaseTestResource.PASSWORD,"application_inventory");
        return Collections.singletonList(new TestResourceEntry(PostgreSQLDatabaseTestResource.class, args));
    }

    @Override
    public Set<String> tags() {
        return Collections.singleton("flyway");
    }
    
    @Override
    public boolean disableGlobalTestResources() {
        return true;
    }
}
