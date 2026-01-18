module com.mia.taskapp {
    requires javafx.controls;
    requires javafx.graphics;

    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.core;    

    opens com.mia.taskapp to com.fasterxml.jackson.databind;

    exports com.mia.taskapp;
}
