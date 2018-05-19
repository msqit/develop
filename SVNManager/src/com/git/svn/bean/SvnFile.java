package com.git.svn.bean;

public class SvnFile {
	private String sourcePath;
	private String descPath;
	private String fileName;
	private String operateType;
	private long fileVersion;
	@Override
	public String toString() {
		return "SvnFile [sourcePath=" + this.sourcePath + ",descPath=" + this.descPath + ",fileName=" + this.fileName + 
				",fileVersion=" + this.fileVersion + ",operateType=" + this.operateType + "]";
	}
	public String getSourcePath() {
		return this.sourcePath + "/";
	}
	public void setSourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
	}
	public String getDescPath() {
		return this.descPath + "/";
	}
	public void setDescPath(String descPath) {
		this.descPath = descPath;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getOperateType() {
		return operateType;
	}
	public void setOperateType(String operateType) {
		this.operateType = operateType;
	}
	public long getFileVersion() {
		return fileVersion;
	}
	public void setFileVersion(long fileVersion) {
		this.fileVersion = fileVersion;
	}
	
	
	public String getSourceFileName(){
		return getSourcePath() + getFileName();
	}
	
	public String getDescFileName(){
		return getDescPath() + getFileName();
	}
	
}
