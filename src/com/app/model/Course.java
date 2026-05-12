package com.app.model;

public class Course {
    private String id;
    private String code;

    public Course(String id, String code) {
        this.id = id;
        this.code = code;
    }

    public String getId() {
        return id;
    }

    public String getCode() {
        return code;
    }
}
