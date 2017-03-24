package eu.dnetlib.iis.common;

import java.util.List;

import org.apache.oozie.client.WorkflowJob;

import com.google.common.collect.Lists;

/**
 * Configuration of workflow test.
 * It contains additional criteria that must met for test to pass.
 * 
 * @author Mateusz Kobos
 * @author madryk
 */
public class OozieWorkflowTestConfiguration {
	
	public static final int defaultTimeoutInSeconds = 360;
	public static final WorkflowJob.Status defaultExpectedFinishStatus = 
			WorkflowJob.Status.SUCCEEDED;
	
	private int timeoutInSeconds = defaultTimeoutInSeconds;
	private WorkflowJob.Status expectedFinishStatus = defaultExpectedFinishStatus;
	
	private final List<String> expectedOutputFiles = Lists.newArrayList();
	private final List<String> expectedOutputAvroDataStores = Lists.newArrayList();
	
	
	/**
	 * See {@link #setTimeoutInSeconds} for description
	 */
	public int getTimeoutInSeconds() {
		return timeoutInSeconds;
	}
	/**
	 * @param timeoutInSeconds timeout in seconds. Workflow will be killed
     * if timeout is exceeded
	 */
	public OozieWorkflowTestConfiguration setTimeoutInSeconds(int timeoutInSeconds) {
		this.timeoutInSeconds = timeoutInSeconds;
		return this;
	}
	
	/**
	 * See {@link #setExpectedFinishStatus} for description
	 * @return
	 */
	public WorkflowJob.Status getExpectedFinishStatus() {
		return expectedFinishStatus;
	}
	/**
	 * @param expectedFinishStatus expected status of the workflow after its
	 * finish
	 */
	public OozieWorkflowTestConfiguration setExpectedFinishStatus(WorkflowJob.Status expectedFinishStatus) {
		this.expectedFinishStatus = expectedFinishStatus;
		return this;
	}
	
	/**
	 * Adds a file generated by test workflow to expected files of the test 
	 * All found and expected files will be included in {@link WorkflowTestResult#getWorkflowOutputFile(String)} after execution 
	 * of {@link AbstractOozieWorkflowTestCase#testWorkflow(String, OozieWorkflowTestConfiguration)}
	 * @param path a path to the file, relative to working_dir
	 */
	public OozieWorkflowTestConfiguration addExpectedOutputFile(String filepath) {
		this.expectedOutputFiles.add(filepath);
		return this;
	}
	
	/**
	 * Returns files generated by test workflow which have to be
	 * included in {@link WorkflowTestResult}
	 */
	public List<String> getExpectedOutputFiles() {
		return expectedOutputFiles;
	}

	/**
	 * Adds a datastore generated by test workflow to expected files of the test. 
	 * All found and expected files will be included in {@link WorkflowTestResult#getAvroDataStore(String)} after execution 
	 * of {@link AbstractOozieWorkflowTestCase#testWorkflow(String, OozieWorkflowTestConfiguration)}
	 * @param path a path to the datastore, relative to working_dir
	 */
	public OozieWorkflowTestConfiguration addExpectedOutputAvroDataStore(String path) {
		this.expectedOutputAvroDataStores.add(path);
		return this;
	}
	
	/**
	 * Returns datastores generated by test workflow which have to be
	 * included in {@link WorkflowTestResult}
	 */
	public List<String> getExpectedOutputAvroDataStore() {
		return expectedOutputAvroDataStores;
	}
}
