package eu.dnetlib.iis.wf.citationmatching;

import java.io.Serializable;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import eu.dnetlib.iis.citationmatching.schemas.DocumentMetadata;
import eu.dnetlib.iis.wf.citationmatching.converter.entity_id.DocEntityId;
import pl.edu.icm.coansys.citations.InputDocumentReader;
import pl.edu.icm.sparkutils.avro.SparkAvroLoader;
import scala.Tuple2;

/**
 * Reader of input documents rdd
 * 
 * @author madryk
 */
public class DocumentMetadataInputReader implements InputDocumentReader<String, DocumentMetadata>, Serializable {

    private static final long serialVersionUID = 1L;


    private final SparkAvroLoader avroLoader = new SparkAvroLoader();

    //------------------------ LOGIC --------------------------

    /**
     * Reads input documents rdd from avro {@link DocumentMetadata} datastore.
     * Keys of returned rdd will contain document id with added {@literal doc_} prefix.
     * Values of returned rdd will contain document in form of {@link DocumentMetadata} object.
     */
    @Override
    public JavaPairRDD<String, DocumentMetadata> readDocuments(JavaSparkContext sparkContext, String inputDocumentsPath) {

        JavaRDD<DocumentMetadata> documents = avroLoader.loadJavaRDD(sparkContext, inputDocumentsPath, DocumentMetadata.class);

        return documents.mapToPair(doc -> new Tuple2<>(buildDocumentId(doc), doc));
    }


    //------------------------ PRIVATE --------------------------

    private String buildDocumentId(DocumentMetadata documentMetadata) {
        return new DocEntityId(documentMetadata.getId().toString()).toString();
    }

}
