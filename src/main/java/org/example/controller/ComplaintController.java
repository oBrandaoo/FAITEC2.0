package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class ComplaintController {
    @FXML
    private ComboBox<String> categoryBox;

    @FXML
    private TextField locationField;

    @FXML
    private TextArea descriptionArea;

    @FXML
    public void initialize() {

        categoryBox.getItems().addAll(
                "Buraco na rua",
                "Iluminação pública",
                "Lixo acumulado",
                "Esgoto",
                "Segurança"
        );

    }

    public void submitComplaint() {

        String category = categoryBox.getValue();
        String location = locationField.getText();
        String description = descriptionArea.getText();

        System.out.println("Categoria: " + category);
        System.out.println("Local: " + location);
        System.out.println("Descrição: " + description);

    }

}