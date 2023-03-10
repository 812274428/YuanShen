package com.xw.service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.xw.entity.RecordEntity;
import com.xw.entity.ThirdJarEntity;
import com.xw.entity.ParameterEntity;
import com.xw.entity.ParameterListEntity;
import com.xw.entity.UpdateDataEntity;
import com.xw.filter.FileFilter;
import com.xw.mapper.RecordMapper;
import com.xw.mapper.UpdateDataEntityMapper;

@Service
public class UpdateCoreServiceImpl implements UpdateCoreService{
	
	private static final Logger logger = LoggerFactory.getLogger(ReadXMLServiceImpl.class);
	
	private static final String fileName = "CE";

	@Autowired
	OperationService operationService;
	
	@Autowired
	RecordMapper recordMapper;
	
	@Autowired
	UpdateDataEntityMapper updateDataEntityMapper;
	
	/**
	 * 
	 * 在更新之前先创建好临时文件夹
	 * */
	public void createUpdateJARs(UpdateDataEntity updateDataEntity) throws IOException {
		String fileBackups = updateDataEntity.getRecordPath()+ File.separator + updateDataEntity.getRecordName() + File.separator + fileName;
		operationService.createDir(fileBackups);
		logger.info(fileName + " 文件夹已创建");
		operationService.copydir(updateDataEntity.getRootDir(), fileBackups);
		
		String jarPath = updateDataEntity.getRecordPath()+ File.separator + updateDataEntity.getRecordName() + File.separator + "JARs";
		File f = new File(jarPath);
		f.mkdir();
		
		//旧jar包存放地
		String oldJars = jarPath + File.separator + "JARs_Old";
		File oldJarsFile = new File(oldJars);
		oldJarsFile.mkdir();
		logger.info("JARs_Old 文件夹已创建");
		
		//新patch jar包存放地
		String newJars = jarPath + File.separator + "JARs_New";
		File newJarsFile = new File(newJars);
		newJarsFile.mkdir();
		logger.info("JARs_New 文件夹已创建");
		if(updateDataEntityMapper.getUpdateDataEntity(updateDataEntity.getRecordName()) == null) {
			updateDataEntityMapper.insert(updateDataEntity);
			logger.info("updateDataEntity成功存入jar表");
		}else {
			logger.info("updateDataEntity已经存入jar表");
		}
	}

	public String fileUpdate(List<String> files, String version, String type, String refNo, String jarName, String packageName, String recordName) {
		UpdateDataEntity updateDataEntity = updateDataEntityMapper.getUpdateDataEntity(recordName);
		String fileBackups = updateDataEntity.getRecordPath()+ File.separator + updateDataEntity.getRecordName() + File.separator + fileName;
		String patchData = updateDataEntity.getPatchDir()+ File.separator + version + File.separator + fileName;
		for(String file :files) {
			String path = "";
			if(packageName.equals("CEWeb.war") && file.contains(".class")){
				path = packageName + File.separator + "WEB-INF"+ File.separator + "classes" + File.separator + file.substring(file.indexOf(File.separator)+1);
			}else if(packageName.equals("CEWeb.war") && file.contains(".js")){
				path = packageName + File.separator + "SYS_JS" + File.separator + file.substring(file.indexOf(File.separator)+1);
			}else {
				path = file;
			}
			FileFilter filter = new FileFilter();
			List<String> filesList = filter.listFile(patchData + File.separator + path);
			filesList.add(path);
			for(String f : filesList) {
				f = f.replace(patchData + File.separator, "");
				RecordEntity recordEntity = new RecordEntity(updateDataEntity.getRecordName(), version, refNo, packageName, f, "Updating", "");
				if(type.equals("update")) {
					recordEntity.setType("update");
				}else {
					recordEntity.setType("delete");
				}
				if(recordMapper.getRecordEntity(recordEntity.getRecordName(), refNo, recordEntity.getPackageName(), recordEntity.getFileName(), version) == null) {
					recordMapper.insert(recordEntity);
					logger.info("初次更新jar包：{} 中的 {} 文件",recordEntity.getPackageName() ,recordEntity.getFileName());
				}else if(("Updating").equals(recordMapper.getRecordEntity(recordEntity.getRecordName(), refNo, recordEntity.getPackageName(), recordEntity.getFileName(), version).getStatus())
						||("Error").equals(recordMapper.getRecordEntity(recordEntity.getRecordName(), refNo, recordEntity.getPackageName(), recordEntity.getFileName(), version).getStatus())){
					logger.info("再次更新jar包：{} 中的 {} 文件",recordEntity.getPackageName() ,recordEntity.getFileName());
				}else {
					return recordMapper.getRecordEntity(recordEntity.getRecordName(), refNo, recordEntity.getPackageName(), recordEntity.getFileName(), version).getStatus();
				}
				if(type.equals("update")) {
					update(updateDataEntity, recordEntity, patchData, path, fileBackups, refNo, packageName, f);
				}else {
					delete(updateDataEntity, recordEntity, patchData, path, fileBackups, refNo, packageName, f);
				}
			}
		}
		return "Success";
	}
	
	private String update(UpdateDataEntity updateDataEntity, RecordEntity recordEntity, String patchData, String path, String fileBackups, String refNo, String packageName, String updateFile){
		String newJars = updateDataEntity.getRecordPath()+ File.separator + updateDataEntity.getRecordName() + File.separator + "JARs" + File.separator + "JARs_New";
		try {
			operationService.copydir(newJars + File.separator + updateFile, fileBackups + File.separator + path.substring(0, path.lastIndexOf(File.separator)));
			RecordEntity recordEntityUpdate = new RecordEntity(updateDataEntity.getRecordName(), recordEntity.getVersion(), refNo, packageName, updateFile, "Success", "update");
			recordMapper.updateStatus(recordEntityUpdate);
		} catch (Exception e) {
			logger.info("文件{} 复制出现异常 {}",updateFile ,e.getMessage());
			RecordEntity recordEntityError = new RecordEntity(updateDataEntity.getRecordName(), recordEntity.getVersion(), refNo, packageName, updateFile, "Error", "update");
			recordMapper.updateStatus(recordEntityError);
			return recordMapper.getRecordEntity(recordEntity.getRecordName(), refNo, recordEntity.getPackageName(), recordEntity.getFileName(), recordEntity.getVersion()).getStatus();
		}
		return "Success" ;
	}

	private String delete(UpdateDataEntity updateDataEntity, RecordEntity recordEntity, String patchData, String path, String fileBackups, String refNo, String packageName, String deleteFile){
		try {
			operationService.deleteFile(fileBackups + File.separator + deleteFile);
			RecordEntity recordEntityUpdate = new RecordEntity(updateDataEntity.getRecordName(), recordEntity.getVersion(), refNo, packageName, deleteFile, "Success", "delete");
			recordMapper.updateStatus(recordEntityUpdate);
		} catch (Exception e) {
			logger.info("文件{} 删除异常 {}",deleteFile ,e.getMessage());
			RecordEntity recordEntityError = new RecordEntity(updateDataEntity.getRecordName(), recordEntity.getVersion(), refNo, packageName, deleteFile, "Error", "delete");
			recordMapper.updateStatus(recordEntityError);
			return "文件"+ deleteFile +"删除异常";
		}
		return "Success" ;
	}	
	
	/**
	 * 
	 * 开始对需要解压的压缩包解压，并备份存放于JARs之中
	 * */
	@Override
	public boolean start(List<ParameterListEntity> lists, UpdateDataEntity updateDataEntity) throws IOException {
		
		String oldJars = updateDataEntity.getRecordPath()+ File.separator + updateDataEntity.getRecordName() + File.separator + "JARs" + File.separator + "JARs_Old";
		String newJars = updateDataEntity.getRecordPath()+ File.separator + updateDataEntity.getRecordName() + File.separator + "JARs" + File.separator + "JARs_New";
		
		try {
			for(ParameterListEntity parameterListEntity : lists) {
				for(ParameterEntity parameterEntity : parameterListEntity.getParameterEntities()) {
					String fileBackups = updateDataEntity.getRecordPath()+ File.separator + updateDataEntity.getRecordName() + File.separator + fileName;
					String patchDir = updateDataEntity.getPatchDir() + File.separator + parameterEntity.getVersion() + File.separator + fileName + File.separator;
					
					if(!parameterEntity.getJarName().equals("") && (parameterEntity.getUpdateFiles().size() != 0 || parameterEntity.getDeleteFiles().size() != 0)){
						String fileOld = fileBackups + File.separator + parameterEntity.getJarName();
						
						//解压源文件
						operationService.unZip(fileOld, fileBackups + File.separator + parameterEntity.getPackageName());
						
						File oldList = new File(oldJars +File.separator+ parameterEntity.getPackageName());
						oldList.mkdir();
						operationService.copydir(fileBackups + File.separator + parameterEntity.getPackageName(), oldJars + File.separator + parameterEntity.getPackageName());
						
						//patch更新包
					    String reportPath = patchDir + parameterEntity.getJarName();
					    
						//解压更新后的文件
					    operationService.unZip(reportPath, newJars + File.separator + parameterEntity.getPackageName());
						
					    File newList = new File(newJars +File.separator+ parameterEntity.getPackageName());
					    newList.mkdir();
		
					}else if(parameterEntity.getJarName().equals("")){
						//复制本地文件后的CE文件夹中的文件
						String fileOld = fileBackups + File.separator + parameterEntity.getPackageName();
						File oldList = new File(oldJars +File.separator+ parameterEntity.getPackageName());
						oldList.mkdir();
						operationService.copydir(fileOld, oldJars + File.separator + parameterEntity.getPackageName());
						
						//patch更新包
						String reportPath = patchDir + parameterEntity.getPackageName();
						File newList = new File(newJars +File.separator+ parameterEntity.getPackageName());
					    newList.mkdir();
					    operationService.copydir(reportPath, newJars + File.separator + parameterEntity.getPackageName());
					}else {
						String fileOld = fileBackups + File.separator + parameterEntity.getJarName();
						operationService.copydir(fileOld, oldJars);
						
						String reportPath = patchDir + parameterEntity.getJarName();
						operationService.copydir(reportPath, newJars);
					}
				}
			}
		}catch (Exception e) {
			logger.error("{} 解压或备份jar包出现异常：{}", updateDataEntity.getRecordName(), e.getMessage());
			return false;
		}
		return true;
		
	}

	@Override
	public boolean end(List<String> jarList, List<String> packageList, String recordName) throws IOException {
		
		UpdateDataEntity updateDataEntity = updateDataEntityMapper.getUpdateDataEntity(recordName);
		String fileBackups = updateDataEntity.getRecordPath()+ File.separator + updateDataEntity.getRecordName() + File.separator + fileName;
		try {
			for(int i = 0; i < jarList.size(); i++) {
				if(jarList.get(i).equals("") || jarList.get(i).equals(null)) {
					continue;
				}
				//删除更新前的压缩包
				operationService.deleteFile(fileBackups + File.separator + jarList.get(i));
				List<String> zlist = operationService.findFileList(fileBackups + File.separator + packageList.get(i));
				//压缩更新后的压缩包
				operationService.zipDocuments(fileBackups + File.separator + jarList.get(i), zlist);
				//删除更新后的文件夹
				operationService.deleteFiles(fileBackups + File.separator + packageList.get(i));
				logger.info("压缩jar包：{}完成", packageList.get(i));
			}
		}catch (Exception e) {
			logger.error("{} 压缩jar包出现异常：{}", updateDataEntity.getRecordName(), e.getMessage());
			return false;
		}
		return true;
	}

	/*
	 * 
	 * 整体文件进行替换：包括jar包、文件夹
	 * */
	/*
	 * public String replaceAll(String refNo, String packageName, String jarName,
	 * String recordName) throws IOException { UpdateDataEntity updateDataEntity =
	 * updateDataEntityMapper.getUpdateDataEntity(recordName); String fileBackups =
	 * updateDataEntity.getRecordPath()+ File.separator +
	 * updateDataEntity.getRecordName() + File.separator + fileName; String patchDir
	 * = updateDataEntity.getPatchDir() + File.separator +
	 * updateDataEntity.getUpgradeableVersion() + File.separator + fileName +
	 * File.separator; String reportPath = ""; if(jarName == null) { reportPath =
	 * patchDir + packageName; fileBackups += File.separator + packageName; }else {
	 * reportPath = patchDir + packageName + ".jar"; }
	 * 
	 * try { operationService.copydir(reportPath, fileBackups); RecordEntity
	 * recordEntityUpdate = new RecordEntity(updateDataEntity.getRecordName(),
	 * refNo, packageName, packageName, "Success", "update");
	 * recordMapper.updateStatus(recordEntityUpdate); } catch (Exception e) {
	 * logger.error("{} 更新jar包：{}出现异常：{}", updateDataEntity.getRecordName(),
	 * packageName, e.getMessage()); RecordEntity recordEntityError = new
	 * RecordEntity(updateDataEntity.getRecordName(), refNo, packageName,
	 * packageName, "Error", "update");
	 * recordMapper.updateStatus(recordEntityError); return "Error"; } return
	 * "Success"; }
	 */

	@Override
	public String thirdJar(ThirdJarEntity thirdJarEntity) throws IOException {
		UpdateDataEntity updateDataEntity = updateDataEntityMapper.getUpdateDataEntity(thirdJarEntity.getRecordName());
		String fileBackups = updateDataEntity.getRecordPath()+ File.separator + updateDataEntity.getRecordName();
		String params = "PARAMS";
		try {
			for(String jarPath : thirdJarEntity.getJarPath()) {
				String newPath = fileBackups + File.separator + params + File.separator + jarPath.substring(0,jarPath.lastIndexOf(File.separator));
				operationService.createDir(newPath);
				operationService.copydir(updateDataEntity.getParameterDir() + File.separator + jarPath, newPath);
			}
		} catch (Exception e) {
			logger.error("更新第三方jar包{}时出现异常{}", thirdJarEntity.getJarPath(),e.getMessage());
			return "Error";
		}
		return "Success";
	}
}