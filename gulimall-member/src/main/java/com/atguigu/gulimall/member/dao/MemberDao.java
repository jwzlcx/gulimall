package com.atguigu.gulimall.member.dao;

import com.atguigu.gulimall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author jzc
 * @email 2575939509@qq.com
 * @date 2025-03-16 15:51:57
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
