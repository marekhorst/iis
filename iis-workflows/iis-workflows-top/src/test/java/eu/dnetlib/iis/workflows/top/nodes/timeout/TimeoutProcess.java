package eu.dnetlib.iis.workflows.top.nodes.timeout;

import java.util.Collections;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.log4j.Logger;

import eu.dnetlib.iis.core.java.PortBindings;
import eu.dnetlib.iis.core.java.porttype.PortType;

public class TimeoutProcess implements eu.dnetlib.iis.core.java.Process {

	public static final String PARAM_TIMEOUT = "timeout";
	
	public static final String PARAM_NODE_ID = "node_id";
	
	public static final long DEFAULT_TIMEOUT_MILLIS = 60000;
	
	public static final Logger log = Logger.getLogger(TimeoutProcess.class);
	
	@Override
	public Map<String, PortType> getInputPorts() {
		return Collections.emptyMap();
	}

	@Override
	public Map<String, PortType> getOutputPorts() {
		return Collections.emptyMap();
	}

	@Override
	public void run(PortBindings portBindings, Configuration conf,
			Map<String, String> parameters) throws Exception {
		System.out.println("entering timeout node");
		
		long waitTime;
		if (parameters.containsKey(PARAM_TIMEOUT)) {
			waitTime = Long.parseLong(parameters.get(PARAM_TIMEOUT));
		} else {
			waitTime = DEFAULT_TIMEOUT_MILLIS;
		}
		
		String nodeId = parameters.get(PARAM_NODE_ID);
		
		log.warn("node "+ nodeId +" is waiting " + waitTime + " millis");
		Thread.sleep(waitTime);
		log.warn("leaving timeout node " + nodeId);
	}

}