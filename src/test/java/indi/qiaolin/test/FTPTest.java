package indi.qiaolin.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPConnectionClosedException;
import org.apache.commons.net.ftp.FTPReply;
import org.junit.Before;
import org.junit.Test;

import indi.qiaolin.test.ureport.ftp.FTPClientFactory;
import indi.qiaolin.test.ureport.ftp.FTPClientPool;
import lombok.extern.slf4j.Slf4j;

/**
 * FTP 普通连接性能 与 连接池测试性能 测试; 以目录下1000个文件测试上传性能。
 * 
 * @author qiaolin
 *
 */

@Slf4j
public class FTPTest {
	private File[] files;

	@Before
	public void init() {
		File file = new File("E:/下载专用/test-fpt");
		files = file.listFiles();
	}

	/**
	 * 测试普通的连接
	 * 
	 * @throws IOException
	 */
	@Test
	public void testFtpClientGeneral() throws IOException {
		long start = System.currentTimeMillis();
		FTPClient ftpClient = new FTPClient();
		ftpClient.setControlEncoding("UTF-8");
		ftpClient.connect("192.168.56.1", 21);
		int replyCode = ftpClient.getReplyCode();
		if (!FTPReply.isPositiveCompletion(replyCode)) {
			ftpClient.disconnect();
			log.warn("FTPServer 拒绝连接 !");
		}
		FTPClientConfig conf = new FTPClientConfig(FTPClientConfig.SYST_NT);
		conf.setServerLanguageCode("zh");
		ftpClient.configure(conf);
		boolean login = ftpClient.login("Administrator", "Liqiaolin");
		if (!login) {
			throw new FTPConnectionClosedException("FTPServer 登录失败！");
		}
		for (File file : files) {
			try {
				String name = file.getName();
				name = new String(name.getBytes("GBK"), "iso-8859-1");
				ftpClient.storeFile(name, new FileInputStream(file));
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
		try {
			if(ftpClient != null && ftpClient.isConnected()){
				ftpClient.logout();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			ftpClient.disconnect();
		}
		System.out.println("文件写入完毕！");
		
		long end = System.currentTimeMillis();
		System.out.println("用时：" + (end - start));

	}

	/**
	 * 测试FTP连接池
	 */
	@Test
	public void testFtpClientPool() {
		long start = System.currentTimeMillis();
		FTPClientFactory factory = new FTPClientFactory();
		factory.setHostname("192.168.56.1");
		factory.setPort(21);
		factory.setUsername("Administrator");
		factory.setPassword("Liqiaolin");
		factory.setEncoding("UTF-8");
		FTPClientPool ftpClientPool = new FTPClientPool(factory);
		for (File file : files) {
			try {
				FTPClient ftpClient = ftpClientPool.borrowObject();
				String name = file.getName();
				name = new String(name.getBytes("GBK"), "iso-8859-1");
				ftpClient.storeFile(name, new FileInputStream(file));
				ftpClientPool.returnObject(ftpClient);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println("文件写入完毕！");
		try {
			ftpClientPool.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		long end = System.currentTimeMillis();
		System.out.println("用时：" + (end - start));
	}
}
