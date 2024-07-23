/*
 * Copyright (c) 2021 IBA Group, a.s. All rights reserved.
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.ibagroup.vf.history.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobHistory implements Serializable {
    private Long id;
    private String jobId;
    private String jobName;
    private String type;
    private String operation;
    private String startedAt;
    private String finishedAt;
    private String startedBy;
    private String status;
    private String logId;

    public JobHistory(
            String jobId,
            String jobName,
            String type,
            String operation,
            String startedAt,
            String finishedAt,
            String startedBy,
            String status,
            String logId) {
        this.jobId = jobId;
        this.jobName = jobName;
        this.type = type;
        this.operation = operation;
        this.startedAt = startedAt;
        this.finishedAt = finishedAt;
        this.startedBy = startedBy;
        this.status = status;
        this.logId = logId;
    }
}
