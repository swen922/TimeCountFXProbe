package com.horovod.timecountfxprobe.test;


import com.horovod.timecountfxprobe.project.AllData;
import com.horovod.timecountfxprobe.project.Project;
import com.horovod.timecountfxprobe.user.AllUsers;
import com.horovod.timecountfxprobe.user.Designer;
import com.horovod.timecountfxprobe.user.Manager;


import java.time.LocalDate;

public class Generator {

    public static void generate() {
        for (int i = 1; i <= 10; i++) {
            Designer designer = new Designer("des-" + i);
            AllUsers.addUser(designer);
        }
        Manager manager1 = new Manager("manager-1");
        Manager manager2 = new Manager("manager-2");
        AllUsers.addUser(manager1);
        AllUsers.addUser(manager2);

        for (int j = 1; j<=100; j++) {
            String companyClient;
            String init;
            int year;
            int month;
            int day;

            if (j % 10 == 0) {
                companyClient = "Philips";
                init = "initiator-1";
                year = 2016;
                month = j / 10 + 2;
                day = month * 2;

            }
            else if (j % 2 == 0) {
                companyClient = "McCormick";
                init = "initiator-2";
                year = 2017;
                month = (int) (Math.random() * 10 + 1);
                day = (int) (Math.random() * 25 + 2);
            }
            else {
                companyClient = "Nestle";
                init = "initiator-3";
                year = 2015;
                month = (int) (Math.random() * 10 + 1);
                day = (int) (Math.random() * 27 + 1);
            }
            LocalDate date = LocalDate.of(year, month, day);
            String descr = new StringBuilder(init).reverse().toString() + " === " + (Math.random() * 20);
            Project project = new Project(companyClient, init, descr, date);

            int works = (int) (Math.random() * 4);

            AllData.addNewProject(project);

            for (int k = 0; k <= works; k++) {
                int ID = (int) (Math.random() * 10 + 1);
                int tmp = (int) ((Math.random() * 1000) / 30);
                double newtime = AllData.intToDouble(tmp);
                AllData.addWorkTime(project.getIdNumber(), date, ID, newtime);
            }
        }
    }
}
