package com.app.service;

import com.app.model.Course;
import com.app.model.Document;
import com.app.model.GeneratedStudyMaterial;
import java.util.ArrayList;
import java.util.List;

public class MockDataService {

    public static List<Course> courses = new ArrayList<>();
    public static List<Document> documents = new ArrayList<>();
    public static List<GeneratedStudyMaterial> recentStudyMaterials = new ArrayList<>();

    //  STATIC DATA (already exists)
    static {
        courses.add(new Course("1", "CSC325"));
        courses.add(new Course("2", "BIO101"));
        courses.add(new Course("3", "PSY200"));
        courses.add(new Course("4", "MTH210"));
        courses.add(new Course("5", "ENG150"));

        documents.add(new Document("Lecture 7 - Design Patterns.pdf", "PDF", "CSC325"));
        documents.add(new Document("Cell Biology Slides.pptx", "Slides", "BIO101"));
        documents.add(new Document("Memory & Attention - Notes.pdf", "Notes", "PSY200"));
        documents.add(new Document("Matrix Operations.pdf", "PDF", "MTH210"));
        documents.add(new Document("Essay Structure Guide.pdf", "PDF", "ENG150"));
        documents.add(new Document("Agile Methodologies.pptx", "Slides", "CSC325"));
    }

    //  ADD YOUR METHOD HERE (outside static block)
    public static List<Document> getRecentDocuments(int limit) {
        int size = documents.size();

        if (size <= limit) {
            return documents;
        }

        return documents.subList(size - limit, size);
    }

    public static List<GeneratedStudyMaterial> getRecentStudyMaterials(int limit) {
        int size = recentStudyMaterials.size();
        if (size <= limit) {
            return new ArrayList<>(recentStudyMaterials);
        }
        return new ArrayList<>(recentStudyMaterials.subList(size - limit, size));
    }
}