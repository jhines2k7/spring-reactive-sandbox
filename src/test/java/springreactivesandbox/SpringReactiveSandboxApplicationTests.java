package springreactivesandbox;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

@Slf4j
class SpringReactiveSandboxApplicationTests {

	@Test
	void basicFluxFactory() {
		Flux<String> names = Flux.just("Tab", "Casey", "Rivkah");

		names.subscribe(System.out::println);
	}
}
