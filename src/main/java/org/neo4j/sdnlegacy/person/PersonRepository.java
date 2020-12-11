package org.neo4j.sdnlegacy.person;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.List;

/**
 * @author Gerrit Meier
 */
public interface PersonRepository extends Neo4jRepository<Person, Long> {

    //List<Person> findByReviewedMoviesMovieNodeTitle(String reviewedMovieTitle);

    List<Person> findByDirectedMoviesTitle(String directedMovieTitle);

    @Query("MATCH (m:Movie {title: $title})-[r:ACTED_IN]-(p:Person) RETURN m, r, p ORDER BY p.name")
    List<ActedInMovieProjection> findByActedInMovieTitle(String title);

    @Query("MATCH (p:Person{name:'Tom Hanks'})-[r:ACTED_IN]->(m:Movie) return p,r,m")
    List<Person> tomHanksCareer();
}