package com.cass.special_methods;

import com.cass.data.CourseRecord;
import com.cass.services.DAO;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.select.Select;

import java.util.List;

public class SpecialMethods {

    static DAO DAO = new DAO();

    static List<String> loadClasses() {
        //        for (StudentClassesEntity item : DAO.getAllClasses()) {
//           if (item.isStatus()) {
//               list .add(item.getClassName());
//           }
//        }

        return DAO.getAllCourses().stream().map(CourseRecord::programme).distinct().toList();
    }

    public static void loadProgrammes(Select<String> selector) {
        selector.setItems(loadClasses());
    }

    public static void loadProgrammes(ComboBox<String> selector) {
        selector.setItems(loadClasses());
//        String classes[] = {"BTECH Computer Sci","Computer Sci 2", "Computer Sci 3",  "Network 2A", "Network 2B","Network 3A", "Network 3B"};
        //    selector.setItems(classes);
    }

    //    public static void setSemester(ComboBox<String> selector) {
//        String classes[] = {"SEMESTER 1", "SEMESTER 2"};
//        selector.setItems(classes);
//    }
    public static void setYear(ComboBox<String> selector) {
        String[] years = {"2024", "2025", "2026", "2027", "2028", "2029", "2030"};
        selector.setItems(years);
    }

    public static void setCourses(ComboBox<String> selector) {
        var courses = DAO.getAllCourses().stream().map(CourseRecord::name).distinct().toList();
        selector.setItems(courses);
    }

    public static void setProgramme(ComboBox<String> selector) {
        var programmes = DAO.getAllCourses().stream().map(CourseRecord::programme).distinct().toList();
//        List<String> values = List.of("HND NETWORK MANAGEMENT", "HND COMPUTER SCIENCE", "BTECH COMPUTER SCIENCE");
        selector.setItems(programmes);
    }

    public static void setLevel(ComboBox<String> comboBox) {
        comboBox.setItems(List.of("100", "200", "300", "400"));
    }

    public static void setClassSections(Select<String> selector) {
        String classes[] = {"BTECH Computer Sci", "BTECH Computer Sci 1", "HND Computer Sci 1", "Computer Sci 2", "Computer Sci 3", "Network 1A", "Network 1B", "Network 2A", "Network 2B", "Network 3A", "Network 3B"};
        selector.setItems(loadClasses());
    }

    public static void setClassSections(ComboBox<String> selector) {
        String[] classes = {"A", "B", "C", "D"};
        selector.setItems(loadClasses());
    }

    public static void setActivityTypes(ComboBox<String> selector) {
        String[] classes = {"Assignment", "Quiz", "Midsem", "Presentation"};
        selector.setItems(classes);
    }

    public static void setUserRoles(ComboBox<String> selector) {
        selector.setItems(List.of("Admin", "Teaching Assistant", "Class Rep"));
    }

}//end of class...
