package com.horovod.timecountfxprobe.view;

import com.horovod.timecountfxprobe.MainApp;
import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.project.Project;
import com.horovod.timecountfxprobe.project.WorkTime;
import com.horovod.timecountfxprobe.user.AllUsers;
import com.horovod.timecountfxprobe.user.Role;
import com.horovod.timecountfxprobe.user.User;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.function.Predicate;

public class TableProjectsManagerController {

    private MainApp mainApp;
    private Stage stage;
    private StatisticWindowController statisticWindowController;

    private ObservableList<Map.Entry<Integer, Project>> showProjects = FXCollections.observableArrayList(AllData.getActiveProjects().entrySet());
    //private ObservableList<Map.Entry<Integer, Project>> allProjects = FXCollections.observableArrayList(AllData.getAllProjects().entrySet());

    private FilteredList<Map.Entry<Integer, Project>> filterData = new FilteredList<>(showProjects, p -> true);
    private Predicate<Map.Entry<Integer, Project>> filterPredicate = new Predicate<Map.Entry<Integer, Project>>() {
        @Override
        public boolean test(Map.Entry<Integer, Project> integerProjectEntry) {
            return true;
        }
    };
    private FilteredList<Map.Entry<Integer, Project>> filterDataWrapper = new FilteredList<>(filterData, filterPredicate);

    private ObservableList<String> datesForChart;
    private ObservableList<XYChart.Data<String, Integer>> workTimeForChart;
    private XYChart.Series<String, Integer> series;

    //private DoubleProperty dayWorkSumProperty = new SimpleDoubleProperty(0);


    @FXML
    private TextField filterField;

    @FXML
    private Button deleteSearchTextButton;

    @FXML
    private CheckBox showArchiveProjectsCheckBox;

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
    private Label todayWorkSumLabel;

    @FXML
    private Label yesterdayWorkSumLabel;

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
    private TableColumn<Map.Entry<Integer, Project>, Void> columnAction;

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


    class ManagerCell extends TableCell<Map.Entry<Integer, Project>, Void> {
        private final Button manageButton = new Button("Инфо");
        private final CheckBox archiveCheckBox = new CheckBox("Архивный");
        private final Button deleteButton = new Button("X");

        @Override
        protected void updateItem(Void item, boolean empty) {
            if (empty) {
                setGraphic(null);
            }
            else {

                Map.Entry<Integer, Project> entry = getTableView().getItems().get(getIndex());

                //System.out.println("entry.getValue().isArchive() = " + entry.getValue().isArchive());

                if (entry.getValue().isArchive()) {
                    archiveCheckBox.setSelected(true);
                    setStyle("-fx-background-color: linear-gradient(#99ccff 0%, #77acff 100%, #e0e0e0 100%);");
                }
                else {
                    archiveCheckBox.setSelected(false);
                    setStyle(null);
                }

                archiveCheckBox.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {


                        if (archiveCheckBox.isSelected()) {

                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                            alert.setTitle("Подтверждение перевода в архив");
                            alert.setHeaderText("Перевести проект id-" + entry.getKey() + " в архив?");

                            Optional<ButtonType> option = alert.showAndWait();

                            if (option.get() == ButtonType.OK) {
                                AllData.changeProjectArchiveStatus(entry.getKey(), true);
                                setStyle("-fx-background-color: linear-gradient(#99ccff 0%, #77acff 100%, #e0e0e0 100%);");
                            }
                            else {
                                AllData.changeProjectArchiveStatus(entry.getKey(), false);
                                setStyle(null);
                            }


                            /*AllData.changeProjectArchiveStatus(entry.getKey(), true);
                            setStyle("-fx-background-color: linear-gradient(#99ccff 0%, #77acff 100%, #e0e0e0 100%);");
                            handleFilters();
                            initialize();*/
                        }
                        else {


                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                            alert.setTitle("Подтверждение перевода в архив");
                            alert.setHeaderText("Вывести проект id-" + entry.getKey() + " из архива?");

                            Optional<ButtonType> option = alert.showAndWait();

                            if (option.get() == ButtonType.OK) {
                                AllData.changeProjectArchiveStatus(entry.getKey(), false);
                                setStyle(null);
                            }
                            else if (option.get() == ButtonType.CANCEL) {
                                AllData.changeProjectArchiveStatus(entry.getKey(), true);
                                setStyle("-fx-background-color: linear-gradient(#99ccff 0%, #77acff 100%, #e0e0e0 100%);");
                            }


                            /*AllData.changeProjectArchiveStatus(entry.getKey(), false);
                            setStyle(null);
                            handleFilters();
                            initialize();*/
                        }
                        handleFilters();
                        initialize();
                    }
                });

                HBox hbox = new HBox();
                hbox.getChildren().addAll(manageButton, archiveCheckBox, deleteButton);
                hbox.setAlignment(Pos.CENTER);
                hbox.setSpacing(10);
                setGraphic(hbox);
            }
        }


        {

            manageButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    System.out.println("it's Manage button!");
                }
            });
            manageButton.setMinHeight(20);
            manageButton.setMaxHeight(20);
            manageButton.setStyle("-fx-font-size:10");



            archiveCheckBox.setStyle("-fx-font-size:10");

            deleteButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    System.out.println("It's Delete Button!");
                }
            });
            deleteButton.setMinHeight(20);
            deleteButton.setMaxHeight(20);
            deleteButton.setStyle("-fx-font-size:10");
        }

    }


    /*class ArchiveRow extends TableRow<Map.Entry<Integer, Project>> {

        @Override
        protected void updateItem(Map.Entry<Integer, Project> item, boolean empty) {
            if (item == null) {
                //setStyle("-fx-background-color: transparent;");
                setStyle(null);
                return;
            }
            if (item.getValue().isArchive()) {
                setStyle("-fx-background-color: linear-gradient(#99ccff 0%, #77acff 100%, #e0e0e0 100%);");
            }
            else {
                //setStyle("-fx-background-color: transparent;");
                setStyle(null);
            }
        }
    }*/





    @FXML
    public void initialize() {

        // Отработка методов данных
        AllData.deleteZeroTime();
        AllData.rebuildTodayWorkSumProperty();
        AllData.rebuildYesterdayWorkSumProperty();
        //AllData.rebuildDesignerWeekWorkSumProperty(today.getYear(), today.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear()));
        //AllData.rebuildDesignerMonthWorkSumProperty(today.getYear(), today.getMonth().getValue());
        //AllData.rebuildDesignerYearWorkSumProperty(today.getYear());

        handleFilters();

        sortTableProjects();

        columnAction.setCellValueFactory(new PropertyValueFactory<>(""));

        columnAction.setCellFactory(new Callback<TableColumn<Map.Entry<Integer, Project>, Void>, TableCell<Map.Entry<Integer, Project>, Void>>() {
            @Override
            public TableCell<Map.Entry<Integer, Project>, Void> call(TableColumn<Map.Entry<Integer, Project>, Void> param) {
                return new ManagerCell();
            }
        });

        columnID.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<Integer, Project>, Integer>, ObservableValue<Integer>>() {
            @Override
            public ObservableValue<Integer> call(TableColumn.CellDataFeatures<Map.Entry<Integer, Project>, Integer> param) {
                return param.getValue().getValue().idNumberProperty().asObject();
            }
        });

        columnID.setStyle("-fx-alignment: CENTER;");

        columnTime.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<Integer, Project>, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Map.Entry<Integer, Project>, String> param) {
                // Для списка менеджера – просто все рабочее время
                //return param.getValue().getValue().workSumProperty();

                int time = param.getValue().getValue().getWorkSum();
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


        filterDataWrapper.setPredicate(filterPredicate);

        SortedList<Map.Entry<Integer, Project>> sortedList = new SortedList<>(filterDataWrapper, new Comparator<Map.Entry<Integer, Project>>() {
            @Override
            public int compare(Map.Entry<Integer, Project> o1, Map.Entry<Integer, Project> o2) {
                //return compareTime(o1, o2);
                return Integer.compare(o1.getKey(), o2.getKey());
            }
        });

        projectsTable.setItems(sortedList);
        sortedList.comparatorProperty().bind(projectsTable.comparatorProperty());

        todayWorkSumLabel.textProperty().bind(AllData.todayWorkSumProperty().asString());
        yesterdayWorkSumLabel.textProperty().bind(AllData.yesterdayWorkSumProperty().asString());
        AllData.rebuildDesignerRatingPosition();

        initializeChart();
        initLoggedUsersChoiceBox();

    }


    public void addPredicateToFilter() {

        filterPredicate = new Predicate<Map.Entry<Integer, Project>>() {
            @Override
            public boolean test(Map.Entry<Integer, Project> integerProjectEntry) {

                String newValue = filterField.getText();

                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase().trim();

                String workTimeInTable = "0.0";
                if (integerProjectEntry.getValue().containsWorkTime()) {
                    workTimeInTable = String.valueOf(AllData.intToDouble(integerProjectEntry.getValue().getWorkSum()));
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
        };

        initialize();
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

        List<Project> myProjects = AllData.getAllProjectsForPeriodWorking(from, till);
        Map<String, Integer> decadeWorkSums = new TreeMap<>();

        for (Project p : myProjects) {

            for (WorkTime wt : p.getWork()) {
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

        workTimeForChart.clear();

        for (String s : datesForChart) {
            workTimeForChart.add(new XYChart.Data<>(s, decadeWorkSums.get(s)));
        }
    }



    public void handleDeleteSearch() {
        filterField.setText("");
        initialize();
    }


    public void sortTableProjects() {

        showProjects.sort(new Comparator<Map.Entry<Integer, Project>>() {
            @Override
            public int compare(Map.Entry<Integer, Project> o1, Map.Entry<Integer, Project> o2) {
                //return compareTime(o1, o2);
                return Integer.compare(o2.getKey(), o1.getKey());
            }
        });
    }



    /** Три метода для чекбокса и двух дейтпикеров
     * сначала запрашивают проверку дат на валидность,
     * затем передают дальнейшее действие методу handleFilters()
     * который фильтрует filterData в зависимости от состояния
     * чекбокса и дейтпикеров.
     * */


    public void handleShowArchiveProjectsCheckBox() {
        handleFilters();
        initialize();
    }

    public void handleFromDatePicker() {
        checkDatePicker(fromDatePicker);
        handleFilters();
        initialize();
    }

    public void handleTillDatePicker() {
        checkDatePicker(tillDatePicker);
        handleFilters();
        initialize();
    }

    private void checkDatePicker(Node node) {
        LocalDate fromDate = fromDatePicker.getValue();
        LocalDate tillDate = tillDatePicker.getValue();

        if (fromDate != null && tillDate != null) {

            if (fromDate.compareTo(tillDate) > 0) {
                if (node == fromDatePicker) {
                    fromDatePicker.setValue(null);
                }
                else {
                    tillDatePicker.setValue(null);
                }
            }
        }
    }


    private void handleFilters() {

        LocalDate fromDate = fromDatePicker.getValue();
        LocalDate tillDate = tillDatePicker.getValue();

        if (fromDate != null && tillDate != null) {

            if (showArchiveProjectsCheckBox.isSelected()) {
                showProjects = FXCollections.observableArrayList(AllData.getAllProjects().entrySet());
            }
            else {
                showProjects = FXCollections.observableArrayList(AllData.getActiveProjects().entrySet());
            }

            sortTableProjects();

            filterData = new FilteredList<>(this.showProjects, new Predicate<Map.Entry<Integer, Project>>() {
                @Override
                public boolean test(Map.Entry<Integer, Project> integerProjectEntry) {
                    if (integerProjectEntry.getValue().containsWorkTime(fromDate, tillDate)) {
                        return true;
                    }
                    return false;
                }
            });
            filterDataWrapper = new FilteredList<>(filterData, p -> true);
        }
        else {
            if (showArchiveProjectsCheckBox.isSelected()) {
                showProjects = FXCollections.observableArrayList(AllData.getAllProjects().entrySet());
            }
            else {
                showProjects = FXCollections.observableArrayList(AllData.getActiveProjects().entrySet());

            }

            sortTableProjects();

            filterData = new FilteredList<>(this.showProjects, p -> true);
            filterDataWrapper = new FilteredList<>(filterData, p -> true);
        }

        projectsTable.refresh();
    }



    public void handleDeleteDatePicker() {
        fromDatePicker.setValue(null);
        tillDatePicker.setValue(null);
        handleFilters();
        initialize();
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



    /*public void testAdd() {
        TestBackgroundUpdate01 testBackgroundUpdate01 = new TestBackgroundUpdate01();
        testBackgroundUpdate01.testBackgroundAddTime();
    }

    public void testDelete() {
        TestBackgroundUpdate01 testBackgroundUpdate01 = new TestBackgroundUpdate01();
        testBackgroundUpdate01.testBackgroundDeleteTime();
    }*/


}
