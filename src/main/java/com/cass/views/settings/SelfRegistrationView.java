package com.cass.views.settings;


import com.cass.data.StudentEntity;
import com.cass.dialogs.UserConfirmDialogs;
import com.cass.services.StudentService;
import com.cass.special_methods.SpecialMethods;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.progressbar.ProgressBarVariant;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import jakarta.annotation.security.PermitAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.util.Map;
import java.util.concurrent.*;

@PageTitle("Self Registration")
@Route(value = "student/self-registration")
@PermitAll
public class SelfRegistrationView extends VerticalLayout implements BeforeEnterObserver {
    private static final Logger log = LoggerFactory.getLogger(SelfRegistrationView.class);
    StudentService STUDENT_SERVICE_OBJ = new StudentService();


    TextField studentNumberField = new TextField("Index Number", "B....00 or O4/..../....");
    TextField fullnameField = new TextField("Student Name");
    Select<String> programmeSelector = new Select<>();

    ComboBox<String> levelSelector = new ComboBox<>("Select Level");
    Button addNewStudentBtn = new Button("Save", LineAwesomeIcon.SAVE.create());
    ComboBox<String> yearSelector = new ComboBox<>("Year Group");
    ComboBox<String> sectionSelector = new ComboBox<>("Section");

    public SelfRegistrationView() {
        addClassNames("list-checker-page");
    }


    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        try {
            Map<String, Object> accessRoute = STUDENT_SERVICE_OBJ.fetchViewAccessRoutes();
            boolean IsAllowed = accessRoute.get("status").equals(true);
            if (!IsAllowed) {
                add(routeClosedComponent());
            } else {
                add(headerSection(), showStudentCheckListComponent());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Notification.show("Failed to retrieve data, kindly check your internet connection", 4000, Notification.Position.TOP_END);
//            UI.getCurrent().getPage().reload();
            ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
            scheduledExecutorService.scheduleAtFixedRate(() -> UI.getCurrent().getPage().reload(), 0, 10, TimeUnit.SECONDS);
        }

        //logics here...
    }

    @Override
    public void onAttach(AttachEvent event) {
        programmeSelector.setLabel("Programme");
        SpecialMethods.loadProgrammes(programmeSelector);
        SpecialMethods.setLevel(levelSelector);
        SpecialMethods.setYear(yearSelector);
        SpecialMethods.setClassSections(sectionSelector);

        studentNumberField.setRequired(true);
        fullnameField.setRequired(true);
        levelSelector.setRequired(true);
        programmeSelector.setRequiredIndicatorVisible(true);
        yearSelector.setRequired(true);
        sectionSelector.setRequired(true);

        studentNumberField.setValueChangeMode(ValueChangeMode.EAGER);
        fullnameField.setValueChangeMode(ValueChangeMode.EAGER);
    }


    private Component routeClosedComponent() {

        Paragraph paragraph = new Paragraph("This page is temporarily closed by administrator. Kindly revisit this page some other time when it's enabled by MC's REPUBLIC GH");
        var returnBtn = new Button("Return To Login", LineAwesomeIcon.BACKWARD_SOLID.create(), event -> {
            UI.getCurrent().navigate("login");
        });
        returnBtn.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_PRIMARY);
        returnBtn.addClassNames("default-button");
        returnBtn.getStyle().set("margin-top", "20px");
        returnBtn.setWidthFull();

        Section section = new Section(paragraph, returnBtn);
        section.setWidthFull();
        section.addClassNames("route-closed-layout");
        return section;
    }

    private VerticalLayout headerSection() {
        H3 headerText = new H3("Student Data Verification");
        Span span = new Span("Kindly provide your student INDEX NUMBER to check if you're part of your class list.");

        var parent = new VerticalLayout(headerText, span);
        parent.setWidthFull();
        parent.addClassNames("list-checker-layout");
        parent.setJustifyContentMode(JustifyContentMode.CENTER);
        parent.setAlignItems(Alignment.CENTER);
        return parent;
    }

    private VerticalLayout showStudentCheckListComponent() {
        VerticalLayout parentViewContainer = new VerticalLayout();
        parentViewContainer.addClassNames("student-check-list-parent-layout");

        TextField indexNumberField = new TextField("Index Number", "", "eg B... or 04/.../...");
        indexNumberField.setWidthFull();
        indexNumberField.addClassNames("input-style", "label-style");
        indexNumberField.setRequired(true);
        indexNumberField.setClearButtonVisible(true);
        indexNumberField.setValueChangeMode(ValueChangeMode.EAGER);

        var progressBar = new ProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setWidthFull();
        progressBar.setHeight("15px");
        progressBar.getStyle().setBoxSizing(Style.BoxSizing.BORDER_BOX);
        progressBar.addThemeVariants(ProgressBarVariant.LUMO_SUCCESS);

        HorizontalLayout notificationLayout = new HorizontalLayout(new Span("Fetching..."), progressBar);
        notificationLayout.getStyle().setMaxWidth("100%");
        notificationLayout.addClassNames("notification-horizontal-layout");

        var rowTwo = new VerticalLayout();
        rowTwo.setPadding(false);
        rowTwo.setMargin(false);
        rowTwo.setWidthFull();
        rowTwo.addClassNames("show-student-checker-row-two");

        Notification notification = new Notification(notificationLayout);
        notification.setPosition(Notification.Position.TOP_CENTER);
        notification.setDuration(50000);

        Button addButton = new Button("Add Yourself", LineAwesomeIcon.PLUS_CIRCLE_SOLID.create(), event -> {
            rowTwo.removeAll();
            rowTwo.add(addStudentForm());
        });

        addButton.addClassName("add-student-button");
        addButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_PRIMARY);

        var addStudentContainer = new Div(addButton);
        addStudentContainer.setWidthFull();
        addStudentContainer.addClassNames("add-student-button-container");

        Button searchButton = new Button("Search", LineAwesomeIcon.SEARCH_SOLID.create(), event -> {
            if (indexNumberField.getValue().isEmpty() || indexNumberField.isInvalid()) {
                indexNumberField.setErrorMessage("Please provide your INDEX NUMBER");
            } else {
                UI ui = UI.getCurrent();
                notification.open();
                String index = indexNumberField.getValue();
                var button = event.getSource();
                button.setText("Processing...");
                button.setEnabled(false);
                rowTwo.removeAll();
                CompletableFuture.supplyAsync(() -> STUDENT_SERVICE_OBJ.getStudentByStudentIndex(index)).thenAcceptAsync(result -> {
                    ui.access(() -> {
                        notification.close();
                        var dataSource = result.stream().findFirst();
                        button.setEnabled(true);
                        button.setText("Search");
                        if (result.isEmpty()) {
                            rowTwo.add(studentNotFoundComponent());
                        } else {
                            rowTwo.add(studentDetailsDisplay(dataSource.get()));
                        }
                    });
                });
            }
        });

        searchButton.setWidth("unset");
        searchButton.addClassNames("default-button");
        searchButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_PRIMARY);

        HorizontalLayout searchLayout = new HorizontalLayout(indexNumberField, searchButton);
        searchLayout.addClassNames("search-layout");
        searchLayout.setWidthFull();
        searchLayout.getStyle().set("gap", "10px");
        searchLayout.setAlignItems(Alignment.CENTER);

        parentViewContainer.add(addStudentContainer, searchLayout, rowTwo);

        return parentViewContainer;
    }

    private Component studentNotFoundComponent() {
        VerticalLayout layout = new VerticalLayout();
        layout.setWidthFull();
        layout.addClassNames("student-not-found-layout");

        Paragraph paragraph = new Paragraph("Student not found in any programme of study or class. Kindly click on the button below to add yourself to your class list.");
        layout.add(paragraph);
        return layout;
    }

    private Component addStudentForm() {
        FormLayout layout = new FormLayout();
        layout.addClassNames("add-student-form-layout");

        H3 headerText = new H3("Self Registration");
        Paragraph span = new Paragraph("Kindly fill in the form below to add yourself to your class list. NOTE: be sure to select and provide correct details about your class before confirming your final input");

        Div headerDiv = new Div(headerText, span);
        headerDiv.setWidthFull();
        headerDiv.addClassName("form-layout-header");

        Button addButton = new Button("Save", LineAwesomeIcon.SAVE.create(), event -> {
            UserConfirmDialogs dialogs = new UserConfirmDialogs();
            var checkIfEmpty = studentNumberField.isEmpty() || fullnameField.isEmpty() || programmeSelector.isEmpty() || levelSelector.isEmpty() ||
                    yearSelector.isEmpty() || sectionSelector.isEmpty();
            if (checkIfEmpty) {
                dialogs.showError("Kindly fill out all required fields");
            } else {
                event.getSource().setEnabled(false);
                event.getSource().setText("Processing...");
                processFormData(event);
            }
        });

        addButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_SMALL);
        addButton.setClassName("default-button");

        layout.add(headerDiv, 2);
        layout.add(studentNumberField, 2);
        layout.add(fullnameField, 2);
        layout.add(programmeSelector, levelSelector, yearSelector, sectionSelector);
        layout.add(new Hr());
        layout.add(addButton, 2);


        return layout;
    }

    private void processFormData(ClickEvent<Button> event) {
        UI ui = UI.getCurrent();
        CompletableFuture.supplyAsync(() -> STUDENT_SERVICE_OBJ.getStudentByStudentIndex(studentNumberField.getValue()).size())
                .thenAccept(studentExist -> {
                    ui.access(() -> {
                        UserConfirmDialogs confirmDialogs = new UserConfirmDialogs("Self Registration",
                                "Please be sure the data you provided is correct and accurate. If correct, kindly confirm to register else cancel to abort the process.");
                        event.getSource().setText("Save");
                        event.getSource().setEnabled(true);
                        if (studentExist > 0) {
                            confirmDialogs.showError("Student Index number is already registered");
                            studentNumberField.setErrorMessage("Index number is already registered");
                            studentNumberField.focus();
                        } else {
                            StudentEntity dataSource = new StudentEntity();
                            dataSource.setFullName(fullnameField.getValue());
                            dataSource.setIndexNumber(studentNumberField.getValue());
                            dataSource.setProgramme(programmeSelector.getValue());
                            dataSource.setLevel(levelSelector.getValue());
                            dataSource.setYearGroup(yearSelector.getValue());
                            dataSource.setSection(sectionSelector.getValue());
                            dataSource.setStudentClass(programmeSelector.getValue());

                            confirmDialogs.saveDialog().addConfirmListener(conf -> {
                                if (STUDENT_SERVICE_OBJ.saveNewStudent(dataSource) > 0) {
                                    UI.getCurrent().getPage().reload();
                                    confirmDialogs.showSuccess("Nice! you have successfully been added");
                                }
                            });
                        }
                    });
                });
    }

    private Component detailsRowLayout(Span span, H4 value) {
        FlexLayout layout = new FlexLayout(span, value);
        layout.setWidthFull();
        layout.getStyle().set("gap", "10px");
        layout.getStyle().setJustifyContent(Style.JustifyContent.SPACE_BETWEEN);
        layout.getStyle().setAlignItems(Style.AlignItems.CENTER);
        layout.addClassName("details-row-layout");

        return layout;
    }

    private Component studentDetailsDisplay(StudentEntity dataSource) {

        Div rowOne = new Div("Student Biodata Successfully Retrieved");
        rowOne.getElement().getThemeList().add("badge success pill");
        rowOne.addClassName("status-indicator");

        Span nameLabel = new Span("Full Name: ");
        H4 nameValue = new H4(dataSource.getFullName());
        var rowTwo = detailsRowLayout(nameLabel, nameValue);

        Span programmeLabel = new Span("Programme: ");
        H4 programmeValue = new H4(dataSource.getProgramme());
        var rowThree = detailsRowLayout(programmeLabel, programmeValue);

        Span levelLabel = new Span("Level: ");
        H4 levelValue = new H4(dataSource.getLevel());
        var rowFour = detailsRowLayout(levelLabel, levelValue);

        Span classLabel = new Span("Class Section: ");
        H4 classValue = new H4(dataSource.getLevel() + "" + dataSource.getSection());
        var rowFive = detailsRowLayout(classLabel, classValue);

        Span statusLabel = new Span("Status: ");
        H4 statusValue = new H4(dataSource.getStatus() == 1 ? "Active" : "Inactive");
        statusValue.getElement().getThemeList().add(statusValue.getText().equalsIgnoreCase("Active") ? "badge success pill" : "badge error pill");
        var rowSix = detailsRowLayout(statusLabel, statusValue);

        Span yearGroupLabel = new Span("Year Group");
        H4 yearGroupValue = new H4(dataSource.getYearGroup());
        var rowSeven = detailsRowLayout(yearGroupLabel, yearGroupValue);

        Button refereButton = new Button("Refresh Page", VaadinIcon.REFRESH.create(), event -> {
            UI.getCurrent().getPage().reload();
        });

        VerticalLayout layout = new VerticalLayout(rowOne, rowTwo, rowThree, rowFour, rowFive, rowSix, rowSeven, refereButton);
        layout.addClassNames("student-details-display-layout");
        layout.setWidthFull();
        return layout;
    }


}//end of class...
