package indi.qiaolin.test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPConnectionClosedException;
import org.apache.commons.net.ftp.FTPReply;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

import com.itextpdf.text.log.SysoCounter;

import indi.qiaolin.test.ureport.entity.UreportFileEntity;
import indi.qiaolin.test.ureport.ftp.FTPClientPool;
import indi.qiaolin.test.ureport.mapper.UreportFileMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestCase {

	@Autowired(required = false)
	private UreportFileMapper mapper;
	
	@Autowired
	private FTPClientPool ftpClientPool;
	
    @Test
    public void test(){
    	Assert.notNull(mapper, "报表文件Mapper为空！");
    	int checkExistByName = mapper.checkExistByName("submit.ureport.xml");
    	log.info("查询到{}个！", checkExistByName);
    	
    	List<UreportFileEntity> list = mapper.queryReportFileList();
    	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	for (UreportFileEntity entity : list) {
			log.info("名称：{}, 内容{}, 创建时间{}", entity.getId(), new String(entity.getContent()), format.format(entity.getCreateTime()));
		}
    }
    
    
    @Test
    public void test2() throws Exception{
    	long start = System.currentTimeMillis();
    	File file = new File("E:/下载专用/test-fpt");
    	File[] listFiles = file.listFiles();
//    	int index = 1;
    	for (File file2 : listFiles) {
    		FileInputStream in = new FileInputStream(file2);
    		FTPClient ftpClient = ftpClientPool.borrowObject();
//    		borrowObject.changeWorkingDirectory("/test");  
    		String name = file2.getName();
    		name = new String(name.getBytes("GBK"),"iso-8859-1");
    		boolean storeFile = ftpClient.storeFile(name, in);   
    		System.out.println(storeFile);
    		ftpClientPool.returnObject(ftpClient);
//    		index++;
//    		System.out.println(index);
    	}
    	long end = System.currentTimeMillis();
    	System.out.println("ok -> " + (end - start) );
    	
    }
    
    
	/**
	 * Description: 向FTP服务器上传文件
	 * @Version1.0 Jul 27, 2008 4:31:09 PM by 崔红保（cuihongbao@d-heaven.com）创建
	 * @param url FTP服务器hostname
	 * @param port FTP服务器端口
	 * @param username FTP登录账号
	 * @param password FTP登录密码
	 * @param path FTP服务器保存目录
	 * @param filename 上传到FTP服务器上的文件名
	 * @param input 输入流
	 * @return 成功返回true，否则返回false
	 */
	public static boolean uploadFile(String url,int port,String username, String password, String path, String filename, InputStream input) {
		boolean success = false;
		FTPClient ftp = new FTPClient();
		try {
			int reply;
			ftp.connect(url, port);//连接FTP服务器
			//如果采用默认端口，可以使用ftp.connect(url)的方式直接连接FTP服务器
			ftp.login(username, password);//登录
			reply = ftp.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				ftp.disconnect();
				return success;
			}
			ftp.changeWorkingDirectory(path);
			ftp.storeFile(filename, input);			
			
			input.close();
			ftp.logout();
			success = true;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (ftp.isConnected()) {
				try {
					ftp.disconnect();
				} catch (IOException ioe) {
				}
			}
		}
		return success;
	}
	
	public void testUpLoadFromString(){
		try {
			InputStream input = new ByteArrayInputStream("test ftp".getBytes("utf-8"));
			boolean flag = uploadFile("192.168.56.1", 21, "Administrator", "Liqiaolin", "adda/", "aa/test.txt", input);
			System.out.println(flag);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		new TestCase().testUpLoadFromString();
	}
   
}
