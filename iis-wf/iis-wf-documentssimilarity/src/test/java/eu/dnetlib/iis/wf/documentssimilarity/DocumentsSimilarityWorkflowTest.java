package eu.dnetlib.iis.wf.documentssimilarity;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import eu.dnetlib.iis.common.IntegrationTest;
import eu.dnetlib.iis.common.OozieWorkflowTestConfiguration;
import eu.dnetlib.iis.common.AbstractOozieWorkflowTestCase;

/**
 * @author madryk
 */
@Category(IntegrationTest.class)
public class DocumentsSimilarityWorkflowTest extends AbstractOozieWorkflowTestCase {

	@Test
	public void documentssimilarity_main_workflow() {
		
		OozieWorkflowTestConfiguration testConfig = new OozieWorkflowTestConfiguration();
		testConfig.setTimeoutInSeconds(3600);
		
		testWorkflow("eu/dnetlib/iis/wf/documentssimilarity/main_workflow", testConfig);
		
	}
}
