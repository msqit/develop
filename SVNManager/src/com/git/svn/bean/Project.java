package com.git.svn.bean;

/**
 * 
 * @file	Project.java
 * @project	SVNManager
 * @Description:      SVN工作空间定义       
 * 
 * @author 	lemon_mj
 * @date 	2017年12月18日下午4:48:57
 */
public class Project {
	
	private String define;
	private String name;
	private String svnUrl;
	private String workPath;
	private String filePath;
	
	
	public String getDefine() {
		return define;
	}
	public void setDefine(String define) {
		this.define = define;
	}
	public String getFilePath() {
		return filePath + "/";
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSvnUrl() {
		return this.svnUrl + "/";
	}
	public void setSvnUrl(String svnUrl) {
		this.svnUrl = svnUrl;
	}
	public String getWorkPath() {
		return workPath;
	}
	public void setWorkPath(String workPath) {
		this.workPath = workPath;
	}
	
	
	@Override
	public String toString() {
		return "Project [name=" + this.name + ",svnUrl=" + this.svnUrl + ",define= " + this.define + 
				",workPath=" + this.workPath + ",filePath=" + this.filePath + "]";
	}
	
	
}
