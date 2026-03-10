module hr.java.financemanagementsystem.financemanagmentsystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires bcrypt;
    requires java.sql;
    requires javafx.base;


    exports hr.java.financemanagementsystem.app;
    opens hr.java.financemanagementsystem.app to javafx.fxml;
    exports hr.java.financemanagementsystem.controller;
    opens hr.java.financemanagementsystem.controller to javafx.fxml;
}