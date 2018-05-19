package com.git.svn.bean;

import java.util.List;

public class SVNDefine {

	private User user;
	
	private List<Project> projectList;
	
	private List<String> sourceList;
	
	private List<String> descList;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public List<Project> getProjectList() {
		return projectList;
	}

	public void setProjectList(List<Project> projectList) {
		this.projectList = projectList;
	}

	public List<String> getSourceList() {
		return sourceList;
	}

	public void setSourceList(List<String> sourceList) {
		this.sourceList = sourceList;
	}

	public List<String> getDescList() {
		return descList;
	}

	public void setDescList(List<String> descList) {
		this.descList = descList;
	}
}
