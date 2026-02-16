package hr.java.financemanagementsystem.controller;

import hr.java.financemanagementsystem.service.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HomeScreenController {
    @FXML
    private Label welcomeMessageLabel;

    public void initialize(){
        welcomeMessageLabel.setText("Hi, " + UserService.getLoggedInUser().getFirstName() + " " + UserService.getLoggedInUser().getLastName() + "!");
    }
}
