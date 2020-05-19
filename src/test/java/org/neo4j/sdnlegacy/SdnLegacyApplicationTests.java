package org.neo4j.sdnlegacy;

import static java.util.Collections.*;
import static org.assertj.core.api.Assertions.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

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
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SdnLegacyApplicationTests {

	@Autowired
	private MovieRepository movieRepository;

	@Autowired
	private PersonRepository personRepository;

	@Autowired
	private SessionFactory sessionFactory;

	@Nested
	@DisplayName("Movie Repository")
	class MovieRepositoryTests {

		@BeforeEach
		void setup() throws IOException {
			try (BufferedReader moviesReader = new BufferedReader(
					new InputStreamReader(this.getClass().getResourceAsStream("/movies.cypher")))) {
				Session session = sessionFactory.openSession();
				session.query("MATCH (n) DETACH DELETE n", emptyMap());
				String moviesCypher = moviesReader.lines().collect(Collectors.joining(" "));
				session.query(moviesCypher, emptyMap());
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

			MovieEntity loadedMovie = sessionFactory.openSession().load(MovieEntity.class, "MyMovie");
			assertThat(loadedMovie).isNotNull();
		}

		@Test
		void deleteMovie() {
			long count = movieRepository.count();
			// breaks 🔥 movieRepository.deleteById("The Matrix");
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