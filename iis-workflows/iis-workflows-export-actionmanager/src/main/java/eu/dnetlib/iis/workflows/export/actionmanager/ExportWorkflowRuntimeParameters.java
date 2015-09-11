package eu.dnetlib.iis.workflows.export.actionmanager;

public abstract class ExportWorkflowRuntimeParameters {

	private ExportWorkflowRuntimeParameters() {}
	
	public static final String EXPORT_TRUST_LEVEL = "export.trust.level";
	public static final String EXPORT_TRUST_LEVEL_THRESHOLD = "export.trust.level.threshold";
	
	public static final String EXPORT_ACTION_SETID = "export.action.setid";
	public static final String EXPORT_ENTITY_ACTION_SETID = "export.entity.action.setid";
	
	public static final char EXPORT_ALGORITHM_PROPERTY_SEPARATOR = '.';	
	
	public static final String EXPORT_ACTION_BUILDER_FACTORY_CLASSNAME = "export.action.builder.factory.classname";
	public static final String EXPORT_ACTION_HBASE_TABLE_NAME = "export.action.hbase.table.name";
	public static final String EXPORT_ACTION_HBASE_TABLE_INITIALIZE = "export.action.hbase.table.initialize";
	public static final String EXPORT_ACTION_HBASE_REMOTE_ZOOKEEPER_QUORUM = "export.action.hbase.remote.zookeeper.quorum";
	public static final String EXPORT_ACTION_HBASE_REMOTE_ZOOKEEPER_CLIENTPORT = "export.action.hbase.remote.zookeeper.clientport";
	
	public static final String EXPORT_DOCUMENTSSIMILARITY_THRESHOLD = "export.documentssimilarity.threshold";
	
	public static final String IMPORT_DOCUMENT_MDSTORE_SERVICE_LOCATION = "import.document.mdstore.service.location";
	public static final String IMPORT_DOCUMENT_MDSTORE_ID = "import.document.mdstore.id";
	public static final String IMPORT_DATACITE_MDSTORE_SERVICE_LOCATION = "import.datacite.mdstore.service.location";
}