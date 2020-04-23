package org.neo4j.sdnlegacy.movie;

import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

/**
 * @author Gerrit Meier
 */
@NodeEntity("Movie")
public class MovieEntity {

	@Id
	private String title;

	private String tagline;

	@Property("released")
	private Integer yearOfRelease;


	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTagline() {
		return tagline;
	}

	public void setTagline(String tagline) {
		this.tagline = tagline;
	}

	public Integer getYearOfRelease() {
		return yearOfRelease;
	}

	public void setYearOfRelease(Integer yearOfRelease) {
		this.yearOfRelease = yearOfRelease;
	}

	@Override
	public String toString() {
		return "MovieEntity{" +
			"title='" + title + '\'' +
			", tagline='" + tagline + '\'' +
			", yearOfRelease=" + yearOfRelease +
			'}';
	}
}
