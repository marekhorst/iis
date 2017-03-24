package eu.dnetlib.iis.wf.importer.infospace;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.SortedMap;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import eu.dnetlib.data.proto.OafProtos.Oaf;

/**
 * {@link Oaf} body along with list of ordered updates which can be applied over this body.
 * 
 * @author mhorst
 *
 */
public class OafBodyWithOrderedUpdates {

    private static final String BODY_QUALIFIER_NAME = "body";
    
    private String body;

    private final Collection<String> orderedUpdates;

    // ------------------------ GETTERS --------------------------
    
    /**
     * Returns body part.
     */
    public String getBody() {
        return body;
    }
    
    /**
     * Returns all update parts ordered by qualifier.
     */
    public Collection<String> getOrderedUpdates() {
        return orderedUpdates;
    }
    
    // ------------------------ CONSTRUCTORS --------------------------
    
    /**
     * @param infoSpaceRecords list of {@link QualifiedOafJsonRecord} entities 
     */
    public OafBodyWithOrderedUpdates(List<QualifiedOafJsonRecord> infoSpaceRecords) {
        Preconditions.checkNotNull(infoSpaceRecords);
        // to be instantiated later because in most cases updates are not available
        SortedMap<String, String> updatesOrderedByQualifier = null;
        for (QualifiedOafJsonRecord record : infoSpaceRecords) {
            if (BODY_QUALIFIER_NAME.equals(record.getQualifier())) {
                this.body = record.getOafJson();
            } else {
                if (updatesOrderedByQualifier==null) {
                    updatesOrderedByQualifier = Maps.newTreeMap();
                }
                updatesOrderedByQualifier.put(record.getQualifier(), record.getOafJson());
            }
        }
        this.orderedUpdates = updatesOrderedByQualifier!=null?updatesOrderedByQualifier.values():Collections.emptyList();
    }
}
