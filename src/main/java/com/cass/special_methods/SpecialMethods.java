package com.cass.special_methods;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.select.Select;

public class SpecialMethods {
    
    public static void setClasses(Select<String> selector) {
        String classes[] = {"BTECH Computer Sci", "Computer Sci 2", "Computer Sci 3",  "Network 2A", "Network 2B","Network 3A", "Network 3B"};
        selector.setItems(classes);
    }
    public static void setClasses(ComboBox<String> selector) {
        String classes[] = {"BTECH Computer Sci","Computer Sci 2", "Computer Sci 3",  "Network 2A", "Network 2B","Network 3A", "Network 3B"};
        selector.setItems(classes);
    }
    public static void setPrograme(Select<String> selector) {
        String classes[] = {"SYSTEMS ANALYSIS & DESIGN", "ITCM", "IT PROJECT MANAGEMENT"};
        selector.setItems(classes);
    }
    public static void setPrograme(ComboBox<String> selector) {
        String classes[] = {"SYSTEMS A. & DESIGN", "ITCM", "IT PROJECT MANAGEMENT"};
        selector.setItems(classes);
    }
    public static void setDepartment(Select<String> selector) {
        String classes[] = {"COMPUTER SCIENCE DPT"};
        selector.setItems(classes);
    }
    public static void setJointClasses(Select<String> selector) {
        String classes[] = {"BTECH Computer Sci", "Computer Sci 2", "Computer Sci 3","Network 2A", "Network 2B","Network 3A", "Network 3B"};
        selector.setItems(classes);
    }
    public static void setJointClasses(ComboBox<String> selector) {
        String classes[] = {"BTECH Computer Sci", "Computer Sci 2", "Computer Sci 3", "Network 2A", "Network 2B","Network 3A", "Network 3B"};
        selector.setItems(classes);
    }
    public static void setActivityTypes(ComboBox<String> selector) {
        String classes[] = {"Assignment", "Quiz", "Midsem", "Presentation"};
        selector.setItems(classes);
    }

    public static void setUserRoles(ComboBox<String> selector) {
        String classes[] = {"Admin", "Class Rep"};
        selector.setItems(classes);
    }

}//end of class...
