package com.cass.views.reports;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import com.cass.views.classActivities.ManageClassActivityView;
import com.vaadin.flow.component.avatar.AvatarVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.dom.Style;
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
        viewButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_CONTRAST);

        layout.add(headerTitle, viewButton);

        // navigate to 'ActivitiesView.class' on button click.
        viewButton.getElement().addEventListener("click", callBack -> {
            UI.getCurrent().navigate(ManageClassActivityView.class);
        });
        return layout;
    }

    /****************************************************************************************************
     * CREATE FORM AND INPUT MINI LAYOUT
     ****************************************************************************************************/
    ComboBox<String> yearGroup = new ComboBox<>("Year Group");

    private Component createMiniSideLayout() {
        VerticalLayout layout = new VerticalLayout();
        ComboBox<String> programmeSelector = new ComboBox<>("Select Programme");
        ComboBox<String> courseSelector = new ComboBox<>("Select Course");
        TextField filterField = new TextField();
        final ComboBox<String> classSelector = new ComboBox<>("Section");
        SpecialMethods.setClassSections(classSelector);
        ComboBox<String> levelSelector = new ComboBox<>("Level");

        ListBox<ActivitiesEntity> listView = new ListBox<>();
        Button loadButton = new Button("Load Students");

        SpecialMethods.loadProgrammes(programmeSelector);
        SpecialMethods.setLevel(levelSelector);
        SpecialMethods.setYear(yearGroup);
        SpecialMethods.setCourses(courseSelector);

        levelSelector.setValue("100");
        loadButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_PRIMARY);
        loadButton.setWidthFull();
        courseSelector.setRequired(true);
        levelSelector.setWidthFull();
        levelSelector.setRequired(true);
        filterField.setWidthFull();
        courseSelector.setWidthFull();
        programmeSelector.setWidthFull();
        yearGroup.setWidthFull();
        yearGroup.setRequired(true);
        programmeSelector.setRequired(true);
        classSelector.setRequired(true);
        classSelector.setValue("A");
        classSelector.setWidthFull();
        classSelector.setInvalid(classSelector.isEmpty());

        // set component class names
        layout.setClassName("list-view-container");
        courseSelector.addClassNames("list-view-picker", "class-picker");
        filterField.setClassName("filter-activity-field");
        listView.setClassName("activity-view-list-view");
        loadButton.setClassName("activity-view-load-button");
        programmeSelector.addClassNames("list-view-picker", "semester-picker");
        // set filter-field placeHolder
        filterField.setPlaceholder("filter by index number");

        Div classAndLeveContainer = new Div(levelSelector, classSelector);
        classAndLeveContainer.addClassNames("class-level-container");
        classSelector.setClassName("item-selector");
        levelSelector.addClassNames("item-selector");

        layout.setWidthFull();
        layout.setSpacing(false);
        layout.add(filterField, new Hr(), programmeSelector, classAndLeveContainer, yearGroup, loadButton);

        // check and disable 'load button' if 'courseSelector' is empty
        layout.getElement().addEventListener("mouseover", callBack -> {
            UI.getCurrent().access(() -> {
                if (yearGroup.isEmpty() || programmeSelector.isEmpty() || levelSelector.isEmpty()) {
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
                Collection<StudentEntity> data = SERVICE_OBJ.getStudentByClass(programmeSelector.getValue(), yearGroup.getValue(), levelSelector.getValue(), classSelector.getValue());
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
                    boolean matchesIndex = filter.getIndexNumber().toLowerCase().contains(event.getValue().toLowerCase());
                    return matchesName || matchesIndex;
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
        studentGrid.addComponentColumn(component -> renderViewButton(component.getId())).setHeader("ACTION");

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
        avator.setImage("/icons/student-100.png");
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

    //create a record to display data...
    private record activityRecord(String activityType, String course, Date activityDate, double maxScore, double score,
                          String programme) {
    }

    private void renderDialogView(int studentIndex) {
        Dialog dialog = new Dialog();
        Grid<activityRecord> grid = new Grid<>();
        dialog.setHeaderTitle("STUDENT RECORD");
        TextField nameField = new TextField("Name");
        TextField indexNumberField = new TextField("Index Number");

        nameField.getStyle().setFontSize("small");
        indexNumberField.getStyle().setFontSize("small");

        nameField.setReadOnly(isAttached());
        indexNumberField.setReadOnly(isAttached());

        grid.addColumn(activityRecord::course).setHeader("COURSE");
//        grid.addColumn(ActivitiesEntity::getActivityTitle).setHeader("TITLE");
        grid.addColumn(activityRecord::activityType).setHeader("ACTIVITY TYPE");
        grid.addColumn(activityRecord::activityDate).setHeader("ENTRY DATE");
        grid.addColumn(activityRecord::score).setHeader("SCORE").setKey("score");
        grid.addColumn(activityRecord::score).setHeader("MAX SCORE").setKey("max");
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
        Collection<activityRecord> data = new ArrayList<>();
        double totalScore = 0;
        double maxScore = 0;
        for (ActivitiesEntity item : SERVICE_OBJ.fetchStudentActivities(yearGroup.getValue())) {
            if (studentIndex == item.getRowNumber()) {
                nameField.setValue(item.getFullname());
                indexNumberField.setValue(item.getIndexNumber());
                data.add(new activityRecord(item.getActivityType(), item.getCourse(), item.getActivityDate(), item.getMaximumSocre(), item.getScore(), item.getPrograme()
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
