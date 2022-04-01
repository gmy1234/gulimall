package com.gmy.gulimall.search;

import org.elasticsearch.client.RestHighLevelClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GulimallSearchApplicationTests {

	@Autowired
	private RestHighLevelClient highLevelClient;

	@Test
	void contextLoads() {
		System.out.println(highLevelClient);
	}

}
