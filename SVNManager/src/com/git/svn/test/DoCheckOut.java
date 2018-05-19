package com.git.svn.test;

import java.io.File;

import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;

import com.git.svn.service.SVNUtil;

public class DoCheckOut {

	public static void main(String[] args) {
		String svnRoot = "https://WIN-IH8QISP1E68/svn/plfp/20171205/branches/dev/test";
		String username = "limengjun";
		String password = "limengjun";
		
		SVNURL url = null;
		try {
			url = SVNURL.parseURIEncoded(svnRoot);
		} catch (SVNException e) {
		}
		
		SVNClientManager clientManager = SVNUtil.authSvn(svnRoot, username, password);
		long versionNum = SVNUtil.checkout(clientManager, url, SVNRevision.HEAD, new File("D:\\svn\\"), SVNDepth.INFINITY) ;
		System.out.println("更新后版本 " + versionNum);
	}

}
