package eu.dnetlib.iis.wf.metadataextraction;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.avro.file.DataFileWriter;
import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;

import eu.dnetlib.iis.common.java.PortBindings;
import eu.dnetlib.iis.common.java.Process;
import eu.dnetlib.iis.common.java.io.DataStore;
import eu.dnetlib.iis.common.java.io.FileSystemPath;
import eu.dnetlib.iis.common.java.porttype.AvroPortType;
import eu.dnetlib.iis.common.java.porttype.PortType;
import eu.dnetlib.iis.importer.schemas.DocumentContent;

/**
 * Generates a directory with a few example avro files containing PDFs.
 * 
 * @author Dominika Tkaczyk
 *
 */
public class ExamplePdfBasedDocumentContentGenerator implements Process {
	
	private static final String PORT_OUT_DOC_CONTENT = "doc_content";
	private static final String PARAM_PDF_SOURCE_DIR = "pdfs_resource_dir";
	
	private final Map<String, PortType> outputPorts = new HashMap<String, PortType>(); 
	
	
	// ------------------------- CONSTRUCTORS --------------------------------
	
	public ExamplePdfBasedDocumentContentGenerator() {
		outputPorts.put(PORT_OUT_DOC_CONTENT, new AvroPortType(DocumentContent.SCHEMA$));
	}

	// ------------------------- LOGIC ---------------------------------------
	
	@Override
	public Map<String, PortType> getInputPorts() {
		return Collections.emptyMap();
	}

	@Override
	public Map<String, PortType> getOutputPorts() {
		return outputPorts;
	}

    @Override
    public void run(PortBindings portBindings, Configuration conf,
            Map<String, String> parameters) throws Exception {
        FileSystem fs = FileSystem.get(conf);
        FileSystemPath fsPath = new FileSystemPath(fs, portBindings.getOutput().get(PORT_OUT_DOC_CONTENT));

        
        int id = 0;
        try (DataFileWriter<DocumentContent> writer = DataStore.create(fsPath, DocumentContent.SCHEMA$)) {
            
            for (InputStream is : StandardPDFExamples.getFilesFromResources(parameters.get(PARAM_PDF_SOURCE_DIR))) {
                DocumentContent.Builder docContentBuilder = DocumentContent.newBuilder();
                docContentBuilder.setId("id" + (id++));
                try {
                    docContentBuilder.setPdf(ByteBuffer.wrap(IOUtils.toByteArray(is)));
                } finally {
                    is.close();
                }
                writer.append(docContentBuilder.build());
            }

        }

	}

}
