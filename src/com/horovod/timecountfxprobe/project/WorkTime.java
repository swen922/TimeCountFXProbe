package com.horovod.timecountfxprobe.project;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.time.LocalDate;
import java.util.Objects;

@XmlRootElement(name = "worktimeinstance")
public class WorkTime {

    private String dateString;
    private int designerID;
    /**
     Время храним умноженное на 10, чтобы обойтись без double или BigDecimal,
     а при выводе на экран делим на 10 и выдаем double
     */
    private volatile int time = 0;

    public WorkTime(LocalDate date, int ID, double time) {
        this.dateString = AllData.formatDate(date);
        this.designerID = ID;
        setTime(AllData.doubleToInt(time));
    }

    public WorkTime() {
    }

    @XmlElement(name = "datestring")
    public String getDateString() {
        return dateString;
    }

    public void setDateString(String dateString) {
        this.dateString = dateString;
    }

    @XmlTransient
    public LocalDate getDate() {
        return AllData.parseDate(this.dateString);
    }

    @XmlElement(name = "designerid")
    public int getDesignerID() {
        return designerID;
    }

    public void setDesignerID(int designerID) {
        this.designerID = designerID;
    }

    @XmlElement(name = "time")
    public int getTime() {
        return this.time;
    }

    @XmlTransient
    public double getTimeDouble() {
        return AllData.intToDouble(time);
    }

    public void setTime(int newTime) {
        this.time = newTime >= 0 ? newTime : 0;
    }

    public void setTimeDouble(double newTimeDouble) {
        if (newTimeDouble == 0) {
            setTime(0);
        }
        else {
            setTime(AllData.doubleToInt(newTimeDouble));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkTime workTime = (WorkTime) o;
        return getDesignerID() == workTime.getDesignerID() &&
                getTime() == workTime.getTime() &&
                Objects.equals(getDateString(), workTime.getDateString());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getDateString(), getDesignerID(), getTime());
    }

    @Override
    public String toString() {
        return "WorkTime{" +
                "date=" + dateString +
                ", designerID=" + designerID +
                ", time=" + AllData.intToDouble(time) +
                '}';
    }


}
