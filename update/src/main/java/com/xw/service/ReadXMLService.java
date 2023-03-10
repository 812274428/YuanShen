package com.xw.service;

import java.util.List;

import com.xw.entity.ParameterListEntity;


public interface ReadXMLService {
	//读取对应流水号的更新配置
	public List<ParameterListEntity> readConfig(String pathDir, String version, String userName);
	//读取当前版本所需更新的流水号配置
	public List<String> readXml(String pathDir, String currentVersion, String upgradeableVersion);
}
