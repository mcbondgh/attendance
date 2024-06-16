package com.cass.special_methods;

import com.cass.data.StudentClassesEntity;
import com.cass.services.DAO;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.select.Select;

import java.time.Year;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class SpecialMethods {

    static DAO DAO = new DAO();

    static List<String> loadClasses() {
        List<String> list = new ArrayList<>();
        for (StudentClassesEntity item : DAO.getAllClasses()) {
           if (item.isStatus()) {
               list .add(item.getClassName());
           }
        }
        return list;
    }

    public static void setClasses(Select<String> selector) {
        selector.setItems(loadClasses());
    }
    public static void setClasses(ComboBox<String> selector) {
        selector.setItems(loadClasses());
//        String classes[] = {"BTECH Computer Sci","Computer Sci 2", "Computer Sci 3",  "Network 2A", "Network 2B","Network 3A", "Network 3B"};
    //    selector.setItems(classes);
    }
    public static void setPrograme(Select<String> selector) {
        String classes[] = {"SYSTEMS ANALYSIS & DESIGN", "PROJECT MANAGEMENT"};
        selector.setItems(classes);
    }
    public static void setSemester(ComboBox<String> selector) {
        String classes[] = {"SEMESTER 1", "SEMESTER 2"};
        selector.setItems(classes);
    }
    public static void setYear(ComboBox<String> selector){
        String[] years = {"2023", "2024", "2025", "2026", "2027", "2028", "2029", "2030"};
        selector.setItems(years);
    }
    public static void setPrograme(ComboBox<String> selector) {
        String classes[] = {"SYSTEMS A. & DESIGN", "PROJECT MANAGEMENT"};
        selector.setItems(classes);
    }
    public static void setDepartment(Select<String> selector) {
        String classes[] = {"COMPUTER SCIENCE DPT"};
        selector.setItems(classes);
    }
    public static void setJointClasses(Select<String> selector) {
        String classes[] = {"BTECH Computer Sci", "Computer Sci 2", "Computer Sci 3","Network 2A", "Network 2B","Network 3A", "Network 3B"};
        
        selector.setItems(loadClasses());
    }
    public static void setJointClasses(ComboBox<String> selector) {
        String classes[] = {"All Classes", "BTECH Computer Sci", "Computer Sci 2", "Computer Sci 3", "Network 2A", "Network 2B","Network 3A", "Network 3B"};
        selector.setItems(loadClasses());
    }
    public static void setActivityTypes(ComboBox<String> selector) {
        String[] classes = {"Assignment", "Quiz", "Midsem", "Presentation"};
        selector.setItems(classes);
    }

    public static void setUserRoles(ComboBox<String> selector) {
        String classes[] = {"Admin", "Class Rep"};
        selector.setItems(classes);
    }

}//end of class...
