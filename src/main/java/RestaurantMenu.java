import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class RestaurantMenu {

    private int selectedIndex = 0;
    private final MenuItem[] items = MenuItem.values();
    private final Label[] labels = new Label[items.length];

    // Area to show output
    private TextArea outputArea;

    public void start(Stage stage) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));

        Label title = new Label("=== Restaurant Menu ===");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        root.getChildren().add(title);

        // Create UI labels for each menu item
        for (int i = 0; i < items.length; i++) {
            Label label = new Label(items[i].toString());
            label.setStyle("-fx-font-size: 16px; -fx-padding: 5px;");
            labels[i] = label;

            final int index = i;

            // 💡 CLICK HANDLER
            label.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                selectedIndex = index;
                refreshHighlight();
                showSelected(items[index]);
            });

            root.getChildren().add(label);
        }

        // Text output area
        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setPromptText("Selections will appear here...");
        outputArea.setPrefHeight(150);

        root.getChildren().add(outputArea);

        refreshHighlight(); // highlight starting item

        Scene scene = new Scene(root, 400, 450);
        stage.setTitle("Restaurant Menu");
        stage.setScene(scene);
        stage.show();
    }

    private void refreshHighlight() {
        for (int i = 0; i < labels.length; i++) {
            if (i == selectedIndex) {
                labels[i].setStyle(
                        "-fx-font-size: 16px; " +
                                "-fx-font-weight: bold; " +
                                "-fx-background-color: lightgray; " +
                                "-fx-padding: 5px;"
                );
            } else {
                labels[i].setStyle(
                        "-fx-font-size: 16px; -fx-padding: 5px;"
                );
            }
        }
    }

    private void showSelected(MenuItem item) {
        outputArea.appendText("Selected: " + item + "\n");
    }
}
