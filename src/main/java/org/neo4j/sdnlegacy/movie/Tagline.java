package org.neo4j.sdnlegacy.movie;

import java.util.HashSet;
import java.util.Set;

import org.neo4j.driver.Value;
import org.neo4j.driver.Values;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;

/**
 * @author Gerrit Meier
 */
public class Tagline {
	private final String tagline;

	private Tagline(String tagline) {
		this.tagline = tagline;
	}

	public static Tagline of(String tagline) {
		return new Tagline(tagline);
	}

	public String getTagline() {
		return tagline;
	}

	public static class TaglineConverter implements GenericConverter {

		@Override
		public Set<ConvertiblePair> getConvertibleTypes() {
			Set<ConvertiblePair> convertibleTypes = new HashSet<>();
			convertibleTypes.add(new ConvertiblePair(Value.class, Tagline.class));
			convertibleTypes.add(new ConvertiblePair(Tagline.class, Value.class));
			return convertibleTypes;
		}

		@Override
		public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
			if (Value.class.isAssignableFrom(sourceType.getType())) {
				return Tagline.of(((Value) source).asString());
			} else {
				return Values.value(((Tagline) source).getTagline());
			}
		}
	}
}
