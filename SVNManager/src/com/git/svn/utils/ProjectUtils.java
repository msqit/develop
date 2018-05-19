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
 * @date 	2017��12��18������4:36:38
 */
public class ProjectUtils {

	private static final Logger logger = Logger.getLogger(ProjectUtils.class);
	
	
	public static final int FILE_PARSE_SUCCESS = 0;
	public static final int FILE_PARSE_ERROR = 1;
	public static final int FILE_PARSE_EXCEPTION = -1;
	
	/**
	 * 
	 * @author 	limengjun
	 * @date 	2017��12��18�� ����4:39:10
	 * @description:	��Դsvnproject�ύ��Ŀ��svnproject
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
		// ��ȡ�ύ���ɵ�
		List<SvnFile> fileList = null;
		try {
			fileList = FileUtils.parseTextFile(filePath, "UTF-8");
		} catch (Exception e) {
			logger.error(e);
			retMap.put("resultCode", FILE_PARSE_EXCEPTION);
			String errmsg = "��ȡ���ɵ��ļ�[" + filePath + "]�����쳣 ��";
			errmsg += e instanceof NullPointerException ? e.toString() : e.getMessage();
			retMap.put("errmsg", errmsg);
			return retMap;
		}
		// ���Ŀ��svnproject
		svnProjectService.checkWorkCopy(desc,user);
			
		if(fileList == null || fileList.size() == 0){
			retMap.put("resultCode", FILE_PARSE_ERROR);
			retMap.put("errmsg", "���ɵ��ļ�[" + filePath + "]Ϊ��");
			return retMap;
		}
		// ʧ���ļ��嵥
		StringBuffer errFile = new StringBuffer();
		// �ύ�嵥
		List<SvnFile> subFileList = new ArrayList<SvnFile>();
		// ɾ���嵥
		List<SvnFile> delFileList = new ArrayList<SvnFile>();
		
		for(SvnFile svnFile : fileList){
			svnFile.setSourcePath(source.getWorkPath());
			svnFile.setDescPath(desc.getWorkPath());
			// Դsvnproject�����ļ�
			String updateErr = svnProjectService.doUpdate(source, user, svnFile);
			if(updateErr != null){
				errFile.append(svnFile.getFileName() + " " + svnFile.getFileVersion());
				errFile.append("\r\n");
				continue;
			}
			if(submitType == 0){		// �ύ����
				// ���µ��ļ���Դproject���Ƶ�Ŀ��project
				String copyErr = FileUtils.copyFile(svnFile.getSourceFileName(), svnFile.getDescFileName());
				if(copyErr != null){
					errFile.append(svnFile.getFileName() + " " + svnFile.getFileVersion());
					errFile.append("\r\n");
				} else 
					subFileList.add(svnFile);
			} else {					// ɾ���ύ
				delFileList.add(svnFile);
			}
		}
		try {
			Map<String,Object> fileListMap = new HashMap<String,Object>();
			StringBuffer sucFile = new StringBuffer();
			// �ύ
			if(submitType == 0){	
				if(subFileList.size() > 0){
					fileListMap = svnProjectService.commitProjectToSvn(desc, user, subFileList);
					for(SvnFile file : subFileList){
						sucFile.append(file.getFileName() + " " + file.getFileVersion());
						sucFile.append("\r\n");
					}
				}
			// ɾ��
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
	 * @date 	2017��12��18�� ����4:39:57
	 * @description:	�ύ��������ɾ����¼��־��ӡ
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
