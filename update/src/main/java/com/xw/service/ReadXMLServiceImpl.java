package com.xw.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.xw.entity.ParameterEntity;
import com.xw.entity.ParameterListEntity;

@Service
public class ReadXMLServiceImpl implements ReadXMLService{
	
	private static final Logger logger = LoggerFactory.getLogger(ReadXMLServiceImpl.class);
	
	@Value("${jar.list}")
	private List<String> jarNameList;
	
	public List<ParameterListEntity> readConfig(String pathDir, String version, String userName) {
		
		List<ParameterListEntity> parameterListEntities = new ArrayList<ParameterListEntity>();
		try {
			// 创建SAXReader对象
			SAXReader reader = new SAXReader();
			// 加载xml文件
			Document document= reader.read(new File(pathDir));
			// 获取根节点
			Element root = document.getRootElement();
			// 获取迭代器
			Iterator elements = root.elementIterator();
			
			// root下的子节点
			while(elements.hasNext()){
				Element element = (Element) elements.next();
				//获取流水号
				String refNo = "";
				refNo = element.attributeValue("number");
	            Iterator iterator = element.elementIterator();
	            
	            List<String> updateJars = new ArrayList<String>();
	            List<String> deleteJars = new ArrayList<String>();
	            List<ParameterEntity> parameterEntities = new ArrayList<ParameterEntity>();
	            //common及同级标签下的更新文件
	            while(iterator.hasNext()) {
	            	
	            	Element element2 = (Element) iterator.next();
	            	Iterator iterator2 = element2.elementIterator();
            		while(iterator2.hasNext()) {
	            		
	            		Element element3 = (Element) iterator2.next();
	            		Iterator iterator3 = element3.elementIterator();
	            		
	            		if((element3.getName().equals("replace") || element3.getName().equals("delete"))) {
		            		while(iterator3.hasNext()) {
		            			Element element4 = (Element) iterator3.next();
		            			String valueString = element4.getStringValue();
		            			valueString = valueString.replace("/", File.separator);
		            			if(valueString.equals("")) {
		            				continue;
		            			}
		            			String jarName = "";
			    		        String packageName = "";
			                	List<String> updateFiles = new ArrayList<String>();
			            		List<String> deleteFiles = new ArrayList<String>();
		            			
		            			if(!valueString.contains(".jar")) {	
		            				packageName = valueString.substring(0, valueString.indexOf(File.separator));
		            				if(packageName.equals("CEWeb")) {
		            					packageName = packageName + ".war";
		            				}
		            				String pathString = pathProcessing(valueString, packageName);
		            				
		            				if(jarNameList.contains(packageName)) {
		            					jarName = packageName + ".jar";
		            				}
		            				if(element2.getName().equals("common") || 
		            						(element2.getName().equals("project") && element2.attributeValue("projectName").equals(""))) {
		            					if(element3.getName().equals("replace")) {
					            			updateFiles.add(pathString);
						            	}else if( element3.getName().equals("delete")){
						            		deleteFiles.add(pathString);
						            	}
		            				}else if(element2.attributeValue("projectName").equals(userName)) {
		            					pathString = pathString.replace(userName + File.separator, "");
		            					if(element3.getName().equals("replace")) {
					            			updateFiles.add(pathString);
						            	}else if( element3.getName().equals("delete")){
						            		deleteFiles.add(pathString);
						            	}
		            				}else {
		            					continue;
		            				}
				            		parameterEntities.add(new ParameterEntity(packageName ,jarName, version, updateFiles, deleteFiles));
		            			}else {
		            				if(element2.getName().equals("common") || 
		            						(element2.getName().equals("project") && element2.attributeValue("projectName").equals(""))) {
			            				if(element3.getName().equals("replace")) {
			            					updateJars.add(valueString);
						            	}else if( element3.getName().equals("delete")){
						            		deleteJars.add(valueString);
						            	}
		            				}else if(element2.attributeValue("projectName").equals(userName)) {
		            					valueString = valueString.replace(userName + File.separator, "");
		            					if(element3.getName().equals("replace")) {
			            					updateJars.add(valueString);
						            	}else if( element3.getName().equals("delete")){
						            		deleteJars.add(valueString);
						            	}
		            				}
		            			}
		            		}
		            	}
	            	}	
	            }
	            List<ParameterEntity> parameter = dealWith(parameterEntities);
	            parameterListEntities.add(new ParameterListEntity(refNo, parameter, updateJars, deleteJars));
			}
		} catch (Exception e) {
			logger.error("readConfig时发生异常 {}", e);
			e.printStackTrace();
		}
		logger.info("readConfig完成");
		return parameterListEntities;
	}

	//处理所有项目路径和压缩包路径不统一的问题
	public String pathProcessing(String path, String packageName) {
		
		if(packageName.equals("CEEJB")) {
			path = path.replace("\\ejbModule\\", File.separator);
		}
		
		if(packageName.equals("CEWebService") || packageName.equals("Core") || packageName.equals("CSBase") || packageName.equals("com.cs.core.xml")) {
			path = path.replace("\\src\\", File.separator);
		}
		
		if(packageName.equals("ProjectJARS")) {
			path = path.replace("\\CEINTERIM\\", File.separator).replace("\\C2B\\", File.separator).replace("\\ARF\\", File.separator).replace("\\TSU\\", File.separator);
		}
		
		if(packageName.equals("CEWeb.war")) {
			if(path.contains("javasource")) {
				path = path.replace("CEWeb", "CEWeb" + File.separator + "WEB-INF" + File.separator + "classes");
			}
			path = path.replace("CEWeb", "CEWeb.war").replace("\\JavaSource\\", File.separator).replace("\\WebContent\\", File.separator);
		}
		
		path = path.replace(".java", ".class");

		return path;
	}
	
	
	@Override
	public List<String> readXml(String pathDir, String currentVersion, String upgradeableVersion) {
		
		List<String> listTrue = new ArrayList<String>();
		List<String> listFalse = new ArrayList<String>();
		List<String> listResult = new ArrayList<String>();
		try {
			// 创建SAXReader对象
			SAXReader reader = new SAXReader();
			// 加载xml文件
			Document document= reader.read(new File(pathDir));
			// 获取根节点
			Element root = document.getRootElement();
			// 获取迭代器
			Iterator elementsSearch = root.elementIterator();
			
			Boolean currentFlag = false;
			Boolean upgradeableFlag = false;
			
			//寻找配置文件中是否存在预期升级的版本号
			while(elementsSearch.hasNext()){
				Element element = (Element) elementsSearch.next();
				//获取流水号
				String version = element.attributeValue("version");
	            if(version.equals(upgradeableVersion)) {
	            	upgradeableFlag = true;
	            	break;
	            }
			}
			Iterator elementsUpdate = root.elementIterator();
			//配置文件中存在预期升级的版本号
			if(upgradeableFlag) {
				while(elementsUpdate.hasNext()){
					Element element = (Element) elementsUpdate.next();
					//获取流水号
					String version = element.attributeValue("version");
					if(version.equals(currentVersion)) {
						currentFlag = true;
						continue;
					}
					//当前版本号不是初始版本
					if(currentFlag) {
						Iterator iterator = element.elementIterator();
			            //upgradeableVersion 下的子节点
			            while(iterator.hasNext()) {
			            	Element element2 = (Element) iterator.next();
			            	Iterator iterator2 = element2.elementIterator();
			            	listTrue.add(element2.getStringValue());
			            }
					}
					//当前版本号是初始版本
					if(!currentFlag) {
						Iterator iterator = element.elementIterator();
			            //upgradeableVersion 下的子节点
			            while(iterator.hasNext()) {
			            	Element element2 = (Element) iterator.next();
			            	Iterator iterator2 = element2.elementIterator();
			            	listFalse.add(element2.getStringValue());
			            }
					}
					
					if(version.equals(upgradeableVersion)) {
		            	break;
		            }
				}
			}
			if(currentFlag) {
				listResult = listTrue;
			}else {
				listResult = listFalse;
			}
		} catch (Exception e) {
			logger.error("readXml时发生异常 {}", e);
			e.printStackTrace();
		}
		logger.info("readXml完成");
		return listResult;
	}
	
	//处理重复的packageName
	private List<ParameterEntity> dealWith( List<ParameterEntity> parameterEntities){
		List<ParameterEntity> parameter = new ArrayList<ParameterEntity>();
        List<String> nameList = new ArrayList<String>();
        for(int i = 0; i < parameterEntities.size(); i++) {
        	if(nameList.contains(parameterEntities.get(i).getPackageName())) {
        		continue;
        	}
        	nameList.add(parameterEntities.get(i).getPackageName());
        	List<String> updateFiles = new ArrayList<String>();
        	List<String> deleteFiles = new ArrayList<String>();
        	updateFiles.addAll(parameterEntities.get(i).getUpdateFiles());
        	deleteFiles.addAll(parameterEntities.get(i).getDeleteFiles());
        	for(int j = 0; j < parameterEntities.size(); j++) {
        		if(i == j) {
        			continue;
        		}
        		if(parameterEntities.get(i).getPackageName().equals(parameterEntities.get(j).getPackageName())) {
        			updateFiles.addAll(parameterEntities.get(j).getUpdateFiles());
        			
        			deleteFiles.addAll(parameterEntities.get(j).getDeleteFiles());
        		}
        	}
        	parameter.add(new ParameterEntity(parameterEntities.get(i).getPackageName() ,parameterEntities.get(i).getJarName(), parameterEntities.get(i).getVersion(), updateFiles, deleteFiles));
        }
        return parameter;
	}
}
