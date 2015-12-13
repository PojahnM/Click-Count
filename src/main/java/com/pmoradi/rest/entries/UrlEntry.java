package com.pmoradi.rest.entries;

public class UrlEntry {

    private String urlName;
    private String link;
    private long[] clicks;

    public String getUrlName() {
        return urlName;
    }

    public void setUrlName(String urlName) {
        this.urlName = urlName;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public long[] getClicks() {
        return clicks;
    }

    public void setClicks(long[] clicks) {
        this.clicks = clicks;
    }
}
