package com.horovod.timecountfxprobe.view;

import com.horovod.timecountfxprobe.MainApp;
import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.project.Project;
import com.horovod.timecountfxprobe.project.WorkTime;
import com.horovod.timecountfxprobe.test.TestBackgroundUpdate01;
import com.horovod.timecountfxprobe.user.AllUsers;
import com.horovod.timecountfxprobe.user.Designer;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Callback;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Predicate;

public class TableProjectsDesignerController {

    private ObservableList<Map.Entry<Integer, Project>> tableProjects = FXCollections.observableArrayList(AllData.getActiveProjects().entrySet());
    FilteredList<Map.Entry<Integer, Project>> filterData = new FilteredList<>(tableProjects, p -> true);


    @FXML
    private TextField filterField;

    @FXML
    private Button deleteSearchTextButton;

    @FXML
    private CheckBox showMyProjectsCheckBox;

    @FXML
    private DatePicker fromDatePicker;

    @FXML
    private DatePicker tillDatePicker;

    @FXML
    private Button clearDatePicker;

    @FXML
    private LineChart decadeLineChart;

    @FXML
    private CategoryAxis xAxis;

    @FXML
    private Button testAddButton;

    @FXML
    private Button testDeleteButton;


    @FXML
    private TableView<Map.Entry<Integer, Project>> projectsTable;

    @FXML
    private TableColumn<Map.Entry<Integer, Project>, Integer> columnID;

    @FXML
    private TableColumn<Map.Entry<Integer, Project>, String> columnTime;

    @FXML
    private TableColumn<Map.Entry<Integer, Project>, String> columnCompany;

    @FXML
    private TableColumn<Map.Entry<Integer, Project>, String> columnManager;

    @FXML
    private TableColumn<Map.Entry<Integer, Project>, String> columnDescription;



    @FXML
    private void initialize() {

        sortTableProjects();

        columnID.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<Integer, Project>, Integer>, ObservableValue<Integer>>() {
            @Override
            public ObservableValue<Integer> call(TableColumn.CellDataFeatures<Map.Entry<Integer, Project>, Integer> param) {
                return param.getValue().getValue().idNumberProperty().asObject();
            }
        });

        columnID.setStyle("-fx-alignment: CENTER;");


        Callback<TableColumn<Map.Entry<Integer, Project>, String>, TableCell<Map.Entry<Integer, Project>, String>> cellFactory =
                (TableColumn<Map.Entry<Integer, Project>, String> p) -> new TableProjectsDesignerController.EditingCell();

        columnTime.setCellFactory(cellFactory);

        columnTime.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<Integer, Project>, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Map.Entry<Integer, Project>, String> param) {
                // Для списка менеджера – просто все рабочее время
                //return param.getValue().getValue().workSumProperty();

                int time = param.getValue().getValue().getWorkSumForDesignerAndDate(AllUsers.getCurrentUser(), LocalDate.now());
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

        columnCompany.setCellFactory(new Callback<TableColumn<Map.Entry<Integer, Project>, String>, TableCell<Map.Entry<Integer, Project>, String>>() {
            @Override
            public TableCell<Map.Entry<Integer, Project>, String> call(TableColumn<Map.Entry<Integer, Project>, String> param) {
                return getTableCell(columnCompany, TextAlignment.CENTER);
            }
        });

        columnCompany.setStyle("-fx-alignment: CENTER;");

        columnManager.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<Integer, Project>, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Map.Entry<Integer, Project>, String> param) {
                return param.getValue().getValue().initiatorProperty();
            }
        });

        columnManager.setCellFactory(new Callback<TableColumn<Map.Entry<Integer, Project>, String>, TableCell<Map.Entry<Integer, Project>, String>>() {
            @Override
            public TableCell<Map.Entry<Integer, Project>, String> call(TableColumn<Map.Entry<Integer, Project>, String> param) {
                return getTableCell(columnManager, TextAlignment.CENTER);
            }
        });

        columnManager.setStyle("-fx-alignment: CENTER;");

        columnDescription.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<Integer, Project>, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Map.Entry<Integer, Project>, String> param) {
                return param.getValue().getValue().descriptionProperty();
            }
        });

        columnDescription.setCellFactory(new Callback<TableColumn<Map.Entry<Integer, Project>, String>, TableCell<Map.Entry<Integer, Project>, String>>() {
            @Override
            public TableCell<Map.Entry<Integer, Project>, String> call(TableColumn<Map.Entry<Integer, Project>, String> param) {
                return getTableCell(columnDescription, TextAlignment.LEFT);
            }
        });



        FilteredList<Map.Entry<Integer, Project>> filterDataWrapper = new FilteredList<>(filterData, p -> true);

        filterField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                filterDataWrapper.setPredicate(new Predicate<Map.Entry<Integer, Project>>() {
                    @Override
                    public boolean test(Map.Entry<Integer, Project> integerProjectEntry) {
                        if (newValue == null || newValue.isEmpty()) {
                            return true;
                        }

                        String lowerCaseFilter = newValue.toLowerCase();

                        String workTimeInTable = "0.0";
                        if (integerProjectEntry.getValue().containsWorkTime(AllUsers.getCurrentUser(), LocalDate.now())) {
                            workTimeInTable = String.valueOf(AllData.intToDouble(integerProjectEntry.getValue().
                                    getWorkSumForDesignerAndDate(AllUsers.getCurrentUser(), LocalDate.now())));
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
                sortTableProjects();
            }
        });


        columnTime.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Map.Entry<Integer, Project>, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Map.Entry<Integer, Project>, String> event) {
                double newTimeDouble = Double.parseDouble(event.getNewValue());

                Project project = (Project) event.getTableView().getItems().get(event.getTablePosition().getRow()).getValue();
                AllData.addWorkTime(project.getIdNumber(), LocalDate.now(), AllUsers.getCurrentUser(), newTimeDouble);
                filterField.setText("-");
                filterField.clear();
            }
        });

        SortedList<Map.Entry<Integer, Project>> sortedList = new SortedList<>(filterDataWrapper, new Comparator<Map.Entry<Integer, Project>>() {
            @Override
            public int compare(Map.Entry<Integer, Project> o1, Map.Entry<Integer, Project> o2) {
                return compareTime(o1, o2);
            }
        });


        projectsTable.setItems(sortedList);

        sortedList.comparatorProperty().bind(projectsTable.comparatorProperty());

    }

    public void handleDeleteSearch() {
        filterField.setText("");
    }

    public void sortTableProjects() {
        tableProjects.sort(new Comparator<Map.Entry<Integer, Project>>() {
            @Override
            public int compare(Map.Entry<Integer, Project> o1, Map.Entry<Integer, Project> o2) {
                return compareTime(o1, o2);
            }
        });
    }


    /** Три метода для чекбокса и двух дейтпикеров
     * сначала запрашивают проверку дат на валидность,
     * затем передают дальнейшее действие методу handleFilters()
     * который фильтрует filterData в зависимости от состояния
     * чекбокса и дейтпикеров.
     * */


    public void handleShowMyProjectsCheckBox() {
        checkDatePicker(showMyProjectsCheckBox);
        handleFilters();
    }

    public void handleFromDatePicker() {
        checkDatePicker(fromDatePicker);
        handleFilters();
    }

    public void handleTillDatePicker() {
        checkDatePicker(tillDatePicker);
        handleFilters();
    }

    public void checkDatePicker(Node node) {
        LocalDate fromDate = fromDatePicker.getValue();
        LocalDate tillDate = tillDatePicker.getValue();

        if (fromDate != null && tillDate != null) {

            if (fromDate.compareTo(tillDate) > 0) {
                if (node == showMyProjectsCheckBox) {
                    fromDatePicker.setValue(null);
                    tillDatePicker.setValue(null);
                }
                else if (node == fromDatePicker) {
                    fromDatePicker.setValue(null);
                }
                else if (node == tillDatePicker) {
                    tillDatePicker.setValue(null);
                }
            }
        }
    }


    public void handleFilters() {

        filterField.clear();
        LocalDate fromDate = fromDatePicker.getValue();
        LocalDate tillDate = tillDatePicker.getValue();

        if (fromDate != null && tillDate != null) {

            if (showMyProjectsCheckBox.isSelected()) {
                filterData = new FilteredList<>(this.tableProjects, new Predicate<Map.Entry<Integer, Project>>() {
                    @Override
                    public boolean test(Map.Entry<Integer, Project> integerProjectEntry) {
                        if (integerProjectEntry.getValue().containsWorkTime(AllUsers.getCurrentUser(), fromDate, tillDate)) {
                            return true;
                        }
                        return false;
                    }
                });
                initialize();
            }
            else {
                filterData = new FilteredList<>(this.tableProjects, new Predicate<Map.Entry<Integer, Project>>() {
                    @Override
                    public boolean test(Map.Entry<Integer, Project> integerProjectEntry) {
                        if (integerProjectEntry.getValue().containsWorkTime(fromDate, tillDate)) {
                            return true;
                        }
                        return false;
                    }
                });
                initialize();
            }
        }
        else {
            if (showMyProjectsCheckBox.isSelected()) {
                filterData = new FilteredList<>(this.tableProjects, new Predicate<Map.Entry<Integer, Project>>() {
                    @Override
                    public boolean test(Map.Entry<Integer, Project> integerProjectEntry) {
                        if (integerProjectEntry.getValue().containsWorkTime(AllUsers.getCurrentUser())) {
                            return true;
                        }
                        return false;
                    }
                });
                initialize();
            }
            else {
                filterData = new FilteredList<>(this.tableProjects, p -> true);
                initialize();
            }
        }
    }


    public void handleDeleteDatePicker() {
        fromDatePicker.setValue(null);
        tillDatePicker.setValue(null);
        handleFilters();
    }


    /** Вытащенный в отдельный метод кусок кода из метода initialize()
     * чтобы не повторять один и тот же код несколько раз
     * */

    private TableCell<Map.Entry<Integer, Project>, String> getTableCell(TableColumn column, TextAlignment textAlignment) {
        TableCell<Map.Entry<Integer, Project>, String> cell = new TableCell<>();
        Text text = new Text();
        text.setTextAlignment(textAlignment);
        text.setLineSpacing(5.0);
        cell.setGraphic(text);
        //cell.setPadding(new Insets(10, 10, 10, 10));
        cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
        text.wrappingWidthProperty().bind(column.widthProperty());
        text.textProperty().bind(cell.itemProperty());
        return cell;

        // Это тоже работающий вариант
                /*return new TableCell<Map.Entry<Integer, Project>, String>() {
                    private Text text;
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!isEmpty()) {
                            text = new Text(item.toString());
                            text.setWrappingWidth(columnDescription.getWidth());
                            this.setWrapText(true);
                            setGraphic(text);
                        }
                    }
                };*/
    }

    private int compareTime(Map.Entry<Integer, Project> o1, Map.Entry<Integer, Project> o2) {

        List<WorkTime> timeList1 = o1.getValue().getWork();
        List<WorkTime> timeList2 = o2.getValue().getWork();

        int time1 = 0;
        int time2 = 0;

        for (WorkTime wt1 : timeList1) {
            if ((wt1.getDesignerID() == AllUsers.getCurrentUser()) && (AllData.parseDate(wt1.getDateString()).equals(LocalDate.now()))) {
                time1 = wt1.getTime();
                break;
            }
        }

        for (WorkTime wt2 : timeList2) {
            if ((wt2.getDesignerID() == AllUsers.getCurrentUser()) && (AllData.parseDate(wt2.getDateString()).equals(LocalDate.now()))) {
                time2 = wt2.getTime();
                break;
            }
        }
        return Integer.compare(time2, time1);
    }

    public void testAdd() {
        TestBackgroundUpdate01 testBackgroundUpdate01 = new TestBackgroundUpdate01();
        testBackgroundUpdate01.testBackgroundAddTime();
    }

    public void testDelete() {
        TestBackgroundUpdate01 testBackgroundUpdate01 = new TestBackgroundUpdate01();
        testBackgroundUpdate01.testBackgroundDeleteTime();
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
                        initialize();
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
                        initialize();
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
    } // Конец класса EditingCell

}
