package eu.dnetlib.iis.common.spark.avro;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;

import eu.dnetlib.iis.common.IntegrationTest;
import eu.dnetlib.iis.common.avro.Country;
import eu.dnetlib.iis.common.utils.AvroTestUtils;
import pl.edu.icm.sparkutils.test.SparkJob;
import pl.edu.icm.sparkutils.test.SparkJobBuilder;
import pl.edu.icm.sparkutils.test.SparkJobExecutor;

/**
 * @author Łukasz Dumiszewski
 */
@Category(IntegrationTest.class)
public class AvroSaverTest {

    
    private static final Logger log = LoggerFactory.getLogger(AvroSaverTest.class);
    
    private SparkJobExecutor executor = new SparkJobExecutor();
    
    private static File workingDir;
    
    private static String outputDirPath;
    
    
    
    
    @Before
    public void before() {
        
        workingDir = Files.createTempDir();
        outputDirPath = workingDir + "/spark_sql_avro_cloner/output";
    }
    
    
    @After
    public void after() throws IOException {
        
        FileUtils.deleteDirectory(workingDir);
        
    }
    
    
    //------------------------ TESTS --------------------------
    
    @Test
    public void test() throws IOException {
        
        // given
        
        SparkJob sparkJob = SparkJobBuilder
                                           .create()
                                           
                                           .setAppName("Spark Avro Saver Test")
                                           
                                           .setMainClass(AvroSaverTest.class)
                                           .build();
        
        
        // execute
        
        executor.execute(sparkJob);
        
        
        
        // assert
        
        
        List<Country> countries = AvroTestUtils.readLocalAvroDataStore(outputDirPath);

        log.info(countries.toString());
        
        assertEquals(4, countries.size());
        
        assertEquals(1, countries.stream().filter(c->c.getIso().equals("PL")).count());
        
        
    }
  
}
