package com.xw.entity;

import java.util.List;

public class ParameterListEntity {
	
	private String refNo;
	
	private List<ParameterEntity> parameterEntities;
	
	private List<String> updateJars;
	
	private List<String> deleteJars;

	public String getRefNo() {
		return refNo;
	}

	public void setRefNo(String refNo) {
		this.refNo = refNo;
	}

	public List<ParameterEntity> getParameterEntities() {
		return parameterEntities;
	}

	public void setParameterEntities(List<ParameterEntity> parameterEntities) {
		this.parameterEntities = parameterEntities;
	}

	public List<String> getUpdateJars() {
		return updateJars;
	}

	public void setUpdateJars(List<String> updateJars) {
		this.updateJars = updateJars;
	}

	public List<String> getDeleteJars() {
		return deleteJars;
	}

	public void setDeleteJars(List<String> deleteJars) {
		this.deleteJars = deleteJars;
	}
	
	public ParameterListEntity(String refNo, List<ParameterEntity> parameterEntities, List<String> updateJars,
			List<String> deleteJars) {
		super();
		this.refNo = refNo;
		this.parameterEntities = parameterEntities;
		this.updateJars = updateJars;
		this.deleteJars = deleteJars;
	}

	@Override
	public String toString() {
		return "ParameterListEntity [refNo=" + refNo + ", parameterEntities=" + parameterEntities + ", updateJars="
				+ updateJars + ", deleteJars=" + deleteJars + "]";
	}

}
