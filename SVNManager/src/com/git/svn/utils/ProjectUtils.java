package com.git.svn.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.git.svn.bean.Project;
import com.git.svn.bean.SvnFile;
import com.git.svn.bean.User;
import com.git.svn.service.SvnProjectService;

/**
 * 
 * @file	ProjectUtils.java
 * @project	SVNManager
 * @Description:             
 * 
 * @author 	limengjun 
 * @date 	2017年12月18日下午4:36:38
 */
public class ProjectUtils {

	private static final Logger logger = Logger.getLogger(ProjectUtils.class);
	
	
	public static final int FILE_PARSE_SUCCESS = 0;
	public static final int FILE_PARSE_ERROR = 1;
	public static final int FILE_PARSE_EXCEPTION = -1;
	
	/**
	 * 
	 * @author 	limengjun
	 * @date 	2017年12月18日 下午4:39:10
	 * @description:	从源svnproject提交到目标svnproject
	 *
	 * @param source
	 * @param desc
	 * @param user
	 * @param filePath
	 * @param svnProjectService
	 * @param submitType
	 * @return
	 * 
	 */
	public static Map<String,Object> makeAll(Project source,Project desc,User user,String filePath,
			SvnProjectService svnProjectService,int submitType){
		Map<String,Object> retMap = new HashMap<String,Object>();
		// 读取提交集成单
		List<SvnFile> fileList = null;
		try {
			fileList = FileUtils.parseTextFile(filePath, "UTF-8");
		} catch (Exception e) {
			logger.error(e);
			retMap.put("resultCode", FILE_PARSE_EXCEPTION);
			String errmsg = "读取集成单文件[" + filePath + "]出现异常 ：";
			errmsg += e instanceof NullPointerException ? e.toString() : e.getMessage();
			retMap.put("errmsg", errmsg);
			return retMap;
		}
		// 检出目标svnproject
		svnProjectService.checkWorkCopy(desc,user);
			
		if(fileList == null || fileList.size() == 0){
			retMap.put("resultCode", FILE_PARSE_ERROR);
			retMap.put("errmsg", "集成单文件[" + filePath + "]为空");
			return retMap;
		}
		// 失败文件清单
		StringBuffer errFile = new StringBuffer();
		// 提交清单
		List<SvnFile> subFileList = new ArrayList<SvnFile>();
		// 删除清单
		List<SvnFile> delFileList = new ArrayList<SvnFile>();
		
		for(SvnFile svnFile : fileList){
			svnFile.setSourcePath(source.getWorkPath());
			svnFile.setDescPath(desc.getWorkPath());
			// 源svnproject更新文件
			String updateErr = svnProjectService.doUpdate(source, user, svnFile);
			if(updateErr != null){
				errFile.append(svnFile.getFileName() + " " + svnFile.getFileVersion());
				errFile.append("\r\n");
				continue;
			}
			if(submitType == 0){		// 提交操作
				// 更新的文件从源project复制到目标project
				String copyErr = FileUtils.copyFile(svnFile.getSourceFileName(), svnFile.getDescFileName());
				if(copyErr != null){
					errFile.append(svnFile.getFileName() + " " + svnFile.getFileVersion());
					errFile.append("\r\n");
				} else 
					subFileList.add(svnFile);
			} else {					// 删除提交
				delFileList.add(svnFile);
			}
		}
		try {
			Map<String,Object> fileListMap = new HashMap<String,Object>();
			StringBuffer sucFile = new StringBuffer();
			// 提交
			if(submitType == 0){	
				if(subFileList.size() > 0){
					fileListMap = svnProjectService.commitProjectToSvn(desc, user, subFileList);
					for(SvnFile file : subFileList){
						sucFile.append(file.getFileName() + " " + file.getFileVersion());
						sucFile.append("\r\n");
					}
				}
			// 删除
			} else {
				if(delFileList.size() > 0){
					fileListMap = svnProjectService.commitDelToSvn(desc, user, delFileList);
					for(SvnFile file : delFileList){
						sucFile.append(file.getFileName() + " " + file.getFileVersion());
						sucFile.append("\r\n");
					}
				}
			}
			if(sucFile.length() > 0)
				FileUtils.createFile(filePath + ".suc", sucFile.toString());
			if(errFile.length() > 0)
				FileUtils.createFile(filePath + ".err", errFile.toString());
			printLog(fileListMap);			
		} catch (Exception e) {
			logger.error(e);
			errFile.delete(0, errFile.length());
			for(SvnFile svnFile : fileList){
				errFile.append(svnFile.getFileName() + " " + svnFile.getFileVersion());
				errFile.append("\r\n");
			}
			FileUtils.createFile(filePath + ".err", errFile.toString());
			retMap.put("resultCode", FILE_PARSE_EXCEPTION);
			retMap.put("errmsg", e.getMessage());
			return retMap;
		}
		retMap.put("resultCode", FILE_PARSE_SUCCESS);
		return retMap;
	}
	
	/**
	 * 
	 * @author 	limengjun
	 * @date 	2017年12月18日 下午4:39:57
	 * @description:	提交、新增、删除记录日志打印
	 *
	 * @param fileListMap
	 * 
	 */
	@SuppressWarnings({ "unchecked"})
	private static void printLog(Map<String,Object> fileListMap){
		long newRevision = fileListMap.get("newRevision") == null ? 0L : Long.valueOf(fileListMap.get("newRevision").toString());
		
		if(fileListMap.get("toCommitFileList") != null){
			List<File> list = (List<File>)fileListMap.get("toCommitFileList");
			for(File file : list){
				logger.debug("commit\t" + file.getPath() + " " + newRevision);
			}
		}
		
		if(fileListMap.get("toAddFileList") != null){
			List<File> list = (List<File>)fileListMap.get("toAddFileList");
			for(File file : list){
				logger.debug("add\t" + file.getPath() + " " + newRevision);
			}
		}
		
		if(fileListMap.get("toDelFileList") != null){
			List<File> list = (List<File>)fileListMap.get("toDelFileList");
			for(File file : list){
				logger.debug("delete\t" + file.getPath() + " " + newRevision);
			}
		}
	}
}
