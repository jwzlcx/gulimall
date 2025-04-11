package com.atguigu.gulimall.product.entity;

import com.atguigu.common.valid.AddGroup;
import com.atguigu.common.valid.ListValue;
import com.atguigu.common.valid.UpdateGroup;
import com.atguigu.common.valid.UpdateStatusGroup;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import com.sun.xml.internal.bind.v2.TODO;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.*;

/**
 * 品牌
 * 
 * @author jzc
 * @email 2575939509@qq.com
 * @date 2025-03-16 16:45:59
 */
@Data
@TableName("pms_brand")
public class BrandEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 品牌id
	 */
	@NotNull(message = "修改时必须指定品牌",groups = {UpdateGroup.class})
	@Null(message = "新增时不能指定id",groups = {AddGroup.class})//是否为null,是null的话不报错，不是null报错
	@TableId
	private Long brandId;
	/**
	 * 品牌名
	 */
	@NotBlank(message="品牌名必须提交",groups = {AddGroup.class,UpdateGroup.class})
	private String name;
	/**
	 * 品牌logo地址
	 */
	@NotBlank(groups = {AddGroup.class})//
	@URL(message = "logo链接必须是合法地址",groups = {AddGroup.class,UpdateGroup.class})
	private String logo;
	/**
	 * 介绍
	 */

	private String descript;
	/**
	 * 显示状态[0-不显示；1-显示]
	 */
	@ListValue(vals={0,1},message = "只能是0和1",groups = {AddGroup.class, UpdateStatusGroup.class})
	private Integer showStatus;
	/**
	 * 检索首字母
	 */
	@NotEmpty(message = "首字母不能为空",groups = {AddGroup.class})
	@Pattern(regexp = "^[a-zA-Z]$",message = "首字母必须是一个字母",groups = {AddGroup.class,UpdateGroup.class})//判断是不是字母，不是直接告诉首字母必须是字母
	private String firstLetter;
	/**
	 * 排序
	 */
	@NotNull(groups = {AddGroup.class})
	@Min(value = 0, message = "排序必须是一个正整数" , groups = {AddGroup.class, UpdateGroup.class}) //判断是否小于0
	private Integer sort;

}
