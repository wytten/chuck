package com.davidparry.chuck;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/")
public class DavidController {

    private final WebClient client;

    public DavidController(@Value("${chuck.api.url}") String apiUrl) {
        this.client = WebClient.create(apiUrl);
    }

    @GetMapping("/quote")
    public Mono<Quote> quote(@RequestParam(name = "actor", required = false) String actor) {
        Mono<Quote> mono = client.get().retrieve().bodyToMono(Quote.class);
		return StringUtils.hasText(actor) ? mono.map(quote -> new Quote(quote.value().replace("Chuck Norris", actor))) : mono;
    }

}
