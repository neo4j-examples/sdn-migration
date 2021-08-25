package org.neo4j.sdnlegacy;

import org.neo4j.sdnlegacy.person.Actor;
import org.neo4j.sdnlegacy.movie.Tagline;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.data.neo4j.core.convert.Neo4jConversions;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class SdnLegacyApplication {

	public static void main(String[] args) {
		SpringApplication.run(SdnLegacyApplication.class, args);
	}

	@Bean
	public Neo4jConversions neo4jConversions() {
		List<? extends GenericConverter> converters = Arrays.asList(new Tagline.TaglineConverter(), new Actor.ActorConverter());

		return new Neo4jConversions(converters);
	}

}