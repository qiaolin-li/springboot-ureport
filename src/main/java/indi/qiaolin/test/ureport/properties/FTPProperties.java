/*
 * frxs Inc.  湖南兴盛优选电子商务有限公司.
 * Copyright (c) 2017-2019. All Rights Reserved.
 */
package indi.qiaolin.test.ureport.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author qiaolin
 * @version $Id: FTPProperties.java,v 0.1 2019年04月09日 14:59 $Exp
 */

@Data
@ConfigurationProperties("ftp.factory")
public class FTPProperties {

    /**
     *  是否开启
     */
    private boolean enable;

    /**
     *  地址
     */
    private String hostname;

    /**
     *  端口
     */
    private int port;

    /**
     *  用户名
     */
    private String username;

    /**
     *  密码
     */
    private String password;

    /**
     *  被动模式
     */
    private boolean passiveMode;

    /**
     *  编码
     */
    private String encoding = "UTF-8";

    /**
     *  客户端超时毫秒数
     */
    private int clientTimeout;

    /**
     *  线程数
     */
    private int threadNum;

    /**
     *  文件上传形式，默认二进制; 参考FTP类中常量
     */
    private int fileType = 2;

    /**
     *  是否重命名上传
     */
    private boolean renameUploaded;

    /**
     *  重试次数
     */
    private int retryTimes;

}