package org.neo4j.sdnlegacy.person;

import java.util.Collections;
import java.util.Set;

import org.neo4j.driver.Value;
import org.neo4j.driver.Values;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;

/**
 * @author Gerrit Meier
 */
public class Actor {

	private final String name;

	public Actor(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public static class ActorConverter implements GenericConverter {

		@Override
		public Set<ConvertiblePair> getConvertibleTypes() {
			return Collections.singleton(new ConvertiblePair(Actor.class, Value.class));
		}

		@Override
		public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
			return Values.value(((Actor) source).getName());
		}
	}
}
