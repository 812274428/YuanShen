package com.xw.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.xw.entity.ParameterListEntity;
import com.xw.entity.ResultEntity;
import com.xw.entity.ResultListEntity;
import com.xw.entity.ThirdJarEntity;
import com.xw.entity.UpdateDataEntity;
import com.xw.mapper.RecordMapper;
import com.xw.service.OperationService;
import com.xw.service.ReadXMLService;
import com.xw.service.UpdateCoreService;

@Component
@RestController
@RequestMapping("/updateCore")
public class UpdateCoreController {

	@Autowired
	UpdateCoreService updateCoreService;
	
	@Autowired
	OperationService operationService;
	
	@Autowired
	RecordMapper recordMapper;
	
	@Autowired
	ReadXMLService readXMLService;
	
	private static final Logger logger = LoggerFactory.getLogger(CompareMd5Controller.class);

	@RequestMapping("/getDataEntities")
	public List<ParameterListEntity> getDataEntities(@RequestBody UpdateDataEntity updateDataEntity) throws IOException {
		//读取refXml 获取当前版本更新所需要的更新的所有refNo
		String refXml = updateDataEntity.getPatchDir() + File.separator + "refno.xml";
		String currentVersion = updateDataEntity.getCurrentVersion();
		String upgradeableVersion = updateDataEntity.getUpgradeableVersion();
		String userName = updateDataEntity.getUserName();
		//读取所需所有流水号配置文件的路径
		List<String> paths = readXMLService.readXml(refXml, currentVersion, upgradeableVersion);
		
		//读取配置的xml
		List<ParameterListEntity> dataEntities = new ArrayList<ParameterListEntity>();
		for(String path:paths) {
			String version = path.substring(0, path.indexOf(File.separator));
			String xmlPath = updateDataEntity.getPatchDir() + File.separator + path;
			dataEntities.addAll(readXMLService.readConfig(xmlPath, version, userName));
		}

		logger.info("返回当前需要升级的文件信息 {}",dataEntities);
		updateCoreService.createUpdateJARs(updateDataEntity);
		updateCoreService.start(dataEntities, updateDataEntity);
		return dataEntities;
	}
	
	@RequestMapping("/updateCore")
	public String updateCore(@RequestBody ResultEntity reslEntity) throws IOException {
		String rs1 = "";
		String rs2 = "";
		try {
			/*if(reslEntity.getParameterEntity().getUpdateFiles().size() == 0 && reslEntity.getParameterEntity().getDeleteFiles().size() == 0) {
				//整包更新
				try {
					 rs1 = updateCoreService.replaceAll(reslEntity.getRefNo(), reslEntity.getParameterEntity().getPackageName(), reslEntity.getParameterEntity().getJarName(), reslEntity.getRecordName());
				} catch (Exception e) {
					logger.error("当前{}进行jar包{}的整包更新时出现异常：{}", reslEntity.getRefNo(), reslEntity.getParameterEntity().getPackageName(), e.getMessage());
					return "当前"+ reslEntity.getRefNo() +"进行jar包"+ reslEntity.getParameterEntity().getPackageName() +"的整包更新时出现异常";
				}
				logger.info("当前{}中的{}更新完毕", reslEntity.getRefNo(), reslEntity.getParameterEntity().getPackageName());
				
				return "当前："+ reslEntity.getRefNo() +"中的"+ reslEntity.getParameterEntity().getPackageName() +"更新完毕";
			}else {*/
			//单个文件更新
			if(reslEntity.getParameterEntity().getUpdateFiles().size() > 0) {
				rs1 = updateCoreService.fileUpdate(reslEntity.getParameterEntity().getUpdateFiles(), reslEntity.getParameterEntity().getVersion(), "update", reslEntity.getRefNo(), reslEntity.getParameterEntity().getJarName(), reslEntity.getParameterEntity().getPackageName(), reslEntity.getRecordName());
			}else {
				rs1 = "Success";
			}
			if(reslEntity.getParameterEntity().getDeleteFiles().size() > 0) {
				rs2 = updateCoreService.fileUpdate(reslEntity.getParameterEntity().getDeleteFiles(), reslEntity.getParameterEntity().getVersion(), "delete", reslEntity.getRefNo(), reslEntity.getParameterEntity().getJarName(), reslEntity.getParameterEntity().getPackageName(), reslEntity.getRecordName());
			}else {
				rs2 = "Success";
			}
			
			if(rs1.equals(rs2) && ("Success").equals(rs1)) {
				logger.info("当前{}中的{}更新完毕", reslEntity.getRefNo(), reslEntity.getParameterEntity().getPackageName());
				return "当前："+ reslEntity.getRefNo() +"中的"+ reslEntity.getParameterEntity().getPackageName() +"更新完毕";
			}else if(!rs1.equals(rs2) && ("Success").equals(rs1)){
				logger.error("当前{}更新delete文件时发生异常：{}",reslEntity.getRefNo(),rs2);
				return "当前："+ reslEntity.getRefNo() +"中的"+ reslEntity.getParameterEntity().getPackageName() +"更新异常";
			}else {
				logger.error("当前{}更新update文件时发生异常:{}",reslEntity.getRefNo(),rs1);
				return "当前："+ reslEntity.getRefNo() +"中的"+ reslEntity.getParameterEntity().getPackageName() +"更新异常";
			}
			/* } */
		} catch (Exception e) {
			logger.error("当前{}中的{}更新异常：{}",reslEntity.getRefNo(), reslEntity.getParameterEntity().getPackageName(), e.getMessage());
			return "当前："+ reslEntity.getRefNo() +"更新异常";
		}
	}
	
	@RequestMapping("/updateJar")
	public String updateJar(@RequestBody ThirdJarEntity thirdJarEntity) throws IOException {
		String rs1 = "";
		rs1 = updateCoreService.thirdJar(thirdJarEntity);
		if(rs1.equals("Success")) {
			logger.info("当前{}中的第三方jar包更新完毕", thirdJarEntity.getRefNo());
			return "当前" + thirdJarEntity.getRefNo() + "中的第三方jar包更新完毕";
		}else {
			logger.error("当前{}中的第三方jar包更新异常", thirdJarEntity.getRefNo());
			return "当前" + thirdJarEntity.getRefNo() + "中的第三方jar包更新异常";
		}
	}
	
	@RequestMapping("/zipJar")
	public String zipJar(@RequestBody ResultListEntity resultListEntity) throws IOException {
		if(updateCoreService.end(resultListEntity.getJarList(), resultListEntity.getPackageList(), resultListEntity.getRecordName())) {
			logger.info("所有更新完的jar包已全部压缩完毕");
			return "所有更新完的jar包已全部压缩完毕";
		}
		logger.error("压缩jar包出现异常");
		return "压缩jar包出现异常";
	}
}
