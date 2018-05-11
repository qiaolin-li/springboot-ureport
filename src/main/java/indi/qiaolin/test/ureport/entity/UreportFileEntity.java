package indi.qiaolin.test.ureport.entity;

import java.util.Date;

import lombok.Data;

/**
 *  Ureport文件 实体类
 * @author qiaolin
 * @version 2018年5月9日
 *
 */

@Data
public class UreportFileEntity {
	
	private Long id;
	private String name;
	private byte[] content;
	private Date createTime;
	private Date updateTime;
	
}
