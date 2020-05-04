package org.neo4j.sdnlegacy.movie;

import org.neo4j.springframework.data.core.schema.Id;
import org.neo4j.springframework.data.core.schema.Node;
import org.neo4j.springframework.data.core.schema.Property;
import org.springframework.data.annotation.Version;

/**
 * @author Gerrit Meier
 */
@Node("Movie")
public class MovieEntity {

	@Id
	private String title;

	private Tagline tagline;

	@Property("released")
	private Integer yearOfRelease;

	@Version
	private Long version;

	public MovieEntity() {}

	public MovieEntity(String title, String tagline, Integer yearOfRelease) {
		this.title = title;
		this.tagline = Tagline.of(tagline);
		this.yearOfRelease = yearOfRelease;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Tagline getTagline() {
		return tagline;
	}

	public void setTagline(Tagline tagline) {
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
