package com.xw.service;

import java.io.IOException;
import com.xw.entity.UpdateDataEntity;

public interface CompareMd5Service {
	//比较md5
	public String CompareMd5(UpdateDataEntity updateData) throws IOException;
	
}
