package com.ufund.api.ufundapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Need {
    @JsonProperty("name")
    private String name;
    @JsonProperty("id")
    private int id;
    @JsonProperty("description")
    private String description;

    static final String STRING_FORMAT = "Need [id=%d, name=%s, description=%s]";

    public Need(@JsonProperty("name") String name, @JsonProperty("id") int id,
            @JsonProperty("description") String description) {
        this.name = name;
        this.id = id;
        this.description = description;
    }

    public void updateNeed(String newName, String newDesc) {
        if (newName != null) { // Only update what is new
            this.name = newName;
        }
        if (newDesc != null) {
            this.description = newDesc;
        }
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return String.format(STRING_FORMAT, id, name, description);
    }
}
