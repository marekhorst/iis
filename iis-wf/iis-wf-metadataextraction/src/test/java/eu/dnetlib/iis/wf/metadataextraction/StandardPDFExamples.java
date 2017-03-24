package eu.dnetlib.iis.wf.metadataextraction;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author Dominika Tkaczyk
 */
public class StandardPDFExamples {
    
    
 // ------------------------- CONSTRUCTORS --------------------------------
 
    
    private StandardPDFExamples() {}
    
 // ------------------------- LOGIC ---------------------------------------
    
    
    public static List<InputStream> getFilesFromResources(String resPaths) {
        String[] paths = StringUtils.split(resPaths, ',');
        List<InputStream> streams = new ArrayList<InputStream>(paths.length);
        for(String path : paths) {
            streams.add(StandardPDFExamples.class.getResourceAsStream(path));
        }
        
        return streams;
    }
}
