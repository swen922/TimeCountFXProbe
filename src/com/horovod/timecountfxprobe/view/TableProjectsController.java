package com.horovod.timecountfxprobe.view;

import com.horovod.timecountfxprobe.MainApp;
import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.project.Project;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;

import java.util.Comparator;

public class TableProjectsController {

    private MainApp mainApp;


    /**
     * TODO здесь противоречие в данных:
     * для таблицы нужны Propertie типа StringProperty
     * так что надо либо переделывать классы с данными,
     * либо делать промежуточные классы-посредники между данными и таблицей
     * */
    private ObservableList<Project> tableProjects = FXCollections.observableArrayList();

    @FXML
    private TableView<Project> projectsTable;

    @FXML
    private TableColumn<Project, Integer> columnID;

    @FXML
    private TableColumn<Project, Double> columnTime;

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
    private void initialize() {

        columnID.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Project, Integer>, ObservableValue<Integer>>() {
            @Override
            public ObservableValue<Integer> call(TableColumn.CellDataFeatures<Project, Integer> param) {
                return param.getValue().idNumberProperty().asObject();
            }
        });

        columnID.setCellFactory(new Callback<TableColumn<Project, Integer>, TableCell<Project, Integer>>() {
            @Override
            public TableCell<Project, Integer> call(TableColumn<Project, Integer> param) {
                TableCell cell = new TableCell() {
                    @Override
                    protected void updateItem(Object item, boolean empty) {
                        if (item != null) {
                            setText(String.valueOf(item));
                        }
                    }
                };
                cell.setAlignment(Pos.BASELINE_CENTER);
                return cell;
            }
        });

        columnTime.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Project, Double>, ObservableValue<Double>>() {
            @Override
            public ObservableValue<Double> call(TableColumn.CellDataFeatures<Project, Double> param) {
                return param.getValue().workSumProperty().asObject();
            }
        });

        columnTime.setCellFactory(new Callback<TableColumn<Project, Double>, TableCell<Project, Double>>() {
            @Override
            public TableCell<Project, Double> call(TableColumn<Project, Double> param) {
                TableCell cell = new TableCell() {
                    @Override
                    protected void updateItem(Object item, boolean empty) {
                        if (item != null) {
                            setText(String.valueOf(item));
                        }
                    }
                };
                cell.setAlignment(Pos.BASELINE_CENTER);

                return cell;
            }
        });

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



        projectsTable.setItems(tableProjects);

    }
}
