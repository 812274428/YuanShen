package com.xw.entity;

public class ResultEntity {

	private ParameterEntity parameterEntity;
	
	private String recordName;
	
	private String refNo;

	public ResultEntity(ParameterEntity parameterEntity, String recordName, String refNo) {
		super();
		this.parameterEntity = parameterEntity;
		this.recordName = recordName;
		this.refNo = refNo;
	}

	public ParameterEntity getParameterEntity() {
		return parameterEntity;
	}

	public void setParameterEntity(ParameterEntity parameterEntity) {
		this.parameterEntity = parameterEntity;
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

	@Override
	public String toString() {
		return "ResultEntity [parameterEntity=" + parameterEntity + ", recordName=" + recordName + ", refNo="
				+ refNo + "]";
	}

}
