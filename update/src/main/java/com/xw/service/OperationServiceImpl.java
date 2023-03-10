package com.xw.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.xw.entity.FileDataEntity;

@Service
public class OperationServiceImpl implements OperationService{
	
	private static final Logger logger = LoggerFactory.getLogger(OperationServiceImpl.class);
	
	private static final int  BUFFER_SIZE = 2 * 1024;
	private static byte[] ZIP_HEADER_1 = new byte[] { 80, 75, 3, 4 };
	private static byte[] ZIP_HEADER_2 = new byte[] { 80, 75, 5, 6 };
	
	
	/*
	 * unzipFile: 需要被解压的文件路径
	 * destDirPath: 解压到的文件路径
	 * */
	public boolean unZip(String unzipFile, String destDirPath) throws RuntimeException {

		File srcFile = new File(unzipFile);
	     // 判断源文件是否存在
	     if (!srcFile.exists()) {
	    	 logger.info( "文件不存在: {}", srcFile.getPath());
	         throw new RuntimeException("文件不存在: " + srcFile.getPath());
	     }
	     // 开始解压
	     ZipFile zipFile = null;
	     InputStream inputStream = null;
	     FileOutputStream fileOutputStream = null;
	     try {
	         zipFile = new ZipFile(srcFile);
	         Enumeration<?> entries = zipFile.entries();
	         logger.info( "正在解压: {}", srcFile);
	         while (entries.hasMoreElements()) {
	             ZipEntry entry = (ZipEntry) entries.nextElement();      
	             // 如果是文件夹，就创建个文件夹
	             if (entry.isDirectory()) {
	                 String dirPath = destDirPath + File.separator + entry.getName();
	                 File dir = new File(dirPath);
	                 dir.mkdirs();
	             } else {
	                 // 如果是文件，就先创建一个文件，然后用io流把内容copy过去
	                 File targetFile = new File(destDirPath + File.separator + entry.getName());
	                 // 保证这个文件的父文件夹必须要存在
	                 if(!targetFile.getParentFile().exists()){
	                     targetFile.getParentFile().mkdirs();
	                 }
	                 targetFile.createNewFile();
	                 // 将压缩文件内容写入到这个文件中
	                 inputStream = zipFile.getInputStream(entry);
	                 fileOutputStream = new FileOutputStream(targetFile);
	                 int len;
	                 byte[] buf = new byte[BUFFER_SIZE];
	                 while ((len = inputStream.read(buf)) != -1) {
	                	 fileOutputStream.write(buf, 0, len);	                	 
	                 }
	                 inputStream.close();
                	 fileOutputStream.close();
	             }
	         }
	     } catch (Exception e) {
	    	 logger.error("Error encountered while decompressing: {}", e.getMessage());
	         throw new RuntimeException("Error encountered while decompressing", e);
	     } finally {
	    	 if(inputStream != null) {
	        	 try {
	                 inputStream.close();
	             } catch (IOException e) {
	            	 logger.error("InputStream close IOException: {}", e.getMessage());
	            	 e.printStackTrace();
	             }
	         }
	    	 if(fileOutputStream != null) {
	        	 try {
	        		 fileOutputStream.close();
	             } catch (IOException e) {
	            	 logger.error("FileOutputStream close IOException: {}", e.getMessage());
	            	 e.printStackTrace();
	             }
	         }
	         if(zipFile != null){
	             try {
	                 zipFile.close();
	             } catch (IOException e) {
	                 logger.error("ZipFile close IOException: {}", e.getMessage());
	            	 e.printStackTrace();
	             }
	         } 
	     }
		return true;
	 }
	
	
	
	/*
	 * zipPath: 压缩包路径(注意加上压缩包命名与格式)
	 * fileList: 要压缩的文件列表
	 * */
	public boolean zipDocuments(String zipPath, List<String> fileList) throws RuntimeException {
		
		ZipOutputStream zipOutputStream = null ;
        try {
        	FileOutputStream out = new FileOutputStream(new File(zipPath));
        	zipOutputStream = new ZipOutputStream(out);
        	for (String filePath : fileList) {
        		File sourceFile = new File(filePath);
              if (sourceFile.exists()) {
            	  zipDocuemnt(sourceFile,zipOutputStream,sourceFile.getName());
              }
          }
			
        } catch (Exception e) {
			throw new RuntimeException("方法zipDocuments 发生错误: {}", e);
        }finally{
			if(zipOutputStream != null){
                try {
                	zipOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    logger.error("zipOutputStream关闭失败: {}", e.getMessage());
                }
            }
        }
		return true;
	}
	
	
	
	/*
	 * sourceFile: 源文件
	 * zipOutputStream: 文件输出路径
	 * name: 文件名
	 * */
	public boolean zipDocuemnt(File sourceFile, ZipOutputStream zipOutputStream, String name) throws Exception {
		
		byte[] buf = new byte[BUFFER_SIZE];
	        
        if(sourceFile.isFile()){
            // 向zip输出流中添加一个zip实体，构造器中name为zip实体的文件的名字
        	zipOutputStream.putNextEntry(new ZipEntry(name));
            // copy文件到zip输出流中
            int len;
            FileInputStream fileInputStream = new FileInputStream(sourceFile);
            while ((len = fileInputStream.read(buf)) != -1){
            	zipOutputStream.write(buf, 0, len);
            }
            // Complete the entry
            zipOutputStream.closeEntry();
            fileInputStream.close();
        } else {
            File[] listFiles = sourceFile.listFiles();
            if(listFiles == null || listFiles.length == 0){
                    // 空文件夹的处理
            	zipOutputStream.putNextEntry(new ZipEntry(name + File.separator));
                    // 没有文件，不需要文件的copy
            	zipOutputStream.closeEntry();
            }else {
                for (File file : listFiles) {
                    // 注意：file.getName()前面需要带上父文件夹的名字加一斜杠,
                    // 不然最后压缩包中就不能保留原来的文件结构,即：所有文件都跑到压缩包根目录下了
                	zipDocuemnt(file, zipOutputStream, name + File.separator + file.getName());  
                }
            }
        }
		return true;
	}
	
	
	
	/*
	 * newFile: 被复制的文件所在路径
	 * oldPath: 复制到的路径
	 * */
	public boolean copyfile(String newFile, String oldPath) throws IOException {
		
		File oldf = new File(newFile);
		FileOutputStream fileOutputStream = new FileOutputStream(new File(oldPath));
		FileInputStream fileInputStream = new FileInputStream(oldf);
		int count = fileInputStream.available();
		byte[] bytes = new byte[count];
		while(fileInputStream.read(bytes) != -1 && oldf.length() > 0) {
			fileOutputStream.write(bytes);
		}
		fileOutputStream.flush();
		fileOutputStream.close();
		fileInputStream.close();
		return true;
	}
	
	
	
	/*
	 * newFile: 被复制的文件所在路径
	 * oldPath: 复制到的路径
	 * */
	public boolean copydir(String newFile, String oldPath) throws IOException {

		File f1 = new File(newFile);
		if(f1.isDirectory()) {
			File[] files = f1.listFiles();
			for(File ff : files) {
				if(!ff.isDirectory()) {
					String newTarget = oldPath + File.separator + ff.getName();
					copyfile(ff.getAbsolutePath(), newTarget);
				}else {
					String newTarget = oldPath + File.separator + ff.getName();
					File newDir = new File(newTarget);
					newDir.mkdir();
					copydir(newFile + File.separator + ff.getName() , oldPath + File.separator + ff.getName());
				}
			}	
		}else {
			String newTarget = oldPath + File.separator + f1.getName();
			copyfile(f1.getAbsolutePath(), newTarget);
		}
		return true;
	}

	
	
	public boolean deleteAllFile(List<String> dirPath) {
		boolean flag = true;
		for(String dir:dirPath) {
			flag = deleteFiles(dir);
			if(!flag) {
				return false;
			}
		}
		return flag;
	}



	public boolean deleteFiles(String dirPath) {
		File dirFile = new File(dirPath);
		if(dirFile.isDirectory()) {
			if(!dirFile.exists()) {
				logger.info( "删除文件夹失败,文件夹 {} 不存在", dirPath);
				return true;
			}
			boolean flag = true;
			File[] files = dirFile.listFiles();
			for(File ff : files) {
				if(ff.isFile()) {
					flag = deleteFile(ff.getAbsolutePath());
					if(!flag) break;	
				}else if(ff.isDirectory()){
					flag = deleteFiles(ff.getAbsolutePath());
					if(!flag) break;				
				}
			}
			if(!flag) {
				logger.info( "删除文件夹 {} 失败", dirPath);
				return false;
			}
			if(dirFile.delete()) {
				logger.info( "删除文件夹 {} 成功", dirPath);
				return true;
			}else {
				logger.info( "删除文件夹 {} 失败", dirPath);
				return false;
			}
		}else {
			return deleteFile(dirPath);
		}
	}



	public boolean deleteFile(String FilePath) {
		File file = new File(FilePath);
		if(file.exists() && file.isFile()) {
			//System.gc();
			if(file.delete()) {		
				logger.info( "删除文件 {} 成功", FilePath);
				return true;
			}else {
				logger.info( "删除文件 {} 失败", FilePath);
				return false;
			}
		}else {
			logger.info( "文件 {}不存在", FilePath);
			return true;
		}
	}

	
	/*
	 * 循环创建文件夹
	 * path: 路径
	 * */
	public boolean createDir(String path) {
		int index = path.lastIndexOf(File.separator);
		if(index != -1) {
			String newPath = path.substring(0,index);
			createDir(newPath);
		}
		File file = new File(path);
		return file.mkdir();
	}
	
	
	/*
     * 根据路径获取所有的文件夹和文件,及文件的md5值
     * path: 文件路径
     */
    public Map<String, FileDataEntity> getFiles(String path) throws IOException {
        Map<String, FileDataEntity> map = new HashMap<String, FileDataEntity>();
        File folder = new File(path);
        Object[] files = unzipFileList(folder).toArray();
        Arrays.sort(files);
        logger.info( "正在计算该路径下 {} 的文件的MD5值", path);
        for (Object obj : files) {
            File file = (File) obj;
            // 去掉根目录
            String key = file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf(File.separator));
            String md5 = "";// 文件夹不比较md5值
            if (file.isFile()) {
                md5 = DigestUtils.md5Hex(new FileInputStream(file));
            }
            FileDataEntity fileModel = new FileDataEntity(file, md5);
            map.put(key, fileModel);
        }
        return map;
    }
    
    
    /*
     * 递归获取路径下所有文件夹和文件
     * filePath: 文件路径
     * */
    public List<File> getFileList(File filePath) {
    	List<File> list = new ArrayList<File>();
        File[] newFiles = filePath.listFiles();
        for(File file2 : newFiles) {
        	if(!isCompressedFile(file2)) {
                if (file2.isDirectory()) {
                    List<File> fileList = getFileList(file2);
                    list.addAll(fileList);
                }else {
                	list.add(file2);
                }
        	}
        }
        return list;
    }
    
    
    public List<File> getFile(File filePath) {
    	List<File> list = new ArrayList<File>();
        File[] newFiles = filePath.listFiles();
        for(File file2 : newFiles) {
        	list.add(file2);
        }
        return list;
    }
    
    /*
	 * 找到根路径下的所有文件或文件夹名，放入list中
	 * destDirPath: 需要更新的包的根路径
	 * */
	public List<String> findFileList(String destDirPath) {
		File dir = new File(destDirPath);
        if (!dir.exists() || !dir.isDirectory()) {// 判断是否存在目录
            return null;
        }
        String[] files = dir.list();// 读取目录下的所有目录文件信息
        for(int i=0; i<files.length; i++) {
        	files[i] = destDirPath + File.separator + files[i];
        }
        List<String> list = Arrays.asList(files);
        return list;
    }
    
    public List<File> unzipFileList(File filePath){
    	List<File> list = new ArrayList<File>();
        File[] files = filePath.listFiles();
        for (File file : files) {
        	if(isCompressedFile(file)) {
        		String destDirPath = file.getAbsolutePath().substring(0,file.getAbsolutePath().lastIndexOf("."));
        		unZip(file.getAbsolutePath(), destDirPath);
        	}
        }
        list = getFileList(filePath);
        return list;
    }
    
    
    /*
	 * 判断文件是否为一个压缩文件
	 * file: 文件名
	 */
	public boolean isCompressedFile(File file) {
 
		if(file == null){
			return false;
		}
		if (file.isDirectory()) {
			return false;
		}
		boolean isArchive = false;
		InputStream input = null;
		try {
			input = new FileInputStream(file);
			byte[] buffer = new byte[4];
			int length = input.read(buffer, 0, 4);
			if (length == 4) {
				isArchive = (Arrays.equals(ZIP_HEADER_1, buffer)) || (Arrays.equals(ZIP_HEADER_2, buffer));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
					logger.error("方法isCompressedFile 发生错误: {}", e.getMessage());
				}
			}
		}
		return isArchive;
	}
}
