package com.horovod.timecountfxprobe.view;

import com.horovod.timecountfxprobe.MainApp;
import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.project.Project;
import com.horovod.timecountfxprobe.project.WorkTime;
import com.horovod.timecountfxprobe.user.AllUsers;
import com.horovod.timecountfxprobe.user.Designer;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Callback;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Predicate;

public class TableProjectsDesignerController {

    private int designerID;
    private ObservableList<Map.Entry<Integer, Project>> tableProjects = FXCollections.observableArrayList(AllData.getActiveProjects().entrySet());


    public int getDesignerID() {
        return designerID;
    }

    public void setDesignerID(int newDesignerID) {
        this.designerID = newDesignerID;
    }

    @FXML
    private TextField filterField;

    @FXML
    Button deleteSearchTextButton;

    @FXML
    private TableView<Map.Entry<Integer, Project>> projectsTable;

    @FXML
    private TableColumn<Map.Entry<Integer, Project>, String> columnID;

    @FXML
    private TableColumn<Map.Entry<Integer, Project>, String> columnTime;

    @FXML
    private TableColumn<Map.Entry<Integer, Project>, String> columnCompany;

    @FXML
    private TableColumn<Map.Entry<Integer, Project>, String> columnManager;

    @FXML
    private TableColumn<Map.Entry<Integer, Project>, String> columnDescription;

    public TableProjectsDesignerController() {
        this.designerID = AllUsers.getCurrentUser();
    }


    @FXML
    private void initialize() {

        tableProjects.sort(new Comparator<Map.Entry<Integer, Project>>() {
            @Override
            public int compare(Map.Entry<Integer, Project> o1, Map.Entry<Integer, Project> o2) {
                return compareTime(o1, o2);
            }
        });

        columnID.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<Integer, Project>, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Map.Entry<Integer, Project>, String> param) {
                return param.getValue().getValue().idNumberProperty();
            }
        });

        columnID.setStyle("-fx-alignment: CENTER;");



        Callback<TableColumn<Map.Entry<Integer, Project>, String>, TableCell<Map.Entry<Integer, Project>, String>> cellFactory =
                (TableColumn<Map.Entry<Integer, Project>, String> p) -> new TableProjectsDesignerController.EditingCell();

        columnTime.setCellFactory(cellFactory);

        /** В этом методе заменить ID-номер дизайнера 5 на реальный,
         * который будет браться из свойств аккаунта дизайнера*/
        columnTime.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<Integer, Project>, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Map.Entry<Integer, Project>, String> param) {
                //return param.getValue().getValue().workSumProperty();
                List<WorkTime> timeList = param.getValue().getValue().getWork();
                int time = 0;
                for (WorkTime wt : timeList) {
                    if ((wt.getDesignerID() == designerID) && (AllData.parseDate(wt.getDateString()).equals(LocalDate.now()))) {
                        time = wt.getTime();
                        break;
                    }
                }
                StringProperty result = new SimpleStringProperty(String.valueOf(AllData.intToDouble(time)));
                return result;
            }
        });

        columnTime.setStyle("-fx-alignment: CENTER;");

        columnCompany.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<Integer, Project>, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Map.Entry<Integer, Project>, String> param) {
                return param.getValue().getValue().companyProperty();
            }
        });

        columnManager.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<Integer, Project>, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Map.Entry<Integer, Project>, String> param) {
                return param.getValue().getValue().initiatorProperty();
            }
        });

        columnDescription.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<Integer, Project>, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Map.Entry<Integer, Project>, String> param) {
                return param.getValue().getValue().descriptionProperty();
            }
        });

        FilteredList<Map.Entry<Integer, Project>> filterData = new FilteredList<>(tableProjects, p -> true);

        filterField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                filterData.setPredicate(new Predicate<Map.Entry<Integer, Project>>() {
                    @Override
                    public boolean test(Map.Entry<Integer, Project> integerProjectEntry) {
                        if (newValue == null || newValue.isEmpty()) {
                            return true;
                        }

                        String lowerCaseFilter = newValue.toLowerCase();

                        String workTimeInTable = "0.0";
                        if (integerProjectEntry.getValue().containsWorkTime(designerID, LocalDate.now())) {
                            workTimeInTable = String.valueOf(AllData.intToDouble(integerProjectEntry.getValue().
                                    getWorkTimeForDesignerAndDate(designerID, LocalDate.now())));
                        }

                        if (String.valueOf(integerProjectEntry.getValue().getIdNumber()).contains(lowerCaseFilter)) {
                            return true;
                        }
                        else if (workTimeInTable.contains(lowerCaseFilter)) {
                            return true;
                        }
                        else if (integerProjectEntry.getValue().getCompany().toLowerCase().contains(lowerCaseFilter)) {
                            return true;
                        }
                        else if (integerProjectEntry.getValue().getInitiator().toLowerCase().contains(lowerCaseFilter)) {
                            return true;
                        }
                        else if (integerProjectEntry.getValue().getDescription().toLowerCase().contains(lowerCaseFilter)) {
                            return true;
                        }
                        return false;
                    }
                });
            }
        });

        SortedList<Map.Entry<Integer, Project>> sortedList = new SortedList<>(filterData);

        sortedList.setComparator(new Comparator<Map.Entry<Integer, Project>>() {
            @Override
            public int compare(Map.Entry<Integer, Project> o1, Map.Entry<Integer, Project> o2) {
                return compareTime(o1, o2);
            }
        });

        sortedList.comparatorProperty().bind(projectsTable.comparatorProperty());



        columnTime.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Map.Entry<Integer, Project>, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Map.Entry<Integer, Project>, String> event) {
                double newTimeDouble = Double.parseDouble(event.getNewValue());

                Project project = (Project) event.getTableView().getItems().get(event.getTablePosition().getRow()).getValue();
                AllData.addWorkTime(project.getIdNumber(), LocalDate.now(), designerID, newTimeDouble);
            }
        });

        projectsTable.setItems(sortedList);

    }

    public void handleDeleteSearch() {
        filterField.setText("");
    }


    class EditingCell extends TableCell<Map.Entry<Integer, Project>, String> {

        private TextField textField;

        public EditingCell() {
        }

        @Override
        public void startEdit() {
            if (!isEmpty()) {
                super.startEdit();
                createTextField();
                setText(null);
                setGraphic(textField);
                textField.selectAll();
            }

        }

        @Override
        public void cancelEdit() {
            super.cancelEdit();

            setText((String) getItem());
            setGraphic(null);
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            /*System.out.println("inside updateItem()");
            System.out.println("item is here ======== " + item);*/

            super.updateItem(item, empty);
            if (empty) {

                setText(null);
                setGraphic(null);
            }
            else {
                if (isEditing()) {
                    if (textField != null) {
                        textField.setText(getString());
                    }
                    setText(null);
                    setGraphic(null);
                }
                else {
                    setText(getString());
                    setGraphic(null);
                }
            }
        }

        private void createTextField() {
            String oldText = getString();
            textField = new TextField(oldText);
            textField.setAlignment(Pos.CENTER);
            textField.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
            textField.setOnKeyPressed(new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent event) {
                    KeyCode keyCode = event.getCode();
                    if (keyCode == KeyCode.ENTER) {
                        commitEdit(formatStringInput(oldText, textField.getText()));
                        EditingCell.this.getTableView().requestFocus();
                        EditingCell.this.getTableView().getSelectionModel().selectAll();
                    }
                }
            });
            textField.focusedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    if (!newValue) {
                        commitEdit(formatStringInput(oldText, textField.getText()));
                        EditingCell.this.getTableView().requestFocus();
                        EditingCell.this.getTableView().getSelectionModel().selectAll();
                    }
                }
            });
            EditingCell.this.textField.selectAll();

        }

        private String formatStringInput(String oldText, String input) {
            String newText = input.replaceAll(" ", ".");
            newText = newText.replaceAll("-", ".");
            newText = newText.replaceAll(",", ".");
            newText = newText.replaceAll("=", ".");

            Double newTimeDouble = null;
            try {
                newTimeDouble = Double.parseDouble(newText);
            } catch (NumberFormatException e) {
                return oldText;
            }
            if (newTimeDouble != null) {
                newText = String.valueOf(AllData.formatDouble(newTimeDouble));
                return newText;
            }

            return oldText;
        }

        private String getString() {
            return getItem() == null ? "" : getItem().toString();
        }
    }


    private int compareTime(Map.Entry<Integer, Project> o1, Map.Entry<Integer, Project> o2) {

        List<WorkTime> timeList1 = o1.getValue().getWork();
        List<WorkTime> timeList2 = o2.getValue().getWork();

        int time1 = 0;
        int time2 = 0;

        for (WorkTime wt1 : timeList1) {
            if ((wt1.getDesignerID() == designerID) && (AllData.parseDate(wt1.getDateString()).equals(LocalDate.now()))) {
                time1 = wt1.getTime();
                break;
            }
        }

        for (WorkTime wt2 : timeList2) {
            if ((wt2.getDesignerID() == designerID) && (AllData.parseDate(wt2.getDateString()).equals(LocalDate.now()))) {
                time2 = wt2.getTime();
                break;
            }
        }
        return Integer.compare(time2, time1);
    }

}
