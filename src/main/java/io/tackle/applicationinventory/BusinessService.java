package io.tackle.applicationinventory;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BusinessService {
    public String id;
    public String name;
}
