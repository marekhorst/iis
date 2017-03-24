package eu.dnetlib.iis.wf.affmatching;

import static eu.dnetlib.iis.common.utils.AvroTestUtils.readLocalAvroDataStore;
import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import eu.dnetlib.iis.importer.schemas.Organization;
import eu.dnetlib.iis.metadataextraction.schemas.Affiliation;
import eu.dnetlib.iis.metadataextraction.schemas.ExtractedDocumentMetadata;
import eu.dnetlib.iis.wf.affmatching.model.SimpleAffMatchResult;

/**
 * A helper that prints results of affiliation matching (actual matched affiliations in relation to
 * the expected ones). Used in classes that test affiliation matching quality. 
 * 
 *  
 * @author Łukasz Dumiszewski
*/

public class AffMatchingResultPrinter {
    
    private static final Comparator<SimpleAffMatchResult> RESULT_COMPARATOR = Comparator
            .comparing(SimpleAffMatchResult::getDocumentId)
            .thenComparingInt(SimpleAffMatchResult::getAffiliationPosition);
    
    
    //------------------------ CONSTRUCTORS -------------------
    
    private AffMatchingResultPrinter() {}
    
    //------------------------ LOGIC --------------------------
    
    /**
     * Prints affiliations that have been matched incorrectly 
     */
    public static void printFalsePositives(String inputAffDirPath, String inputOrgDirPath, List<SimpleAffMatchResult> expectedMatches, List<SimpleAffMatchResult> actualMatches) throws IOException {
        
        List<ExtractedDocumentMetadata> docsAffiliations = readLocalAvroDataStore(inputAffDirPath);
        
        List<Organization> organizations = readLocalAvroDataStore(inputOrgDirPath);
        
        
        List<SimpleAffMatchResult> falsePositives = actualMatches.stream()
                .filter(x -> !expectedMatches.contains(x))
                .sorted(RESULT_COMPARATOR)
                .collect(toList());
        
        System.out.println("\n\t-------------------- false positives ---------------------");
        
        for (SimpleAffMatchResult falsePositive : falsePositives) {
            
            String documentId = falsePositive.getDocumentId();
            int affiliationPosition = falsePositive.getAffiliationPosition();
            
            Affiliation affiliation = fetchAffiliation(docsAffiliations, documentId, affiliationPosition);
            
            List<String> expectedOrgIds = fetchMatchedOrganizationIds(expectedMatches, documentId, affiliationPosition);
            List<Organization> expectedOrgs = expectedOrgIds.stream().map(x -> fetchOrganization(organizations, x)).collect(toList());
            
            Organization actualOrg = fetchOrganization(organizations, falsePositive.getOrganizationId());
            
            System.out.println("Document id:     " + documentId + " \tPosition: " + affiliationPosition);
            System.out.println("Affiliation:     " + affiliation);
            System.out.println("Was matched to:  " + actualOrg);
            
            
            if (expectedOrgs.isEmpty()) {
                System.out.println("Should match to: null");
            }
            for (int i=0; i<expectedOrgs.size(); ++i) {
                
                SimpleAffMatchResult expectedMatch = new SimpleAffMatchResult(documentId, affiliationPosition, expectedOrgs.get(i).getId().toString());
                boolean alreadyMatched = actualMatches.contains(expectedMatch);
                
                
                String shouldMatchPrefix = (i == 0) ? "Should match to: " : "and:             ";
                String alreadyMatchedString = alreadyMatched ? "(already matched) " : "";
                
                System.out.println(shouldMatchPrefix + alreadyMatchedString + expectedOrgs.get(i));
                
            }
            System.out.println();
        
        }
        
    }
    
    /**
     * Prints affiliations that have NOT been matched
     */
    public static void printNotMatched(String inputAffDirPath, String inputOrgDirPath, List<SimpleAffMatchResult> expectedMatches, List<SimpleAffMatchResult> actualMatches) throws IOException {
        
        List<ExtractedDocumentMetadata> docsAffiliations = readLocalAvroDataStore(inputAffDirPath);
        
        List<Organization> organizations = readLocalAvroDataStore(inputOrgDirPath);
        
        
        List<SimpleAffMatchResult> notMatched = expectedMatches.stream()
                .filter(x -> !actualMatches.contains(x))
                .sorted(RESULT_COMPARATOR)
                .collect(toList());
        
        
        System.out.println("\n\t--------------------- not matched --------------------");
        
        for (SimpleAffMatchResult match : notMatched) {
            
            Affiliation affiliation = fetchAffiliation(docsAffiliations, match.getDocumentId(), match.getAffiliationPosition());
            
            Organization expectedOrg = fetchOrganization(organizations, match.getOrganizationId());
            
            
            System.out.println("Document id:     " + match.getDocumentId() + " \tPosition: " + match.getAffiliationPosition());
            System.out.println("Affiliation:     " + affiliation);
            System.out.println("Should match to: " + expectedOrg);
            System.out.println();
        }
        
    }
    
    
    
    //------------------------ PRIVATE --------------------------
    
    private static List<String> fetchMatchedOrganizationIds(List<SimpleAffMatchResult> matches, String documentId, int pos) {
        return matches.stream()
                .filter(match -> StringUtils.equals(match.getDocumentId(), documentId) && match.getAffiliationPosition() == pos)
                .map(match -> match.getOrganizationId())
                .collect(toList());
    }
    
    
    private static Affiliation fetchAffiliation(List<ExtractedDocumentMetadata> docsWithAffs, String documentId, int affPosition) {
        ExtractedDocumentMetadata doc = docsWithAffs.stream().filter(x -> StringUtils.equals(x.getId(), documentId)).findFirst().get();
        return doc.getAffiliations().get(affPosition - 1);
        
    }
    
    
    private static Organization fetchOrganization(List<Organization> organizations, String organizationId) {
        return organizations.stream().filter(x -> StringUtils.equals(x.getId().toString(), organizationId)).findFirst().get();
        
    }

}
