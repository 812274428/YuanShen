package com.xw.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.xw.entity.RecordEntity;

@Mapper
public interface RecordMapper {
	
	int insert(RecordEntity recordEntity);
	
	List<RecordEntity> getRecordEntityByRecord(@Param("recordName") String recordName);
	
	RecordEntity getRecordEntity(@Param("recordName") String recordName, @Param("refNo") String refNo, @Param("packageName") String packageName, @Param("fileName") String fileName, @Param("version") String version);
	
	void updateStatus(RecordEntity recordEntity);
}
