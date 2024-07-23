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

package eu.ibagroup.vf.history.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.ibagroup.vf.history.model.JobLog;
import eu.ibagroup.vf.history.model.JobLogHistory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class DatabricksJobLogHistoryService {
    private static final String JOB_LOGS_PREFIX = "jobLogs:";
    private final RedisTemplate<String, String> redisTemplate;

    /**
     * Save job log.
     *
     */
    public String saveJobLog(String jobId, List<JobLog> jobLogs) throws JsonProcessingException {
        long logId = Math.abs(UUID.randomUUID().getLeastSignificantBits());
        String folderKey = JOB_LOGS_PREFIX + jobId;
        JobLogHistory jobLogHistory = JobLogHistory.builder().id(logId).jobLogs(jobLogs)
                .timestamp(Instant.now().toEpochMilli()).build();
        ObjectMapper objectMapper = new ObjectMapper();
        String jobHistoryJson = objectMapper.writeValueAsString(jobLogHistory);
        redisTemplate.opsForHash().put(folderKey, String.valueOf(logId), jobHistoryJson);
        return String.valueOf(logId);
    }

    /**
     * Delete all logs by jobId.
     *
     * @param jobId     job id
     */
    public void deleteByJobId(String jobId) {
        String folderKey = JOB_LOGS_PREFIX + jobId;
        redisTemplate.opsForHash().entries(folderKey).keySet()
                .forEach(key -> redisTemplate.opsForHash().delete(folderKey, key));
    }

    public List<JobLogHistory> getAll(String jobId) {
        List<JobLogHistory> logs = new ArrayList<>();
        String folderKey = JOB_LOGS_PREFIX + jobId;
        redisTemplate.opsForHash().entries(folderKey).forEach((Object key, Object value) -> {
            try {
                logs.add(new ObjectMapper().readValue((String) value, JobLogHistory.class));
            } catch (JsonProcessingException e) {
                LOGGER.error("Error while executing getAll method: " + e.getMessage());
            }
        });
        return logs;
    }

    public List<JobLog> getLastJobLogHistory(String jobId) {
        List<JobLog> result = new ArrayList<>();
        getAll(jobId).stream()
                .max(Comparator.comparingLong(JobLogHistory::getTimestamp))
                .ifPresent(jobLogHistory -> result.addAll(jobLogHistory.getJobLogs()));
        return result;
    }

    /**
     * Return job log by id.
     *
     * @param jobId job id
     * @return list of job history
     */
    public List<JobLog> getLogsById(String jobId, String logId) throws JsonProcessingException {
        String folderKey = JOB_LOGS_PREFIX + jobId;
        JobLogHistory log =
                new ObjectMapper()
                        .readValue((String) redisTemplate.opsForHash().get(folderKey, logId), JobLogHistory.class);
        return log.getJobLogs();
    }
}
