/*
 * frxs Inc.  湖南兴盛优选电子商务有限公司.
 * Copyright (c) 2017-2019. All Rights Reserved.
 */
package indi.qiaolin.test.ureport.config;

import indi.qiaolin.test.ureport.ftp.FTPClientFactory;
import indi.qiaolin.test.ureport.ftp.FTPClientPool;
import indi.qiaolin.test.ureport.ftp.FTPClientUtils;
import indi.qiaolin.test.ureport.properties.FTPProperties;
import indi.qiaolin.test.ureport.provider.FTPProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *  提供这配置
 * @author qiaolin
 * @version $Id: ProviderConfig.java,v 0.1 2019年04月09日 14:49 $Exp
 */

@Configuration
public class ProviderConfig {





    /**
     *  FTP 配置
     */
    @Configuration
    @ConditionalOnProperty(prefix = "ftp.factory", name = "enable", havingValue = "true")
    @EnableConfigurationProperties(FTPProperties.class)
    public static class FTPConfig{

        @Bean
        public FTPClientFactory ftpClientFactory(){
            return new FTPClientFactory();
        }


        @Bean
        public FTPClientPool ftpClientPool(){
            return new FTPClientPool(ftpClientFactory());
        }

        @Bean
        public FTPClientUtils ftpClientUtils(){
            return new FTPClientUtils();
        }

        @Bean
        @ConfigurationProperties(prefix = "ureport.ftp.provider")
        public FTPProvider ftpProvider(){
            return new FTPProvider();
        }

    }


}