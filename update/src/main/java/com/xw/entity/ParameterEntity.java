package com.xw.entity;

import java.util.List;

public class ParameterEntity {
	
	private String packageName;
	
	private String jarName;
	
	private String version;
	
	private List<String> updateFiles;
	
	private List<String> deleteFiles;

	public List<String> getUpdateFiles() {
		return updateFiles;
	}

	public void setUpdateFiles(List<String> updateFiles) {
		this.updateFiles = updateFiles;
	}

	public List<String> getDeleteFiles() {
		return deleteFiles;
	}

	public void setDeleteFiles(List<String> deleteFiles) {
		this.deleteFiles = deleteFiles;
	}

	public String getJarName() {
		return jarName;
	}

	public void setJarName(String jarName) {
		this.jarName = jarName;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public ParameterEntity(String packageName, String jarName, String version, List<String> updateFiles,
			List<String> deleteFiles) {
		super();
		this.packageName = packageName;
		this.jarName = jarName;
		this.version = version;
		this.updateFiles = updateFiles;
		this.deleteFiles = deleteFiles;
	}

	@Override
	public String toString() {
		return "ParameterEntity [packageName=" + packageName + ", jarName=" + jarName + ", version="
				+ version + ", updateFiles=" + updateFiles + ", deleteFiles=" + deleteFiles + "]";
	}

}
