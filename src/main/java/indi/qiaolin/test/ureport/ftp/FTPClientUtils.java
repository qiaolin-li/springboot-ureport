package indi.qiaolin.test.ureport.ftp;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * FTPClient 工具类，代理ftpClient得操作。
 * @author qiaolin
 * @version 2018年5月9日
 *
 */

@Slf4j
@Setter
@ConfigurationProperties(prefix = "ftp.utils")
@Component
public class FTPClientUtils {

	// FTP 客户端连接池
	@Autowired
	private FTPClientPool ftpClientPool;

	/**
	 *  获取全部的文件列表
	 * @param path 路径
	 * @return
	 */
	public List<FTPFile> getFileList(String path){
		List<FTPFile> list = new ArrayList<>();
		FTPClient ftpClient = borrowObject();
		try {
			FTPFile[] listFiles = ftpClient.listFiles(path);
			for (FTPFile ftpFile : listFiles) {
				byte[] bytes = ftpFile.getName().getBytes("iso-8859-1");
				ftpFile.setName(new String(bytes, "GBK"));
				list.add(ftpFile);
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}finally {
			returnObject(ftpClient);
		}
		return  list;
	}
	
	/**
	 * FTP上传文件
	 * 
	 * @throws Exception
	 */
	public boolean uploadFile(String remote, InputStream local)   {
		FTPClient ftpClient = borrowObject();
		try {
			byte[] bytes = remote.getBytes("GBK");
			return ftpClient.storeFile(new String(bytes, "iso-8859-1"), local);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}finally {
			returnObject(ftpClient);
		}
	}
	
	/**
	 *  FTP下载文件到本地路径
	 * @param path ftp文件路径 
	 * @param locaPath 本地路径
	 * @return ftp文件流
	 * @throws FileNotFoundException
	 */
	public boolean downloadFile(String path, String locaPath) throws FileNotFoundException  {
		FileOutputStream fileOutputStream = new FileOutputStream(locaPath);
		return downloadFile(locaPath, fileOutputStream);
	}
	
	/**
	 * FTP下载文件
	 * @param path 文件路径 
	 * @throws IOException 
 	 */
	public InputStream downloadFile(String path)  {
		FTPClient ftpClient = borrowObject();
		try {
			byte[] bytes = path.getBytes("GBK");
			InputStream fileStream = ftpClient.retrieveFileStream(new String(bytes, "iso-8859-1"));
			log.info("文件 {} 下载成功！", path);
			return fileStream;
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}finally {
			returnObject(ftpClient);
		}
	}
	
	/**
	 *  FTP下载文件 到 输出流
	 * @param path ftp文件路径 
	 * @param ops  
	 * @return 是否写入成功
	 */
	public boolean downloadFile(String path, OutputStream ops)  {
		FTPClient ftpClient = borrowObject();
		try {
			boolean result = ftpClient.retrieveFile(path, ops);
			return result;
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}finally {
			returnObject(ftpClient);
		}
	}
	
	/**
	 * FTP删除文件
	 */
	public boolean delete(String path) {
		FTPClient ftpClient = borrowObject();
		try {
			boolean result = ftpClient.deleteFile(path);
			return result;
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}finally {
			returnObject(ftpClient);
		}
	}

	/**
	 * 修改 ftp 客户端文件名称
	 * @param from 原文件名
	 * @param to 新文件名
	 * @return 是否成功
	 */
	public boolean rename(String from, String to)  {
		FTPClient ftpClient = borrowObject();
		try {
			boolean result = ftpClient.rename(from, to);
			return result;
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}finally {
			returnObject(ftpClient);
		}
	}

	/**
	 *  获取FTP客户端
	 * @return
	 */
	private FTPClient borrowObject() {
		try {
			return ftpClientPool.borrowObject();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	/**
	 *  返还FTP客户端
	 * @param ftpClient FTP客户端
	 */
	private void returnObject(FTPClient ftpClient) {
		try {
			ftpClientPool.returnObject(ftpClient);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}