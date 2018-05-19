package com.git.svn.service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.tmatesoft.svn.core.SVNCommitInfo;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNStatus;
import org.tmatesoft.svn.core.wc.SVNStatusType;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import com.git.svn.bean.Project;
import com.git.svn.bean.SvnFile;
import com.git.svn.bean.User;
import com.git.svn.handlers.CommitEventHandler;

/**
 * 
 * @file	SvnProjectService.java
 * @project	SVNManager
 * @Description:   SVN提交、更新、删除、检出
 * 
 * @author 	lemon_mj
 * @date 	2017年12月18日下午4:49:48
 */
public class SvnProjectService {
	
	private static final Logger logger = Logger.getLogger(SvnProjectService.class);
	
	
	private static List<File> toCommitFileList = new ArrayList<File>();
	private static List<File> toAddFileList = new ArrayList<File>();
	private static List<File> toDelFileList = new ArrayList<File>();
	private static Map<String,Object> fileListMap = new HashMap<String,Object>();
	

	public void clear(){
		fileListMap.clear();
		toCommitFileList.clear();
		toAddFileList.clear();
		toDelFileList.clear();
	}	
	
	
	/**
	 * 
	 * @author 	lemon_mj
	 * @date 	2017年12月18日 下午4:50:32
	 * @description:	提交项目到SVN
	 *
	 * @param project
	 * @param user
	 * @param fileList
	 * @return
	 * 
	 */
	public Map<String,Object> commitProjectToSvn(Project project,User user,List<SvnFile> fileList) {
		clear();
        SVNClientManager clientManager = SVNUtil.authSvn(project.getSvnUrl(), user.getUserName(),user.getPassword());
        clientManager.getCommitClient().setEventHandler(new CommitEventHandler());
        File wc_project = new File(project.getWorkPath());
        Map<String,Object> fileListMap = checkVersiondDirectory(clientManager,wc_project);
        SVNCommitInfo svnCommitInfo = SVNUtil.commit(clientManager, wc_project, true, user.getMessage());
		fileListMap.put("newRevision", svnCommitInfo.getNewRevision());
		return fileListMap;
	}
	
	/**
	 * 
	 * @author 	lemon_mj
	 * @date 	2017年12月18日 下午4:50:55
	 * @description:	 提交删除文件到SVN
	 *
	 * @param project
	 * @param user
	 * @param fileList
	 * @return
	 * 
	 */
	public Map<String,Object> commitDelToSvn(Project project,User user,List<SvnFile> fileList){
		clear();
        SVNClientManager clientManager = SVNUtil.authSvn(project.getSvnUrl(), user.getUserName(),user.getPassword());
        clientManager.getCommitClient().setEventHandler(new CommitEventHandler());
		for(SvnFile file : fileList){
			File wc = new File(file.getDescFileName());
			if(wc.exists()) {
				SVNUtil.delEntry(clientManager, wc);
				logger.info("D 删除文件：" + wc.getPath());
				toDelFileList.add(wc);
			} else {
				logger.info("[" + wc.getPath() + "]文件不存在，不执行删除操作");
			}
		}
		File wc_project = new File(project.getWorkPath());
		fileListMap.put("toDelFileList", toDelFileList);
		SVNCommitInfo svnCommitInfo = SVNUtil.commit(clientManager, wc_project, true, user.getMessage());
		fileListMap.put("newRevision", svnCommitInfo.getNewRevision());
		return fileListMap;
	}
	
	/**
	 * 
	 * @author 	lemon_mj
	 * @date 	2017年12月18日 下午4:51:15
	 * @description:	递归检查不在版本控制的文件，并add到svn
	 *
	 * @param clientManager
	 * @param wc
	 * @return
	 *
	 */
	private Map<String,Object> checkVersiondDirectory(SVNClientManager clientManager,File wc){
		SVNStatus fileStatus = SVNUtil.showStatus(clientManager, wc, false);
		if(!SVNWCUtil.isVersionedDirectory(wc)){
			if(fileStatus == null || fileStatus.getContentsStatus() == SVNStatusType.STATUS_NONE){
				SVNUtil.addEntry(clientManager, wc);
				logger.info("A 增加文件：" + wc.getPath());
				toAddFileList.add(wc);
			} else if(fileStatus.getContentsStatus() == SVNStatusType.STATUS_MODIFIED){
				logger.info("M 修改文件：" + wc.getPath());
				toCommitFileList.add(wc);
			}
		}
		if(wc.isDirectory()){
			for(File sub:wc.listFiles()){
				if(sub.isDirectory() && sub.getName().equals(".svn")){
					continue;
				}
				checkVersiondDirectory(clientManager,sub);
			}
		}
		fileListMap.put("toCommitFileList", toCommitFileList);
		fileListMap.put("toAddFileList", toAddFileList);
		return fileListMap;
	}
	
	/**
	 * 
	 * @author 	lemon_mj
	 * @date 	2017年12月18日 下午4:51:33
	 * @description:	工程检出
	 *
	 * @param project
	 * @param user
	 * @return
	 * 
	 */
	public long checkWorkCopy(Project project,User user){
		String username = user.getUserName();
		String password = user.getPassword();
		SVNClientManager clientManager = SVNUtil.authSvn(project.getSvnUrl(), username, password);
		SVNURL repositoryURL = null;	
		try {
			repositoryURL = SVNURL.parseURIEncoded(project.getSvnUrl()).appendPath(project.getName(), false);
		} catch (SVNException e) {
			logger.error(e);
		}
		logger.info("开始检出工程" + project.getName() + "[" + repositoryURL + "]至路径[" + project.getWorkPath() + "]");
		File wc = new File(project.getWorkPath());
		long workingVersion = 0L;
		if(!SVNUtil.isWorkingCopy(wc)){
			if(!SVNUtil.isURLExist(repositoryURL,username,password)){
				workingVersion = SVNUtil.checkout(clientManager, repositoryURL, SVNRevision.HEAD, wc, SVNDepth.EMPTY);
			}else{
				workingVersion = SVNUtil.checkout(clientManager, repositoryURL, SVNRevision.HEAD, wc, SVNDepth.INFINITY);
			}
		}else{
			workingVersion = SVNUtil.update(clientManager, wc, SVNRevision.HEAD, SVNDepth.INFINITY);
		}
		return workingVersion;
	}

	
	/**
	 * 
	 * @author 	lemon_mj
	 * @date 	2017年12月18日 下午4:51:48
	 * @description:	更新文件
	 *
	 * @param project
	 * @param user
	 * @param svnFile
	 * @return
	 * 
	 */
	public String doUpdate(Project project,User user,SvnFile svnFile){
		String errMsg = null;
		try {
			SVNClientManager clientManager = SVNUtil.authSvn(project.getSvnUrl(), user.getUserName(), user.getPassword());
			SVNURL.parseURIEncoded(project.getSvnUrl());
			// 需要更新到本地的文件
			File updateFile = new File(svnFile.getSourceFileName());
			
			SVNStatus fileStatus = SVNUtil.showStatus(clientManager, updateFile, false);
			if(fileStatus == null || fileStatus.getContentsStatus() == SVNStatusType.STATUS_MISSING){
				errMsg = "[" + svnFile.getSourceFileName() + "]资源库"  + project.getDefine() + "不存在该版本号文件，不执行更新操作";
				logger.error(errMsg);
				return errMsg;
			}
			long versionNum = -1L;
			// 根据svnFile版本号更新  无版本号默认更新最新版本
			boolean isHead = svnFile.getFileVersion() == 0L;
			if(isHead)
				versionNum = SVNUtil.update(clientManager, updateFile, SVNRevision.HEAD, SVNDepth.INFINITY);
			else
				versionNum = SVNUtil.update(clientManager, updateFile, SVNRevision.create(svnFile.getFileVersion()), SVNDepth.INFINITY);
			logger.info("[" + svnFile.getSourceFileName() + "]更新后的版本：version = " + versionNum);
		} catch (Exception e) {
			errMsg = e instanceof NullPointerException ? e.toString() : e.getMessage();
			logger.error("更新文件[" + svnFile.getSourceFileName() + "]发生异常", e);
		}
		return errMsg;
	}
	

}
