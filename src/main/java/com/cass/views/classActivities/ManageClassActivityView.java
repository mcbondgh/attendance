package com.cass.views.classActivities;

import java.sql.Date;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import com.cass.views.reports.ActivitiesView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.dom.Style;
import org.vaadin.lineawesome.LineAwesomeIcon;

import com.cass.data.ActivitiesEntity;
import com.cass.data.LoadTableGrid;
import com.cass.dialogs.UserConfirmDialogs;
import com.cass.services.ActivityService;
import com.cass.services.DAO;
import com.cass.special_methods.SpecialMethods;
import com.cass.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@PageTitle("Class Activity")
@Route(value = "activities", layout = MainLayout.class)
// @RolesAllowed({ "ADMIN", "USERS" })
@AnonymousAllowed
@Uses(Icon.class)
public class ManageClassActivityView extends Composite<VerticalLayout> {

    private VerticalLayout tableLayout = new VerticalLayout();
    private Grid<ActivitiesEntity> activityTable = new Grid<>();
    private Button saveActivityButton = new Button("Save");
    private Button createActivityButton = new Button("Generate");
    private ComboBox<String> activitySelector = new ComboBox<>("Activity Type");
    private DatePicker datePicker = new DatePicker("Activity Date");
    private ComboBox<String> programmeSelector = new ComboBox<>("Programme");
    private TextField titleField = new TextField("Title");
    private ComboBox<String> courseSelector = new ComboBox<>("Course");
    private NumberField maxScoreField = new NumberField("Maximum Score");
    private Button toggleButton = new Button(" Add Activity");
    private final ComboBox<String> sectionSelector = new ComboBox<>("Section");
    private final ComboBox<String> levelSelector = new ComboBox<>("Level");

    public ManageClassActivityView() {
        setRequiredFields();
        getContent().add(renderPageHeader(), renderPageView());
        loadFields();
    }

    /****************************************************************************************************
     * OTHER METHODS IMPLEMENTATION
     ****************************************************************************************************/
    private void setRequiredFields() {
        createActivityButton.setEnabled(false);
        createActivityButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
        createActivityButton.addClassNames("default-button");
        datePicker.setRequired(true);
        datePicker.setInvalid(datePicker.isEmpty());
        maxScoreField.setRequired(true);
        maxScoreField.setAllowedCharPattern("[0-9.]");
        maxScoreField.setInvalid(maxScoreField.isEmpty());
        programmeSelector.setInvalid(programmeSelector.isEmpty());
        programmeSelector.setRequired(true);
        sectionSelector.setRequired(true);
        sectionSelector.setWidthFull();
        sectionSelector.setInvalid(sectionSelector.isEmpty());
        courseSelector.setRequired(true);
        courseSelector.setWidthFull();
        courseSelector.setInvalid(courseSelector.isEmpty());
        activitySelector.setRequired(true);
        activitySelector.setInvalid(activitySelector.isEmpty());
        levelSelector.setRequired(true);
        levelSelector.setInvalid(levelSelector.isEmpty());
        SpecialMethods.setClassSections(sectionSelector);
    }

    private void checkForEmptyFields(Component component) {
        component.getElement().addEventListener("mouseover", callBack -> {
            UI.getCurrent().access(() -> {
                createActivityButton.setEnabled(
                        !(datePicker.isInvalid() || levelSelector.isInvalid() || maxScoreField.isEmpty() || activitySelector.isInvalid() ||
                                programmeSelector.isInvalid() || courseSelector.isInvalid()));
            });
        });

    }

    private void loadFields() {
        SpecialMethods.setProgramme(programmeSelector);
        SpecialMethods.setActivityTypes(activitySelector);
        SpecialMethods.setCourses(courseSelector);
        SpecialMethods.setLevel(levelSelector);
    }

    /****************************************************************************************************
     * CREATE PAGE HEADER WITH 'VIEW-ACTIVITY' BUTTON
     ****************************************************************************************************/
    private Component renderPageHeader() {
        HorizontalLayout layout = new HorizontalLayout();
        H5 headerTitle = new H5("ACADEMIC ACTIVITY BOARD");
        Button viewButton = new Button("View Records");
        viewButton.addClassNames("default-button");

        layout.addClassNames("dashboard-header-container", "view-header-container");
        headerTitle.setClassName("dashboard-header-text");
        viewButton.addClassName("activity-button");
        viewButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_CONTRAST);

        layout.add(headerTitle, viewButton);

        //navigate to 'ActivitiesView.class' on button click.
        viewButton.getElement().addEventListener("click", callBack -> {
            UI.getCurrent().navigate(ActivitiesView.class);
        });
        return layout;
    }

    /****************************************************************************************************
     * CREATE INPUT FORM FOR DATA CAPTURING
     ****************************************************************************************************/
    private Component createForm() {
        H6 headerText = new H6("Add Activity");
        FormLayout formlayout = new FormLayout();
        VerticalLayout layout = new VerticalLayout();

        formlayout.addClassNames("activity-formlayout");
        layout.addClassName("activity-inner-formlayout");
        headerText.setClassName("activity-inner-teaderText");
        toggleButton.setClassName("toggle-button");

        formlayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("760px", 2));

        toggleButton.setPrefixComponent(VaadinIcon.PLUS.create());
        toggleButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
        createActivityButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);

        // Button logics to hide and show form based on screen size...
        // toggleButton.addClickListener(click -> {
        // boolean hasClassName = formlayout.hasClassName("show-content");
        // if (!hasClassName) {
        // formlayout.addClassName("show-content");
        // formlayout.removeClassName("hide-content");
        // } else {
        // formlayout.removeClassName("show-content");
        // formlayout.addClassName("hide-content");
        // }
        // });

        var dateAndActivityContainer = new FlexLayout(activitySelector, datePicker);
        dateAndActivityContainer.setAlignItems(Alignment.BASELINE);
        dateAndActivityContainer.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        dateAndActivityContainer.setFlexGrow(1);
        dateAndActivityContainer.getStyle().set("gap", "5px");
        dateAndActivityContainer.addClassNames("date-activity-container");

        Div classAndCourseContainer = new Div(courseSelector, sectionSelector);
        classAndCourseContainer.addClassNames("date-activity-container");
        classAndCourseContainer.getStyle().setAlignItems(Style.AlignItems.CENTER);

        activitySelector.addClassNames("item-selector", "activity-selector");
        datePicker.addClassNames("item-selector", "date-picker");
        programmeSelector.addClassNames("item-selector", "programme-selector");
        courseSelector.addClassNames("item-selector", "course-selector");
        sectionSelector.addClassNames("item-selector", "course-selector");
        levelSelector.addClassNames("item-selector", "level-selector");
        formlayout.add(programmeSelector, classAndCourseContainer, levelSelector, dateAndActivityContainer, maxScoreField, new Hr(),
                createActivityButton);

        layout.add(headerText, formlayout);

        // check for invalid fields and set button enabled or disables same
        checkForEmptyFields(formlayout);

        // ADD CLICK LISTENER TO THE generateButton TO FETCH AND FILL TABLE RECORDS.
        createActivityButton.addClickListener(clickEvent -> {
            UI.getCurrent().access(() -> {
                Collection<ActivitiesEntity> data = new DAO().fetchClassListByClassName(programmeSelector.getValue(), sectionSelector.getValue(), levelSelector.getValue());
                if (data.isEmpty()) {
                    new UserConfirmDialogs().showError("Empty class list");
                    activityTable.setItems(Collections.emptyList());
                } else {
                    LoadTableGrid.loadTable(activityTable, data);
                }
            });
        });
        return layout;
    }

    /****************************************************************************************************
     * CREATE ACTIVITY TABLE TO DISPLAY GENERATED CONTENT
     ****************************************************************************************************/
    private Component createTable() {
        TextField filterField = new TextField();

        activityTable.addClassName("activity-table");
        filterField.addClassName("filter-activity-field");
        filterField.setPlaceholder("filter by index number or name");
        saveActivityButton.addClassName("save-button");
        saveActivityButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_SUCCESS);
        saveActivityButton.setPrefixComponent(LineAwesomeIcon.SAVE.create());
        tableLayout.setWidthFull();

        // SET TABLE COLUMNS FOR THE GIRD
        activityTable.addColumn(ActivitiesEntity::getId).setHeader("ROW NO.");
        activityTable.addColumn(ActivitiesEntity::getIndexNumber).setHeader("INDEX NUMBER");
        activityTable.addColumn(ActivitiesEntity::getName).setHeader("FULL NAME");
        activityTable.addComponentColumn(conponent -> {
            conponent.getScoreField().setMax(maxScoreField.getValue());
            return conponent.getScoreField();
        }).setHeader("SCORE");

        activityTable.getColumns().forEach(each -> each.setAutoWidth(true));
        activityTable.getColumns().forEach(each -> each.setSortable(true));

        tableLayout.add(filterField, activityTable, saveActivityButton);
        tableLayout.setAlignSelf(Alignment.END, saveActivityButton);

        //ADD VALUE CHANGE LISTER TO FILTER TABLE BY SEARCH PARAMETER 
        filterField.setValueChangeMode(ValueChangeMode.EAGER);
        filterField.addValueChangeListener(change -> {
            activityTable.getListDataView().setFilter(filter -> {
                if (filterField.isEmpty()) {
                    return true;
                }
                boolean matchesName = filter.getName().toLowerCase().contains(change.getValue().toLowerCase());
                boolean matchesIndex = filter.getIndexNumber().toLowerCase().contains(change.getValue().toLowerCase());
                return matchesName || matchesIndex;
            }).refreshAll();

        });

        //ENABLE OR DISABLE SAVE BUTTON ON HOVER.
        tableLayout.getElement().addEventListener("mousemove", callBack -> {
            boolean isEmpty = activityTable.getListDataView().getItemCount() == 0;
            if (isEmpty) {
                saveActivityButton.setEnabled(false);
                saveActivityButton.addClassName("disable-button");
            } else {
                saveActivityButton.setEnabled(true);
                saveActivityButton.removeClassName("disable-button");
            }
        });

        //ADD CLICK EVENT TO SAVE BUTTON 
        saveActivityButton.addClickListener(event -> {
            ActivitiesEntity entity = new ActivitiesEntity();

            AtomicBoolean isFieldInvalid = new AtomicBoolean(false);
            activityTable.getListDataView().getItems().forEach(item -> {
                if (item.getScoreField().isInvalid()) {
                    isFieldInvalid.set(true);
                }
            });

            if (isFieldInvalid.getAcquire()) {
                new UserConfirmDialogs().showError("You have one or more invalid score inputs, check fields");
            } else {
                new UserConfirmDialogs("SAVE ACADEMIC ACTIVITY", "Confirm to save scores for specified activity.")
                        .saveDialog().addConfirmListener(confirm -> {
                            AtomicInteger counter = new AtomicInteger(0);
                            UI.getCurrent().access(() -> {
                                activityTable.getListDataView().getItems().forEach(item -> {
                                    entity.setRowNumber(item.getId());
                                    entity.setActivityType(activitySelector.getValue());
                                    entity.setCourse(courseSelector.getValue());
                                    entity.setClassName(sectionSelector.getValue());
                                    entity.setPrograme(programmeSelector.getValue());
                                    entity.setMaximumScore(maxScoreField.getValue());
                                    entity.setActivityDate(Date.valueOf(datePicker.getValue()));
                                    entity.setScore(item.getScoreField().getValue());
                                    entity.setLevel(levelSelector.getValue());
                                    counter.getAndAdd(new ActivityService().saveAcademicActivity(entity));
                                });
                                if (counter.get() > 0) {
                                    new UserConfirmDialogs().showSuccess("Nice, activity has successfully been saved.");
                                    activityTable.setItems(Collections.emptyList());
                                    levelSelector.clear();
                                    programmeSelector.clear();
                                    courseSelector.clear();
                                    maxScoreField.clear();
                                }
                            });
                        });
            }
        });
        return tableLayout;
    }

    /****************************************************************************************************
     * RENDER PAGE VIEW CONTAINING THE 'INPUT FORM' AND THE 'ACTIVITY-TABLE'
     ****************************************************************************************************/
    private Component renderPageView() {
        VerticalLayout layout = new VerticalLayout();
        FormLayout formlayout = new FormLayout();

        layout.setClassName("container");

        formlayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("600px", 4));

        formlayout.add(createForm(), createTable());

        formlayout.setColspan(tableLayout, 3);
        layout.add(formlayout);
        return layout;
    }

}// end of class...
