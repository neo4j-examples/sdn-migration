package org.neo4j.sdnlegacy.movie;

import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;
import org.neo4j.springframework.data.repository.ReactiveNeo4jRepository;
import org.neo4j.springframework.data.repository.query.Query;
import org.springframework.data.repository.query.Param;

/**
 * @author Gerrit Meier
 */
public interface MovieRepository extends ReactiveNeo4jRepository<MovieEntity, String> {

	Mono<MovieEntity> findByTitle(String movieTitle);

	@Query("MATCH (m:Movie)<-[:ACTED_IN]-(p:Person) WHERE p.name = $actorName RETURN m")
	Flux<MovieEntity> findMoviesByActorNameWithCypherPlaceholder(String actorName);

	@Query("MATCH (m:Movie)<-[:ACTED_IN]-(p:Person) WHERE p.name = ?#{[0]} RETURN m")
	Flux<MovieEntity> findMoviesByActorNameWithSpElIndexPlaceholder(String actorName);

	@Query("MATCH (m:Movie)<-[:ACTED_IN]-(p:Person) WHERE p.name = :#{[0]} RETURN m")
	Flux<MovieEntity> findMoviesByActorNameWithSpElIndexColonPlaceholder(String actorName);

	@Query("MATCH (m:Movie)<-[:ACTED_IN]-(p:Person) WHERE p.name = :#{#actorName} RETURN m")
	Flux<MovieEntity> findMoviesByActorNameWithSpElNamedPlaceholder(@Param("actorName") String actorName);

	@Query("MATCH (m:Movie)<-[:ACTED_IN]-(p:Person) WHERE p.name = :#{#actor.name} RETURN m")
	Flux<MovieEntity> findMoviesByActorNameWithSpElSearchObjectPlaceholder(@Param("actor") Actor actor);

}
