import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

public class RestaurantMenu {

    private final ArrayList<Dish> menuItems = new ArrayList<>();
    private final ArrayList<Label> labels = new ArrayList<>();

    private ListView<Dish> selectedList;
    private Label totalLabel;

    private double totalPrice = 0.0;

    public RestaurantMenu() {
        loadMenuFromFile("Menu.txt");
    }

    private void loadMenuFromFile(String filePath) {
        File menuFile = new File(filePath);
        if (!menuFile.exists()) {
            showAlertAndExit("Menu file not found");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(menuFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.isBlank()) {
                    String[] parts = line.split(",");
                    if (parts.length == 2) {
                        String name = parts[0].trim();
                        double price = Double.parseDouble(parts[1].trim());
                        menuItems.add(new Dish(name, price));
                    }
                }
            }
        } catch (IOException e) {
            showAlertAndExit("Error reading menu file: " + e.getMessage());
        } catch (NumberFormatException e) {
            showAlertAndExit("Invalid price format in menu file: " + e.getMessage());
        }
    }

    public void start(Stage stage) {

        VBox root = new VBox(12);
        root.setPadding(new Insets(20));

        Label title = new Label("=== Restaurant Menu ===");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        root.getChildren().add(title);

        for (Dish dish : menuItems) {
            Label label = new Label(dish.toString());
            label.setStyle("-fx-font-size: 16px; -fx-padding: 5px;");
            labels.add(label);

            label.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> addItem(dish));

            label.setOnMouseEntered(e -> label.setStyle(
                    "-fx-font-size: 16px; -fx-padding: 5px; -fx-background-color: lightgray; -fx-font-weight: bold; -fx-cursor: hand;"
            ));
            label.setOnMouseExited(e -> label.setStyle("-fx-font-size: 16px; -fx-padding: 5px;"));

            root.getChildren().add(label);
        }

        selectedList = new ListView<>();
        selectedList.setPrefHeight(180);
        root.getChildren().add(selectedList);

        HBox bottomArea = new HBox(20);

        totalLabel = new Label("Total: 0.00 RON");
        totalLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Button placeOrderButton = new Button("Place Order");
        placeOrderButton.setStyle("-fx-font-size: 16px;");
        placeOrderButton.setOnAction(e -> placeOrder());

        bottomArea.getChildren().addAll(totalLabel, placeOrderButton);
        root.getChildren().add(bottomArea);

        Button removeButton = new Button("Remove Selected Item");
        removeButton.setStyle("-fx-font-size: 16px;");
        removeButton.setOnAction(e -> removeSelectedItem());
        root.getChildren().add(removeButton);

        Button mostPopularButton = new Button("Most Popular Dish");
        mostPopularButton.setStyle("-fx-font-size: 16px;");
        mostPopularButton.setOnAction(e -> generateDishReview());
        root.getChildren().add(mostPopularButton);

        Scene scene = new Scene(root, 450, 650);
        stage.setScene(scene);
        stage.setTitle("Restaurant Menu");
        stage.show();
    }

    private void addItem(Dish dish) {
        selectedList.getItems().add(dish);
        totalPrice += dish.getPrice();
        updateTotal();
    }

    private void removeSelectedItem() {
        Dish selected = selectedList.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        selectedList.getItems().remove(selected);
        totalPrice -= selected.getPrice();
        updateTotal();
    }

    private void placeOrder() {
        if (selectedList.getItems().isEmpty()) {
            showAlert("No items selected!");
            return;
        }

        try {
            File folder = new File("orders");
            if (!folder.exists()) folder.mkdir();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy(HH:mm)");
            String timestamp = LocalDateTime.now().format(formatter);
            File orderFile = new File(folder, "Order:" + timestamp + ".txt");

            try (FileWriter writer = new FileWriter(orderFile)) {
                writer.write("=== ORDER ===\n\n");
                for (Dish dish : selectedList.getItems()) {
                    writer.write(dish.toString() + "\n");
                }
                writer.write(String.format("\nTotal: %.2f RON\n", totalPrice));
                writer.write("Ticket issued on: " + timestamp + "\n");
                writer.write("=====================\n");
            }

        } catch (IOException e) {
            showAlert("Error writing order to file.");
            return;
        }

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

    private void showAlertAndExit(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();  // Wait for user to close the alert
        System.exit(0);       // Exit application
    }

    private void generateDishReview() {
        File folder = new File("orders");
        if (!folder.exists() || folder.listFiles() == null) {
            showAlert("No orders found to generate review.");
            return;
        }

        Map<String, Integer> dishCount = new HashMap<>();
        for (Dish dish : menuItems) dishCount.put(dish.getName(), 0);

        File[] files = folder.listFiles((dir, name) -> name.endsWith(".txt"));
        if (files != null) {
            for (File file : files) {
                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        for (String name : dishCount.keySet()) {
                            if (line.contains(name)) dishCount.put(name, dishCount.get(name) + 1);
                        }
                    }
                } catch (IOException e) {
                    showAlert("Error reading file: " + file.getName());
                    return;
                }
            }
        }

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dishCount.forEach((dish, count) -> dataset.addValue(count, "Orders", dish));

        JFreeChart chart = ChartFactory.createBarChart(
                "Most Popular Dishes",
                "Dish",
                "Orders",
                dataset
        );

        File outputFile = new File("DishReview.png");
        try {
            ChartUtils.saveChartAsPNG(outputFile, chart, 800, 600);
        } catch (IOException e) {
            showAlert("Error saving DishReview.png");
            return;
        }

        showAlert("Dish review chart generated: DishReview.png");
    }

}
