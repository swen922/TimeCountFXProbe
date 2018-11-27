package com.horovod.timecountfxprobe.project;

import com.horovod.timecountfxprobe.user.AllUsers;
import com.horovod.timecountfxprobe.view.RootLayoutController;
import com.horovod.timecountfxprobe.view.TableProjectsDesignerController;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.application.Platform;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class AllData {

    private static volatile AtomicInteger idNumber = new AtomicInteger(0);
    private static volatile IntegerProperty idNumberProperty = new SimpleIntegerProperty(idNumber.get());

    private static ObservableMap<Integer, Project> allProjects = FXCollections.synchronizedObservableMap(FXCollections.observableHashMap());
    private static ObservableMap<Integer, Project> activeProjects = FXCollections.synchronizedObservableMap(FXCollections.observableHashMap());

    private static volatile AtomicInteger workSumProjects = new AtomicInteger(0);
    private static volatile IntegerProperty workSumProjectsProperty = new SimpleIntegerProperty(workSumProjects.get());
    private static DoubleProperty dayWorkSumProperty = new SimpleDoubleProperty(0);


    /** Поле нужно, чтобы передавать его отдельным нитям (см. класс TestBackgroundUpdate01)
     * */
    private static TableProjectsDesignerController tableProjectsDesignerController;
    private static BorderPane rootLayout;


    /** Стандартные геттеры и сеттеры */

    public static int incrementIdNumberAndGet() {
        return idNumber.incrementAndGet();

    }

    public static int getIdNumber() {
        return idNumber.get();
    }

    public static synchronized void setIdNumber(int newIdNumber) {
        idNumber.set(newIdNumber);
        idNumberProperty.set(newIdNumber);
    }

    public static int getIdNumberProperty() {
        return idNumberProperty.get();
    }

    public static IntegerProperty idNumberProperty() {
        return idNumberProperty;
    }

    private static void setIdNumberProperty(int newIdNumberProperty) {
        AllData.idNumberProperty.set(newIdNumberProperty);
    }


    public static ObservableMap<Integer, Project> getAllProjects() {
        return allProjects;
    }

    public static synchronized void setAllProjects(Map<Integer, Project> newAllProjects) {
        AllData.allProjects.clear();
        AllData.allProjects.putAll(newAllProjects);
        rebuildActiveProjects();
    }

    public static Map<Integer, Project> getActiveProjects() {
        return activeProjects;
    }

    public static synchronized void setActiveProjects(ObservableMap<Integer, Project> newActiveProjects) {
        AllData.activeProjects = newActiveProjects;
    }



    public static int getWorkSumProjects() {
        return workSumProjects.get();
    }

    public static synchronized void setWorkSumProjects(int newWorkSumProjects) {
        AllData.workSumProjects.set(newWorkSumProjects);
        AllData.workSumProjectsProperty().set(newWorkSumProjects);
    }

    public static synchronized void addWorkSumProjects(int addTime) {
        AllData.workSumProjects.addAndGet(addTime);
        AllData.workSumProjectsProperty().set(workSumProjects.get());
    }

    public static int getWorkSumProjectsProperty() {
        return workSumProjectsProperty.get();
    }

    public static IntegerProperty workSumProjectsProperty() {
        return workSumProjectsProperty;
    }

    public static void setWorkSumProjectsProperty(int newWorkSumProjectsProperty) {
        AllData.workSumProjectsProperty.set(newWorkSumProjectsProperty);
    }


    public static double getDayWorkSumProperty() {
        return dayWorkSumProperty.get();
    }

    public static DoubleProperty dayWorkSumProperty() {
        return dayWorkSumProperty;
    }

    public static synchronized void setDayWorkSumProperty(double day) {
        AllData.dayWorkSumProperty.set(day);
    }

    public static synchronized void rebuildDayWorkSumProperty() {
        AllData.dayWorkSumProperty.set(0);
        int counter = 0;
        for (Project p : activeProjects.values()) {
            if (p.containsWorkTime(AllUsers.getCurrentUser(), LocalDate.now())) {
                counter += p.getWorkSumForDesignerAndDate(AllUsers.getCurrentUser(), LocalDate.now());
            }
        }
        AllData.dayWorkSumProperty.set(AllData.intToDouble(counter));
    }


    public static TableProjectsDesignerController getTableProjectsDesignerController() {
        return tableProjectsDesignerController;
    }

    public static void setTableProjectsDesignerController(TableProjectsDesignerController newTableProjectsDesignerController) {
        AllData.tableProjectsDesignerController = newTableProjectsDesignerController;
    }

    public static BorderPane getRootLayout() {
        return rootLayout;
    }

    public static void setRootLayout(BorderPane newRootLayout) {
        AllData.rootLayout = newRootLayout;
    }


    /** Геттеры активного, неактивного и любого проекта из мапы
     * @return null
     * */

    public static Project getOneActiveProject(int idActiveProject) {
        if (isProjectExist(idActiveProject)) {
            return activeProjects.get(idActiveProject);
        }
        return null;
    }

    public static Project getAnyProject(int idProject) {
        if (isProjectExist(idProject)) {
            return allProjects.get(idProject);
        }
        return null;
    }

    public static Project getOneArchiveProject(int idArchiveProject) {
        if (isProjectArchive(idArchiveProject)) {
            return allProjects.get(idArchiveProject);
        }
        return null;
    }

    public static List<Project> getActiveProjectsForPeriodCreation(LocalDate fromDate, LocalDate tillDate) {
        List<Project> result = new ArrayList<>();
        for (Project p : activeProjects.values()) {
            LocalDate date = AllData.parseDate(p.getDateCreationString());
            if ((date.compareTo(fromDate) >= 0) && (date.compareTo(tillDate) <= 0)) {
                result.add(p);
            }
        }
        return result;
    }


    public static List<Project> getActiveProjectsForDesignerAndDate(int designerIDnumber, LocalDate workDate) {
        List<Project> result = new ArrayList<>();
        for (Project p : activeProjects.values()) {
            if (p.containsWorkTime(designerIDnumber, workDate)) {
                result.add(p);
            }
        }
        return result;
    }

    public static List<Project> getActiveProjectsForPeriodWorking(LocalDate fromDate, LocalDate tillDate) {
        List<Project> result = new ArrayList<>();
        for (Project p : activeProjects.values()) {
            if (p.containsWorkTime(fromDate, tillDate)) {
                result.add(p);
            }
        }
        return result;
    }

    public static List<Project> getActiveProjectsForDesignerAndPeriodWorking(int designerIDnumber, LocalDate fromDate, LocalDate tillDate) {
        List<Project> result = new ArrayList<>();
        for (Project p : activeProjects.values()) {
            if (p.containsWorkTime(designerIDnumber, fromDate, tillDate)) {
                result.add(p);
            }
        }
        return result;
    }

    public static List<Project> getActiveProjectsForDesignerAndMonth(int designerIDnumber, Year year, Month month) {
        List<Project> result = new ArrayList<>();
        LocalDate fromDate = LocalDate.of(year.getValue(), month.getValue(), 1);
        LocalDate tillDate = LocalDate.of(year.getValue(), month.getValue(), month.length(year.isLeap()));
        for (Project p : activeProjects.values()) {
            if (p.containsWorkTime(designerIDnumber, fromDate, tillDate)) {
                result.add(p);
            }
        }
        return result;
    }





    /** Методы добавления, удаления проектов */

    public synchronized static boolean addNewProject(Project newProject) {
        if (!isProjectExist(newProject.getIdNumber())) {
            allProjects.put(newProject.getIdNumber(), newProject);
            activeProjects.put(newProject.getIdNumber(), newProject);

            // Добавляем время в общее суммарное
            int tmp = workSumProjects.get();
            tmp += newProject.getWorkSum();
            workSumProjects.set(tmp);

            return true;
        }
        return false;
    }

    public synchronized static boolean deleteProject(int deadProject) {
        if (isProjectExist(deadProject)) {
            int deleteWorkTime = allProjects.get(deadProject).getWorkSum();
            allProjects.remove(deadProject);
            activeProjects.remove(deadProject);

            // Удаляем время из общего суммарного
            int tmp = workSumProjects.get();
            tmp -= deleteWorkTime;
            if (tmp < 0) {
                tmp = 0;
            }
            workSumProjects.set(tmp);

            return true;
        }
        return false;
    }

    public static synchronized void changeProjectArchiveStatus(int changedProject, boolean projectIsArchive) {
        if (isProjectExist(changedProject)) {
            Project chProject = allProjects.get(changedProject);
            chProject.setArchive(projectIsArchive);

            if (projectIsArchive == true) {
                activeProjects.remove(changedProject);
            }
            else if (projectIsArchive == false) {
                activeProjects.put(changedProject, chProject);
            }
        }
    }


    /** Методы проверки существования проекта в списке
     * и его проверки на архивное состояние */

    public static boolean isProjectExist(int idProject) {
        if (idProject <= 0 || idProject > getIdNumber()) {
            return false;
        }
        return allProjects.containsKey(idProject);
    }

    // Перед применением данного метода ВСЕГДА сначала вызывать isProjectExist(int idProject),
    // то есть проверять проект на существование
    public static boolean isProjectArchive(int idProject) {
        return allProjects.get(idProject).isArchive();
    }


    /** Метод добавления и корректировки рабочего времени в проектах */

    public static synchronized boolean addWorkTime(int projectIDnumber, LocalDate correctDate, int idUser, double newTime) {

        if (isProjectExist(projectIDnumber) && (!isProjectArchive(projectIDnumber))) {
            Project project = getOneActiveProject(projectIDnumber);

            int difference = project.addWorkTime(correctDate, idUser, newTime);
            addWorkSumProjects(difference);

            if (idUser == AllUsers.getCurrentUser() && correctDate.equals(LocalDate.now())) {

                int old = AllData.doubleToInt(dayWorkSumProperty.get());

                dayWorkSumProperty.set(AllData.intToDouble(old + difference));
            }

            return true;
        }
        return false;
    }


    public static boolean containsWorkTime(int projectID, int designerID, LocalDate date) {
        if (allProjects.containsKey(projectID)) {
            return allProjects.get(projectID).containsWorkTime(designerID, date);
        }
        return false;
    }



    /** Метод сверки и синхронизации списков и поля суммарного времени */
    public static synchronized void rebuildActiveProjects() {
        Map<Integer, Project> newActiveProjects = new HashMap<>();
        allProjects.forEach((k,v)-> {
            if (!v.isArchive()) {
                newActiveProjects.put(k, v);
            }
        });
        activeProjects.clear();
        activeProjects.putAll(newActiveProjects);
    }

    public static synchronized int computeWorkSum() {
        int result = 0;

        Collection<Project> values = allProjects.values();
        for (Project p : values) {
            result += p.getWorkSum();
        }
        workSumProjects.set(result);
        return result;
    }

    public static synchronized void computeProjectsProperties() {
        Collection<Project> coll = activeProjects.values();
        for (Project p : coll) {
            p.setIdNumberProperty(p.getIdNumber());
            p.setCompanyProperty(p.getCompany());
            p.setInitiatorProperty(p.getInitiator());
            p.setDescriptionProperty(p.getDescription());
            p.setWorkSumProperty(String.valueOf(p.getWorkSumDouble()));
        }
    }


    public static void deleteZeroTime() {
        for (Project p : activeProjects.values()) {
            Iterator<WorkTime> iter = p.getWork().iterator();
            while (iter.hasNext()) {
                WorkTime wt = iter.next();
                if (wt.getTime() == 0) {
                    iter.remove();
                }
            }
        }
    }



    /** методы-утилиты */

    public static int doubleToInt(double argument) {
        return (int) (argument * 10);
    }

    public static double intToDouble(int argument) {
        double tmp = (double) argument / 10;
        BigDecimal result = new BigDecimal(Double.toString(tmp));
        result = result.setScale(1, RoundingMode.HALF_UP);
        return result.doubleValue();
    }

    public static double formatDouble(double argDouble) {
        BigDecimal result = new BigDecimal(Double.toString(argDouble));
        result = result.setScale(1, RoundingMode.HALF_UP);
        return result.doubleValue();
    }

    public static String formatWorkTime(Double timeDouble) {
        String result = String.valueOf(timeDouble);
        result = result.replaceAll("\\.", ",");
        result = result.replaceAll(",0", "");
        return result;
    }

    public static String formatHours(String input) {
        if (input.endsWith("11") || input.endsWith("12") || input.endsWith("13") || input.endsWith("14")) {
            return "часов";
        }
        if (input.endsWith("1")) {
            return "час";
        }
        else if (input.endsWith("2") || input.endsWith("3") || input.endsWith("4")) {
            return "часа";
        }
        else if (!input.contains(",")) {
            return "часов";
        }
        return "часа";
    }



    /** Форматировщик даты.
     * @return null
     * */
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    public static String formatDate(LocalDate date) {
        if (date == null) {
            return null;
        }
        return DATE_FORMATTER.format(date);
    }

    public static LocalDate parseDate(String dateString) {
        try {
            return DATE_FORMATTER.parse(dateString, LocalDate::from);
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean validDate(String dateString) {
        return parseDate(dateString) != null;
    }

    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("MM", Locale.getDefault());


}
