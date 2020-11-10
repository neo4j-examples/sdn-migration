package org.neo4j.sdnlegacy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.neo4j.sdnlegacy.movie.Actor;
import org.neo4j.sdnlegacy.movie.MovieEntity;
import org.neo4j.sdnlegacy.movie.MovieRepository;
import org.neo4j.sdnlegacy.person.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.neo4j.DataNeo4jTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.Neo4jContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataNeo4jTest
class SdnLegacyApplicationTests {

	@Autowired
	private MovieRepository movieRepository;

	@Autowired
	private PersonRepository personRepository;

	@Autowired
	private SessionFactory sessionFactory;

	@Container
	private static final Neo4jContainer<?> neo4jContainer = new Neo4jContainer<>("neo4j:3.5");

	@DynamicPropertySource
	static void neo4jProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.data.neo4j.uri", neo4jContainer::getBoltUrl);
		registry.add("spring.data.neo4j.username", () -> "neo4j");
		registry.add("spring.data.neo4j.password", neo4jContainer::getAdminPassword);
	}

	@BeforeEach
	void setup() throws IOException {
		try (BufferedReader moviesReader = new BufferedReader(
				new InputStreamReader(this.getClass().getResourceAsStream("/movies.cypher")))) {
			Session session = sessionFactory.openSession();
			session.query("MATCH (n) DETACH DELETE n", emptyMap());
			String moviesCypher = moviesReader.lines().collect(Collectors.joining(" "));
			session.query(moviesCypher, emptyMap());
			session.query("MATCH (m:Movie) SET m.`version` = 0", emptyMap());
		}
	}

	@Nested
	@DisplayName("Movie Repository")
	class MovieRepositoryTests {

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

			MovieEntity loadedMovie = sessionFactory.openSession().load(MovieEntity.class, "MyMovie");
			assertThat(loadedMovie).isNotNull();
		}

		@Test
		void deleteMovie() {
			long count = movieRepository.count();
			movieRepository.deleteById("The Matrix");

			assertThat(movieRepository.count()).isEqualTo(count - 1);
		}
	}

	@Nested
	@DisplayName("Person Repository")
	class PersonRepositoryTests {

		@Test
		void findPersonsWhoReviewedCertainMovie() {
			assertThat(personRepository.findByReviewedMoviesMovieNodeTitle("The Da Vinci Code")).hasSize(2);
		}

		@Test
		void findPersonsWhoDirectedCertainMovie() {
			assertThat(personRepository.findByDirectedMoviesTitle("The Da Vinci Code").get(0).getName())
					.isEqualTo("Ron Howard");
		}
	}
}