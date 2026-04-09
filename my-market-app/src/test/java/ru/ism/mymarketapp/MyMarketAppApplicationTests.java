package ru.ism.mymarketapp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.r2dbc.autoconfigure.R2dbcConnectionDetails;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
class MyMarketAppApplicationTests {

	@Container
	@ServiceConnection(type = {R2dbcConnectionDetails.class})
	static PostgreSQLContainer<?> postgreSQLContainer =
			new PostgreSQLContainer<>("postgres:15");


	@Test
	void contextLoads() {
	}

}
