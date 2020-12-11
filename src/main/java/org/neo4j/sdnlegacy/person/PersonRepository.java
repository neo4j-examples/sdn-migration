package org.neo4j.sdnlegacy.person;

import java.util.List;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;

/**
 * @author Gerrit Meier
 */
public interface PersonRepository extends Neo4jRepository<Person, Long> {

	List<Person> findByReviewedMoviesMovieNodeTitle(String reviewedMovieTitle);

	List<Person> findByDirectedMoviesTitle(String directedMovieTitle);

	@Query("MATCH (:Movie {title: $title})<-[:ACTED_IN]-(p:Person) RETURN p.name AS name, p.born AS born ORDER BY p.name")
	List<ActedInMovieProjection> findByActedInMovieTitle(String title);

	@Query("MATCH (p:Person{name:'Tom Hanks'})-[r:ACTED_IN]->(m:Movie) return p,r,m")
	List<Person> tomHanksCareer();
}