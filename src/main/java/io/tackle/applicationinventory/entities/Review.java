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
import io.tackle.commons.entities.AbstractEntity;
import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "review")
@SQLDelete(sql = "UPDATE review SET deleted = true WHERE id = ?", check = ResultCheckStyle.COUNT)
@Where(clause = "deleted = false")
public class Review extends AbstractEntity {
    public String proposedAction;
    public String effortEstimate;
    public Integer businessCriticality;
    public Integer workPriority;
    @Column(length = 1024)
    public String comments;
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JsonIgnoreProperties(value = {"review"}, allowSetters = true)
    public Application application;

    // No foreign keys associated to this field since foreign keys might
    // complicate DELETE operations.
    public Long copiedFromReviewId;
}
