package com.xw.entity;

import java.sql.Date;

public class RecordEntity {
	
	private String recordName;
	
	private String version;
	
	private String refNo;
	
	private String packageName;
	
	private String fileName;
	
	private String status;
	
	private String type;
	
	private Date date;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getRecordName() {
		return recordName;
	}

	public void setRecordName(String recordName) {
		this.recordName = recordName;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getRefNo() {
		return refNo;
	}

	public void setRefNo(String refNo) {
		this.refNo = refNo;
	}

	

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public RecordEntity(String recordName, String version, String refNo, String packageName, String fileName,
			String status, String type) {
		super();
		this.recordName = recordName;
		this.version = version;
		this.refNo = refNo;
		this.packageName = packageName;
		this.fileName = fileName;
		this.status = status;
		this.type = type;
	}

	@Override
	public String toString() {
		return "RecordEntity [recordName=" + recordName + ", version=" + version + ", refNo=" + refNo + ", packageName="
				+ packageName + ", fileName=" + fileName + ", status=" + status + ", type=" + type + ", date=" + date
				+ "]";
	}

}
