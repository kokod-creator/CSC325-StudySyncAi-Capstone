package com.app.view;

import com.app.model.Document;
import com.app.navigation.SceneManager;
import com.app.service.MockDataService;

import javafx.scene.control.*;
import javafx.scene.layout.*;

public class DocumentsView extends BorderPane {

    public DocumentsView() {

        VBox box = new VBox(15);
        box.getStyleClass().add("main-content");

        Label title = new Label("Documents");
        title.getStyleClass().add("section-title");

        TextField search = new TextField();
        search.setPromptText("Search documents...");

        ListView<String> list = new ListView<>();

        refreshList(list, "");

        search.textProperty().addListener(
                (obs, oldValue, newValue) ->
                        refreshList(list, newValue)
        );

        Button back = new Button("Back");
        back.getStyleClass().add("button-secondary");

        back.setOnAction(e ->
                SceneManager.showDashboard()
        );

        box.getChildren().addAll(
                title,
                search,
                list,
                back
        );

        setCenter(box);
    }

    private void refreshList(
            ListView<String> list,
            String filter
    ) {

        list.getItems().clear();

        for (Document doc : MockDataService.documents) {

            if (filter.isEmpty()
                    || doc.title.toLowerCase()
                    .contains(filter.toLowerCase())) {

                list.getItems().add(
                        doc.title
                                + " - "
                                + doc.courseCode
                                + " (" + doc.type + ")"
                );
            }
        }
    }
}