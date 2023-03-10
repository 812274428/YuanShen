package com.xw.test;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CoreMd5ApplicationTest {
	
	@Test
	void Md5() throws IOException {
		String dateString = LocalDateTime.parse("2023-03-08").format(DateTimeFormatter.ISO_DATE_TIME);
		System.out.println(dateString);
	}
}
		
