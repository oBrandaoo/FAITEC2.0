package org.example.util;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;

/** Applies a neutral selected/hover state to every ComboBox popup in a view. */
public final class ComboBoxStyleManager {

    private ComboBoxStyleManager() {
    }

    public static void applyTo(Node node) {
        if (node instanceof ComboBox<?> comboBox) {
            styleComboBox(comboBox);
        }

        if (node instanceof Parent parent) {
            parent.getChildrenUnmodifiable().forEach(ComboBoxStyleManager::applyTo);
        }
    }

    private static <T> void styleComboBox(ComboBox<T> comboBox) {
        comboBox.setCellFactory(listView -> new ListCell<>() {
            {
                selectedProperty().addListener(observable -> updateHighlight());
                hoverProperty().addListener(observable -> updateHighlight());
            }

            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.toString());
                updateHighlight();
            }

            private void updateHighlight() {
                if (isEmpty()) {
                    setStyle("");
                } else if (isSelected()) {
                    setStyle("-fx-background-color: #E9EDEF; -fx-text-fill: #263D48;");
                } else if (isHover()) {
                    setStyle("-fx-background-color: #F2F4F4; -fx-text-fill: #263D48;");
                } else {
                    setStyle("");
                }
            }
        });
    }
}
