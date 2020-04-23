package org.neo4j.sdnlegacy.movie;

import java.util.List;

import org.neo4j.springframework.data.repository.Neo4jRepository;
import org.neo4j.springframework.data.repository.query.Query;
import org.springframework.data.repository.query.Param;

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
