package org.neo4j.sdnlegacy.person;

import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.Objects;

@QueryResult
public class ActedInMovieProjection {

    public String name;

    public int born;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActedInMovieProjection that = (ActedInMovieProjection) o;
        return born == that.born && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, born);
    }

    @Override
    public String toString() {
        return "ActedInMovieProjection{" +
                "name='" + name + '\'' +
                ", born=" + born +
                '}';
    }
}
