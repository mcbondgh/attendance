package com.cass.views.reports;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import com.cass.views.managecourse.ManageCourseView;
import com.vaadin.flow.component.avatar.AvatarVariant;
import org.vaadin.lineawesome.LineAwesomeIcon;

import com.cass.data.ActivitiesEntity;
import com.cass.data.StudentEntity;
import com.cass.dialogs.UserConfirmDialogs;
import com.cass.services.ActivityService;
import com.cass.special_methods.SpecialMethods;
import com.cass.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@PageTitle("View Activity")
@Route(value = "view-activity", layout = MainLayout.class)
// @RolesAllowed({ "ADMIN", "USERS" })
@AnonymousAllowed
@Uses(Icon.class)
public class ActivitiesView extends VerticalLayout {
    private final Grid<StudentEntity> studentGrid = new Grid<>();
    private final ActivityService SERVICE_OBJ = new ActivityService();

    public ActivitiesView() {
        add(renderPageHeader(), renderPageView());
    }

    /****************************************************************************************************
     * CREATE PAGE HEADER WITH 'ADD-ACTIVITY' BUTTON
     ****************************************************************************************************/
    private Component renderPageHeader() {
        HorizontalLayout layout = new HorizontalLayout();
        H5 headerTitle = new H5("STUDENT ACTIVITY RECORDS");
        Button viewButton = new Button("Add Records");

        layout.addClassNames("dashboard-header-container", "view-header-container");
        headerTitle.setClassName("dashboard-header-text");
        viewButton.addClassName("activity-button");
        viewButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_SUCCESS);

        layout.add(headerTitle, viewButton);

        // navigate to 'ActivitiesView.class' on button click.
        viewButton.getElement().addEventListener("click", callBack -> {
            UI.getCurrent().navigate(ManageCourseView.class);
        });
        return layout;
    }

    /****************************************************************************************************
     * CREATE FORM AND INPUT MINI LAYOUT
     ****************************************************************************************************/
    private Component createMiniSideLayout() {
        VerticalLayout layout = new VerticalLayout();
        ComboBox<String> classPicker = new ComboBox<>("Select Class");
        TextField filterField = new TextField();
        ComboBox<String> semesterSelector = new ComboBox<>("Select Semester");
        ListBox<ActivitiesEntity> listView = new ListBox<>();
        Button loadButton = new Button("Load Students");

        SpecialMethods.setClasses(classPicker);
        SpecialMethods.setSemester(semesterSelector);
        loadButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_PRIMARY);
        loadButton.setWidthFull();
        classPicker.setRequired(true);
        filterField.setWidthFull();
        classPicker.setWidthFull();
        semesterSelector.setWidthFull();
        semesterSelector.setRequired(true);

        // set component class names
        layout.setClassName("list-view-container");
        classPicker.setClassName("list-view-classpicker");
        filterField.setClassName("filter-activity-field");
        listView.setClassName("activity-view-list-view");
        loadButton.setClassName("activity-view-load-button");

        // set filter-field placeHolder
        filterField.setPlaceholder("filter by index number");

        layout.setWidthFull();
        layout.add(classPicker, semesterSelector, loadButton, filterField);

        // check and disable 'load button' if 'classPicker' is empty
        layout.getElement().addEventListener("mousemove", callBack -> {
            UI.getCurrent().access(() -> {
                if (classPicker.isEmpty()) {
                    loadButton.addClassName("disable-button");
                    loadButton.setEnabled(false);
                } else {
                    loadButton.removeClassName("disable-button");
                    loadButton.setEnabled(true);
                }
            });
        });

        // Add Click Listener to 'load button'
        loadButton.addClickListener(event -> {
            UI.getCurrent().access(() -> {
                Collection<StudentEntity> data = SERVICE_OBJ.getStudentByClass(classPicker.getValue());
                if (data.isEmpty()) {
                    studentGrid.setItems(Collections.emptyList());
                    new UserConfirmDialogs().showError("Class list is empty");
                } else {
                    studentGrid.setItems(data);
                }
            });
        });

        // ADD FILTER TO FILTER 'listView'
        filterField.setValueChangeMode(ValueChangeMode.EAGER);
        filterField.addValueChangeListener(event -> {
            UI.getCurrent().access(() -> {
               studentGrid.getListDataView().setFilter(filter -> {
                if (filterField.isEmpty()) {
                    return true;
                }
                boolean matchesName = filter.getFullName().toLowerCase().contains(event.getValue().toLowerCase());
                boolean matchesindex = filter.getIndexNumber().toLowerCase().contains(event.getValue().toLowerCase());
                return matchesName || matchesindex;
               }).refreshAll();
            });
        });

        return layout;
    }

    /****************************************************************************************************
     * CREATE TABLE-VIEW
     ****************************************************************************************************/
    private Component createTable() {
        studentGrid.setWidthFull();
        studentGrid.addClassName("grid-layout");
        // set grid columns
        studentGrid.addComponentColumn(component -> renderAvatar());
        studentGrid.addColumn(StudentEntity::getId).setHeader("ROW NUMBER").setClassName("row-number");
        studentGrid.addColumn(StudentEntity::getFullName).setHeader("NAME");
        studentGrid.addColumn(StudentEntity::getIndexNumber).setHeader("INDEX NUMBER");
        studentGrid.addColumn(StudentEntity::getStudentClass).setHeader("CLASS").setFooter(new Span(""));
        studentGrid.addComponentColumn(component -> {return renderViewButton(component.getId());
        }).setHeader("ACTION");

        studentGrid.getColumns().forEach(each -> each.setAutoWidth(true));
        studentGrid.getColumns().forEach(each -> each.setSortable(true));

        return studentGrid;
    }

    /****************************************************************************************************
     * CREATE PAGE HEADER WITH 'VIEW-ACTIVITY' BUTTON
     ****************************************************************************************************/
    private Component renderPageView() {
        VerticalLayout layout = new VerticalLayout();
        FormLayout formLayout = new FormLayout();

        formLayout.setWidthFull();
        formLayout.setClassName("activity-view-formlayout");
        layout.addClassName("container");
        layout.setWidthFull();
        layout.setPadding(true);

        formLayout.setResponsiveSteps(
            new FormLayout.ResponsiveStep("0", 1),
            new FormLayout.ResponsiveStep("600px", 4)
        );

        formLayout.setColspan(studentGrid, 3);
        formLayout.add(createMiniSideLayout(), createTable());
        
        layout.add(formLayout);
        return layout;
    }

    /*
     * IMPLEMENTATION OF SUPPORTING METHODS...
     */

    private Component renderAvatar() {
        Avatar avator = new Avatar();
        avator.setImage("/icons/user-100.png");
        avator.addThemeVariants(AvatarVariant.LUMO_SMALL);
        avator.setClassName("activity-view-avator");
        return avator;
    }

    private Component renderViewButton(int studentIndex) {
        Button button = new Button(LineAwesomeIcon.EDIT.create());
        button.setClassName("activity-view-update-button");
        button.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);

        button.addClickListener(click -> {
            renderDialogView(studentIndex);
        });

        return button;
    }

    private void renderDialogView(int studentIndex) {
        Dialog dialog = new Dialog();
        Grid<ActivitiesEntity> grid = new Grid<>();
        dialog.setHeaderTitle("STUDENT RECORD");
        TextField nameField = new TextField("Name");
        TextField indexNumberField = new TextField("Index Number");

        nameField.getStyle().setFontSize("small");
        indexNumberField.getStyle().setFontSize("small");
        
        nameField.setReadOnly(isAttached());
        indexNumberField.setReadOnly(isAttached());
        
        
        grid.addColumn(ActivitiesEntity::getPrograme).setHeader("PROGRAM");
        grid.addColumn(ActivitiesEntity::getActivityTitle).setHeader("TITLE");
        grid.addColumn(ActivitiesEntity::getActivityType).setHeader("ACTIVITY TYPE");
        grid.addColumn(ActivitiesEntity::getActivityDate).setHeader("ENTRY DATE");
        grid.addColumn(ActivitiesEntity::getScore).setHeader("SCORE").setKey("score");
        grid.addColumn(ActivitiesEntity::getmaximumScore).setHeader("MAX SCORE").setKey("max");
        grid.getColumns().forEach(each -> each.setAutoWidth(true));

        FlexLayout vLayout = new FlexLayout(nameField, indexNumberField);
        vLayout.setWidthFull();

        grid.addClassName("activity-view-dialog-grid");
        vLayout.setClassName("activity-view-dialog-vlayout");
        dialog.setClassName("activity-view-dialog");
        
                
        dialog.add(vLayout, grid);
        dialog.setResizable(isAttached());
        dialog.setWidth("900px");
        dialog.setDraggable(true);
        
        //load grid based on selected student
        Collection<ActivitiesEntity> data = new ArrayList<>();
        double totalScore = 0;
        double maxScore = 0;
        for(ActivitiesEntity item : SERVICE_OBJ.fetchStudentActivities()) {
            if (studentIndex == item.getRowNumber()) {
              nameField.setValue(item.getFullname());
              indexNumberField.setValue(item.getIndexNumber());
                data.add(new ActivitiesEntity(
                    item.getActivityType(), item.getActivityTitle(), item.getActivityDate(), item.getmaximumScore(), item.getScore(), item.getPrograme()
                )); 
                totalScore += item.getScore(); 
                maxScore += item.getmaximumScore();  
            }           
        }
        grid.getColumnByKey("score").setFooter("Total Score: " + totalScore);
        grid.getColumnByKey("max").setFooter("Max Marks: " + maxScore);
        grid.setItems(data);
        dialog.open();
    }

}// end of class...
