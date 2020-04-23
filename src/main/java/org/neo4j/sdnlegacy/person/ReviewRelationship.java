package org.neo4j.sdnlegacy.person;

import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;
import org.neo4j.sdnlegacy.movie.MovieEntity;

/**
 * @author Gerrit Meier
 */
@RelationshipEntity("REVIEWED")
public class ReviewRelationship {

	@Id
	@GeneratedValue
	private Long id;

	private String summary;

	private Integer rating;

	@StartNode
	private Person personNode;

	@EndNode
	private MovieEntity movieNode;

	public Person getPersonNode() {
		return personNode;
	}

	public void setPersonNode(Person personNode) {
		this.personNode = personNode;
	}

	public MovieEntity getMovieNode() {
		return movieNode;
	}

	public void setMovieNode(MovieEntity movieNode) {
		this.movieNode = movieNode;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public Integer getRating() {
		return rating;
	}

	public void setRating(Integer rating) {
		this.rating = rating;
	}
}
