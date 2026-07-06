package com.easyjava.mappers;

import com.easyjava.entity.po.AuditConfig;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AuditConfigMapper extends BaseMapper<AuditConfig, Object> {
	List<AuditConfig> selectAll();
	AuditConfig selectByKey(String configKey);
	void updateConfigValue(AuditConfig config);
}
