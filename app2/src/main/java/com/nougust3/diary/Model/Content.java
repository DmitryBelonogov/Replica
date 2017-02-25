package com.nougust3.diary.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Content {

    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("content")
    @Expose
    private String content;
    @SerializedName("date_published")
    @Expose
    private String datePublished;
    @SerializedName("lead_image_url")
    @Expose
    private String leadImageUrl;
    @SerializedName("dek")
    @Expose
    private String dek;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("domain")
    @Expose
    private String domain;
    @SerializedName("excerpt")
    @Expose
    private String excerpt;
    @SerializedName("word_count")
    @Expose
    private Integer wordCount;
    @SerializedName("direction")
    @Expose
    private String direction;
    @SerializedName("total_pages")
    @Expose
    private Integer totalPages;
    @SerializedName("rendered_pages")
    @Expose
    private Integer renderedPages;
    @SerializedName("next_page_url")
    @Expose
    private Object nextPageUrl;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDatePublished() {
        return datePublished;
    }

    public void setDatePublished(String datePublished) {
        this.datePublished = datePublished;
    }

    public String getLeadImageUrl() {
        return leadImageUrl;
    }

    public void setLeadImageUrl(String leadImageUrl) {
        this.leadImageUrl = leadImageUrl;
    }

    public String getDek() {
        return dek;
    }

    public void setDek(String dek) {
        this.dek = dek;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getExcerpt() {
        return excerpt;
    }

    public void setExcerpt(String excerpt) {
        this.excerpt = excerpt;
    }

    public Integer getWordCount() {
        return wordCount;
    }

    public void setWordCount(Integer wordCount) {
        this.wordCount = wordCount;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public Integer getRenderedPages() {
        return renderedPages;
    }

    public void setRenderedPages(Integer renderedPages) {
        this.renderedPages = renderedPages;
    }

    public Object getNextPageUrl() {
        return nextPageUrl;
    }

    public void setNextPageUrl(Object nextPageUrl) {
        this.nextPageUrl = nextPageUrl;
    }

}