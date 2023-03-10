package com.xw.service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipOutputStream;

import com.xw.entity.FileDataEntity;

public interface OperationService {

	//解压文件
	public boolean unZip(String unzipFile, String destDirPath) throws RuntimeException;
	//压缩文件夹
	public boolean zipDocuments(String zipPath, List<String> fileList) throws RuntimeException;
	//压缩单个文件
	public boolean zipDocuemnt(File sourceFile, ZipOutputStream zipOutputStream, String name) throws Exception;
	//复制文件夹
	public boolean copydir(String newFile, String oldPath) throws IOException;
	//复制单个文件
	public boolean copyfile(String newFile, String oldPath) throws IOException;
	//删除多个文件夹
	public boolean deleteAllFile(List<String> dirPath);
	//删除单个文件夹
	public boolean deleteFiles(String dirPath);
	//删除单个文件
	public boolean deleteFile(String FilePath);
	//循环创建文件夹
	public boolean createDir(String path);
	//获取所有路径下的所有文件，及文件的md5值
	public Map<String, FileDataEntity> getFiles(String path) throws IOException;
	//获取单个路径下的所有文件
	public List<File> getFileList(File filePath);
	//获取单个路径下当前文件及文件夹
	public List<File> getFile(File filePath);
	//找到根路径下的所有文件或文件夹名，放入list中
	public List<String> findFileList(String destDirPath);
	//解压文件夹中的压缩包
	public List<File> unzipFileList(File filePath);
	//判断文件是否为一个压缩文件
	public boolean isCompressedFile(File file);
}
