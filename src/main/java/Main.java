import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        RestaurantMenu menu = new RestaurantMenu();
        menu.start(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}


