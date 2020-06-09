package org.neo4j.sdnlegacy.person;

import reactor.core.publisher.Flux;
import org.neo4j.springframework.data.repository.ReactiveNeo4jRepository;

/**
 * @author Gerrit Meier
 */
public interface PersonRepository extends ReactiveNeo4jRepository<Person, Long> {

//	List<Person> findByReviewedMoviesMovieNodeTitle(String reviewedMovieTitle);

	Flux<Person> findByDirectedMoviesTitle(String directedMovieTitle);
}
