package org.neo4j.sdnlegacy.person;

import org.springframework.data.neo4j.repository.query.Query;
import reactor.core.publisher.Flux;

import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;

/**
 * @author Gerrit Meier
 */
public interface PersonRepository extends ReactiveNeo4jRepository<Person, Long> {

	Flux<Person> findByReviewedMoviesMovieEntityTitle(String reviewedMovieTitle);

	Flux<Person> findByDirectedMoviesTitle(String directedMovieTitle);

	@Query("MATCH (m:Movie {title: $title})-[r:ACTED_IN]-(p:Person) RETURN m, r, p ORDER BY p.name")
	Flux<ActedInMovieProjection> findByActedInMovieTitle(String title);
}
