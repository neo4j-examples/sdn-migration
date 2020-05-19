package org.neo4j.sdnlegacy.person;

import java.util.List;
import org.springframework.data.neo4j.repository.Neo4jRepository;

/**
 * @author Gerrit Meier
 */
public interface PersonRepository extends Neo4jRepository<Person, Long> {

	List<Person> findByReviewedMoviesMovieNodeTitle(String reviewedMovieTitle);

	List<Person> findByDirectedMoviesTitle(String directedMovieTitle);
}