package com.ufund.api.ufundapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class code for a {@link Need need} object
 * 
 * @author Matthew Beicke
 */
public class Need {
    @JsonProperty("name")
    private String name; // A Needs name
    @JsonProperty("id")
    private int id; // A Needs unique id
    @JsonProperty("description")
    private String description; // A Needs description

    static final String STRING_FORMAT = "Need [id=%d, name=%s, description=%s]";

    /**
     * Constructor for a {@link Need need} object
     * 
     * @param name        name of a need
     * @param id          unique id of a need
     * @param description description of a need
     */
    public Need(@JsonProperty("name") String name, @JsonProperty("id") int id,
            @JsonProperty("description") String description) {
        this.name = name;
        this.id = id;
        this.description = description;
    }

    /**
     * Updates a need's name and/or description
     * 
     * @param newName new name
     * @param newDesc new description
     */
    public void updateNeed(String newName, String newDesc) {
        if (newName != null) { // Only update what is new
            this.name = newName;
        }
        if (newDesc != null) {
            this.description = newDesc;
        }
    }

    /**
     * Returns the Need's name
     * 
     * @return need's name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the Need's description
     * 
     * @return need's description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the Need's id
     * 
     * @return need's id
     */
    public int getId() {
        return id;
    }

    /**
     * Standard toString method, uses custom format
     * 
     * @return A string containing need data
     */
    @Override
    public String toString() {
        return String.format(STRING_FORMAT, id, name, description);
    }
}
