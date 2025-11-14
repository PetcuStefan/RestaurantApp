import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class RestaurantMenu {

    private final MenuItem[] items = MenuItem.values();
    private final Label[] labels = new Label[items.length];

    private TextArea outputArea;

    public void start(Stage stage) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));

        Label title = new Label("=== Restaurant Menu ===");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        root.getChildren().add(title);

        for (int i = 0; i < items.length; i++) {
            Label label = new Label(items[i].toString());
            label.setStyle("-fx-font-size: 16px; -fx-padding: 5px;");
            labels[i] = label;

            final int index = i;

            // CLICK HANDLER (just prints, no selection)
            label.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                showSelected(items[index]);
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

            // REMOVE HOVER EFFECT
            label.addEventHandler(MouseEvent.MOUSE_EXITED, e -> {
                label.setStyle(
                        "-fx-font-size: 16px; " +
                                "-fx-padding: 5px;"
                );
            });

            root.getChildren().add(label);
        }

        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setPromptText("Selections will appear here...");
        outputArea.setPrefHeight(150);

        root.getChildren().add(outputArea);

        Scene scene = new Scene(root, 400, 450);
        stage.setTitle("Restaurant Menu");
        stage.setScene(scene);
        stage.show();
    }

    private void showSelected(MenuItem item) {
        outputArea.appendText("Selected: " + item + "\n");
    }
}
