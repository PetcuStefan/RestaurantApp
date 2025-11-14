import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class RestaurantMenu {

    private final MenuItem[] items = MenuItem.values();
    private final Label[] labels = new Label[items.length];

    private ListView<MenuItem> selectedList;
    private Label totalLabel;

    private double totalPrice = 0.0;

    public void start(Stage stage) {

        VBox root = new VBox(12);
        root.setPadding(new Insets(20));

        Label title = new Label("=== Restaurant Menu ===");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        root.getChildren().add(title);

        // MENU ITEMS WITH HOVER + CLICK
        for (int i = 0; i < items.length; i++) {
            MenuItem menuItem = items[i];
            Label label = new Label(menuItem.toString());
            label.setStyle("-fx-font-size: 16px; -fx-padding: 5px;");
            labels[i] = label;

            // add on click
            label.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> addItem(menuItem));

            // hover effects
            label.setOnMouseEntered(e -> label.setStyle(
                    "-fx-font-size: 16px; -fx-padding: 5px; -fx-background-color: lightgray; -fx-font-weight: bold; -fx-cursor: hand;"
            ));
            label.setOnMouseExited(e -> label.setStyle("-fx-font-size: 16px; -fx-padding: 5px;"));

            root.getChildren().add(label);
        }

        // LISTVIEW FOR SELECTED ITEMS
        selectedList = new ListView<>();
        selectedList.setPrefHeight(180);
        root.getChildren().add(selectedList);

        // TOTAL PRICE
        totalLabel = new Label("Total: 0.00 RON");
        totalLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        root.getChildren().add(totalLabel);

        // BUTTON REMOVE SELECTED ITEM
        Button removeButton = new Button("Remove Selected Item");
        removeButton.setStyle("-fx-font-size: 16px;");
        removeButton.setOnAction(e -> removeSelectedItem());
        root.getChildren().add(removeButton);

        Scene scene = new Scene(root, 420, 600);
        stage.setScene(scene);
        stage.setTitle("Restaurant Menu");
        stage.show();
    }

    // Add item to ListView
    private void addItem(MenuItem item) {
        selectedList.getItems().add(item);
        totalPrice += item.getPrice();
        updateTotal();
    }

    // Remove currently selected item
    private void removeSelectedItem() {
        MenuItem selected = selectedList.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        selectedList.getItems().remove(selected);
        totalPrice -= selected.getPrice();
        updateTotal();
    }

    private void updateTotal() {
        totalLabel.setText(String.format("Total: %.2f RON", totalPrice));
    }
}
