package eu.ibagroup.vf.history.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import eu.ibagroup.vf.history.model.JobLog;
import eu.ibagroup.vf.history.services.DatabricksJobLogHistoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DatabricksJobLogHistoryControllerTest {
    @Mock
    private DatabricksJobLogHistoryService logHistoryService;
    private DatabricksJobLogHistoryController logHistoryController;

    @BeforeEach
    void setUp() {
        logHistoryController = new DatabricksJobLogHistoryController(logHistoryService);
    }

    @Test
    void testSaveJobLogHistory() throws JsonProcessingException {
        when(logHistoryService.saveJobLog(anyString(), any())).thenReturn("id");
        ResponseEntity<String> response = logHistoryController.saveJobLogHistory("jobId", List.of());

        verify(logHistoryService).saveJobLog(anyString(), any());
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Status must be OK");
    }

    @Test
    void testGetJobLogHistory() throws JsonProcessingException {
        when(logHistoryService.getLogsById("jobId", "logId")).thenReturn(List.of(JobLog.builder().build()));
        List<JobLog> response = logHistoryController.getJobLogHistory("jobId", "logId").getBody();

        assertEquals(1, response.size(), "Jobs log size must be 1");
        verify(logHistoryService).getLogsById(anyString(), anyString());
    }

    @Test
    void testGetTheLastJobLogHistory() throws JsonProcessingException {
        when(logHistoryService.getLastJobLogHistory("jobId")).thenReturn(List.of(JobLog.builder().build()));
        List<JobLog> response = logHistoryController.getTheLastJobLogHistory("jobId").getBody();

        assertEquals(1, response.size(), "Jobs log size must be 1");
        verify(logHistoryService).getLastJobLogHistory(anyString());
    }
}
