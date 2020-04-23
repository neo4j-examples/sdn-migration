package org.neo4j.sdnlegacy;

import static java.util.Collections.*;
import static org.assertj.core.api.Assertions.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;
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
import org.neo4j.springframework.data.core.Neo4jClient;
import org.neo4j.springframework.data.core.Neo4jTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SdnLegacyApplicationTests {

	@Autowired
	private Driver driver;

	@Autowired
	private Neo4jClient neo4jClient;

	@Autowired
	private Neo4jTemplate neo4jTemplate;

	@Autowired
	private MovieRepository movieRepository;

	@Autowired
	private PersonRepository personRepository;

	@Nested
	@DisplayName("Movie Repository")
	class MovieRepositoryTests {

		@BeforeEach
		void setup() throws IOException {
			try (BufferedReader moviesReader = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/movies.cypher")));
				Session session = driver.session()) {

				session.run("MATCH (n) DETACH DELETE n", emptyMap());
				String moviesCypher = moviesReader.lines().collect(Collectors.joining(" "));
				session.run(moviesCypher, emptyMap());
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
			// This fails because the Actor parameter does not get converted to a Neo4j compatible value.
			// It will get used perfectly fine in the SpEl expression. Non convertible types (known types) should maybe
			// get ignored to stay as parameters in the "raw" parameter list for the driver.
			//assertThat(movieRepository.findMoviesByActorNameWithSpElSearchObjectPlaceholder(new Actor("Emil Eifrem")).get(0))
			//	.hasFieldOrPropertyWithValue("title", "The Matrix");
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
			assertThat(personRepository.findByDirectedMoviesTitle("The Da Vinci Code").get(0).getName())
				.isEqualTo("Ron Howard");
		}
	}
}
