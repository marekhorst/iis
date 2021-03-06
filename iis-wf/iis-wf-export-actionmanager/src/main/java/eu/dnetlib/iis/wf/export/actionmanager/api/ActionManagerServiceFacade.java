package eu.dnetlib.iis.wf.export.actionmanager.api;

import java.io.Closeable;
import java.util.Collection;

import eu.dnetlib.actionmanager.actions.AtomicAction;
import eu.dnetlib.actionmanager.rmi.ActionManagerException;

/**
 * Action manager service facade responsible for persisting actions. 
 * Each action conveys either entity or relation generated by IIS.
 * 
 * @author mhorst
 *
 */
public interface ActionManagerServiceFacade extends Closeable {

	
	/**
	 * Stores actions in underlying action manager persistence layer.
	 * 
	 * @param actions collection of actions conveying either entity or relation
	 */
	void storeActions(Collection<AtomicAction> actions) throws ActionManagerException;
	
}
