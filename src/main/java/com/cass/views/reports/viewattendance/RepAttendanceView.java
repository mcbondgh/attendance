package com.cass.views.reports.viewattendance;

import com.cass.data.AttendanceRecordsEntity;
import com.cass.data.StudentEntity;
import com.cass.documents.DocumentGenerator;
import com.cass.security.SessionManager;
import com.cass.services.DAO;
import com.cass.special_methods.SpecialMethods;
import com.cass.views.dashboard.RepDashboardView;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@PageTitle("Attendance Record")
@Route("/rep-attendance-record")
public class RepAttendanceView extends VerticalLayout implements BeforeEnterObserver {

    private AtomicReference<String> activeUser, section, level, programme, studentYearGroup, programmeType;
    private DAO DATA_SOURCE;
    private final Grid<AttendanceRecordsEntity> studentsGrid = new Grid<>();
    private final ComboBox<String> courseSelector = new ComboBox<>();
    private DatePicker startDatePicker = new DatePicker("Start From");
    private DatePicker endDatePicker = new DatePicker("End At");
    private final Anchor exportLink = new Anchor();

    public RepAttendanceView() {
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        try {
            DATA_SOURCE = new DAO();
            String sessionUser = SessionManager.getAttribute("activeUser").toString();
            activeUser = new AtomicReference<>(sessionUser.toUpperCase());
            studentYearGroup = new AtomicReference<>(SessionManager.getAttribute("yearGroup").toString());
            level = new AtomicReference<>(SessionManager.getAttribute("level").toString());
            programme = new AtomicReference<>(SessionManager.getAttribute("class").toString());
            programmeType = new AtomicReference<>(SessionManager.getAttribute("programmeType").toString());
            section = new AtomicReference<>(SessionManager.getAttribute("section").toString());

//            Notification.show("Successful Navigation to Attendance View: " + activeUser.get());
        } catch (NullPointerException e) {
            UI.getCurrent().getPage().setLocation("/");
        }
    }

    @Override
    public void onAttach(AttachEvent event) {
        add(headerLayout(), bodyLayout());
    }

    private void configureGridColumns() {
        studentsGrid.setSizeUndefined();
        studentsGrid.setClassName("view-attendance-grid");

        // SET GRID COLUMNS
        studentsGrid.addColumn(AttendanceRecordsEntity::getId).setHeader("NO.");
        studentsGrid.addColumn(AttendanceRecordsEntity::getIndexNumber).setHeader("INDEX NUMBER");
        studentsGrid.addColumn(AttendanceRecordsEntity::getFullname).setHeader("FULL NAME");
        studentsGrid.addColumn(AttendanceRecordsEntity::getLevel).setHeader("LEVEL");
        studentsGrid.addComponentColumn(AttendanceRecordsEntity::getPresentLabel).setHeader("PRESENT").setKey("presentColumn");
        studentsGrid.addComponentColumn(AttendanceRecordsEntity::getAbscentLabel).setHeader("ABSENT").setKey("absentColumn");
//        studentsGrid.addComponentColumn(AttendanceRecordsEntity::getExcusedLabel).setHeader("EXCUSED").setKey("excusedColumn");
        studentsGrid.addComponentColumn(AttendanceRecordsEntity::getTotalAttendanceLabel).setHeader("TOTAL");

        studentsGrid.getColumns().forEach(each -> each.setAutoWidth(true));
        studentsGrid.getColumns().forEach(each -> each.setSortable(true));
        studentsGrid.getColumns().forEach(each -> each.setTextAlign(ColumnTextAlign.START));
    }

    private Component headerLayout() {
        FlexLayout layout = new FlexLayout();

        Button backButton = new Button("Back", VaadinIcon.ARROW_BACKWARD.create(), e -> {
            e.getSource().getUI().get().navigate(RepDashboardView.class);
        });

        backButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
        backButton.addClassName("back-button");

        H4 headerTitle = new H4(activeUser.get());
        Div container = new Div(new Span("Welcome"), headerTitle);
        container.addClassNames("rep-dashboard-title-div");
        container.getStyle().setAlignItems(Style.AlignItems.CENTER).setPadding("10px");

        layout.setClassName("rep-dashboard-header-container");
        layout.setJustifyContentMode(JustifyContentMode.BETWEEN);
        headerTitle.setClassName("dashboard-header-text");
        layout.setWidthFull();

        layout.add(container, backButton);
        return layout;
    }

    private Component filterSection() {
        var header = new H5("Filter Records");
        header.addClassName("add-student-title");

        startDatePicker.setWidthFull();
        startDatePicker.setValue(LocalDate.now());
        startDatePicker.setRequired(true);

        endDatePicker.setWidthFull();
        endDatePicker.setRequired(true);
        endDatePicker.setValue(LocalDate.now());

        courseSelector.setLabel("Select Course");
        courseSelector.setPlaceholder("Select Course");
        SpecialMethods.setCourses(courseSelector);
        courseSelector.setWidthFull();
        courseSelector.getStyle().setMinWidth("100%");
        courseSelector.setRequired(true);

        UI ui = UI.getCurrent();
        var button = new Button("Generate", LineAwesomeIcon.CHECK_CIRCLE_SOLID.create(), e -> {
            var btn = e.getSource();
            btn.setText("Generating...");
            AtomicInteger presentCounter = new AtomicInteger(0);
            AtomicInteger abscentCounter = new AtomicInteger(0);

            if (courseSelector.isEmpty()) {
                Notification.show("Please select a course.", 3000, Notification.Position.TOP_CENTER);
                return;
            }
            btn.setEnabled(false);
            var result = DATA_SOURCE.fetchAttendanceRecords(Date.valueOf(startDatePicker.getValue()), Date.valueOf(endDatePicker.getValue()), section.get(), programme.get(), studentYearGroup.get(), courseSelector.getValue());
            ui.access(() -> {
                btn.setEnabled(true);
                btn.setText("Generate");
                if (result.isEmpty()) {
                    Notification.show("No records found for the selected period.", 3000, Notification.Position.TOP_CENTER);
                }
                studentsGrid.setItems(result);

                if (studentsGrid.getListDataView().getItems().findAny().isPresent()) {
                    studentsGrid.getListDataView().getItems().forEach(eachItem -> {
                        if (eachItem.getPresent() >= 1) {
                            presentCounter.incrementAndGet();
                        }
                        if (eachItem.getAbscent() >= 1) {
                            abscentCounter.incrementAndGet();
                        }
                    });
                    studentsGrid.getColumnByKey("absentColumn").setFooter(new Span("TOTAL: " + abscentCounter.get()));
                    studentsGrid.getColumnByKey("presentColumn").setFooter(new Span("TOTAL: " + presentCounter.get()));
                    //do something here...
                    var inputStream = DocumentGenerator.generateAttendancePdf(startDatePicker.getValue(),
                            endDatePicker.getValue(), section.get(), courseSelector.getValue(), programme.get(), studentsGrid);
                    var resource = new StreamResource("AttendanceRecords.pdf", () -> inputStream);
                    exportLink.setHref(resource);
                    exportLink.setTarget("_blank");
                    exportLink.setEnabled(true);
                    exportLink.getElement().setAttribute("download", false);
                }
            });
//            CompletableFuture.supplyAsync(() -> {
//                var start = Date.valueOf(startDatePicker.getValue());
//                var end = Date.valueOf(endDatePicker.getValue());
//                return DATA_SOURCE.fetchAttendanceRecords(start, end, section.get(), programme.get(), studentYearGroup.get(), courseSelector.getValue());
//            }).thenAccept((result) -> {
//                ui.access(() -> {
//                    btn.setText("Generate");
//                    btn.setEnabled(true);
//                    studentsGrid.setItems(result);
//                    studentsGrid.setEnabled(true);
//                });
//            });
//            AtomicInteger excusedCounter = new AtomicInteger(0);
//            studentsGrid.getColumnByKey("excusedColumn").setFooter(new Span("TOTAL: " + excusedCounter.get()));
        });

        button.setWidthFull();
        button.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ICON);
        button.addClassName("default-button-style");

        var section = new Section(header, startDatePicker, endDatePicker, courseSelector, new Hr(), button);
        section.addClassNames("record-form-inner-box", "section-one");
        section.setWidthFull();
        return section;
    }

    private Component gridSection() {
        var header = new H5("Attendance Records");
        header.addClassName("add-student-title");

        configureGridColumns();

        var filterField = new TextField("", "filter by student name or index number");
        filterField.setClassName("filter-field");
        filterField.setClearButtonVisible(true);
        filterField.setWidthFull();
        filterField.setValueChangeMode(ValueChangeMode.EAGER);

        filterField.addValueChangeListener(action -> {
            studentsGrid.getListDataView().addFilter(filter -> {
                String filterText = filterField.getValue().toLowerCase();
                boolean matchesIndexNo = filter.getIndexNumber().toLowerCase().contains(filterText);
                boolean matchesName = filter.getFullname().toLowerCase().contains(filterText);
                return matchesIndexNo || matchesName;
            });
            studentsGrid.getListDataView().refreshAll();
        });
        exportLink.setText("Export");
        exportLink.setClassName("export-link");

        FlexLayout flexLayout = new FlexLayout(filterField, exportLink);
        flexLayout.setAlignItems(Alignment.CENTER);
        flexLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);
        flexLayout.setWidthFull();
        flexLayout.addClassNames("filter-export-container");


        var section = new VerticalLayout(header, new Hr(), flexLayout, studentsGrid);
        section.addClassNames("record-form-inner-box", "section-two");
        section.setWidthFull();
        return section;
    }

    private FormLayout bodyLayout() {
        FormLayout formLayout = new FormLayout();
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("600px", 8)
        );
        formLayout.setWidthFull();
        formLayout.addClassName("add-student-body-container");

        formLayout.add(filterSection(), 2);
        formLayout.add(gridSection(), 6);

        return formLayout;
    }
}//end of class...
