package com.ufund.api.ufundapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CompletedNeed {
    @JsonProperty("need")
    private Need need;
    @JsonProperty("contributor")
    private int contributorID;
    // The contributor's known username: can be changed by request
    @JsonProperty("contributor_name")
    private String contributorUsername;
    @JsonProperty("timestamp")
    private long timestamp;

    public CompletedNeed(@JsonProperty("need") Need need, @JsonProperty("contributor") int contributor,
            @JsonProperty("contributor_name") String contributorName, @JsonProperty("timestamp") long timestamp) {
        this.need = need;
        this.contributorID = contributor;
        this.contributorUsername = contributorName;
        this.timestamp = timestamp;
    }

    public Need getNeed() {
        return need;
    }

    public int getContributorID() {
        return contributorID;
    }

    public String getContributorUsername() {
        return contributorUsername;
    }

    public void setContributorUsername(String name) {
        contributorUsername = name;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
