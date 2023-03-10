package com.xw.service;

import java.io.IOException;
import java.util.List;
import com.xw.entity.ParameterListEntity;
import com.xw.entity.ThirdJarEntity;
import com.xw.entity.UpdateDataEntity;

public interface UpdateCoreService {
	//创建本地更新临时文件夹
	public void createUpdateJARs(UpdateDataEntity updateDataEntity) throws IOException;

	/*
	 * //替换整个jar包 public String replaceAll(String refNo, String packageName, String
	 * jarName, String recordName)throws IOException;
	 */
	
	//解压所有jar包
	public boolean start(List<ParameterListEntity> lists, UpdateDataEntity updateDataEntity)throws IOException;
	//压缩所有jar的文件夹
	public boolean end(List<String> jarName, List<String> packageName, String recordName) throws IOException;
	//更新第三方jar包
	public String thirdJar(ThirdJarEntity thirdJarEntity)throws IOException;
	//遍历文件集合
	public String fileUpdate(List<String> files, String version, String type, String refNo, String jarName, String packageName, String recordName);
}
