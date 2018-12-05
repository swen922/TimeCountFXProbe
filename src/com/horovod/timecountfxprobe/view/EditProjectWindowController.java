package com.horovod.timecountfxprobe.view;

import com.horovod.timecountfxprobe.MainApp;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class EditProjectWindowController {

    private MainApp mainApp;
    private Stage editStage;
    private int idNumber;


    @FXML
    private Label idNumberLabel;

    @FXML
    private TextArea projectNameTextArea;

    @FXML
    private CheckBox archiveCheckBox;

    @FXML
    private AnchorPane topColoredPane;

    @FXML
    private TextArea companyName;



    public MainApp getMainApp() {
        return mainApp;
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public Stage getEditStage() {
        return editStage;
    }

    public void setEditStage(Stage editStage) {
        this.editStage = editStage;
    }

    public int getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(int idNumber) {
        this.idNumber = idNumber;
    }
}
