package org.neo4j.sdnlegacy.person;

import org.neo4j.springframework.data.core.schema.RelationshipProperties;

/**
 * @author Gerrit Meier
 */
@RelationshipProperties
public class ReviewRelationship {

	private String summary;

	private Integer rating;

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
