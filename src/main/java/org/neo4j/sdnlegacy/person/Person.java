package org.neo4j.sdnlegacy.person;

import java.util.List;
import java.util.Map;

import org.neo4j.sdnlegacy.movie.MovieEntity;
import org.neo4j.springframework.data.core.schema.GeneratedValue;
import org.neo4j.springframework.data.core.schema.Id;
import org.neo4j.springframework.data.core.schema.Node;
import org.neo4j.springframework.data.core.schema.Relationship;

/**
 * @author Gerrit Meier
 */
@Node(primaryLabel = "Person")
public class Person {

	@Id
	@GeneratedValue
	private Long id;

	private String name;

	private int born;

	@Relationship("DIRECTED")
	List<MovieEntity> directedMovies;

	@Relationship("ACTED_IN")
	List<MovieEntity> actedInMovies;

	@Relationship("REVIEWED")
	Map<MovieEntity, ReviewRelationship> reviewedMovies;

	@Relationship("PRODUCED")
	List<MovieEntity> producedMovies;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getBorn() {
		return born;
	}

	public void setBorn(int born) {
		this.born = born;
	}

	public List<MovieEntity> getDirectedMovies() {
		return directedMovies;
	}

	public void setDirectedMovies(List<MovieEntity> directedMovies) {
		this.directedMovies = directedMovies;
	}

	public List<MovieEntity> getActedInMovies() {
		return actedInMovies;
	}

	public void setActedInMovies(List<MovieEntity> actedInMovies) {
		this.actedInMovies = actedInMovies;
	}

	public Map<MovieEntity, ReviewRelationship> getReviewedMovies() {
		return reviewedMovies;
	}

	public void setReviewedMovies(Map<MovieEntity, ReviewRelationship> reviewedMovies) {
		this.reviewedMovies = reviewedMovies;
	}

	public List<MovieEntity> getProducedMovies() {
		return producedMovies;
	}

	public void setProducedMovies(List<MovieEntity> producedMovies) {
		this.producedMovies = producedMovies;
	}
}