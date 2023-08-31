package application;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
	    FXMLLoader welcomeLoader = new FXMLLoader(getClass().getResource("welcome.fxml"));
	    Parent welcomeRoot = welcomeLoader.load();
	    Stage welcomeStage = new Stage();
	    welcomeStage.setTitle("Welcome");
	    Scene scene1 = new Scene(welcomeRoot, 600, 400);
        scene1.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
	    welcomeStage.setScene(scene1);
	    welcomeStage.show();

	    WelcomeController welcomeController = welcomeLoader.getController();

	    welcomeController.setOnStartButtonClicked(event -> {
	        welcomeStage.close();

	        try {
	            FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("sample.fxml"));
	            Parent root = mainLoader.load();
	            Stage mainStage = new Stage();
	            mainStage.setTitle("CPU Scheduler");
	            mainStage.getIcons().add(new Image(getClass().getResourceAsStream("icon.png")));
	            Scene scene = new Scene(root, 850, 450);
	            scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
	            mainStage.setScene(scene);
	            mainStage.show();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    });
	}
	 public static void main(String[] args) {
	        launch(args);
	    }

}
