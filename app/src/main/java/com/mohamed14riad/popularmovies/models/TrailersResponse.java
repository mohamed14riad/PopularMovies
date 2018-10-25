package com.mohamed14riad.popularmovies.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TrailersResponse {

    @SerializedName("results")
    private List<Trailer> results;

    public TrailersResponse() {
    }

    public List<Trailer> getResults() {
        return results;
    }

    public void setResults(List<Trailer> results) {
        this.results = results;
    }
}
