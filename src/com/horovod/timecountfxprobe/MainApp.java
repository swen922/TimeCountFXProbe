package com.horovod.timecountfxprobe;

import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.test.Generator;
import com.horovod.timecountfxprobe.user.AllUsers;
import com.horovod.timecountfxprobe.user.Designer;
import com.horovod.timecountfxprobe.user.Role;
import com.horovod.timecountfxprobe.view.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApp extends Application {

    private Stage primaryStage;
    private BorderPane rootLayout;
    private AnchorPane statisticWindow;
    private Stage statStage;
    private StatisticWindowController controller;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Time Count System Probe FX -1");

        /** TODO убрать эту строчку в рабочем варианте */
        Generator.generateUsers();

        initRootLayut();

        if (AllUsers.getCurrentUser() == 0) {
            showLoginWindowOnStart();
        }
        else {
            Role role = AllUsers.getOneUser(AllUsers.getCurrentUser()).getRole();
            if (role.equals(Role.DESIGNER)) {
                showTableProjectsDesigner();
            }
            else if (role.equals(Role.MANAGER)) {
                // TODO аписать класс таблицы для менеджера
                //showTableProjectsManager();
            }
        }

        //showTableProjectsDesigner();
    }

    public void initRootLayut() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
            rootLayout = (BorderPane) loader.load();

            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();

            RootLayoutController controller = loader.getController();
            controller.setMainApp(this);
            AllData.setRootLayout(rootLayout);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showTableProjectsDesigner() {
        try {
            FXMLLoader loader3 = new FXMLLoader();
            loader3.setLocation(MainApp.class.getResource("view/TableProjectsDesigner.fxml"));
            AnchorPane tableDesigner = (AnchorPane) loader3.load();

            rootLayout.setCenter(tableDesigner);

            TableProjectsDesignerController controller = loader3.getController();
            AllData.setTableProjectsDesignerController(controller);
            controller.setMainApp(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showLoginWindow() {
        try {
            FXMLLoader loaderLoginWindow = new FXMLLoader();
            loaderLoginWindow.setLocation(MainApp.class.getResource("view/LoginWindow.fxml"));
            AnchorPane loginWindow = (AnchorPane) loaderLoginWindow.load();

            Stage logWinStage = new Stage();
            logWinStage.setTitle("Вот вам окно логина");
            logWinStage.initModality(Modality.APPLICATION_MODAL);
            logWinStage.initOwner(primaryStage);
            Scene scene = new Scene(loginWindow);
            logWinStage.setScene(scene);
            LoginWindowController loginWindowController = loaderLoginWindow.getController();
            loginWindowController.setMainApp(this);

            loginWindowController.setStage(logWinStage);
            logWinStage.showAndWait();
        } catch (IOException e) {

        }
    }

    public void showLoginWindowOnStart() {
        try {
            FXMLLoader loaderLoginWindowOnStart = new FXMLLoader();
            loaderLoginWindowOnStart.setLocation(MainApp.class.getResource("view/LoginWindowOnStart.fxml"));
            AnchorPane loginWindowOnStart = (AnchorPane) loaderLoginWindowOnStart.load();

            Stage logWinStage = new Stage();
            logWinStage.setTitle("Войти в программу");
            logWinStage.initModality(Modality.APPLICATION_MODAL);
            logWinStage.initOwner(primaryStage);
            Scene scene = new Scene(loginWindowOnStart);
            logWinStage.setScene(scene);
            LoginWindowOnStartController loginWindowOnStartController = loaderLoginWindowOnStart.getController();
            loginWindowOnStartController.setMainApp(this);

            loginWindowOnStartController.setStage(logWinStage);
            logWinStage.showAndWait();
        } catch (IOException e) {

        }
    }

    public void showStatisticWindow() {

        if (statisticWindow == null || statStage == null || controller == null) {
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(MainApp.class.getResource("view/StatisticWindow.fxml"));
                statisticWindow = (AnchorPane) loader.load();
                statStage = new Stage();
                statStage.initModality(Modality.NONE);
                //statStage.initOwner(primaryStage);
                Scene scene = new Scene(statisticWindow);
                statStage.setScene(scene);
                statStage.show();

                StatisticWindowController controller = loader.getController();
                controller.setMainApp(this);
                controller.setStage(statStage);
            } catch (IOException e) {
            }
        }
        else {
            /*statStage.hide();
            statStage.show();*/
            controller.initialize();

        }
    }

    public void closeApp() {
        primaryStage.close();
        System.exit(0);
    }
}
