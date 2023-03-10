package com.xw.entity;

import java.util.List;

public class ResultListEntity {
	
	private List<String> jarList;
	
	private List<String> packageList;
	
	private String recordName;

	public ResultListEntity(List<String> jarList, List<String> packageList, String recordName) {
		super();
		this.jarList = jarList;
		this.packageList = packageList;
		this.recordName = recordName;
	}

	public List<String> getJarList() {
		return jarList;
	}

	public void setJarList(List<String> jarList) {
		this.jarList = jarList;
	}

	public List<String> getPackageList() {
		return packageList;
	}

	public void setPackageName(List<String> packageList) {
		this.packageList = packageList;
	}

	public String getRecordName() {
		return recordName;
	}

	public void setRecordName(String recordName) {
		this.recordName = recordName;
	}

	@Override
	public String toString() {
		return "ResultListEntity [jarList=" + jarList + ", packageList=" + packageList + ", recordName=" + recordName
				+ "]";
	}
}
