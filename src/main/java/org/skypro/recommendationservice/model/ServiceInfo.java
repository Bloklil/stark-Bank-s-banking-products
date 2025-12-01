package org.skypro.recommendationservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ServiceInfo {
    private final String name;
    private final String version;

    public ServiceInfo(String name, String version) {
        this.name = name;
        this.version = version;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("version")
    public String getVersion() {
        return version;
    }
}
