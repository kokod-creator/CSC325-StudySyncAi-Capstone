package com.app.view;

import com.app.model.Course;
import com.app.navigation.SceneManager;
import com.app.service.MockDataService;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

public class CoursesView extends BorderPane {
    public CoursesView() {
        VBox box = new VBox(15);
        box.getStyleClass().add("main-content");

        Label title = new Label("Courses");
        title.getStyleClass().add("section-title");

        TableView<Course> table = new TableView<>();
        table.setItems(FXCollections.observableArrayList(MockDataService.courses));
        TableColumn<Course, String> codeCol = new TableColumn<>("Course Code");
        codeCol.setCellValueFactory(new PropertyValueFactory<>("code"));
        table.getColumns().add(codeCol);
        table.setPrefHeight(400);

        TextField newCourse = new TextField();
        newCourse.setPromptText("Add new course (e.g. CSC400)");

        Button addBtn = new Button("+ Add Course");
        addBtn.getStyleClass().add("button");
        addBtn.setOnAction(e -> {
            if (!newCourse.getText().isEmpty()) {
                Course c = new Course(String.valueOf(System.currentTimeMillis()), newCourse.getText());
                MockDataService.courses.add(c);
                table.setItems(FXCollections.observableArrayList(MockDataService.courses));
                newCourse.clear();
            }
        });

        Button back = new Button("Back");
        back.getStyleClass().add("button-secondary");
        back.setOnAction(e -> SceneManager.showDashboard());

        box.getChildren().addAll(title, table, newCourse, addBtn, back);
        setCenter(box);
    }
}