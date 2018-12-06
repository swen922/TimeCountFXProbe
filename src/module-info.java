module TimeCountFXProbe {
    requires java.xml.bind;
    requires javafx.graphics;
    requires javafx.fxml;
    requires javafx.controls;
    requires java.desktop;
    exports com.horovod.timecountfxprobe;
    exports com.horovod.timecountfxprobe.view;
    exports com.horovod.timecountfxprobe.project;
    exports com.horovod.timecountfxprobe.user;
    exports com.horovod.timecountfxprobe.test;
}