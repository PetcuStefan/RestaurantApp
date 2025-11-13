import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class RestaurantMenu {

    private int selectedIndex = 0;
    private final MenuItem[] items = MenuItem.values();
    private final Label[] labels = new Label[items.length];

    public void start(Stage stage) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));

        Label title = new Label("=== Restaurant Menu ===");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        root.getChildren().add(title);

        // Create label for each menu item
        for (int i = 0; i < items.length; i++) {
            Label label = new Label(items[i].toString());
            label.setStyle("-fx-font-size: 16px;");
            labels[i] = label;
            root.getChildren().add(label);
        }

        // Highlight the first item
        refreshHighlight();

        Scene scene = new Scene(root, 400, 300);

        // Keyboard handling
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.UP) {
                selectedIndex = (selectedIndex - 1 + items.length) % items.length;
                refreshHighlight();
            } else if (event.getCode() == KeyCode.DOWN) {
                selectedIndex = (selectedIndex + 1) % items.length;
                refreshHighlight();
            } else if (event.getCode() == KeyCode.ENTER) {
                System.out.println("Selected: " + items[selectedIndex]);
            }
        });

        stage.setTitle("Restaurant Menu");
        stage.setScene(scene);
        stage.show();
    }

    private void refreshHighlight() {
        for (int i = 0; i < labels.length; i++) {
            if (i == selectedIndex) {
                labels[i].setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-background-color: lightgray;");
            } else {
                labels[i].setStyle("-fx-font-size: 16px;");
            }
        }
    }
}
