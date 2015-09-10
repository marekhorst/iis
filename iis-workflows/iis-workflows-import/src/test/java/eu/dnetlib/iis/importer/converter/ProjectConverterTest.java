package eu.dnetlib.iis.importer.converter;

import static org.junit.Assert.*;

import java.io.StringWriter;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

/**
 * {@link ProjectConverter} test class.
 * @author mhorst
 *
 */
public class ProjectConverterTest {

	@Test
	public void testFundingClassExtraction() throws Exception {
		StringWriter strWriter = new StringWriter();
		IOUtils.copy(ProjectConverter.class.getResourceAsStream(
				"/eu/dnetlib/iis/importer/converter/fundingclass_example.json"), 
				strWriter, "utf8");
		List<String> fundingTreeList = Collections.singletonList(strWriter.toString());
		String fundingClass = ProjectConverter.extractFundingClass(fundingTreeList);
		assertNotNull(fundingClass);
		assertEquals("WT::WT", fundingClass);
		
	}
}
