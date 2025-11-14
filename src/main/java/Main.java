import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        RestaurantMenu menu = new RestaurantMenu();
        menu.start(primaryStage);   // RestaurantMenu handles the UI
    }

    public static void main(String[] args) {
        launch(args);
    }
}
