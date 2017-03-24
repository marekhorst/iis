package eu.dnetlib.iis.wf.referenceextraction.dataset;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;

import eu.dnetlib.iis.common.java.PortBindings;
import eu.dnetlib.iis.common.java.Process;
import eu.dnetlib.iis.common.java.io.DataStore;
import eu.dnetlib.iis.common.java.io.FileSystemPath;
import eu.dnetlib.iis.common.java.io.JsonStreamWriter;
import eu.dnetlib.iis.common.java.porttype.AnyPortType;
import eu.dnetlib.iis.common.java.porttype.AvroPortType;
import eu.dnetlib.iis.common.java.porttype.PortType;
import eu.dnetlib.iis.importer.schemas.DataSetReference;

/**
 *
 * @author Dominika Tkaczyk
 */
public class DatasetDBBuilder implements Process {
    private final static String datasetPort = "dataset";
	private final static String datasetDBPort = "dataset_db";
	private final static String scriptLocationParam = "scriptLocation";
	
	
	@Override
	public Map<String, PortType> getInputPorts() {
		return createInputPorts();
	}
	
	@Override
	public Map<String, PortType> getOutputPorts() {
		return createOutputPorts();
	}

	private static Map<String, PortType> createInputPorts(){
		Map<String, PortType> inputPorts = new HashMap<String, PortType>();
		inputPorts.put(datasetPort, new AvroPortType(DataSetReference.SCHEMA$));
		return inputPorts;
	}

	private static Map<String, PortType> createOutputPorts(){
		Map<String, PortType> outputPorts = new HashMap<String, PortType>();
		outputPorts.put(datasetDBPort, new AnyPortType());
		return outputPorts;	
	}
	
	@Override
	public void run(PortBindings portBindings, Configuration conf,
			Map<String, String> parameters) throws IOException, InterruptedException {

	    String scriptLocation = parameters.get(scriptLocationParam);
		if (StringUtils.isBlank(scriptLocation)) {
		    throw new RuntimeException("sql script location not provided, '" + scriptLocationParam + "' parameter is missing!");
		}
		
		String targetDbLocation = System.getProperty("java.io.tmpdir") + 
				File.separatorChar + "datasets.db";
		File targetDbFile = new File(targetDbLocation);
		targetDbFile.setWritable(true);
		
        java.lang.Process process = Runtime.getRuntime().exec(
                "python scripts/madis/mexec.py -w " + targetDbLocation + " -f "+ scriptLocation);
        BufferedOutputStream stdin = new BufferedOutputStream(process.getOutputStream());
        InputStream errorStream = process.getErrorStream();
    
        FileSystem fs = FileSystem.get(conf);
        Iterator<DataSetReference> datasets = DataStore.getReader(new FileSystemPath(fs, portBindings.getInput().get(datasetPort)));

        JsonStreamWriter<DataSetReference> writer = 
                new JsonStreamWriter<DataSetReference>(DataSetReference.SCHEMA$, stdin);
        try {
        	
    		while (datasets.hasNext()) {
                writer.write(datasets.next());
            }
        	
       		writer.close();	

        	process.waitFor();
        } catch (Exception e) {
//        	providing error details from Madis error stream
        	BufferedReader stderr = new BufferedReader(new InputStreamReader(errorStream));
            StringBuilder errorBuilder = new StringBuilder();
            String line;
            while ((line = stderr.readLine()) != null) {
                errorBuilder.append(line);
            }
            stderr.close();
            throw new IOException("got error while writing to Madis stream: " + 
            		errorBuilder.toString(), e);
        }
        
        if (process.exitValue() != 0) {
        	BufferedReader stderr = new BufferedReader(new InputStreamReader(errorStream));
            StringBuilder errorBuilder = new StringBuilder();
            String line;
            while ((line = stderr.readLine()) != null) {
                errorBuilder.append(line);
            }
            stderr.close();
            throw new RuntimeException("MadIS execution failed with error: " + errorBuilder.toString());
        }
        
        InputStream inStream = null;
        OutputStream outStream = null;
        try {
            inStream = new FileInputStream(targetDbFile);
            outStream = fs.create(new FileSystemPath(fs, portBindings.getOutput().get(datasetDBPort)).getPath());
            IOUtils.copy(inStream, outStream);  
        } finally {
            if (inStream != null) {
                inStream.close();
            }
            if (outStream != null) {
                outStream.close();
            }
        }
	}
}
