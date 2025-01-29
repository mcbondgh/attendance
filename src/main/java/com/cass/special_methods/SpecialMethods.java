package com.cass.special_methods;

import com.cass.data.StudentClassesEntity;
import com.cass.services.DAO;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.select.Select;

import java.util.ArrayList;
import java.util.List;

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

    public static void setSemester(ComboBox<String> selector) {
        String classes[] = {"SEMESTER 1", "SEMESTER 2"};
        selector.setItems(classes);
    }
    public static void setYear(ComboBox<String> selector){
        String[] years = {"2024", "2025", "2026", "2027", "2028", "2029", "2030"};
        selector.setItems(years);
    }
    public static void setCourses(ComboBox<String> selector) {
        List<String> items = new ArrayList<>();
        new DAO().getAllCourses().forEach(ex -> items.add(ex.name()));
        selector.setItems(items);
    }
    public static void setProgramme(ComboBox<String> selector) {
        List<String> values = List.of("HND NETWORK MANAGEMENT", "HND COMPUTER SCIENCE", "BTECH COMPUTER SCIENCE");
        selector.setItems(values);
    }

    public static void setLevel(ComboBox<String> comboBox) {
        comboBox.setItems(List.of("100", "200", "300", "400"));
    }
    public static void setJointClasses(Select<String> selector) {
        String classes[] = {"BTECH Computer Sci", "BTECH Computer Sci 1",  "HND Computer Sci 1", "Computer Sci 2", "Computer Sci 3", "Network 1A", "Network 1B", "Network 2A", "Network 2B","Network 3A", "Network 3B"};
        selector.setItems(loadClasses());
    }
    public static void setJointClasses(ComboBox<String> selector) {
        String classes[] = {"BTECH Computer Sci", "BTECH Computer Sci 1",  "HND Computer Sci 1", "Computer Sci 2", "Computer Sci 3", "Network 1A", "Network 1B", "Network 2A", "Network 2B","Network 3A", "Network 3B"};
        selector.setItems(loadClasses());
    }

    public static void setActivityTypes(ComboBox<String> selector) {
        String[] classes = {"Assignment", "Quiz", "Midsem", "Presentation"};
        selector.setItems(classes);
    }

    public static void setUserRoles(ComboBox<String> selector) {
        selector.setItems(List.of("Admin", "Teaching Assistant"));
    }

}//end of class...
