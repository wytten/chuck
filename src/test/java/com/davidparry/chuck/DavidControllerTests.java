package com.davidparry.chuck;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.reactive.function.client.WebClient;

import com.sun.net.httpserver.HttpServer;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class DavidControllerTests {

	private static final String JOKE = "{\"value\":\"Chuck Norris can unit test an entire application with a single assert.\"}";

	private static HttpServer stubApi;

	@Value("${local.server.port}")
	int port;

	@DynamicPropertySource
	static void stubChuckApi(DynamicPropertyRegistry registry) throws IOException {
		stubApi = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
		stubApi.createContext("/jokes/random", exchange -> {
			byte[] body = JOKE.getBytes(StandardCharsets.UTF_8);
			exchange.getResponseHeaders().add("Content-Type", "application/json");
			exchange.sendResponseHeaders(200, body.length);
			exchange.getResponseBody().write(body);
			exchange.close();
		});
		stubApi.start();
		registry.add("chuck.api.url", () -> "http://localhost:" + stubApi.getAddress().getPort() + "/jokes/random");
	}

	@AfterAll
	static void stopStubApi() {
		stubApi.stop(0);
	}

	private Quote getQuote(String uri) {
		return WebClient.create("http://localhost:" + port).get().uri(uri)
				.retrieve().bodyToMono(Quote.class).block();
	}

	@Test
	void quoteReturnsJoke() {
		assertThat(getQuote("/quote").value())
				.isEqualTo("Chuck Norris can unit test an entire application with a single assert.");
	}

	@Test
	void quoteSubstitutesActor() {
		assertThat(getQuote("/quote?actor=Dale").value())
				.isEqualTo("Dale can unit test an entire application with a single assert.");
	}

}
