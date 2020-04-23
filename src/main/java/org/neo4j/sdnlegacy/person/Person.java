package org.neo4j.sdnlegacy.person;

import java.util.List;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.sdnlegacy.movie.MovieEntity;

/**
 * @author Gerrit Meier
 */
@NodeEntity
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
	List<ReviewRelationship> reviewedMovies;

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

	public List<ReviewRelationship> getReviewedMovies() {
		return reviewedMovies;
	}

	public void setReviewedMovies(List<ReviewRelationship> reviewedMovies) {
		this.reviewedMovies = reviewedMovies;
	}

	public List<MovieEntity> getProducedMovies() {
		return producedMovies;
	}

	public void setProducedMovies(List<MovieEntity> producedMovies) {
		this.producedMovies = producedMovies;
	}
}
