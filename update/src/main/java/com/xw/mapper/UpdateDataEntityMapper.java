package com.xw.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.xw.entity.UpdateDataEntity;

@Mapper
public interface UpdateDataEntityMapper {
	
	int insert(UpdateDataEntity updateDataEntity);
	
	UpdateDataEntity getUpdateDataEntity(@Param("recordName") String recordName);
}
