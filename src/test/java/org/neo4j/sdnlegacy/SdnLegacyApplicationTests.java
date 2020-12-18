package org.neo4j.sdnlegacy;

import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.neo4j.sdnlegacy.movie.MovieEntity;
import org.neo4j.sdnlegacy.movie.MovieRepository;
import org.neo4j.sdnlegacy.person.ActedInMovieProjection;
import org.neo4j.sdnlegacy.person.Person;
import org.neo4j.sdnlegacy.person.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.neo4j.DataNeo4jTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.neo4j.core.ReactiveDatabaseSelectionProvider;
import org.springframework.data.neo4j.core.ReactiveNeo4jTemplate;
import org.springframework.data.neo4j.core.transaction.ReactiveNeo4jTransactionManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.Neo4jContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataNeo4jTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@ContextConfiguration(classes = SdnLegacyApplicationTests.Configuration.class)
class SdnLegacyApplicationTests {

	@Autowired
	private Driver driver;

	@Container
	private static final Neo4jContainer<?> neo4jContainer = new Neo4jContainer<>("neo4j:4.0");

	@DynamicPropertySource
	static void neo4jProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.neo4j.uri", neo4jContainer::getBoltUrl);
		registry.add("spring.neo4j.authentication.username", () -> "neo4j");
		registry.add("spring.neo4j.authentication.password", neo4jContainer::getAdminPassword);
	}

	@BeforeEach
	void setup() throws IOException {
		try (BufferedReader moviesReader = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/movies.cypher")));
			Session session = driver.session()) {

			session.writeTransaction(tx -> {
				String moviesCypher = moviesReader.lines().collect(Collectors.joining(" "));

				tx.run("MATCH (n) DETACH DELETE n");
				tx.run(moviesCypher);
				return null;
			});
		}
	}

	@Nested
	@DisplayName("Movie Repository")
	class MovieRepositoryTests {

		@Autowired
		private MovieRepository movieRepository;

		@Test
		void findsAllMovies() {
			int expectedMovieCount = 38;
			StepVerifier.create(movieRepository.findAll())
					.expectNextCount(expectedMovieCount)
					.verifyComplete();
		}

		@Test
		void findsMovieByTitle() {
			StepVerifier.create(movieRepository.findByTitle("The Matrix")).assertNext(movieEntity -> {
				assertThat(movieEntity.getYearOfRelease()).isEqualTo(1999);
			}).verifyComplete();
		}

		@Test
		void findMoviesByActorsCypherPlaceholder() {
			StepVerifier.create(movieRepository.findMoviesByActorNameWithCypherPlaceholder("Emil Eifrem")).assertNext(movieEntity -> {
				assertThat(movieEntity.getTitle()).isEqualTo("The Matrix");
			}).verifyComplete();
		}

		@Test
		void findMoviesByActorsSpElIndexPlaceholder() {
			StepVerifier.create(movieRepository.findMoviesByActorNameWithCypherPlaceholder("Emil Eifrem")).assertNext(movieEntity -> {
				assertThat(movieEntity.getTitle()).isEqualTo("The Matrix");
			}).verifyComplete();
		}

		@Test
		void findMoviesByActorsSpElIndexColonPlaceholder() {
			StepVerifier.create(movieRepository.findMoviesByActorNameWithCypherPlaceholder("Emil Eifrem")).assertNext(movieEntity -> {
				assertThat(movieEntity.getTitle()).isEqualTo("The Matrix");
			}).verifyComplete();
		}

		@Test
		void findMoviesByActorsSpElNamedPlaceholder() {
			StepVerifier.create(movieRepository.findMoviesByActorNameWithCypherPlaceholder("Emil Eifrem")).assertNext(movieEntity -> {
				assertThat(movieEntity.getTitle()).isEqualTo("The Matrix");
			}).verifyComplete();
		}

		@Test
		void findMoviesByActorsSpElSearchObjectPlaceholder() {
			StepVerifier.create(movieRepository.findMoviesByActorNameWithCypherPlaceholder("Emil Eifrem")).assertNext(movieEntity -> {
				assertThat(movieEntity.getTitle()).isEqualTo("The Matrix");
			}).verifyComplete();
		}

		@Test
		void persistMovie(@Autowired ReactiveNeo4jTemplate neo4jTemplate) {
			MovieEntity entity = new MovieEntity("MyMovie", "best catchy tagline ever", 2020);

			StepVerifier.create(
					movieRepository.save(entity)
							.then(neo4jTemplate.findById("MyMovie", MovieEntity.class)))
					.expectNextCount(1)
					.verifyComplete();
		}

		@Test
		void deleteMovie() {
			Mono<Tuple2<Long, Long>> countPublisher = movieRepository.count()
					.flatMap(oldCount -> movieRepository.deleteById("The Matrix")
							.then(movieRepository.count())
							.map(newCount -> Tuples.of(oldCount, newCount)));

			StepVerifier.create(countPublisher)
					.assertNext(counts -> {
						final Long oldCount = counts.getT1();
						final Long newCount = counts.getT2();
						assertThat(newCount).isEqualTo(oldCount - 1);
					})
					.verifyComplete();
		}
	}


	@Nested
	@DisplayName("Person Repository")
	class PersonRepositoryTests {

		@Autowired
		private PersonRepository personRepository;

		@Test
		void findPersonsWhoReviewedCertainMovie() {
			StepVerifier.create(personRepository.findByReviewedMoviesMovieEntityTitle("The Da Vinci Code"))
					.expectNextCount(2)
					.verifyComplete();
		}

		@Test
		void findPersonsWhoDirectedCertainMovie() {
			StepVerifier.create(personRepository.findByDirectedMoviesTitle("The Da Vinci Code")).assertNext(personEntity -> {
				assertThat(personEntity.getName()).isEqualTo("Ron Howard");
			}).verifyComplete();
		}

		@Test
		void findPersonsWhoActedInCertainMovie() {
			StepVerifier.create(personRepository.findByActedInMovieTitle("The Da Vinci Code").buffer(4))
					.assertNext(results -> assertThat(results)
							.extracting(ActedInMovieProjection::getBorn, ActedInMovieProjection::getName)
							.containsExactly(
									actorInMovie(1976, "Audrey Tautou"),
									actorInMovie(1939, "Ian McKellen"),
									actorInMovie(1971, "Paul Bettany"),
									actorInMovie(1956, "Tom Hanks")
							))
					.verifyComplete();
		}

		@Test
		void tomHanksCareer() {
			int expectedCount = 12;
			StepVerifier.create(personRepository.tomHanksCareer().buffer(expectedCount))
					.assertNext(results -> {
						assertThat(results)
								.overridingErrorMessage("Expected Spring Data Neo4j 6 to return as many rows as distinct patterns")
								.hasSize(expectedCount)
								.extracting(Person::getName)
								.containsOnly("Tom Hanks");
						Set<Integer> hashCodes = collectIdentityHashCodes(results);
						assertThat(hashCodes)
								.overridingErrorMessage("Expected Spring Data Neo4j 6.0.2+ to deduplicate instances")
								.hasSize(1)
								.containsOnly(hashCodes.iterator().next());
					})
					.verifyComplete();
		}

		private Set<Integer> collectIdentityHashCodes(List<Person> results) {
			return results.stream().map(System::identityHashCode).collect(Collectors.toSet());
		}

		private Tuple actorInMovie(int born, String name) {
			return Tuple.tuple(born, name);
		}
	}

	@TestConfiguration(proxyBeanMethods = false)
	static class Configuration {

		@Bean
		public ReactiveNeo4jTransactionManager reactiveTransactionManager(Driver driver,
			ReactiveDatabaseSelectionProvider databaseSelectionProvider) {

			return new ReactiveNeo4jTransactionManager(driver, databaseSelectionProvider);
		}

	}
}
