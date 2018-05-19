package com.git.svn.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.git.svn.bean.Project;
import com.git.svn.bean.SVNDefine;
import com.git.svn.bean.User;

/**
 * 
 * @file	ConfigManager.java
 * @project	SVNManager
 * @Description: 	加载读取配置文件            
 * 
 * @author 	lemon_mj
 * @date 	2017年12月18日下午4:53:49
 */
public class ConfigManager {
	
	private static final Logger logger = Logger.getLogger(ConfigManager.class);
	
	private static SVNDefine svnDefine;
	
	private static Properties config;
	
	public static void loadProperties(){
		try {
			config = new Properties();
			InputStream in = ConfigManager.class.getResourceAsStream("/config.properties");
			config.load(in);
			logger.info("Loading properties[config.properties]");
		} catch (IOException e) {
			logger.error(e);
		}
	}
	
	public static void init(){
		Properties prop = new Properties();
		try {
			InputStream in = ConfigManager.class.getResourceAsStream("/config.properties");
			prop.load(in);
			// svn定义
			svnDefine = new SVNDefine();
			// 操作用户
			User user = new User();
			user.setUserName(prop.getProperty("userName"));
			user.setPassword(prop.getProperty("password"));
			// svn定义列表
			List<Project> projectList = new ArrayList<Project>();
			// 提交源SVN
			List<String> sourceList = new ArrayList<String>();
			// 提交目标SVN
			List<String> descList = new ArrayList<String>();
			svnDefine.setUser(user);
			for(int i = 1; i < 20; i++){
				String source = prop.getProperty("SOURCE_SVN_" + i);
				if(source != null && !source.equals(""))
					sourceList.add(source);
				String desc = prop.getProperty("DESC_SVN_" + i);
				if(desc != null && !desc.equals(""))
					descList.add(desc);
				
				String define = prop.getProperty("SVN_" + i);
				if(define == null || define.equals(""))
					continue;
				Project project = new Project();
				project.setName(prop.getProperty(define + "_projectName"));
				project.setSvnUrl(prop.getProperty(define + "_projectSvnUrl"));
				project.setDefine(define);
				project.setWorkPath(prop.getProperty(define + "_projectWorkPath"));
				project.setFilePath(prop.getProperty(define + "_filePath"));
				projectList.add(project);
			}
			svnDefine.setProjectList(projectList);
			svnDefine.setSourceList(sourceList);
			svnDefine.setDescList(descList);
			in.close();
			logger.info("Loading properties[config.properties]");
		} catch (Exception e) {
			logger.error(e);
		}
	}
	
	
	public static String getStringProperty(String key){
		if(config == null){
			loadProperties();
		}
		return config.getProperty(key).trim();
	}
	
	public static int getIntProperty(String key){
		if(config == null){
			loadProperties();
		}
		return Integer.valueOf(config.getProperty(key).toString());
	}
	
	
	public static SVNDefine getSVNDefine(){
		if(svnDefine == null){
			init();
		}
		return svnDefine;
	}
}
