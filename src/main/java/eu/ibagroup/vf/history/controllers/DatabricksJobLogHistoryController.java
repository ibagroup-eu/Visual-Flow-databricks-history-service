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

package eu.ibagroup.vf.history.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import eu.ibagroup.vf.history.model.JobLog;
import eu.ibagroup.vf.history.services.DatabricksJobLogHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/databricks")
@Tag(name = "Job log history", description = "Job log history API")
public class DatabricksJobLogHistoryController {

    private final DatabricksJobLogHistoryService databricksJobLogHistoryService;

    /**
     * Saving job log history.
     *
     */
    @Operation(summary = "Save job log history", description = "Save job log history")
    @PostMapping("history/job/{jobId}/log")
    public ResponseEntity<String> saveJobLogHistory(@PathVariable String jobId, @RequestBody List<JobLog> jobLogs)
            throws JsonProcessingException {
        String id = databricksJobLogHistoryService.saveJobLog(jobId, jobLogs);
        return ResponseEntity.status(HttpStatus.OK).body(id);
    }

    /**
     * Retrieving job log.
     *
     */
    @Operation(summary = "Get job log history", description = "Get job log history")
    @GetMapping("history/job/{jobId}/log/{logId}")
    public ResponseEntity<List<JobLog>> getJobLogHistory(@PathVariable String jobId, @PathVariable String logId)
            throws JsonProcessingException {
        return ResponseEntity.status(HttpStatus.OK).body(databricksJobLogHistoryService.getLogsById(jobId, logId));
    }

    /**
     * Retrieving the last job log.
     *
     */
    @Operation(summary = "Get the last job log history", description = "Get the last job log history")
    @GetMapping("history/job/{jobId}/log/last")
    public ResponseEntity<List<JobLog>> getTheLastJobLogHistory(@PathVariable String jobId) {
        return ResponseEntity.status(HttpStatus.OK).body(databricksJobLogHistoryService.getLastJobLogHistory(jobId));
    }
}
