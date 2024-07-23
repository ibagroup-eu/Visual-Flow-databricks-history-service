package eu.ibagroup.vf.history;

import eu.ibagroup.vf.history.controllers.DatabricksJobHistoryController;
import eu.ibagroup.vf.history.services.DatabricksJobHistoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class VfApiHistoryApplicationTests {
	@Autowired
	private DatabricksJobHistoryService databricksJobHistoryService;
	@Autowired
	private DatabricksJobHistoryController databricksJobHistoryController;


	@Test
	void contextLoads() {
		assertNotNull(databricksJobHistoryController, "DatabricksJobHistoryController should not be null!");
		assertNotNull(databricksJobHistoryService, "DatabricksJobHistoryService should not be null!");
	}
}
