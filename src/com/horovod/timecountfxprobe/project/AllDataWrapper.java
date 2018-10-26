package com.horovod.timecountfxprobe.project;

import com.horovod.timecountfxprobe.user.*;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


/** Специальный класс-обертка для всех данных,
 * чтобы сохранить их в XML-файл.
 * Используется классом Loader для сохранения и чтения сохраненных данных
 *
 * Поскольку JAXB не поддерживает сохранение интерфейсов,
 * сохраняем юзеров в индивидуальные списки согласно классам-имплементаторам */

@XmlRootElement(name = "alldatawrapper")
public class AllDataWrapper {

    //поля класса AllData
    private int allProjectsIdNumber;
    private Map<Integer, Project> allProjects = new HashMap<>();
    private int workSumProjects;

    //поля класса AllUsers
    private int IDCounterAllUsers;
    private Map<Integer, Designer> designers = new HashMap<>();
    private Map<Integer, Manager> managers = new HashMap<>();
    private Map<Integer, Designer> deletedDesigners = new HashMap<>();
    private Map<Integer, Manager> deletedManagers = new HashMap<>();
    //private Map<Integer, User> users = new HashMap<>();



    public AllDataWrapper() {
        this.allProjectsIdNumber = AllData.getIdNumber();
        this.allProjects.putAll(AllData.getAllProjects());
        this.workSumProjects = AllData.getWorkSumProjects();

        this.IDCounterAllUsers = AllUsers.getIDCounterAllUsers();

        Collection<? extends User> collUsers = AllUsers.getUsers().values();
        for (User u : collUsers) {
            if (u.getRole().equals(Role.DESIGNER)) {
                Designer des = (Designer) u;
                this.designers.put(des.getIDNumber(), des);
            }
            else if (u.getRole().equals(Role.MANAGER)) {
                Manager man = (Manager) u;
                this.managers.put(man.getIDNumber(), man);
            }
        }

        Collection<? extends User> collDeletedUsers = AllUsers.getDeletedUsers().values();
        for (User u : collDeletedUsers) {
            if (u.getRole().equals(Role.DESIGNER)) {
                Designer des = (Designer) u;
                this.deletedDesigners.put(des.getIDNumber(), des);
            }
            else if (u.getRole().equals(Role.MANAGER)) {
                Manager man = (Manager) u;
                this.deletedManagers.put(man.getIDNumber(), man);
            }
        }
    }

    @XmlElement(name = "allprojectsidnumber")
    public int getAllProjectsIdNumber() {
        return allProjectsIdNumber;
    }

    public void setAllProjectsIdNumber(int newAllProjectIdNumber) {
        this.allProjectsIdNumber = newAllProjectIdNumber;
    }

    @XmlElement(name = "allprojects")
    public Map<Integer, Project> getAllProjects() {
        return allProjects;
    }

    public void setAllProjects(Map<Integer, Project> newAllProjects) {
        this.allProjects = newAllProjects;
    }

    @XmlElement(name = "worksumprojects")
    public int getWorkSumProjects() {
        return workSumProjects;
    }

    public void setWorkSumProjects(int newWorkSumProjects) {
        this.workSumProjects = newWorkSumProjects;
    }

    @XmlElement(name = "idcounterallusers")
    public int getIDCounterAllUsers() {
        return IDCounterAllUsers;
    }

    public void setIDCounterAllUsers(int newIDCounterAllUsers) {
        this.IDCounterAllUsers = newIDCounterAllUsers;
    }

    @XmlElement(name = "designers")
    public Map<Integer, Designer> getDesigners() {
        return designers;
    }

    public void setDesigners(Map<Integer, Designer> newdesigners) {
        this.designers = newdesigners;
    }

    @XmlElement(name = "managers")
    public Map<Integer, Manager> getManagers() {
        return managers;
    }

    public void setManagers(Map<Integer, Manager> newmanagers) {
        this.managers = newmanagers;
    }

    @XmlElement(name = "deleteddesigners")
    public Map<Integer, Designer> getDeletedDesigners() {
        return deletedDesigners;
    }

    public void setDeletedDesigners(Map<Integer, Designer> deletedDesigners) {
        this.deletedDesigners = deletedDesigners;
    }

    @XmlElement(name = "deletedmanagers")
    public Map<Integer, Manager> getDeletedManagers() {
        return deletedManagers;
    }

    public void setDeletedManagers(Map<Integer, Manager> deletedManagers) {
        this.deletedManagers = deletedManagers;
    }

    @Override
    public String toString() {
        return "AllDataWrapper{" + "\n" +
                "allProjectsIdNumber=" + allProjectsIdNumber + "\n" +
                ", allProjects=" + allProjects + "\n" +
                ", workSumProjects=" + workSumProjects + "\n" +
                ", IDCounterAllUsers=" + IDCounterAllUsers + "\n" +
                ", designers=" + designers + "\n" +
                ", managers=" + managers + "\n" +
                ", deletedDesigners=" + deletedDesigners + "\n" +
                ", deletedManagers=" + deletedManagers + "\n" +
                '}' + "\n";
    }
}

