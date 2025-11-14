import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Scene;

public class RestaurantMenu {

    private final MenuItem[] items = MenuItem.values();
    private final Label[] labels = new Label[items.length];

    private TextArea outputArea;
    private Label totalLabel;

    private double totalPrice = 0.0;

    public void start(Stage stage) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));

        Label title = new Label("=== Restaurant Menu ===");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        root.getChildren().add(title);

        // Create clickable + hoverable labels
        for (int i = 0; i < items.length; i++) {
            MenuItem menuItem = items[i];
            Label label = new Label(menuItem.toString());
            label.setStyle("-fx-font-size: 16px; -fx-padding: 5px;");
            labels[i] = label;

            final int index = i;

            // CLICK → add to list and update total
            label.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                addItem(menuItem);
            });

            // HOVER EFFECT
            label.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> {
                label.setStyle(
                        "-fx-font-size: 16px; " +
                                "-fx-padding: 5px; " +
                                "-fx-background-color: lightgray; " +
                                "-fx-font-weight: bold; " +
                                "-fx-cursor: hand;"
                );
            });

            // REMOVE EFFECT ON EXIT
            label.addEventHandler(MouseEvent.MOUSE_EXITED, e -> {
                label.setStyle("-fx-font-size: 16px; -fx-padding: 5px;");
            });

            root.getChildren().add(label);
        }

        // TextArea showing items
        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setPromptText("Selections will appear here...");
        outputArea.setPrefHeight(150);
        root.getChildren().add(outputArea);

        // TOTAL LABEL
        totalLabel = new Label("Total: 0.00 RON");
        totalLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        root.getChildren().add(totalLabel);

        Scene scene = new Scene(root, 400, 500);
        stage.setScene(scene);
        stage.setTitle("Restaurant Menu");
        stage.show();
    }

    // Adds an item to the list and updates the total
    private void addItem(MenuItem item) {
        outputArea.appendText(item.getName() + " - " + item.getPrice() + " RON\n");

        totalPrice += item.getPrice();
        totalLabel.setText(String.format("Total: %.2f RON", totalPrice));
    }
}
