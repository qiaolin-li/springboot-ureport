package indi.qiaolin.test.ureport.ftp;

import java.io.IOException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPConnectionClosedException;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.pool.PoolableObjectFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import indi.qiaolin.test.ureport.exception.ConnectionPoolException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * FTPClient 工厂类， 提供FTPClient实例的创建、销毁、验证工作
 * @author qiaolin
 * @version 2018年5月9日
 *
 */

@Data
@Slf4j
@Component
@ConfigurationProperties("ftp.factory")
public class FTPClientFactory implements PoolableObjectFactory<FTPClient>{
	private String hostname; // 地址
	private int port; // 端口
	private String username; // 用户名
	private String password; // 密码
	private boolean passiveMode; // 被动模式
	private String encoding = "UTF-8"; // 文件编码
	private int clientTimeout; // 客户端超时毫秒数
	private int threadNum; // 线程数
	private int fileType = 2; // 文件上传形式，默认二进制; 参考FTP类中常量  
	private boolean renameUploaded;
	private int retryTimes;
	
	/**
	 * 创建一个FTPClient对象
	 */
	@Override
	public FTPClient makeObject() throws Exception {
		FTPClient ftpClient = new FTPClient();
//		ftpClient.setConnectTimeout(clientTimeout);
		ftpClient.connect(hostname, port);
		int replyCode = ftpClient.getReplyCode();
		if(!FTPReply.isPositiveCompletion(replyCode)){
			ftpClient.disconnect();
			log.warn("FTPServer 拒绝连接 !");
			return null;
		}
		boolean login = ftpClient.login(username, password);
		if(!login){
			throw new FTPConnectionClosedException("FTPServer 登录失败！");
		}
//		ftpClient.setControlEncoding("UTF-8");
		FTPClientConfig conf = new FTPClientConfig(FTPClientConfig.SYST_NT);
		conf.setServerLanguageCode("zh");
		ftpClient.configure(conf);
		//		ftpClient.setFileType(fileType);
//		ftpClient.setBufferSize(1024);
//		ftpClient.setControlEncoding(encoding);
//		if (passiveMode) {
//           ftpClient.enterLocalPassiveMode();
//        }

		return ftpClient;
	}

	/**
	 * 销毁一个FTPClient对象
	 */
	@Override
	public void destroyObject(FTPClient ftpClient) throws Exception {
		try {
			if(ftpClient != null && ftpClient.isConnected()){
				ftpClient.logout();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			ftpClient.disconnect();
		}
	}

	/**
	 * 验证一个FTPClient对象是否可用
	 */
	@Override
	public boolean validateObject(FTPClient ftpClient) {
		try {
			return ftpClient.sendNoOp();
		} catch (IOException e) {
			e.printStackTrace();
			throw new ConnectionPoolException("验证FTPClient 对象失败！");
		}
	}
	

	@Override
	public void activateObject(FTPClient obj) throws Exception {
		
	}

	@Override
	public void passivateObject(FTPClient obj) throws Exception {
		
	}
}
