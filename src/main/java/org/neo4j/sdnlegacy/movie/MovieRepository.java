package org.neo4j.sdnlegacy.movie;

import org.neo4j.sdnlegacy.person.Actor;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.List;

/**
 * @author Gerrit Meier
 */
public interface MovieRepository extends Neo4jRepository<MovieEntity, String> {

	MovieEntity findByTitle(String movieTitle);

	@Query("MATCH (m:Movie)<-[:ACTED_IN]-(p:Person) WHERE p.name = $actorName RETURN m")
	List<MovieEntity> findMoviesByActorNameWithCypherPlaceholder(String actorName);

	@Query("MATCH (m:Movie)<-[:ACTED_IN]-(p:Person) WHERE p.name = ?#{[0]} RETURN m")
	List<MovieEntity> findMoviesByActorNameWithSpElIndexPlaceholder(String actorName);

	@Query("MATCH (m:Movie)<-[:ACTED_IN]-(p:Person) WHERE p.name = :#{[0]} RETURN m")
	List<MovieEntity> findMoviesByActorNameWithSpElIndexColonPlaceholder(String actorName);

	@Query("MATCH (m:Movie)<-[:ACTED_IN]-(p:Person) WHERE p.name = :#{#actorName} RETURN m")
	List<MovieEntity> findMoviesByActorNameWithSpElNamedPlaceholder(@Param("actorName") String actorName);

	@Query("MATCH (m:Movie)<-[:ACTED_IN]-(p:Person) WHERE p.name = :#{#actor.name} RETURN m")
	List<MovieEntity> findMoviesByActorNameWithSpElSearchObjectPlaceholder(@Param("actor") Actor actor);

}