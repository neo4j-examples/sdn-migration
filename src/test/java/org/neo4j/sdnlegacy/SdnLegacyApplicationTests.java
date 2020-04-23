package org.neo4j.sdnlegacy;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.neo4j.sdnlegacy.movie.Actor;
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

	@Nested
	@DisplayName("Movie Repository")
	class MovieRepositoryTests {

		@Test
		void findsAllMovies() {
			assertThat(movieRepository.findAll()).hasSize(38);
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
