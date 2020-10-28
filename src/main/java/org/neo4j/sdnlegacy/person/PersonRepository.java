package org.neo4j.sdnlegacy.person;

import reactor.core.publisher.Flux;

import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;

/**
 * @author Gerrit Meier
 */
public interface PersonRepository extends ReactiveNeo4jRepository<Person, Long> {

	Flux<Person> findByReviewedMoviesMovieEntityTitle(String reviewedMovieTitle);

	Flux<Person> findByDirectedMoviesTitle(String directedMovieTitle);
}
