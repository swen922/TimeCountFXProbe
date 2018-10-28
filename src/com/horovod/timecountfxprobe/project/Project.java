package com.horovod.timecountfxprobe.project;


import javafx.beans.property.*;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.time.LocalDate;
import java.util.*;

@XmlRootElement(name = "project")
public class Project {

    private int idNumber;
    private IntegerProperty idNumberProperty;
    private String company;
    private StringProperty companyProperty;
    private String initiator;
    private StringProperty initiatorProperty;
    private String description;
    private StringProperty descriptionProperty;
    private String dateCreationString;
    private volatile boolean isArchive = false;
    private String comment;
    private Set<Integer> linkedProjects = new TreeSet<>();
    private int PONumber;
    private volatile int workSum = 0;
    private DoubleProperty workSumProperty = new SimpleDoubleProperty(AllData.intToDouble(workSum));
    private List<WorkTime> work = new ArrayList<>();


    public Project(String comp, String initiator, String description) {
        this.idNumber = AllData.incrementIdNumberAndGet();
        this.idNumberProperty = new SimpleIntegerProperty(idNumber);
        this.company = comp;
        this.companyProperty = new SimpleStringProperty(company);
        this.initiator = initiator;
        this.initiatorProperty = new SimpleStringProperty(initiator);
        this.description = description;
        this.descriptionProperty = new SimpleStringProperty(description);
        this.dateCreationString = AllData.formatDate(LocalDate.now());
    }

    public Project(String comp, String initiator, String description, LocalDate newDate) {
        this.idNumber = AllData.incrementIdNumberAndGet();
        this.idNumberProperty = new SimpleIntegerProperty(new Integer(idNumber));
        this.company = comp;
        this.companyProperty = new SimpleStringProperty(company);
        this.initiator = initiator;
        this.initiatorProperty = new SimpleStringProperty(initiator);
        this.description = description;
        this.descriptionProperty = new SimpleStringProperty(description);
        this.dateCreationString = AllData.formatDate(newDate);
    }

    public Project() {
        this.idNumber = 0;
        this.idNumberProperty = new SimpleIntegerProperty(idNumber);
        this.company = "";
        this.companyProperty = new SimpleStringProperty("");
        this.initiator = "";
        this.initiatorProperty = new SimpleStringProperty("");
        this.description = "";
        this.descriptionProperty = new SimpleStringProperty("");
        this.dateCreationString = AllData.formatDate(LocalDate.now());
    }

    @XmlElement(name = "projectidnumber")
    public int getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(int idNumber) {
        this.idNumber = idNumber;
        this.idNumberProperty.set(idNumber);
    }

    @XmlTransient
    public int getIdNumberProperty() {
        return idNumberProperty.get();
    }

    @XmlTransient
    public IntegerProperty idNumberProperty() {
        return idNumberProperty;
    }

    public void setIdNumberProperty(int newIdNumberProperty) {
        this.idNumberProperty.set(newIdNumberProperty);
    }

    @XmlElement(name = "clientcompany")
    public String getCompany() {
        return company;
    }

    public void setCompany(String newCompany) {
        this.company = newCompany;
        this.companyProperty.set(newCompany);
    }

    @XmlTransient
    public String getCompanyProperty() {
        return companyProperty.get();
    }

    @XmlTransient
    public StringProperty companyProperty() {
        return companyProperty;
    }

    public void setCompanyProperty(String newCompanyProperty) {
        this.companyProperty.set(newCompanyProperty);
    }

    @XmlElement(name = "initby")
    public String getInitiator() {
        return initiator;
    }

    public void setInitiator(String newInitiator) {
        this.initiator = newInitiator;
        this.initiatorProperty.set(newInitiator);
    }

    @XmlTransient
    public String getInitiatorProperty() {
        return initiatorProperty.get();
    }

    @XmlTransient
    public StringProperty initiatorProperty() {
        return initiatorProperty;
    }

    public void setInitiatorProperty(String newInitiatorProperty) {
        this.initiatorProperty.set(newInitiatorProperty);
    }

    @XmlElement(name = "descr")
    public String getDescription() {
        return description;
    }

    public void setDescription(String newDescription) {
        this.description = newDescription;
        this.descriptionProperty.set(newDescription);
    }

    @XmlTransient
    public String getDescriptionProperty() {
        return descriptionProperty.get();
    }

    @XmlTransient
    public StringProperty descriptionProperty() {
        return descriptionProperty;
    }

    public void setDescriptionProperty(String newDescriptionProperty) {
        this.descriptionProperty.set(newDescriptionProperty);
    }

    @XmlElement(name = "datecreationstring")
    public String getDateCreationString() {
        return dateCreationString;
    }

    public void setDateCreationString(String newDateCreationString) {
        this.dateCreationString = newDateCreationString;
    }

    @XmlElement(name = "isarchive")
    public boolean isArchive() {
        return isArchive;
    }

    public void setArchive(boolean archive) {
        isArchive = archive;
    }

    @XmlElement(name = "comment")
    public String getComment() {
        return comment;
    }

    public void setComment(String newComment) {
        this.comment = newComment;
    }

    @XmlElement(name = "linkedprojects")
    public Set<Integer> getLinkedProjects() {
        return this.linkedProjects;
    }

    public void setLinkedProject(Set<Integer> newLinkedProjects) {
        this.linkedProjects = newLinkedProjects;
    }

    public void addLinkedProjects(Integer... args) {
        this.linkedProjects.addAll(Arrays.asList(args));
    }

    @XmlElement(name = "ponumber")
    public int getPONumber() {
        return PONumber;
    }

    public void setPONumber(int newPONumber) {
        this.PONumber = newPONumber;
    }

    @XmlElement(name = "worksumint")
    public int getWorkSum() {
        return workSum;
    }

    private void setWorkSum(int newWorkSum) {
        this.workSum = newWorkSum >= 0 ? newWorkSum : 0;
    }

    @XmlTransient
    public double getWorkSumDouble() {
        return AllData.intToDouble(workSum);
    }

    protected void setWorkSumDouble(double newWorkSumDouble) {
        if (newWorkSumDouble <= 0) {
            setWorkSum(0);
        }
        else {
            this.workSum = AllData.doubleToInt(newWorkSumDouble);
        }
    }

    @XmlTransient
    public double getWorkSumProperty() {
        return workSumProperty.get();
    }

    @XmlTransient
    public DoubleProperty workSumProperty() {
        return workSumProperty;
    }

    public void setWorkSumProperty(double newWorkSumProperty) {
        this.workSumProperty.set(newWorkSumProperty);
    }

    @XmlElement(name = "listworks")
    public List<WorkTime> getWork() {
        return work;
    }

    public void setWork(List<WorkTime> newWork) {
        this.work = newWork;
    }


    /** Метод рассчитан на вызов из класса AllData,
     * в котором содержится одноименный метод для обращения извне.
     * Возвращаемое значение int используется в AllData для добавления
     * к суммарному общему рабочему времени workSumProjects
     * */
    protected int addWorkTime(LocalDate newDate, int idUser, double newTimeDouble) {

        int newTimeInt = AllData.doubleToInt(newTimeDouble);

        for (WorkTime wt : work) {
            // Проверяем наличие такого дня + дизайнера
            if ((AllData.parseDate(wt.getDateString()).equals(newDate)) && (wt.getDesignerID() == idUser)) {
                // сначала правим суммарное рабочее время всего проекта
                int diff = newTimeInt - wt.getTime();
                int newWorkSumInt = getWorkSum() + diff;
                setWorkSum(newWorkSumInt);

                // теперь вносим время в список рабочего времени
                wt.setTime(newTimeInt);
                return diff;
            }
        }

        // Создаем новый экземпляр WorkTime и кладем в список
        int newWorkSumInt = getWorkSum() + newTimeInt;
        setWorkSum(newWorkSumInt);
        work.add(new WorkTime(newDate, idUser, newTimeDouble));
        return newTimeInt;
    }


    public void computeWorkSum() {
        int result = 0;
        for (WorkTime wt : this.work) {
            result += wt.getTime();
        }
        this.workSum = result;
    }

    public void computeProperties() {
        this.idNumberProperty = new SimpleIntegerProperty(idNumber);
        this.companyProperty = new SimpleStringProperty(company);
        this.initiatorProperty = new SimpleStringProperty(initiator);
        this.descriptionProperty = new SimpleStringProperty(description);
        this.workSumProperty = new SimpleDoubleProperty(AllData.intToDouble(workSum));
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;
        return getIdNumber() == project.getIdNumber() &&
                isArchive() == project.isArchive() &&
                getPONumber() == project.getPONumber() &&
                getWorkSum() == project.getWorkSum() &&
                Objects.equals(getCompany(), project.getCompany()) &&
                Objects.equals(getInitiator(), project.getInitiator()) &&
                Objects.equals(getDescription(), project.getDescription()) &&
                Objects.equals(getDateCreationString(), project.getDateCreationString()) &&
                Objects.equals(getComment(), project.getComment()) &&
                Objects.equals(getLinkedProjects(), project.getLinkedProjects()) &&
                Objects.equals(getWork(), project.getWork());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getIdNumber(), getCompany(), getInitiator(), getDescription(), getDateCreationString(), isArchive(), getComment(), getLinkedProjects(), getPONumber(), getWorkSum(), getWork());
    }

    @Override
    public String toString() {
        return "Project{" +
                "idNumber=" + idNumber +
                ", company='" + company + '\'' +
                ", initiator='" + initiator + '\'' +
                ", description='" + description + '\'' +
                ", dateCreationString='" + dateCreationString + '\'' +
                ", isArchive=" + isArchive +
                ", workSum=" + AllData.intToDouble(workSum) +
                '}';
    }
}