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
package io.tackle.applicationinventory.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
@JsonIgnoreProperties({ "createUser", "updateUser", "errorMessage", "valid", "isValid", "filename", "id"})
public abstract class ApplicationImportForCsv {
    @JsonProperty("Record Type 1")
    private String recordType1;
    @JsonProperty("Application Name")
    private String applicationName;
    @JsonProperty("Description")
    private String description;
    @JsonProperty("Comments")
    private String comments;
    @JsonProperty("Business Service")
    private String businessService;
    @JsonProperty("Dependency")
    private String dependency;
    @JsonProperty("Dependency Direction")
    private String dependencyDirection;
    @JsonProperty("Tag Type 1")
    private String tagType1;
    @JsonProperty("Tag 1")
    private String tag1;
    @JsonProperty("Tag Type 2")
    private String tagType2;
    @JsonProperty("Tag 2")
    private String tag2;
    @JsonProperty("Tag Type 3")
    private String tagType3;
    @JsonProperty("Tag 3")
    private String tag3;
    @JsonProperty("Tag Type 4")
    private String tagType4;
    @JsonProperty("Tag 4")
    private String tag4;
    @JsonProperty("Tag Type 5")
    private String tagType5;
    @JsonProperty("Tag 5")
    private String tag5;
    @JsonProperty("Tag Type 6")
    private String tagType6;
    @JsonProperty("Tag 6")
    private String tag6;
    @JsonProperty("Tag Type 7")
    private String tagType7;
    @JsonProperty("Tag 7")
    private String tag7;
    @JsonProperty("Tag Type 8")
    private String tagType8;
    @JsonProperty("Tag 8")
    private String tag8;
    @JsonProperty("Tag Type 9")
    private String tagType9;
    @JsonProperty("Tag 9")
    private String tag9;
    @JsonProperty("Tag Type 10")
    private String tagType10;
    @JsonProperty("Tag 10")
    private String tag10;
    @JsonProperty("Tag Type 11")
    private String tagType11;
    @JsonProperty("Tag 11")
    private String tag11;
    @JsonProperty("Tag Type 12")
    private String tagType12;
    @JsonProperty("Tag 12")
    private String tag12;
    @JsonProperty("Tag Type 13")
    private String tagType13;
    @JsonProperty("Tag 13")
    private String tag13;
    @JsonProperty("Tag Type 14")
    private String tagType14;
    @JsonProperty("Tag 14")
    private String tag14;
    @JsonProperty("Tag Type 15")
    private String tagType15;
    @JsonProperty("Tag 15")
    private String tag15;
    @JsonProperty("Tag Type 16")
    private String tagType16;
    @JsonProperty("Tag 16")
    private String tag16;
    @JsonProperty("Tag Type 17")
    private String tagType17;
    @JsonProperty("Tag 17")
    private String tag17;
    @JsonProperty("Tag Type 18")
    private String tagType18;
    @JsonProperty("Tag 18")
    private String tag18;
    @JsonProperty("Tag Type 19")
    private String tagType19;
    @JsonProperty("Tag 19")
    private String tag19;
    @JsonProperty("Tag Type 20")
    private String tagType20;
    @JsonProperty("Tag 20")
    private String tag20;
}



