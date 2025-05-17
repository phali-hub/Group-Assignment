module module_name {
    requires com.gluonhq.charm.glisten;
    requires com.gluonhq.attach.display;
    requires com.gluonhq.attach.storage; // <-- NEW
    requires com.gluonhq.attach.lifecycle;
    requires com.gluonhq.attach.util;
    requires com.gluonhq.attach.statusbar;

    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.media;

    exports com.gluonapplication;
}
