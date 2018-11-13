package com.horovod.timecountfxprobe.test;


import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.project.Project;
import com.horovod.timecountfxprobe.user.AllUsers;
import com.horovod.timecountfxprobe.user.Designer;
import com.horovod.timecountfxprobe.user.Manager;
import com.horovod.timecountfxprobe.user.Role;


import java.time.LocalDate;

public class Generator {

    public static void generate() {
        for (int i = 1; i <= 10; i++) {
            AllUsers.createUser("des" + i, "pass", Role.DESIGNER);
        }

        for (int i = 1; i <= 2; i++) {
            AllUsers.createUser("manager" + i, "pass", Role.MANAGER);
        }


        for (int j = 1; j<=100; j++) {
            String companyClient;
            String init;
            int year;
            int month;
            int day;

            if (j % 10 == 0) {
                companyClient = "Philips";
                init = "Бриксин Сергей, Донская Светлана";
                year = 2018;
                month = (int) (Math.random() * 10 + 1);
                if (month < 10) {
                    month = 11;
                }
                day = month * 2;

            }
            else if (j % 2 == 0) {
                companyClient = "McCormick";
                init = "Чепига Людмила";
                year = 2018;
                month = (int) (Math.random() * 10 + 1);
                if (month < 10) {
                    month = 10;
                }
                day = (int) (Math.random() * 25 + 2);
            }
            else {
                companyClient = "Nestle";
                init = "Романко Марина, Чечелева Ксения, Киндякова Полина, Лялина Ольга";
                year = 2018;
                //month = (int) (Math.random() * 10 + 1);
                month = 11;
                //day = (int) (Math.random() * 27 + 1);
                day = 13;
            }
            LocalDate date = LocalDate.of(year, month, day);
            String descr = new StringBuilder("project number ").append(j).append(" is of ").append(init).append(", and company is ").append(companyClient).toString();
            if (j % 10 == 0) {
                descr = descr + " | " + descr + " | " + descr;
            }
            Project project = new Project(companyClient, init, descr, date);

            int works = (int) (Math.random() * 5);

            AllData.addNewProject(project);

            for (int k = 0; k <= works; k++) {
                int ID = (int) (Math.random() * 10 + 1);
                int tmp = (int) ((Math.random() * 1000) / 30);
                double newtime = AllData.intToDouble(tmp);
                if (ID == 5) {
                    System.out.println("it's designer ID = 5 ! and worktime = " + newtime + " projectID = " + project.getIdNumber() +
                            "   and date is " + project.getDateCreationString());
                }
                AllData.addWorkTime(project.getIdNumber(), date, ID, newtime);
            }
        }
    }
}
