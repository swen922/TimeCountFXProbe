package com.horovod.timecountfxprobe.project;


import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class AllData {

    private static volatile AtomicInteger idNumber = new AtomicInteger(0);

    private static Map<Integer, Project> activeProjects = new ConcurrentHashMap<>();

    private static Map<Integer, Project> allProjects = new ConcurrentHashMap<>();

    private static volatile AtomicInteger workSumProjects = new AtomicInteger(0);



    /** Стандартные геттеры и сеттеры */

    public static int incrementIdNumberAndGet() {
        return idNumber.incrementAndGet();

    }

    public static int getIdNumber() {
        return idNumber.get();
    }

    public static void setIdNumber(int newIdNumber) {
        idNumber.set(newIdNumber);
    }

    public static Map<Integer, Project> getActiveProjects() {
        return activeProjects;
    }

    public static synchronized void setActiveProjects(Map<Integer, Project> newActiveProjects) {
        AllData.activeProjects = newActiveProjects;
    }

    public static Map<Integer, Project> getAllProjects() {
        return allProjects;
    }

    public static synchronized void setAllProjects(Map<Integer, Project> newAllProjects) {
        AllData.allProjects = newAllProjects;
    }

    public static int getWorkSumProjects() {
        return workSumProjects.get();
    }

    public static synchronized void setWorkSumProjects(int newWorkSumProjects) {
        AllData.workSumProjects.set(newWorkSumProjects);
    }

    private static void addWorkSumProjects(int addTime) {
        AllData.workSumProjects.addAndGet(addTime);
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

    public static boolean changeProjectArchiveStatus(int changedProject, boolean projectIsArchive) {
        if (isProjectExist(changedProject)) {
            Project chProject = allProjects.get(changedProject);
            chProject.setArchive(projectIsArchive);

            if (projectIsArchive) {
                activeProjects.remove(changedProject);
            }
            else if (!projectIsArchive) {
                activeProjects.put(changedProject, chProject);
            }
            return true;
        }
        return false;
    }


    /** Методы проверки существования проекта в списке
     * и его проверки на архивное состояние */

    public static boolean isProjectExist(int idProject) {
        if (idProject <= 0 || idProject > getIdNumber()) {
            return false;
        }
        if (allProjects.containsKey(idProject)) {
            return true;
        }
        return false;
    }

    public static boolean isProjectArchive(int idProject) {

        if (isProjectExist(idProject)) {
            Project project = allProjects.get(idProject);
            if (project.isArchive()) {
                return true;
            }
        }
        return false;
    }


    /** Метод добавления и корректировки рабочего времени в проектах */

    public static synchronized boolean addWorkTime(int projectIDnumber, LocalDate correctDate, int idUser, double newTime) {

        if (isProjectExist(projectIDnumber) && (!isProjectArchive(projectIDnumber))) {
            Project project = getOneActiveProject(projectIDnumber);

            int difference = project.addWorkTime(correctDate, idUser, newTime);
            addWorkSumProjects(difference);
            return true;
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
            p.setWorkSumProperty(p.getWorkSumDouble());
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

    /** Форматировщик даты. */
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


}
