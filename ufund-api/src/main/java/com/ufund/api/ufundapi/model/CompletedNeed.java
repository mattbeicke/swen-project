package com.ufund.api.ufundapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CompletedNeed {
    @JsonProperty("need")
    private Need need;
    @JsonProperty("contributor")
    private int contributorID;
    @JsonProperty("timestamp")
    private long timestamp;

    public CompletedNeed(@JsonProperty("need") Need need, @JsonProperty("contributor") int contributor,
                         @JsonProperty("timestamp") long timestamp) {
        this.need = need;
        this.contributorID = contributor;
        this.timestamp = timestamp;
    }

    public Need getNeed() {
        return need;
    }

    public int getContributorID() {
        return contributorID;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
