package eu.ibagroup.vf.history.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DatabricksJobLogHistoryServiceTest {
    @Mock
    private HashOperations hashOperations;
    @Mock
    private RedisTemplate<String, String> redisTemplate;
    @InjectMocks
    private DatabricksJobLogHistoryService logHistoryService;

    @Test
    void testGetLogsById() throws JsonProcessingException {
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        String log1 = "{\n" +
                "    \"id\": \"4584542565645\",\n" +
                "    \"jobLogs\": [{\n" +
                "    \"timestamp\": \"24/06/06 07:03:57\",\n" +
                "    \"level\": \"INFO\",\n" +
                "    \"message\": \"SparkHadoopUtil: Installing CredentialsScopeFilesystem for scheme s3. Previous value: com.databricks.common.filesystem.LokiFileSystem\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"timestamp\": \"24/06/06 07:03:58\",\n" +
                "    \"level\": \"INFO\",\n" +
                "    \"message\": \"SparkHadoopUtil: Installing CredentialsScopeFilesystem for scheme s3a. Previous value: com.databricks.common.filesystem.LokiFileSystem\"\n" +
                "  }]" +
                "  }";
        when(hashOperations.get(any(), any())).thenReturn( log1);
        logHistoryService.getLogsById("0ee8f216-435e-40ff-935f-44e4b850683e", "logId");
        verify(hashOperations).get(any(), any());
    }


    @Test
    void testSaveJobLog() throws JsonProcessingException {
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        doNothing().when(hashOperations).put(anyString(), anyString(), any());
        logHistoryService.saveJobLog("jobId", List.of());
        verify(hashOperations).put(eq("jobLogs:jobId"), anyString(), any());
    }

    @Test
    void testGetAll() throws IOException {
        Path file = Path.of("", "src/test/resources").resolve("jobLogHistory.json");
        String json = Files.readString(file);
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.entries(anyString())).thenReturn(Map.of("log1", json, "log2", json));
        logHistoryService.getAll("jobId");
        verify(hashOperations).entries(anyString());
    }

    @Test
    void testGetLastJobLogHistory() throws IOException {
        Path path = Path.of("", "src/test/resources");
        Path file1 = path.resolve("jobLogHistory.json");
        Path file2 = path.resolve("jobLogHistory2.json");
        String json1 = Files.readString(file1);
        String json2 = Files.readString(file2);
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.entries(anyString())).thenReturn(Map.of("log1", json1, "log2", json2));
        assertEquals("ERROR", logHistoryService.getLastJobLogHistory("jobId").get(0).getLevel());
    }
}
