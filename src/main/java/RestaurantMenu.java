import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

        // HBOX WITH TOTAL + PLACE ORDER BUTTON
        HBox bottomArea = new HBox(20);

        totalLabel = new Label("Total: 0.00 RON");
        totalLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Button placeOrderButton = new Button("Place Order");
        placeOrderButton.setStyle("-fx-font-size: 16px;");
        placeOrderButton.setOnAction(e -> placeOrder());

        bottomArea.getChildren().addAll(totalLabel, placeOrderButton);
        root.getChildren().add(bottomArea);

        // REMOVE BUTTON (removes selected from ListView)
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

    // WRITE ORDER TO TXT AND RESET LIST
    private void placeOrder() {
        if (selectedList.getItems().isEmpty()) {
            showAlert("No items selected!");
            return;
        }

        try {
            // Ensure "orders" folder exists
            File folder = new File("orders");
            if (!folder.exists()) {
                folder.mkdir();
            }

            // Create timestamped filename
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy(HH:mm)");
            String timestamp = LocalDateTime.now().format(formatter);
            File orderFile = new File(folder, "Order:" + timestamp + ".txt");

            // Write the order to the file
            try (FileWriter writer = new FileWriter(orderFile)) {
                writer.write("=== ORDER ===\n\n");
                for (MenuItem item : selectedList.getItems()) {
                    writer.write(item.toString() + "\n");
                }
                writer.write(String.format("\nTotal: %.2f RON\n", totalPrice));
                writer.write("Ticket issued on: " + timestamp + "\n");
                writer.write("=====================\n");
            }

        } catch (IOException e) {
            showAlert("Error writing order to file.");
            return;
        }

        // Clear selection + reset total
        selectedList.getItems().clear();
        totalPrice = 0.0;
        updateTotal();

        showAlert("Order placed!\nFile saved in orders folder.");
    }

    private void updateTotal() {
        totalLabel.setText(String.format("Total: %.2f RON", totalPrice));
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
