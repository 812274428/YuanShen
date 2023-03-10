package com.xw.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.xw.entity.FileDataEntity;
import com.xw.entity.UpdateDataEntity;
import com.xw.mapper.UpdateDataEntityMapper;

@Service
public class CompareMd5ServiceImpl implements CompareMd5Service{
	
	private static final Logger logger = LoggerFactory.getLogger(CompareMd5ServiceImpl.class);
	
	@Autowired
	OperationService operationServce;
	
	@Autowired
	ReadXMLService readXMLService;
	
	@Autowired
    private UpdateDataEntityMapper updateDateEntityMapper;

	public String CompareMd5(UpdateDataEntity updateData) throws IOException{
		if(updateDateEntityMapper.getUpdateDataEntity(updateData.getRecordName()) == null) {
			
			String xmlPath = updateData.getPatchDir() + File.separator + updateData.getUpgradeableVersion() + File.separator + "config.xml";
			//ParameterListEntity lists = readXMLService.readXml(xmlPath);
			String dirName = "CoreMD5";
			//获取当前项目所在路径，并取上一级目录中的记录文件路径，加上name
			String fileBackups = updateData.getRecordPath() + File.separator + dirName;
			operationServce.createDir(fileBackups);
			logger.info( "文件夹已创建: {}",dirName);
			
			String oldMD5Name = File.separator + "CoreMD5_Old";
			File p1 = new File(fileBackups + oldMD5Name);
			if(!p1.exists()) {
				p1.mkdir();
			}
			logger.info( "文件夹已创建: CoreMD5_Old");
			
			String newMD5Name = File.separator + "CoreMD5_New";
			File p2 = new File(fileBackups + newMD5Name);
			if(!p2.exists()) {
				p2.mkdir();
			}
			logger.info( "文件夹已创建: CoreMD5_New");
			
			operationServce.copydir(updateData.getRootDir(), fileBackups + oldMD5Name);
			
			operationServce.copydir(updateData.getPatchDir()+File.separator + updateData.getCurrentVersion() + File.separator + "CE", fileBackups + newMD5Name);
			
		 	Map<String, FileDataEntity> FileDataOld = operationServce.getFiles(fileBackups + oldMD5Name);
	
		 	Map<String, FileDataEntity> FileDataNew = operationServce.getFiles(fileBackups + newMD5Name);
	
		 	List<FileDataEntity> resultList = new ArrayList<FileDataEntity>();
	        
	        resultList.addAll(compareFile(FileDataOld, FileDataNew));
	        
	        if(resultList.size() == 0) {
	        	logger.info("所选版本与本地版本的md5值相符");
				return "Success";
			}
	        logger.info("所选版本与本地版本的md5值不符");
	        return "Exceptional: 所选版本与本地版本的md5值不符";
		}else {
			return "Error: 记录名已存在，请重新填写";
		}
	}

	
	
	/*
     * 比较两个文件集合的不同
     * fileMapOld: 文件集合
     * fileMapNew: 文件集合
     */
    public List<FileDataEntity> compareFile(Map<String, FileDataEntity> fileMapOld, Map<String, FileDataEntity> fileMapNew) {
        
    	List<FileDataEntity> list = new ArrayList<FileDataEntity>();
        for (String key : fileMapNew.keySet()) {
        	
        	FileDataEntity fileModelOld = fileMapOld.get(key);
        	FileDataEntity fileModelNew = fileMapNew.get(key);
            // 将fileModelNew中没有的文件夹和文件,添加到结果集中
            if (fileModelOld == null) {
                list.add(fileModelNew);
                logger.info( "版本文件缺失: {} ",fileModelNew.getFile().getAbsolutePath());
                continue;
            }
            // 文件的md5值不同则add到比较结果集中
            if (fileModelOld.getFile().isFile() && !fileModelOld.getMd5().equals(fileModelNew.getMd5())) {
                list.add(fileModelNew);
                logger.info( "版本文件不符合当前版本: {}",fileModelNew.getFile().getAbsolutePath());
            }
        }
        return list;
    }

}
