package com.git.svn.handlers;

import org.apache.log4j.Logger;
import org.tmatesoft.svn.core.SVNCancelException;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.wc.ISVNEventHandler;
import org.tmatesoft.svn.core.wc.SVNEvent;
import org.tmatesoft.svn.core.wc.SVNEventAction;

public class UpdateEventHandler implements ISVNEventHandler {

	private static final Logger logger = Logger.getLogger(UpdateEventHandler.class);
	
	@Override
	public void checkCancelled() throws SVNCancelException {
		
	}

	@Override
	public void handleEvent(SVNEvent arg0, double arg1) throws SVNException {
		SVNEventAction action = arg0.getAction();
		SVNNodeKind nodeKind = arg0.getNodeKind();
		
		if(SVNNodeKind.DIR.equals(nodeKind))
			logger.debug(arg0.getFile().getName());
		else if (action == SVNEventAction.UPDATE_DELETE)
			logger.debug(arg0.getFile().getName() + "\t" + arg0.getFile().getName());
		else {
			if(action != SVNEventAction.UPDATE_ADD && action != SVNEventAction.UPDATE_UPDATE)
				return;
			logger.debug(arg0.getFile().getName() + "\t" + arg0.getFile().getName());
		}
		
	}

}
