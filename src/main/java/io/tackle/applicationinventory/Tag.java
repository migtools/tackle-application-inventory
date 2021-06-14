package io.tackle.applicationinventory;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Tag {
    public String id;
    public String name;
    public TagType tagType;

    public static class TagType {
        public String id;
        public String name;
    }
}
