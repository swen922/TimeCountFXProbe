package com.horovod.timecountfxprobe.test;

import javafx.application.Platform;
import javafx.concurrent.Task;
import com.horovod.timecountfxprobe.project.AllData;

import java.time.LocalDate;

public class TestBackgroundUpdate01 {

    public void testBackgroundAddTime() {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                AllData.addWorkTime(10, LocalDate.now().minusDays(2),5, 21);

                Platform.runLater(new Runnable() {
                    @Override public void run() {
                        AllData.getTableProjectsDesignerController().initialize();
                    }
                });
            }
        };

        Thread t = new Thread(runnable);
        t.setDaemon(true);
        t.start();
    }

    public void testBackgroundDeleteTime() {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                AllData.addWorkTime(10, LocalDate.now().minusDays(2),5, 2);

                Platform.runLater(new Runnable() {
                    @Override public void run() {
                        AllData.getTableProjectsDesignerController().initialize();
                    }
                });
            }
        };

        Thread t = new Thread(runnable);
        t.setDaemon(true);
        t.start();

    }
}
