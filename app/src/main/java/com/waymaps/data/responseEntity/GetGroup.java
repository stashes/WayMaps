package com.waymaps.data.responseEntity;

/**
 * Created by Admin on 05.02.2018.
 */

public class GetGroup {
    private String id;
    private String title;

    public GetGroup() {
    }

    public GetGroup(String id, String title) {
        this.id = id;
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "GetGroup{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
