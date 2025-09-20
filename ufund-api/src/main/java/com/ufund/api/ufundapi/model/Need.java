package com.ufund.api.ufundapi.model;

public class Need {
    private String name;
    private int id;
    private String description;

    public Need(String name, int id, String description) {
        this.name = name;
        this.id = id;
        this.description = description;
    }

    public void updateNeed(String newName, String newDesc) {
        if (newName != null) {          // Only update what is new
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

    public int getId(){
        return id;
    }
}
