package com.sln.glassroom;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("local")	// also see: https://www.mkyong.com/spring-boot/spring-boot-profiles-example/
public class GlassRoomApplicationTests {

	@Test
	public void contextLoads() {
	}

}
