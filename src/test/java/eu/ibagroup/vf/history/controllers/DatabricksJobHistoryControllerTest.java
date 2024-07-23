package eu.ibagroup.vf.history.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import eu.ibagroup.vf.history.dto.JobHistoryDto;
import eu.ibagroup.vf.history.services.DatabricksJobHistoryService;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DatabricksJobHistoryControllerTest {
    @Mock
    private DatabricksJobHistoryService databricksJobHistoryService;
    @Mock
    private DatabricksJobLogHistoryService databricksJobLogHistoryService;
    private DatabricksJobHistoryController databricksJobHistoryController;

    @BeforeEach
    void setUp() {
        databricksJobHistoryController = new DatabricksJobHistoryController(databricksJobHistoryService, databricksJobLogHistoryService);
    }

    @Test
    void testSaveJobHistory() throws JsonProcessingException {
        when(databricksJobHistoryService.saveJobHistory(any())).thenReturn("id");
        ResponseEntity<String> response = databricksJobHistoryController.saveJobHistory(JobHistoryDto.builder().build());

        verify(databricksJobHistoryService).saveJobHistory(any());
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Status must be OK");
    }

    @Test
    void testGetJobHistoryById() {
        String id = "id";
        when(databricksJobHistoryService.getJobHistoryById(id)).thenReturn(List.of(
                JobHistoryDto.builder()
                        .jobId("id")
                        .jobName("name")
                        .type("type")
                        .operation("operation")
                        .startedAt("startedAt")
                        .finishedAt("finishedAt")
                        .status("status")
                        .logId("1")
                        .build()
        ));
        List<JobHistoryDto> response = databricksJobHistoryController.getJobHistoryById("id");

        assertEquals(1, response.size(), "Jobs size must be 1");
        verify(databricksJobHistoryService).getJobHistoryById(anyString());
    }
}
