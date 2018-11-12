package com.horovod.timecountfxprobe.test;

import com.horovod.timecountfxprobe.project.AllData;

import java.time.LocalDate;

public class TestBackgroundUpdate01 {

    public void testBackgroundAddTime() {
        AllData.addWorkTime(50, LocalDate.now(),5, 21);
    }

    public void testBackgroundDeleteTime() {
        AllData.addWorkTime(50, LocalDate.now(), 5, 12);
    }
}
