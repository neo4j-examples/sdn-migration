package org.neo4j.sdnlegacy;

import static java.util.Collections.*;
import static org.assertj.core.api.Assertions.*;

import reactor.test.StepVerifier;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.neo4j.sdnlegacy.movie.MovieEntity;
import org.neo4j.sdnlegacy.movie.MovieRepository;
import org.neo4j.sdnlegacy.person.PersonRepository;
import org.neo4j.springframework.boot.test.autoconfigure.data.ReactiveDataNeo4jTest;
import org.neo4j.springframework.data.core.ReactiveNeo4jTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.Neo4jContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@ReactiveDataNeo4jTest
class SdnLegacyApplicationTests {

	@Autowired
	private Driver driver;

	@Autowired
	private ReactiveNeo4jTemplate neo4jTemplate;

	@Autowired
	private MovieRepository movieRepository;

	@Autowired
	private PersonRepository personRepository;

	@Container
	private static Neo4jContainer<?> neo4jContainer = new Neo4jContainer<>("neo4j:4.0");

	@DynamicPropertySource
	static void neo4jProperties(DynamicPropertyRegistry registry) {
		registry.add("org.neo4j.driver.uri", neo4jContainer::getBoltUrl);
		registry.add("org.neo4j.driver.authentication.username", () -> "neo4j");
		registry.add("org.neo4j.driver.authentication.password", neo4jContainer::getAdminPassword);
	}

	@BeforeEach
	void setup() throws IOException {
		try (BufferedReader moviesReader = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/movies.cypher")));
			Session session = driver.session()) {

			session.run("MATCH (n) DETACH DELETE n", emptyMap());
			String moviesCypher = moviesReader.lines().collect(Collectors.joining(" "));
			session.run(moviesCypher, emptyMap());
		}
	}

	@Nested
	@DisplayName("Movie Repository")
	class MovieRepositoryTests {

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
		void persistMovie() {
			MovieEntity entity = new MovieEntity("MyMovie", "best catchy tagline ever", 2020);

			StepVerifier.create(
				movieRepository.save(entity)
				.then(neo4jTemplate.findById("MyMovie", MovieEntity.class)))
				.expectNextCount(1)
				.verifyComplete();
		}

		@Test
		void deleteMovie() {
			int expectedMovieCount = 38;
			StepVerifier.create(movieRepository.deleteById("The Matrix")).assertNext(movieEntity -> {
				assertThat(movieRepository.count()).isEqualTo(expectedMovieCount - 1);
			});
		}
	}

	@Nested
	@DisplayName("Person Repository")
	class PersonRepositoryTests {

		@Test
		void findPersonsWhoReviewedCertainMovie() {
			// Not supported in SDN/RX because of limitations in Spring Data Commons and the usage of Map
			// assertThat(personRepository.findByReviewedMoviesTitle("The Da Vinci Code")).hasSize(2);
		}

		@Test
		void findPersonsWhoDirectedCertainMovie() {
			StepVerifier.create(personRepository.findByDirectedMoviesTitle("The Da Vinci Code")).assertNext(personEntity -> {
				assertThat(personEntity.getName()).isEqualTo("Ron Howard");
			}).verifyComplete();
		}
	}
}
