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
import eu.ibagroup.vf.history.dto.JobHistoryDto;
import eu.ibagroup.vf.history.model.JobHistory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DatabricksJobHistoryService {

    private static final String JOB_PREFIX = "job:";
    private static final String HISTORY_PREFIX = ":history:";

    private final RedisTemplate<String, String> redisTemplate;

    public DatabricksJobHistoryService(@Qualifier("redisTemplate") RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Save job history.
     *
     */
    public String saveJobHistory(JobHistoryDto jobHistoryDto) throws JsonProcessingException {
        long historyId = Math.abs(UUID.randomUUID().getLeastSignificantBits());
        String folderKey = JOB_PREFIX + jobHistoryDto.getJobId();
        String historyKey = folderKey + HISTORY_PREFIX + historyId;
        JobHistory jobHistory = JobHistory.builder()
                .id(historyId)
                .jobName(jobHistoryDto.getJobName())
                .jobId(jobHistoryDto.getJobId())
                .type(jobHistoryDto.getType())
                .startedAt(jobHistoryDto.getStartedAt())
                .finishedAt(jobHistoryDto.getFinishedAt())
                .startedBy(jobHistoryDto.getStartedBy())
                .status(jobHistoryDto.getStatus())
                .operation(null)
                .logId(jobHistoryDto.getLogId())
                .build();
        ObjectMapper objectMapper = new ObjectMapper();
        String jobHistoryJson = objectMapper.writeValueAsString(jobHistory);
        redisTemplate.opsForHash().put(folderKey, historyKey, jobHistoryJson);
        return String.valueOf(historyId);
    }

    /**
     * Return job history by id.
     *
     * @param jobId job id
     * @return list of job history
     */
    public List<JobHistoryDto> getJobHistoryById(String jobId) {
        String folderKey = JOB_PREFIX + jobId;
        Map<String, JobHistory> resultMap = redisTemplate.opsForHash().entries(folderKey)
                .entrySet()
                .stream()
                .collect(Collectors.toMap(entry -> entry.getKey().toString(), entry -> {
                    try {
                        return jsonToJobHistory((String) entry.getValue());
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }));
        List<JobHistoryDto> jobHistories = new ArrayList<>();
        resultMap.forEach((key, value) -> jobHistories.add(JobHistoryDto.builder()
                .jobId(value.getJobId())
                .jobName(value.getJobName())
                .type(value.getType())
                .startedAt(value.getStartedAt())
                .finishedAt(value.getFinishedAt())
                .operation(value.getOperation())
                .startedBy(value.getStartedBy())
                .status(value.getStatus())
                .logId(value.getLogId())
                .build()));
        return jobHistories;
    }

    private JobHistory jsonToJobHistory(String jobHistoryJson) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JobHistory jobHistory = objectMapper.readValue(jobHistoryJson, JobHistory.class);
        return jobHistory;
    }

    /**
     * Delete job history by id.
     *
     * @param jobId     job id
     * @param historyId history id
     */
    public void delete(String jobId, String historyId) {
        String folderKey = JOB_PREFIX + jobId;
        String historyKey = folderKey + HISTORY_PREFIX + historyId;
        redisTemplate.opsForHash().delete(folderKey, historyKey);
    }

    /**
     * Delete all histories by jobId.
     *
     * @param jobId     job id
     */
    public void deleteByJobId(String jobId) {
        String folderKey = JOB_PREFIX + jobId;
        redisTemplate.opsForHash().entries(folderKey).keySet()
                .forEach(key -> redisTemplate.opsForHash().delete(folderKey, key));
    }
}
