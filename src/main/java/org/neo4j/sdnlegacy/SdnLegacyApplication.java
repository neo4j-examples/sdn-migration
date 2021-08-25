package org.neo4j.sdnlegacy;

import java.util.ArrayList;
import java.util.List;

import org.neo4j.sdnlegacy.person.Actor;
import org.neo4j.sdnlegacy.movie.Tagline;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.data.neo4j.core.convert.Neo4jConversions;

@SpringBootApplication
public class SdnLegacyApplication {

	public static void main(String[] args) {
		SpringApplication.run(SdnLegacyApplication.class, args);
	}

	@Bean
	public Neo4jConversions neo4jConversions() {
		List<GenericConverter> converters = new ArrayList<>();
		converters.add(new Tagline.TaglineConverter());
		converters.add(new Actor.ActorConverter());

		return new Neo4jConversions(converters);
	}
}
