package eu.dnetlib.iis.core.examples.spark;

import java.io.FileInputStream;

import org.apache.oozie.util.IOUtils;
import org.junit.Test;

import eu.dnetlib.iis.core.AbstractOozieWorkflowTestCase;
import eu.dnetlib.iis.core.OozieWorkflowTestConfiguration;
import eu.dnetlib.iis.core.TestsIOUtils;
import eu.dnetlib.iis.core.WorkflowTestResult;

/**
 * 
 * @author madryk
 *
 */
public class PersonIdExtractorWorkflowTest extends AbstractOozieWorkflowTestCase {

    @Test
    public void personIdExtractTest() throws Exception {
        
        // given
        OozieWorkflowTestConfiguration conf = new OozieWorkflowTestConfiguration();
        conf.addExpectedOutputFile("output/person_id/part-00000");
        
        
        // execute
        WorkflowTestResult workflowTestResult = testWorkflow("eu/dnetlib/iis/core/examples/spark/person_id_extractor", conf);
        
        
        // assert
        TestsIOUtils.assertUtf8TextContentsEqual(IOUtils.getResourceAsStream("eu/dnetlib/iis/core/examples/simple_csv_data/person_id.csv", 0),
                new FileInputStream(workflowTestResult.getWorkflowOutputFile("output/person_id/part-00000")));
    }
}