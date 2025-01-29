package com.cass.views.takeattendance;

import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

import org.vaadin.lineawesome.LineAwesomeIcon;

import com.cass.data.AttendanceEntity;
import com.cass.data.LoadTableGrid;
import com.cass.data.StudentEntity;
import com.cass.dialogs.UserConfirmDialogs;
import com.cass.services.StudentService;
import com.cass.special_methods.SpecialMethods;
import com.cass.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@PageTitle("Take Attendance")
@Route(value = "view/take_attendance", layout = MainLayout.class)
// @RolesAllowed({ "ADMIN", "USERS" })
@AnonymousAllowed
public class TakeAttendanceView extends VerticalLayout {
    StudentService SERVICE_OBJ = new StudentService();

    private DatePicker datePicker = new DatePicker("Attendance Date");
    private ComboBox<String> courseSelector = new ComboBox<>("Select Course");
    private final ComboBox<String> classSelect = new ComboBox<>("Select Class");
    private final Grid<StudentEntity> attendanceTable = new Grid<>();
    private final ComboBox<String> yearSelector = new ComboBox<>("Select Year");
    private final Button generateSheetButton = new Button("Generate Sheet");

    public TakeAttendanceView() {
        setSpacing(false);
        add(renderPageView());
    }

    // check for empty field
    boolean isFieldEmpty() {
        String error = "fill field";
        datePicker.setErrorMessage(error);
        courseSelector.setErrorMessage(error);
        classSelect.setErrorMessage(error);
        return datePicker.isInvalid() || courseSelector.isInvalid() || classSelect.isInvalid();
    }

    private Component renderPageView() {
        VerticalLayout layout = new VerticalLayout();

        courseSelector.addClassName("take-attendance-combobox");
        classSelect.addClassName("take-attendance-combobox");

        // LOAD SELECTORS.
        SpecialMethods.setCourses(courseSelector);
        SpecialMethods.setClasses(classSelect);
        SpecialMethods.setYear(yearSelector);

        // SET REQUIRED FIELDS
        datePicker.setRequired(true);
        datePicker.setInvalid(datePicker.isEmpty());
        courseSelector.setRequired(true);
        courseSelector.setInvalid(courseSelector.isEmpty());
        classSelect.setRequired(true);
        classSelect.setInvalid(classSelect.isEmpty());
        yearSelector.setRequired(true);
        yearSelector.setInvalid(yearSelector.isEmpty());
        // restrict date picker to today.
        LocalDate today = LocalDate.now(ZoneId.systemDefault());
        datePicker.setMin(today);

        HorizontalLayout headerLayout = new HorizontalLayout(datePicker, courseSelector, classSelect, yearSelector, generateSheetButton);

        layout.setSizeFull();
        layout.setClassName("main-container");
        headerLayout.setWidthFull();
        headerLayout.setClassName("header-layout");
        generateSheetButton.setClassName("filter-button");
        generateSheetButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_PRIMARY);

        layout.add(headerLayout, createTable());

        // ADD EVENT LISTENERS
        generateSheetButton.addClickListener(event -> {
            try {
                Date date = Date.valueOf(datePicker.getValue());
                if (isFieldEmpty()) {
                    new UserConfirmDialogs().showError("You have empty fields, please fill all.");
                } else if (SERVICE_OBJ.checkAttendanceByDate(date, courseSelector.getValue(), classSelect.getValue(), yearSelector.getValue())) {
                    new UserConfirmDialogs()
                            .showError("Sorry Attendance for the specified date and Programme already recorded");
                } else {
                    String studentClass = classSelect.getValue();
                    // loadTabelData(studentClass);
                    UI.getCurrent().access(()-> {
                        LoadTableGrid.loadTable(attendanceTable,
                                SERVICE_OBJ.fetchActiveStudentsForAttendanceTable(studentClass, yearSelector.getValue()));
                        int tableSize = attendanceTable.getListDataView().getItemCount();
                        H6 counterText = new H6(tableSize + " Students");
                        counterText.setClassName("table-counter");
                        attendanceTable.getColumnByKey("rollNoColumn").setFooter(counterText);
                    });
                }
            }catch(Exception e) {
                e.printStackTrace();
                new UserConfirmDialogs().showError("Please select attendance date");
            }
        });
        return layout;
    }
    // --------------------------------------

    // Load Data into a GridList
    protected GridListDataView<StudentEntity> loadTabelData(String searchString) {
        GridListDataView<StudentEntity> data = attendanceTable
                .setItems(SERVICE_OBJ.fetchActiveStudentsForAttendanceTable(searchString, yearSelector.getValue()));
        return data;
    }

    private Component createTable() {
        VerticalLayout layout = new VerticalLayout();
        Paragraph tableTitle = new Paragraph("Student Attendance Table");
        TextField filterField = new TextField();
        Button saveAttendanceBtn = new Button("Save Attendance");
        FlexLayout buttonsLayout = new FlexLayout(saveAttendanceBtn);
        filterField.setClassName("filter-text-field");
        filterField.setClearButtonVisible(true);
        layout.addClassName("table-layout");
        tableTitle.setClassName("attendance-table-title");
        saveAttendanceBtn.setClassName("save-button");
        buttonsLayout.setClassName("buttons-layout");
        filterField.setPlaceholder("Filter by index no. or name");
        layout.setSpacing(false);

        filterField.setPrefixComponent(LineAwesomeIcon.SEARCH_SOLID.create());

        // set table columns
        attendanceTable.addColumn(StudentEntity::getId).setHeader("ROLL NO.").setKey("rollNoColumn");
        attendanceTable.addColumn(StudentEntity::getIndexNumber).setHeader("INDEX NUMBER");
        attendanceTable.addColumn(StudentEntity::getFullName).setHeader("NAME");
        attendanceTable.addColumn(StudentEntity::getStudentClass).setHeader("CLASS");

        attendanceTable.addComponentColumn(student -> {
            return student.getAttendanceButton();
        }).setHeader("OPTION").setFrozenToEnd(true);

        attendanceTable.getColumns().forEach(each -> each.setAutoWidth(true));
        attendanceTable.getColumns().forEach(each -> each.setSortable(true));
        layout.add(tableTitle, filterField, attendanceTable, buttonsLayout);

        // ADD EVENT LISTENERS
        filterField.setValueChangeMode(ValueChangeMode.EAGER);
        filterField.addValueChangeListener(value -> {
            // add table filter to filter by user input...
            attendanceTable.getListDataView().setFilter(filter -> {
                if (filterField.isEmpty()) {
                    return true;
                }
                boolean matchesName = filter.getFullName().toLowerCase().contains(value.getValue().toLowerCase());
                boolean matchesIndex = filter.getIndexNumber().toLowerCase().contains(value.getValue().toLowerCase());
                boolean matchesRowNo = String.valueOf(filter.getId()).contains(value.getValue());
                return matchesName || matchesIndex || matchesRowNo;
            });
            attendanceTable.getListDataView().refreshAll();
        });

        // ADD EVENT LISTENER TO SAVE BUTTON TO ITERATE THROUGH TABLE AND GET ALL VALUES
        saveAttendanceBtn.addClickListener(event -> {
            boolean isTableEmpty = attendanceTable.getListDataView().getItemCount() == 0;
            if (isTableEmpty) {
                new UserConfirmDialogs().showError("Please generate attendance sheet first.");
                return;
            } else {
                new UserConfirmDialogs("SAVE ATTENDANCE",
                    "Please confirm to save attendance. NOTE: once confirmed, attendance for current date cannot be taken again.")
                    .saveDialog().addConfirmListener(confirm -> {
                        AttendanceEntity entity = new AttendanceEntity();
                        AtomicInteger responseStatus = new AtomicInteger(0);
                        attendanceTable.getListDataView().getItems().forEach(each -> {

                            Date date = Date.valueOf(datePicker.getValue());
                            String attendanceValue = each.getAttendanceButton().isEmpty() ? "A" : each.getAttendanceButton().getValue();
                            entity.setRowNumber(each.getId());
                            entity.setIndexNumber(each.getIndexNumber());
                            entity.setclassName(each.getStudentClass());
                            entity.setprogrameName(courseSelector.getValue());
                            entity.setAttendanceValue(attendanceValue);
                            entity.setAttendanceDate(date);
                            entity.setYearGroup(yearSelector.getValue());
                            responseStatus.addAndGet(SERVICE_OBJ.saveAttendance(entity));
                        });
                        if (responseStatus.get() > 0) {
                            UI.getCurrent().access(() -> {
                                attendanceTable.setItems(Collections.emptyList());
                                attendanceTable.getColumnByKey("rollNoColumn").setFooter("Empty Table");
                                new UserConfirmDialogs()
                                        .showSuccess("NICE, attendance for ".concat(datePicker.getValue().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)))
                                                + " successfully saved.");
                            });
                        } else {
                            new UserConfirmDialogs().showError("Sorry, failed to save attendance for current date");
                        }
                    });
            }
        });
        return layout;
    }

    // this method when invoked shall generate the attendance buttons for each
    // specified value
    private RadioButtonGroup<String> generateButtons() {
        HorizontalLayout layout = new HorizontalLayout();
        RadioButtonGroup<String> radioButtons = new RadioButtonGroup<>();
        radioButtons.setItems("P", "A", "excused");

        layout.setClassName("radio-button-layout");
        radioButtons.setClassName("radio-buttons");
        // radioButtons.addThemeVariant(RadioGroupVariant.LUMO_VERTICAL)
        // layout.add(radioButtons);
        return radioButtons;
    }

}
