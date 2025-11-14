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

    // Load menu dynamically from Menu.txt
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

        // MENU ITEMS WITH HOVER + CLICK
        for (Dish dish : menuItems) {
            Label label = new Label(dish.toString());
            label.setStyle("-fx-font-size: 16px; -fx-padding: 5px;");
            labels.add(label);

            // add on click
            label.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> addItem(dish));

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

        // MOST POPULAR DISH BUTTON
        Button mostPopularButton = new Button("Most Popular Dish");
        mostPopularButton.setStyle("-fx-font-size: 16px;");
        mostPopularButton.setOnAction(e -> generateDishReview());
        root.getChildren().add(mostPopularButton);

        Scene scene = new Scene(root, 450, 650);
        stage.setScene(scene);
        stage.setTitle("Restaurant Menu");
        stage.show();
    }

    // Add dish to ListView
    private void addItem(Dish dish) {
        selectedList.getItems().add(dish);
        totalPrice += dish.getPrice();
        updateTotal();
    }

    // Remove currently selected item
    private void removeSelectedItem() {
        Dish selected = selectedList.getSelectionModel().getSelectedItem();
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
            if (!folder.exists()) folder.mkdir();

            // Create timestamped filename
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy(HH:mm)");
            String timestamp = LocalDateTime.now().format(formatter);
            File orderFile = new File(folder, "Order:" + timestamp + ".txt");

            // Write the order to the file
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

    // Show alert and exit app
    private void showAlertAndExit(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();  // Wait for user to close the alert
        System.exit(0);       // Exit application
    }

    // Generate dish review file
    private void generateDishReview() {
        File folder = new File("orders");
        if (!folder.exists() || folder.listFiles() == null) {
            showAlert("No orders found to generate review.");
            return;
        }

        // Count number of orders for each dish
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

        // Create JFreeChart dataset
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dishCount.forEach((dish, count) -> dataset.addValue(count, "Orders", dish));

        // Create bar chart
        JFreeChart chart = ChartFactory.createBarChart(
                "Most Popular Dishes",
                "Dish",
                "Orders",
                dataset
        );

        // Save chart as PNG
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
