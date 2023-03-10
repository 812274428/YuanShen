package com.xw.entity;

import java.sql.Date;

public class UpdateDataEntity {
	
	//当前版本
	private String currentVersion;
	
	//可升级版本
	private String upgradeableVersion;
	
	//环境目录
	private String rootDir;
	
	//更新包目录
	private String patchDir;
	
	//参数目录
	private String parameterDir;
	
	//记录名
	private String recordName;
	
	//服务器类型
	private String serverType;
	
	//部署方式
	private String deploymentModel;
	
	//记录文件路径
	private String recordPath;
	
	//记录当前项目组用户
	private String userName;
	
	private Date date;

	public UpdateDataEntity(String currentVersion, String upgradeableVersion, String rootDir, String patchDir,
			String parameterDir, String recordName, String serverType, String deploymentModel, String recordPath,
			String userName, Date date) {
		super();
		this.currentVersion = currentVersion;
		this.upgradeableVersion = upgradeableVersion;
		this.rootDir = rootDir;
		this.patchDir = patchDir;
		this.parameterDir = parameterDir;
		this.recordName = recordName;
		this.serverType = serverType;
		this.deploymentModel = deploymentModel;
		this.recordPath = recordPath;
		this.userName = userName;
		this.date = date;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getCurrentVersion() {
		return currentVersion;
	}

	public void setCurrentVersion(String currentVersion) {
		this.currentVersion = currentVersion;
	}

	public String getUpgradeableVersion() {
		return upgradeableVersion;
	}

	public void setUpgradeableVersion(String upgradeableVersion) {
		this.upgradeableVersion = upgradeableVersion;
	}

	public String getRootDir() {
		return rootDir;
	}

	public void setRootDir(String rootDir) {
		this.rootDir = rootDir;
	}

	public String getPatchDir() {
		return patchDir;
	}

	public void setPatchDir(String patchDir) {
		this.patchDir = patchDir;
	}

	public String getParameterDir() {
		return parameterDir;
	}

	public void setParameterDir(String parameterDir) {
		this.parameterDir = parameterDir;
	}

	public String getRecordName() {
		return recordName;
	}

	public void setRecordName(String recordName) {
		this.recordName = recordName;
	}

	public String getServerType() {
		return serverType;
	}

	public void setServerType(String serverType) {
		this.serverType = serverType;
	}

	public String getDeploymentModel() {
		return deploymentModel;
	}

	public void setDeploymentModel(String deploymentModel) {
		this.deploymentModel = deploymentModel;
	}

	public String getRecordPath() {
		return recordPath;
	}

	public void setRecordPath(String recordPath) {
		this.recordPath = recordPath;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Override
	public String toString() {
		return "UpdateDataEntity [currentVersion=" + currentVersion + ", upgradeableVersion=" + upgradeableVersion
				+ ", rootDir=" + rootDir + ", patchDir=" + patchDir + ", parameterDir=" + parameterDir + ", recordName="
				+ recordName + ", serverType=" + serverType + ", deploymentModel=" + deploymentModel + ", recordPath="
				+ recordPath + ", userName=" + userName + ", date=" + date + "]";
	}	
}
