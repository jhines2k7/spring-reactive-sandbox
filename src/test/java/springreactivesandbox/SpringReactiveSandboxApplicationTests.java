package springreactivesandbox;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;

@Slf4j
class SpringReactiveSandboxApplicationTests {

	@Test
	void basicFluxFactory() {
		Flux<String> names = Flux.just("Tab", "Casey", "Rivkah", "Avi");

		names.subscribe(System.out::println);
	}

	@Test
	void fluxWithGenerate() {
		Flux<String> flux = Flux.generate(
				() -> 0,
				(state, sink) -> {
					sink.next("3 x " + state + " = " + 3 * state);

					if(state == 10) sink.complete();

					return state + 1;
				});

		flux.subscribe(System.out::println);
	}

	interface MyEventListener<T> {
		void onDataChunk(List<T> chunk);
		void processComplete();
	}

	@Test
	public void fluxWithCreate() throws InterruptedException {
		// Create a Somple Async API
		// Defining it here so that test can run independently
		class EventProcessor {
			public MyEventListener l;

			public void register(MyEventListener listener) {
				this.l = listener;
			}

			public void process() throws InterruptedException {
				Arrays.asList(1, 2, 3, 4, 5)
						.forEach(i -> {
							try {
								Thread.sleep(500);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}

							l.onDataChunk(Arrays.asList(i.toString()));
						});

				Thread.sleep(2000);

				l.processComplete();
			}
		}

		EventProcessor processor = new EventProcessor();

		Flux<String> bridge = Flux.create(sink -> {
			processor.register(
					new MyEventListener<String>() {
						@Override
						public void onDataChunk(List<String> chunk) {
							for(String s : chunk) {
								sink.next(s);
							}
						}

						@Override
						public void processComplete() {
							sink.complete();
						}
					}
			);
		});

		bridge.subscribe(
				System.out::println,
				null,
				() -> { System.out.println("Complete Done"); });

		// Need to trigger else nothing will happen
		processor.process();
	}
}
