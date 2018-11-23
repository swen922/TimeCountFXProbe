package com.horovod.timecountfxprobe.view;

import com.horovod.timecountfxprobe.MainApp;
import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.project.Project;
import com.horovod.timecountfxprobe.project.WorkTime;
import com.horovod.timecountfxprobe.user.AllUsers;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.util.Callback;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

public class TableProjectsController {

    private MainApp mainApp;

    private ObservableList<Project> tableProjects = FXCollections.observableArrayList();

    @FXML
    private TextField filterField;

    @FXML
    Button deleteSearchTextButton;

    @FXML
    private TableView<Project> projectsTable;

    @FXML
    private TableColumn<Project, String> columnID;

    @FXML
    private TableColumn<Project, String> columnTime;

    @FXML
    private TableColumn<Project, String> columnCompany;

    @FXML
    private TableColumn<Project, String> columnManager;

    @FXML
    private TableColumn<Project, String> columnDescription;

    public TableProjectsController() {
        AllData.computeProjectsProperties();
        tableProjects.addAll(AllData.getActiveProjects().values());
        tableProjects.sort(new Comparator<Project>() {
            @Override
            public int compare(Project o1, Project o2) {
                int o1ID = o1.getIdNumber();
                int o2ID = o2.getIdNumber();
                return o2ID > o1ID ? 1 : -1;
            }
        });
    }


    public MainApp getMainApp() {
        return mainApp;
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    public void initialize() {

        //filterField.setPrefColumnCount(20);

        /*columnID.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Project, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Project, String> param) {
                return param.getValue().idNumberProperty();
            }
        });*/

        columnID.setStyle("-fx-alignment: CENTER;");



        Callback<TableColumn<Project, String>, TableCell<Project, String>> cellFactory =
                (TableColumn<Project, String> p) -> new EditingCell();

        columnTime.setCellFactory(cellFactory);

        /*columnTime.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Project, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Project, String> param) {
                return param.getValue().workSumProperty();
            }
        });*/

        columnTime.setStyle("-fx-alignment: CENTER;");

        columnCompany.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Project, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Project, String> param) {
                return param.getValue().companyProperty();
            }
        });

        columnManager.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Project, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Project, String> param) {
                return param.getValue().initiatorProperty();
            }
        });

        columnDescription.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Project, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Project, String> param) {
                return param.getValue().descriptionProperty();
            }
        });

        FilteredList<Project> filterData = new FilteredList<>(tableProjects, p -> true);

        filterField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                filterData.setPredicate(new Predicate<Project>() {
                    @Override
                    public boolean test(Project project) {
                        if (newValue == null || newValue.isEmpty()) {
                            return true;
                        }

                        String lowerCaseFilter = newValue.toLowerCase();

                        if (String.valueOf(project.getIdNumber()).contains(lowerCaseFilter)) {
                            return true;
                        }
                        else if (String.valueOf(AllData.intToDouble(project.getWorkSum())).contains(lowerCaseFilter)) {
                            return true;
                        }
                        else if (project.getCompany().toLowerCase().contains(lowerCaseFilter)) {
                            return true;
                        }
                        else if (project.getInitiator().toLowerCase().contains(lowerCaseFilter)) {
                            return true;
                        }
                        else if (project.getDescription().toLowerCase().contains(lowerCaseFilter)) {
                            return true;
                        }
                        return false;
                    }
                });
            }
        });

        SortedList<Project> sortedList = new SortedList<>(filterData);

        sortedList.comparatorProperty().bind(projectsTable.comparatorProperty());





        /*columnTime.setOnEditCommit((TableColumn.CellEditEvent<Project, String> t) -> {
            double newTimeDouble = Double.parseDouble(t.getNewValue());
            WorkTime newWorkTime = new WorkTime(LocalDate.now(), 5, newTimeDouble);
            List<WorkTime> newTimeList = new ArrayList<>();
            newTimeList.addAll(t.getTableView().getItems().get(t.getTablePosition()).getRow().getWork());
            newTimeList.add(newWorkTime);
            ((Project) t.getTableView().getItems().get(t.getTablePosition().getRow())).setWork(newTimeList);
        });*/

        columnTime.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Project, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Project, String> event) {
                double newTimeDouble = Double.parseDouble(event.getNewValue());
                WorkTime newWorkTime = new WorkTime(AllUsers.getCurrentUser(), LocalDate.now(), 5, newTimeDouble);
                List<WorkTime> newTimeList = new ArrayList<>();
                newTimeList.addAll((List<WorkTime>) event.getTableView().getItems().get(event.getTablePosition().getRow()).getWork());
                newTimeList.add(newWorkTime);
                event.getTableView().getItems().get(event.getTablePosition().getRow()).setWork(newTimeList);
            }
        });



        projectsTable.setItems(sortedList);

    }

    public void handleDeleteSearch() {
        filterField.setText("");
    }


    class EditingCell extends TableCell<Project, String> {

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
            textField = new TextField(getString());
            textField.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
            textField.focusedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    if (!newValue) {
                        commitEdit(textField.getText());
                    }
                }
            });
        }

        private String getString() {
            return getItem() == null ? "" : getItem().toString();
        }
    }

}
