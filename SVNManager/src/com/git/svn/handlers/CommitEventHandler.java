package com.git.svn.handlers;

import org.tmatesoft.svn.core.SVNCancelException;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.wc.ISVNEventHandler;
import org.tmatesoft.svn.core.wc.SVNEvent;

public class CommitEventHandler implements ISVNEventHandler {

	@Override
	public void checkCancelled() throws SVNCancelException {
		
	}

	@Override
	public void handleEvent(SVNEvent arg0, double arg1) throws SVNException {
		
	}

}
