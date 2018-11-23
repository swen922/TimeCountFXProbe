package com.horovod.timecountfxprobe.view;

import com.horovod.timecountfxprobe.MainApp;
import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.project.Project;
import com.horovod.timecountfxprobe.project.WorkTime;
import com.horovod.timecountfxprobe.user.AllUsers;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.*;

public class StatisticWindowController {

    private MainApp mainApp;
    private Stage stage;

    @FXML
    private DatePicker selectDayDatePicker;

    @FXML
    private Button clearSelectDayButton;

    @FXML
    private TextArea selectedDayTextArea;

    @FXML
    private TextField projectNumberTextField;

    @FXML
    private Button clearProjectNumberButton;

    @FXML
    private TextArea projectNumberTextArea;




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


    public void handleSelectDayDatePicker() {

        LocalDate localDate = selectDayDatePicker.getValue();
        List<Project> myProjects = null;
        selectedDayTextArea.clear();

        if (localDate != null) {
            myProjects = AllData.getActiveProjectsForDesignerAndDate(AllUsers.getCurrentUser(), localDate);
        }
        if (myProjects == null || myProjects.isEmpty()) {
            selectedDayTextArea.setText("В этот день у меня нет рабочего времени");
        }
        else {

            myProjects.sort(new Comparator<Project>() {
                @Override
                public int compare(Project o1, Project o2) {
                    int o1WorkSum = o1.getWorkSumForDesignerAndDate(AllUsers.getCurrentUser(), localDate);
                    int o2WorkSum = o2.getWorkSumForDesignerAndDate(AllUsers.getCurrentUser(), localDate);
                    return Integer.compare(o2WorkSum, o1WorkSum);
                }
            });

            int sum = 0;
            StringBuilder sb = new StringBuilder("В этот день у меня было время в проектах:\n");
            sb.append("-------------------\n");
            for (Project p : myProjects) {
                sb.append("id-").append(p.getIdNumber()).append("  —  ");
                String hour = AllData.formatWorkTime(AllData.intToDouble(p.getWorkSumForDesignerAndDate(AllUsers.getCurrentUser(), localDate)));
                sb.append(hour);
                sb.append(" ").append(AllData.formatHours(hour)).append("\n");
                sum += p.getWorkSumForDesignerAndDate(AllUsers.getCurrentUser(), localDate);
            }
            sb.append("-------------------\n");
            String hourSum = AllData.formatWorkTime(AllData.intToDouble(sum));
            sb.append("Итого  –  ").append(hourSum).append(" ").append(AllData.formatHours(hourSum));
            selectedDayTextArea.setText(sb.toString());
        }
    }

    public void handleClearSelectDayButton() {
        selectDayDatePicker.setValue(null);
        selectedDayTextArea.clear();
    }

    public void handleProjectNumberTextField() {

        projectNumberTextArea.clear();
        String projectString = projectNumberTextField.getText();
        Integer projectIDnumber;
        if (projectString != null && !projectString.isEmpty()) {
            try {
                projectIDnumber = Integer.parseInt(projectString);
            } catch (NumberFormatException e) {
                projectNumberTextArea.setText("Введите корректный номер проекта");
                return;
            }

            if (!AllData.isProjectExist(projectIDnumber)) {
                projectNumberTextArea.setText("Такого проекта не существует, либо он удален в архив");
            }
            else {

                List<WorkTime> myWorks = AllData.getOneActiveProject(projectIDnumber).getWorkTimeForDesigner(AllUsers.getCurrentUser());

                if (myWorks.isEmpty()) {
                    projectNumberTextArea.setText("В этом проекте у меня нет рабочего времени");
                }
                else {

                    myWorks.sort(new Comparator<WorkTime>() {
                        @Override
                        public int compare(WorkTime o1, WorkTime o2) {
                            return o2.getDateString().compareTo(o1.getDateString());
                        }
                    });

                    int sum = 0;
                    StringBuilder sb = new StringBuilder("В этом проекте у меня было следуюшее рабочее время:\n");
                    sb.append("-------------------\n");
                    for (WorkTime wt : myWorks) {
                        sb.append(wt.getDateString()).append("  —  ");
                        String hour = AllData.formatWorkTime(AllData.intToDouble(wt.getTime()));
                        sb.append(hour).append(" ").append(AllData.formatHours(hour)).append("\n");
                        sum += wt.getTime();
                    }
                    sb.append("-------------------\n");
                    String hourSum = AllData.formatWorkTime(AllData.intToDouble(sum));
                    sb.append("Итого  –  ").append(hourSum).append(" ").append(AllData.formatHours(hourSum));
                    projectNumberTextArea.setText(sb.toString());

                }
            }
        }
    }

    public void handleClearProjectNumberButton() {
        projectNumberTextField.setText("");
        projectNumberTextArea.clear();
    }

    public void handleProjctBar() {
        LocalDate date = LocalDate.now();
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
    }






}
