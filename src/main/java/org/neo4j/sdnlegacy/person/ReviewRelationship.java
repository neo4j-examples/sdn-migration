package org.neo4j.sdnlegacy.person;

import org.neo4j.sdnlegacy.movie.MovieEntity;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

/**
 * @author Gerrit Meier
 */
@RelationshipProperties
public class ReviewRelationship {
	@Id
	@GeneratedValue
	private final Long neoId;

	private String summary;
	private Integer rating;

	@TargetNode
	private MovieEntity movieEntity;

	public ReviewRelationship(Long neoId, String summary, Integer rating) {
		this.neoId = neoId;
		this.summary = summary;
		this.rating = rating;
	}

	public Long getNeoId() {
		return neoId;
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

	public MovieEntity getMovieEntity() {
		return movieEntity;
	}

	public void setMovieEntity(MovieEntity movieEntity) {
		this.movieEntity = movieEntity;
	}
}
