package com.git.svn.test;

import java.io.File;

import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
//import org.tmatesoft.svn.core.wc.SVNStatus;

import com.git.svn.service.SVNUtil;

public class DoUpdate {

	public static void main(String[] args) {
		String svnRoot = "https://WIN-IH8QISP1E68/svn/plfp/20171205/branches/dev/test";
		String username = "limengjun";
		String password = "limengjun";
		
		try {
			SVNURL.parseURIEncoded(svnRoot);
		} catch (SVNException e) {
		}
		
		File file = new File("D:\\svntest\\plfp_dev\\aa.txt");
		
		SVNClientManager clientManager = SVNUtil.authSvn(svnRoot, username, password);
		
		//SVNStatus fileStatus = SVNUtil.showStatus(clientManager, file, false);
		//System.out.println(fileStatus.getContentsStatus());
		
		long versionNum = -1L;
		versionNum = SVNUtil.update(clientManager,file, SVNRevision.create(67L),SVNDepth.INFINITY);
		System.out.println("工作副本更新后的版本：" + versionNum);
	}

}
