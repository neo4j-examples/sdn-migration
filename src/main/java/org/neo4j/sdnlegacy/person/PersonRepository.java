package org.neo4j.sdnlegacy.person;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.List;

/**
 * @author Gerrit Meier
 */
public interface PersonRepository extends Neo4jRepository<Person, Long> {

    List<Person> findByReviewedMoviesMovieTitle(String reviewedMovieTitle);

    List<Person> findByDirectedMoviesTitle(String directedMovieTitle);

    @Query("MATCH (m:Movie {title: $title})-[r:ACTED_IN]-(p:Person) RETURN m, r, p ORDER BY p.name")
    List<ActedInMovieProjection> findByActedInMovieTitle(String title);

    // see the related test to understand the semantic difference between SDN 5/OGM and SDN 6
    // to keep SDN 5/OGM semantics, the query could become:
    // MATCH (p:Person{name:'Tom Hanks'})-[r:ACTED_IN]->(m:Movie) return p,COLLECT(r),COLLECT(m)
    @Query("MATCH (p:Person{name:'Tom Hanks'})-[r:ACTED_IN]->(m:Movie) return p,r,m")
    List<Person> tomHanksCareer();
}