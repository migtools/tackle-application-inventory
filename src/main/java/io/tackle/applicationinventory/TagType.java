package io.tackle.applicationinventory;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TagType {
    public String id;
    public String name;
    public List<Tag> tags;

    public static class Tag {
        public String id;
        public String name;
    }
}
