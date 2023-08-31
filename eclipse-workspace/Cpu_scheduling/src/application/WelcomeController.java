package application;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class WelcomeController {

    @FXML
    private Button startButton;
    @FXML


    private EventHandler<ActionEvent> onStartButtonClicked;

    public void setOnStartButtonClicked(EventHandler<ActionEvent> handler) {
        onStartButtonClicked = handler;
    }

    @FXML
    private void handleStartButton(ActionEvent event) {
        if (onStartButtonClicked != null) {
            onStartButtonClicked.handle(event);
        }
    }
}
