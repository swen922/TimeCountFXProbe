package com.horovod.timecountfxprobe.view;

import com.horovod.timecountfxprobe.MainApp;
import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.project.Project;
import com.horovod.timecountfxprobe.user.AllUsers;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;

public class StatisticWindowController {

    private MainApp mainApp;
    private Stage stage;

    @FXML
    private DatePicker selectDayDatePicker;

    @FXML
    private Button clearSelectDayButton;

    @FXML
    private TextArea selectedDayTextField;

    @FXML
    private TextField projectNumberTextField;

    @FXML
    private Button clearProjectNumberButton;




    public MainApp getMainApp() {
        return mainApp;
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void initialize() {

    }

    /** Все классно, но надо добавить сортировку выдачи по размеру рабочего времени */

    public void handleSelectDayDatePicker() {

        LocalDate localDate = selectDayDatePicker.getValue();
        List<Project> myProjects = null;

        if (localDate != null) {
            myProjects = AllData.getActiveProjectsForDesignerAndDate(AllUsers.getCurrentUser(), localDate);
        }
        if (myProjects == null || myProjects.isEmpty()) {
            selectedDayTextField.clear();
            selectedDayTextField.setText("В этот день у вас нет рабочего времени");
        }
        else {
            int sum = 0;
            StringBuilder sb = new StringBuilder("В этот день у вас было время в проектах:\n");
            sb.append("-------------------\n");
            for (Project p : myProjects) {
                sb.append("id-").append(p.getIdNumber()).append("  —  ");
                sb.append(AllData.formatWorkTime(AllData.intToDouble(p.getWorkSumForDesignerAndDate(AllUsers.getCurrentUser(), localDate))));
                sb.append(" часов\n");
                sum += p.getWorkSumForDesignerAndDate(AllUsers.getCurrentUser(), localDate);
            }
            sb.append("-------------------\n");
            sb.append("Итого  –  ").append(AllData.formatWorkTime(AllData.intToDouble(sum))).append(" часов");
            selectedDayTextField.clear();
            selectedDayTextField.setText(sb.toString());
        }

    }

}
