package com.horovod.timecountfxprobe.view;

import com.horovod.timecountfxprobe.MainApp;
import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.project.Project;
import com.horovod.timecountfxprobe.project.WorkTime;
import com.horovod.timecountfxprobe.test.Generator;
import com.horovod.timecountfxprobe.test.TestBackgroundUpdate01;
import com.horovod.timecountfxprobe.user.AllUsers;
import com.horovod.timecountfxprobe.user.Designer;
import com.horovod.timecountfxprobe.user.Role;
import com.horovod.timecountfxprobe.user.User;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.function.Predicate;

public class TableProjectsDesignerController {

    private MainApp mainApp;
    private Stage stage;
    private StatisticWindowController statisticWindowController;

    private ObservableList<Map.Entry<Integer, Project>> tableProjects = FXCollections.observableArrayList(AllData.getActiveProjects().entrySet());
    private FilteredList<Map.Entry<Integer, Project>> filterData = new FilteredList<>(tableProjects, p -> true);

    private ObservableList<String> datesForChart;
    private ObservableList<XYChart.Data<String, Integer>> workTimeForChart;
    private XYChart.Series<String, Integer> series;

    //private DoubleProperty dayWorkSumProperty = new SimpleDoubleProperty(0);


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
    private LineChart<String, Integer> decadeLineChart;

    @FXML
    private CategoryAxis xAxis;

    @FXML
    private Label dayWorkSumLabel;

    @FXML
    private Label ratingPositionLabel;

    @FXML
    private Button buttonReload;

    @FXML
    private Button buttonStatistic;

    @FXML
    private Label aboutProgramLabel;

    @FXML
    private ChoiceBox<String> usersLoggedChoiceBox;

    @FXML
    private Label statusLabel;



    /** Таблица и ее колонки */

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


    /** Временные кнопки под тестирование */

    /*@FXML
    private Button testAddButton;

    @FXML
    private Button testDeleteButton;*/


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

    public TextField getFilterField() {
        return filterField;
    }

    public StatisticWindowController getStatisticWindowController() {
        return statisticWindowController;
    }

    public void setStatisticWindowController(StatisticWindowController statisticWindowController) {
        this.statisticWindowController = statisticWindowController;
    }



    @FXML
    public void initialize() {

        /** В чистовой версии перенести все поля по суммам рабочего времени за день, неделю, месяц и год
         * сюда внутрь класса, а в AllData оставить только глобальные суммы по всем дизайнерам */

        // Отработка методов данных
        LocalDate today = LocalDate.now();
        AllData.deleteZeroTime();
        AllData.rebuildDesignerDayWorkSumProperty();
        AllData.rebuildDesignerWeekWorkSumProperty(today.getYear(), today.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear()));
        AllData.rebuildDesignerMonthWorkSumProperty(today.getYear(), today.getMonth().getValue());
        AllData.rebuildDesignerYearWorkSumProperty(today.getYear());


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

                        String lowerCaseFilter = newValue.toLowerCase().trim();

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

        dayWorkSumLabel.textProperty().bind(AllData.designerDayWorkSumProperty().asString());
        ratingPositionLabel.textProperty().bind(AllData.designerRatingPositionProperty().asString());
        AllData.rebuildDesignerRatingPosition();

        initializeChart();
        initLoggedUsersChoiceBox();

    }

    // Может, перенести весь метод в AllData или в другой общий класс?

    public void initLoggedUsersChoiceBox() {

        String toLoginWindow = "Выйти в окно логина";

        usersLoggedChoiceBox.setItems(AllUsers.getUsersLogged());

        if (!usersLoggedChoiceBox.getItems().contains(toLoginWindow)) {
            usersLoggedChoiceBox.getItems().add(toLoginWindow);
        }

        usersLoggedChoiceBox.setValue(AllUsers.getOneUser(AllUsers.getCurrentUser()).getFullName());

        usersLoggedChoiceBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                String selectUser = usersLoggedChoiceBox.getValue();

                if (selectUser != null) {
                    if (selectUser.equalsIgnoreCase(toLoginWindow)) {

                        AllData.getRootLayout().setCenter(null);
                        mainApp.showLoginWindow();
                    }
                    else if (!selectUser.equalsIgnoreCase(AllUsers.getOneUser(AllUsers.getCurrentUser()).getFullName())) {
                        User user = AllUsers.getOneUserForFullName(selectUser);

                        Role role = user.getRole();
                        if (role.equals(Role.DESIGNER)) {
                            AllData.getRootLayout().setCenter(null);
                            AllUsers.setCurrentUser(user.getIDNumber());
                            initialize();
                            mainApp.showTableProjectsDesigner();
                            if (AllData.getStatStage() != null) {
                                if (AllData.getStatStage().isShowing()) {
                                    mainApp.showStatisticWindow();
                                }
                            }


                        }
                        else if (role.equals(Role.MANAGER)) {
                            AllData.getRootLayout().setCenter(null);
                            AllUsers.setCurrentUser(user.getIDNumber());
                            initialize();
                            mainApp.showTableProjectsManager();

                            // Переписать для окна статистики менеджера
                            if (AllData.getStatStage() != null) {
                                if (AllData.getStatStage().isShowing()) {
                                    mainApp.showStatisticWindow();
                                }
                            }
                        }
                    }
                }
            }
        });
    }



    /** Три метода для инициализации / обновления лайнчарта
     * Один метод – диспетчер, два других служебные для него */

    private void initializeChart() {

        decadeLineChart.setStyle("-fx-font-size: " + 1 + "px;");

        xAxis.tickLabelFontProperty().set(Font.font(5.0));
        xAxis.setLabel("");

        LocalDate from = LocalDate.now().minusDays(15);
        LocalDate till = LocalDate.now().minusDays(1);

        if (datesForChart == null) {
            datesForChart = FXCollections.observableArrayList();
            xAxis.setCategories(datesForChart);
        }

        fillDatesChart(from, till);

        if (workTimeForChart == null) {
            workTimeForChart = FXCollections.observableArrayList();
        }

        if (series == null) {
            series = new XYChart.Series<>();
            series.setData(workTimeForChart);
            series.setName(null);
            decadeLineChart.getData().add(series);
        }

        fillXYChartSeries(from, till);

    }

    private void fillDatesChart(LocalDate from, LocalDate till) {

        List<Project> decadeProjects = AllData.getAllProjectsForPeriodWorking(from, till);

        TreeSet<String> setForSorting = new TreeSet<>();
        for (Project p : decadeProjects) {
            for (WorkTime wt : p.getWork()) {
                if (wt.getDate().compareTo(from) >= 0 && wt.getDate().compareTo(till) <= 0) {
                    setForSorting.add(wt.getDateString());
                }
            }
        }

        datesForChart.clear();
        datesForChart.addAll(setForSorting);

    }


    private void fillXYChartSeries(LocalDate from, LocalDate till) {

        List<Project> myProjects = AllData.getAllProjectsForDesignerAndPeriodWorking(AllUsers.getCurrentUser(), from, till);
        Map<String, Integer> decadeWorkSums = new TreeMap<>();

        for (Project p : myProjects) {
            for (WorkTime wt : p.getWork()) {
                if (wt.getDesignerID() == AllUsers.getCurrentUser()) {
                    String dateWork = wt.getDateString();
                    if (decadeWorkSums.containsKey(dateWork)) {
                        int currentSum = decadeWorkSums.get(dateWork);
                        decadeWorkSums.put(dateWork, (currentSum + wt.getTime()));
                    }
                    else {
                        decadeWorkSums.put(dateWork, wt.getTime());
                    }
                }
            }
        }

        workTimeForChart.clear();

        for (String s : datesForChart) {
            if (decadeWorkSums.containsKey(s)) {
                workTimeForChart.add(new XYChart.Data<>(s, decadeWorkSums.get(s)));
            }
            else {
                workTimeForChart.add(new XYChart.Data<>(s, 0));
            }
        }
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

    private void checkDatePicker(Node node) {
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


    private void handleFilters() {

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
                //projectsTable.refresh();

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
                //projectsTable.refresh();
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

    public void handleReloadButton() {
        initialize();
    }

    public void handleStatisticButton() {
        mainApp.showStatisticWindow();
    }

    public void handleAbout() {
        this.mainApp.showAboutWindow();
    }

    public void updateStatus(String message) {
        statusLabel.setText("Статус: " + message);
    }

    public void resetStatus() {
        statusLabel.setText("Статус: все системы работают нормально");
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

    /*public void testAdd() {
        TestBackgroundUpdate01 testBackgroundUpdate01 = new TestBackgroundUpdate01();
        testBackgroundUpdate01.testBackgroundAddTime();
    }

    public void testDelete() {
        TestBackgroundUpdate01 testBackgroundUpdate01 = new TestBackgroundUpdate01();
        testBackgroundUpdate01.testBackgroundDeleteTime();
    }*/



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
                        //projectsTable.refresh();
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
                        //projectsTable.refresh();
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
