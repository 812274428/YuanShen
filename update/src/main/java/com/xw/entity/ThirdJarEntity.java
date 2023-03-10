package com.xw.entity;

import java.util.List;

public class ThirdJarEntity {

	private List<String> jarPath;
	
	private String recordName;
	
	private String refNo;

	public List<String> getJarPath() {
		return jarPath;
	}

	public void setJarPath(List<String> jarPath) {
		this.jarPath = jarPath;
	}

	public String getRecordName() {
		return recordName;
	}

	public void setRecordName(String recordName) {
		this.recordName = recordName;
	}

	public String getRefNo() {
		return refNo;
	}

	public void setRefNo(String refNo) {
		this.refNo = refNo;
	}

	public ThirdJarEntity(List<String> jarPath, String recordName, String refNo) {
		super();
		this.jarPath = jarPath;
		this.recordName = recordName;
		this.refNo = refNo;
	}

	@Override
	public String toString() {
		return "ThirdJarEntity [jarPath=" + jarPath + ", recordName=" + recordName + ", refNo=" + refNo + "]";
	}

}
