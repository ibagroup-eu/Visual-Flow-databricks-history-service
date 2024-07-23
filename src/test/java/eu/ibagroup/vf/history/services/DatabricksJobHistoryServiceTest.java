package eu.ibagroup.vf.history.services;


import com.fasterxml.jackson.core.JsonProcessingException;
import eu.ibagroup.vf.history.dto.JobHistoryDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DatabricksJobHistoryServiceTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private HashOperations hashOperations;

    @InjectMocks
    private DatabricksJobHistoryService databricksJobHistoryService;

    @Test
    void testGetJobHistoryById() {
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        String jsonJob1 = "{\n" +
                "    \"jobId\": \"0ee8f216-435e-40ff-935f-44e4b850683e\",\n" +
                "    \"jobName\": \"job1\",\n" +
                "    \"type\": \"job\",\n" +
                "    \"startedAt\": \"1714555957193\",\n" +
                "    \"finishedAt\": \"1714556345924\",\n" +
                "    \"startedBy\": \"vyarmolenka@ibagroup.eu\",\n" +
                "    \"status\": \"Succeeded\"\n" +
                "  }";
        String jsonJob2 = "{\n" +
                "    \"jobId\": \"0ee8f216-435e-40ff-935f-44e4b850683h\",\n" +
                "    \"jobName\": \"job2\",\n" +
                "    \"type\": \"job\",\n" +
                "    \"startedAt\": \"1714555957193\",\n" +
                "    \"finishedAt\": \"1714556345924\",\n" +
                "    \"startedBy\": \"vyarmolenka@ibagroup.eu\",\n" +
                "    \"status\": \"Succeeded\"\n" +
                "  }";
        when(hashOperations.entries(any())).thenReturn(Map.of("project:project3:job:41b95016-d0fd-4d5f-acbf-45764b6694d1", jsonJob1, "project:project3:job:468ba0e3-5364-44fa-acbf-f474215715b4", jsonJob2));
        databricksJobHistoryService.getJobHistoryById("jobId");
        verify(hashOperations).entries(any());
    }


    @Test
    void testSaveJobHistory() throws JsonProcessingException {
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        doNothing().when(hashOperations).put(anyString(), anyString(), any());
        databricksJobHistoryService.saveJobHistory(JobHistoryDto.builder().jobId("jobId").build());
        verify(hashOperations).put(eq("job:jobId"), anyString(), any());
    }
}
