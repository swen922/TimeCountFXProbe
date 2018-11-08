package com.horovod.timecountfxprobe.view;

import com.horovod.timecountfxprobe.MainApp;
import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.user.AllUsers;
import com.horovod.timecountfxprobe.user.Role;
import com.horovod.timecountfxprobe.user.SecurePassword;
import com.horovod.timecountfxprobe.user.User;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class LoginWindowOnStartController {

    private MainApp mainApp;
    private Stage stage;

    public MainApp getMainApp() {
        return mainApp;
    }

    public void setMainApp(MainApp newMainApp) {
        this.mainApp = newMainApp;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage newStage) {
        this.stage = newStage;
    }

    @FXML
    private Label headLine;

    @FXML
    private TextField loginField;

    @FXML
    private PasswordField passField;

    @FXML
    private Button buttonOK;

    @FXML
    private Button buttonCancel;



    @FXML
    private void initialize() {

        loginField.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                loginField.clear();
            }
        });

        passField.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                passField.clear();
            }
        });

    }


    public void handlOK() {

        while (true) {
            String login = loginField.getText();
            String pass = passField.getText();

            if (login == null || login.isEmpty()) {
                loginField.setText("Введите логин и пароль!");
                passField.setText("");
                break;
            }
            if (pass == null || pass.isEmpty()) {
                loginField.setText("Введите логин и пароль!");
                passField.setText("");
                break;
            }

            User user = AllUsers.getOneUser(login);
            if (user == null) {
                loginField.setText("Неправильно! Введите еще раз логин и пароль!");
                passField.setText("");
                break;
            }
            boolean result = AllUsers.isPassCorrectForUser(user.getIDNumber(), pass);
            if (!result) {
                loginField.setText("Неправильно! Введите еще раз логин и пароль!");
                passField.setText("");
                break;
            }
            else {
                AllUsers.setCurrentUser(user.getIDNumber());
                Role role = user.getRole();
                if (role.equals(Role.DESIGNER)) {
                    this.stage.close();
                    this.mainApp.showTableProjectsDesigner();
                    break;
                }
                else if (role.equals(Role.MANAGER)) {
                    this.stage.close();
                    // TODO аписать класс таблицы для менеджера
                    //this.mainApp.showTableProjectsDesigner();
                    break;
                }
                break;
            }
        }
    }

    public void handleCancel() {
        this.mainApp.closeApp();
    }
}