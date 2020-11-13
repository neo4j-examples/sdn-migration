package org.neo4j.sdnlegacy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.neo4j.sdnlegacy.movie.Actor;
import org.neo4j.sdnlegacy.movie.MovieEntity;
import org.neo4j.sdnlegacy.movie.MovieRepository;
import org.neo4j.sdnlegacy.person.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.neo4j.DataNeo4jTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.neo4j.core.DatabaseSelectionProvider;
import org.springframework.data.neo4j.core.Neo4jTemplate;
import org.springframework.data.neo4j.core.transaction.Neo4jTransactionManager;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.Neo4jContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataNeo4jTest
class SdnLegacyApplicationTests {

	@Autowired
	private Driver driver;

	@Autowired
	private Neo4jTemplate neo4jTemplate;

	@Autowired
	private MovieRepository movieRepository;

	@Autowired
	private PersonRepository personRepository;

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

	@Test
	void findsAllMovies() {
		assertThat(movieRepository.findAll()).hasSizeGreaterThan(30);
	}

	@Test
	void findsMovieByTitle() {
		assertThat(movieRepository.findByTitle("The Matrix")).hasFieldOrPropertyWithValue("title", "The Matrix");
	}

	@Test
	void findMoviesByActorsCypherPlaceholder() {
		assertThat(movieRepository.findMoviesByActorNameWithCypherPlaceholder("Emil Eifrem").get(0))
				.hasFieldOrPropertyWithValue("title", "The Matrix");
	}

	@Test
	void findMoviesByActorsSpElIndexPlaceholder() {
		assertThat(movieRepository.findMoviesByActorNameWithSpElIndexPlaceholder("Emil Eifrem").get(0))
				.hasFieldOrPropertyWithValue("title", "The Matrix");
	}

	@Test
	void findMoviesByActorsSpElIndexColonPlaceholder() {
		assertThat(movieRepository.findMoviesByActorNameWithSpElIndexColonPlaceholder("Emil Eifrem").get(0))
				.hasFieldOrPropertyWithValue("title", "The Matrix");
	}

	@Test
	void findMoviesByActorsSpElNamedPlaceholder() {
		assertThat(movieRepository.findMoviesByActorNameWithSpElNamedPlaceholder("Emil Eifrem").get(0))
				.hasFieldOrPropertyWithValue("title", "The Matrix");
	}

	@Test
	void findMoviesByActorsSpElSearchObjectPlaceholder() {
		assertThat(movieRepository.findMoviesByActorNameWithSpElSearchObjectPlaceholder(new Actor("Emil Eifrem")).get(0))
				.hasFieldOrPropertyWithValue("title", "The Matrix");
	}

	@Test
	void persistMovie() {
		MovieEntity entity = new MovieEntity("MyMovie", "best catchy tagline ever", 2020);
		movieRepository.save(entity);

		Optional<MovieEntity> loadedMovie = neo4jTemplate.findById("MyMovie", MovieEntity.class);
		assertThat(loadedMovie).isPresent();
	}

	@Test
	void deleteMovie() {
		long count = movieRepository.count();
		movieRepository.deleteById("The Matrix");
		assertThat(movieRepository.count()).isEqualTo(count - 1);
	}

	@Test
	@Disabled("Not supported in latest SDN because of limitations in Spring Data Commons and the usage of Map")
	void findPersonsWhoReviewedCertainMovie() {
		//assertThat(personRepository.findByReviewedMoviesMovieNodeTitle("The Da Vinci Code")).hasSize(2);
	}

	@Test
	void findPersonsWhoDirectedCertainMovie() {
		assertThat(personRepository.findByDirectedMoviesTitle("The Da Vinci Code").get(0).getName())
				.isEqualTo("Ron Howard");
	}

	@TestConfiguration(proxyBeanMethods = false)
	static class Configuration {

		@Bean
		public Neo4jTransactionManager transactionManager(Driver driver, DatabaseSelectionProvider databaseSelectionProvider) {
			return new Neo4jTransactionManager(driver, databaseSelectionProvider);
		}

	}
}