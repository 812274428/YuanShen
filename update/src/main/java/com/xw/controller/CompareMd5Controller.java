package com.xw.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.xw.entity.UpdateDataEntity;
import com.xw.service.CompareMd5Service;

@Component
@RestController
@RequestMapping("/compareMd5")
public class CompareMd5Controller {
	
	@Autowired
	CompareMd5Service compareMd5Service;
	
	@RequestMapping("/compareCurrentVersion")
	public String compareCurrentVersion(@RequestBody UpdateDataEntity updateDataEntity) throws IOException {
		String result =  compareMd5Service.CompareMd5(updateDataEntity);
		
		return result;
	}
	
	
}
