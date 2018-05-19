package com.git.svn.test;

import java.io.File;

import org.tmatesoft.svn.core.SVNCommitInfo;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNStatus;

import com.git.svn.service.SVNUtil;

public class DoComit {

	public static void main(String[] args) {
		String svnRoot = "https://WIN-IH8QISP1E68/svn/plfp/20171205/branches/dev/test";
		String username = "limengjun";
		String password = "limengjun";
		String message = "ÐÞ¸´bug";
	
		
		File file = new File("D:\\svntest\\plfp_dev\\aa.txt");
		
		SVNClientManager clientManager = SVNUtil.authSvn(svnRoot, username, password);
		SVNStatus status = SVNUtil.showStatus(clientManager, file, true);
		System.out.println(status.getContentsStatus());
		/*if(status == null || status.getContentsStatus() == SVNStatusType.STATUS_UNVERSIONED){
			SVNUtil.addEntry(clientManager, file);
			SVNUtil.commit(clientManager, file, true, message);
			System.out.println("add commit");
		} else {
			SVNUtil.commit(clientManager, file, true, message);
			System.out.println("commit");
		}*/
		SVNUtil.delEntry(clientManager, file);
		//file.delete();
		SVNCommitInfo info = SVNUtil.commit(clientManager, file, false, message);
		System.out.println(info.getNewRevision());
	}

}
