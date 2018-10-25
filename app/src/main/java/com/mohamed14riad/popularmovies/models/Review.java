package com.mohamed14riad.popularmovies.models;

import com.google.gson.annotations.SerializedName;

public class Review {
    @SerializedName("author")
    private String author;
    @SerializedName("content")
    private String content;
    @SerializedName("id")
    private String reviewId;
    @SerializedName("url")
    private String url;

    public Review() {
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
