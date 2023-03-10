package com.xw.filter;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileFilter {
	
	private static final Logger logger = LoggerFactory.getLogger(FileFilter.class);
	
	public List<String> listFile(String dirPath){
		logger.info("正在调用listFile方法");
		List<String> fileList = new ArrayList<String>();
		
		String dir = dirPath.substring(0, dirPath.lastIndexOf(File.separator));
		String filepre = dirPath.replace(dir + File.separator, "").replace(".class", "")+"$";
		
		File fileTarget = new File(dir);
		if(fileTarget.exists()){
			File[] files = fileTarget.listFiles(new FilenameFilter(){
				public boolean accept(File dir, String name) {
					return (name.startsWith(filepre));
				}
			});
			if(files.length > 0){
				for(int i = 0; i<files.length; i++){
					fileList.add(files[i].getPath());
				}
			}
		}else{
			logger.error("目录{}不存在",dir);
		}
		logger.info("listFile方法调用完成，fileList为：{}", fileList);
		return fileList;
	}
	
//	public static void main(String[] args) {
//		FileFilter lf = new FileFilter();
//		lf.listFile("D:\\workspace\\patch\\v003\\CE\\CEWeb.war\\WEB-INF\\classes\\com\\cs\\ce\\rest\\service\\CERecvMessgaeByAPIProcessor.class");
//	}
}
