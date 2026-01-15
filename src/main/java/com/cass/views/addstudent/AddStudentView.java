package com.cass.views.addstudent;

import com.cass.data.LoadTableGrid;
import com.cass.data.StudentEntity;
import com.cass.dialogs.UserConfirmDialogs;
import com.cass.services.StudentService;
import com.cass.special_methods.SpecialMethods;
import com.cass.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.vaadin.lineawesome.LineAwesomeIcon;

@PageTitle("Add Student")
@Route(value = "add-student", layout = MainLayout.class)
// @RolesAllowed("ADMIN")
@AnonymousAllowed
public class AddStudentView extends Composite<VerticalLayout> {

    StudentEntity STUDENT_ENTITY_OBJ = new StudentEntity();
    StudentService STUDENT_SERVICE_OBJ = new StudentService();

    private ComboBox<String> sectionPicker = new ComboBox<>("Select Section");
    private VerticalLayout pageLayout = new VerticalLayout();
    private FormLayout formLayout = new FormLayout();
    private TextField studentNumberField = new TextField("Student Number");
    private TextField fullnameField = new TextField("Student Name");
    private Select<String> programmeSelector = new Select<>();
    private final ComboBox<String> courseSelector = new ComboBox<>("Select Course");
    private final ComboBox<String> levelSelector = new ComboBox<>("Select Level");
    private final Button addNewStudentBtn = new Button("Add Student");
    private final Grid<StudentEntity> studentsTable = new Grid<>();
    private final ComboBox<String> yearGroup = new ComboBox<>();
    private final ComboBox<String> yearSelector = new ComboBox<>("Year Group");
    ComboBox<String> studentSearchLevel = new ComboBox<>();

    public AddStudentView() {
        pageLayout.setClassName("container");
        setClassName("page-container");
        SpecialMethods.setClassSections(sectionPicker);

        pageLayout.add(renderPageHeader(), studentGrid());
        pageLayout.setSizeFull();
        setRequiredFields();

        getContent().add(pageLayout);
    }

    private final Select<String> filterSelector = new Select<>();

    private Component renderPageHeader() {
        FlexLayout layout = new FlexLayout();
        layout.setWidthFull();
        layout.setAlignItems(Alignment.CENTER);
        layout.setJustifyContentMode(JustifyContentMode.BETWEEN);
        layout.addClassName("header-container");

        //declare needed components
        Button getStudentButton = new Button("Load Students");
        getStudentButton.addClassNames("default-button", "load-students-button");
        getStudentButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_CONTRAST);
        addNewStudentBtn.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_PRIMARY);
        addNewStudentBtn.addClassNames("default-button");

        ComboBox<String> programmePicker = new ComboBox<>();
        ComboBox<String> yearPicker = new ComboBox<>();
        ComboBox<String> sectionPicker = new ComboBox<>();
        ComboBox<String> levelPicker = new ComboBox<>();
        SpecialMethods.setClassSections(sectionPicker);

        programmePicker.addClassNames("item-picker");
        yearPicker.addClassNames("item-picker");
        sectionPicker.addClassNames("item-picker");
        levelPicker.addClassNames("item-picker");

        programmePicker.setPlaceholder("Select Programme");
        yearPicker.setPlaceholder("Select Year");
        sectionPicker.setPlaceholder("Select Section");

        SpecialMethods.setLevel(levelPicker);
        SpecialMethods.setYear(yearPicker);
        SpecialMethods.loadProgrammes(programmePicker);

        sectionPicker.setValue("A");
        levelPicker.setValue("100");
        yearPicker.setValue(String.valueOf(LocalDate.now().getYear()));

        programmePicker.setRequired(true);
        yearPicker.setRequired(true);

        HorizontalLayout itemsLayout = new HorizontalLayout(programmePicker, levelPicker, yearPicker, sectionPicker, getStudentButton);
        itemsLayout.addClassName("items-layout");
        itemsLayout.setAlignItems(Alignment.BASELINE);
        itemsLayout.setJustifyContentMode(JustifyContentMode.START);

        layout.add(itemsLayout, addNewStudentBtn);

        //ACTION EVENT LISTENERS
        getStudentButton.addClickListener(event -> {
            boolean checkFields = programmePicker.isEmpty() || yearPicker.isEmpty() || sectionPicker.isEmpty() || levelPicker.isEmpty();
            String errormsg = "Please make a selection";
            programmePicker.setErrorMessage(programmePicker.isInvalid() ? errormsg : null);
            yearPicker.setErrorMessage(yearPicker.isInvalid() ? errormsg : null);
            sectionPicker.setErrorMessage(sectionPicker.isInvalid() ? errormsg : null);

            if (!checkFields) {
                tableData(programmePicker.getValue(), yearPicker.getValue(), levelPicker.getValue(), sectionPicker.getValue());
                int tableSize = studentsTable.getListDataView().getItemCount();
                H6 tableLabel = new H6("TOTAL STUDENTS " + tableSize);
                tableLabel.setClassName("table-counter");
                studentsTable.getColumnByKey("indexNumberColumn").setFooter(tableLabel);
            }
        });
        addNewStudentBtn.addClickListener(event -> studentDialog());
        return layout;
    }


    private Component studentGrid() {
        TextField filterField = new TextField();
        filterField.setClassName("filter-field");
        filterField.setClearButtonVisible(true);

        filterField.setPlaceholder("Filter by Index Number or name");

        // studentsTable.addColumn(StudentEntity::getId).setHeader("ID").setFooter("TOTAL STUDENTS");
        studentsTable.addColumn(StudentEntity::getIndexNumber).setHeader("INDEX NUMBER").setKey("indexNumberColumn");
        studentsTable.addColumn(StudentEntity::getFullName).setHeader("FULL NAME");
        studentsTable.addColumn(StudentEntity::getStudentClass).setHeader("CLASS");
        studentsTable.addComponentColumn(item -> studentStatus(item.getStatus())).setHeader("STATUS");

        // studentsTable.addColumn(StudentEntity::getPrograme).setHeader("PROGRAM");
        // studentsTable.addComponentColumn(item -> {return editButton(item.getId());}).setHeader("ACTION");
        studentsTable.setItemDetailsRenderer(showStudentDetails());
        studentsTable.getColumns().forEach(col -> col.setAutoWidth(true));
        studentsTable.getColumns().forEach(col -> col.setSortable(true));
        studentsTable.setSizeUndefined();

        VerticalLayout layout = new VerticalLayout(filterField, studentsTable);
        layout.setSpacing(false);
        layout.setSizeFull();

        //filter table values 
        filterField.setValueChangeMode(ValueChangeMode.EAGER);
        filterField.addValueChangeListener(action -> {
            studentsTable.getListDataView().addFilter(filter -> {
                String filterText = filterField.getValue().toLowerCase();
                boolean matchesIndexNo = filter.getIndexNumber().toLowerCase().contains(filterText);
                boolean matchesName = filter.getFullName().toLowerCase().contains(filterText);
                return matchesIndexNo || matchesName;
            });
            studentsTable.getListDataView().refreshAll();
        });

        return layout;
    }

    //STUDENT STABLE DATA PROVIDER.
    protected GridListDataView<StudentEntity> tableData(String programme, String yearGroup, String level, String section) {
        return studentsTable.setItems(STUDENT_SERVICE_OBJ.getStudentByClass(programme, yearGroup, level, section));
    }

    //TABLE BUTTON IMPLEMEMTATION.
    protected Span editButton(int studentId) {
        Span span = new Span(LineAwesomeIcon.PEN_SOLID.create());
        span.setClassName("action-button");

        span.addClickListener(action -> {

        });
        return span;
    }

    protected Span studentStatus(byte status) {
        Span badge = new Span();
        badge.setText(status == 1 ? "active" : "inactive");
        badge.getElement().getThemeList().add(status == 1 ? "badge success pill" : "badge error pill");
        return badge;
    }

    //CREATE TABLE DETAILED CONTENT VIA A COMPONENT RENDERER
    private ComponentRenderer<FormLayout, StudentEntity> showStudentDetails() {
        return new ComponentRenderer<>(student -> {
            FormLayout updateFormLayout = new FormLayout();
            TextField nameField = new TextField("Name");
            TextField studentNumberField = new TextField("Index Number");
            RadioButtonGroup<String> checkbox = new RadioButtonGroup<>("Student Status", "active", "inactive");
            TextField rowNumber = new TextField("Roll Number");
            Select<String> studentClass = new Select<>();
            Div div1 = new Div(new H5("Update Student Data"));
            ComboBox<String> studentLevelSelector = new ComboBox<>("Level");
            SpecialMethods.setLevel(studentLevelSelector);
            ComboBox<String> studentYearSelector = new ComboBox<>("Year");
            SpecialMethods.setYear(studentYearSelector);
            ComboBox<String> studentSectionSelector = new ComboBox<>("Section");
            SpecialMethods.setClassSections(studentSectionSelector);

            studentLevelSelector.setClassName("item-selector");
            studentLevelSelector.setPlaceholder("Select Level");
            studentYearSelector.setClassName("item-selector");
            studentSectionSelector.setClassName("item-selector");

            //<theme-editor-local-classname>
            rowNumber.addClassName("add-student-view-select-2");
            //<theme-editor-local-classname>
            studentClass.setOverlayClassName("add-student-view-select-3");
            //<theme-editor-local-classname>
            studentClass.addClassName("add-student-view-select-3");
            Button updateButton = new Button("Update");
            Button removeButton = new Button("Remove");

            //set component class names
            div1.setClassName("updateFormLayout-header-div");
            updateFormLayout.setClassName("updateFormLayout-layout");

            checkbox.setValue(student.getStatus() == 1 ? "active" : "inactive");
            studentClass.setLabel("Class");
            checkbox.setLabel("Student Status");
            updateButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
            removeButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);

            SpecialMethods.loadProgrammes(studentClass);

            nameField.setValue(student.getFullName());
            studentNumberField.setValue(student.getIndexNumber());
            rowNumber.setValue(String.valueOf(student.getId()));
            rowNumber.setReadOnly(true);
            studentClass.setValue(student.getStudentClass());
            studentYearSelector.setValue(student.getYearGroup());
            studentLevelSelector.setValue(student.getLevel());
            studentSectionSelector.setValue(student.getSection());

            //set all fields as required
            nameField.setRequired(true);
            studentNumberField.setRequired(true);

            updateFormLayout.setResponsiveSteps(
                    new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
                    new FormLayout.ResponsiveStep("600px", 4, FormLayout.ResponsiveStep.LabelsPosition.TOP)
            );

            FlexLayout buttonsContainer = new FlexLayout(updateButton, removeButton);
            buttonsContainer.addClassNames("update-buttons-container");
            buttonsContainer.getStyle().set("gap", "10px");

            updateFormLayout.setColspan(div1, 4);
//            updateFormLayout.setColspan(nameField, 2);
            updateFormLayout.add(div1, nameField, studentNumberField, rowNumber, studentClass, studentYearSelector,
                    studentLevelSelector, studentSectionSelector, checkbox, buttonsContainer);

            //ACTION EVENTS 
            updateButton.addClickListener(event -> {

                if (studentNumberField.isEmpty() || nameField.isEmpty()) {
                    studentNumberField.setErrorMessage("required field");
                    nameField.setErrorMessage("required field");
                } else {
                    STUDENT_ENTITY_OBJ.setId(student.getId());
                    STUDENT_ENTITY_OBJ.setFullName(nameField.getValue());
                    STUDENT_ENTITY_OBJ.setIndexNumber(studentNumberField.getValue());
                    STUDENT_ENTITY_OBJ.setStudentClass(studentClass.getValue());
                    STUDENT_ENTITY_OBJ.setStatus((byte) (checkbox.getValue().equals("active") ? 1 : 0));
                    STUDENT_ENTITY_OBJ.setDateUpdated(Timestamp.valueOf(LocalDateTime.now()));
                    STUDENT_ENTITY_OBJ.setYearGroup(studentYearSelector.getValue());
                    STUDENT_ENTITY_OBJ.setLevel(studentLevelSelector.getValue());
                    STUDENT_ENTITY_OBJ.setSection(studentSectionSelector.getValue());
                    STUDENT_ENTITY_OBJ.setProgramme(studentClass.getValue());

                    new UserConfirmDialogs("UPDATE STUDENT DATA", "Do you wish to updated student data to current values").
                            saveDialog().addConfirmListener(ev -> {
                                if (STUDENT_SERVICE_OBJ.updateStudentData(STUDENT_ENTITY_OBJ) > 0) {
                                    UI.getCurrent().access(() -> {
                                        new UserConfirmDialogs().showSuccess("Nice, Student data successfully updated.");
                                        LoadTableGrid.loadTable(studentsTable, STUDENT_SERVICE_OBJ.getStudentByClass(studentClass.getValue(),
                                                yearGroup.getValue(),
                                                studentSearchLevel.getValue(),
                                                sectionPicker.getValue()));
                                    });
                                }
                            });
                }
            });

            //REMOVE STUDENT BUTTON CLICKED
            removeButton.addClickListener(event -> {
                UserConfirmDialogs confirmDialogs = new UserConfirmDialogs();
                new UserConfirmDialogs("REMOVE STUDENT", "By confirming, this student shall be permanently removed from the class list. Do you wish to continue?").saveDialog().addConfirmListener(confirmEvent -> {
                    int responseStatus = STUDENT_SERVICE_OBJ.removeStudent(Integer.parseInt(rowNumber.getValue()));
                    if (responseStatus > 0) {
                        confirmDialogs.showSuccess("NICE, Student successfully removed from class list.");
                        LoadTableGrid.loadTable(studentsTable, STUDENT_SERVICE_OBJ.getStudentByClass(studentClass.getValue(), yearGroup.getValue(),
                                studentSearchLevel.getValue(), sectionPicker.getValue()));
                    }
                });
            });
            return updateFormLayout;
        });
    }

    /*----------------------------------------*/

    private void setRequiredFields() {
        studentNumberField.setRequired(true);
        fullnameField.setRequired(true);
        programmeSelector.setEmptySelectionAllowed(false);
        programmeSelector.setRequiredIndicatorVisible(true);
        studentSearchLevel.setRequired(true);
//        programSelector.setEmptySelectionAllowed(false);
//        departmentSelector.setEmptySelectionAllowed(false);
        studentNumberField.setInvalid(studentNumberField.isEmpty());
        fullnameField.setInvalid(fullnameField.isEmpty());
        programmeSelector.setLabel("Select Programme");

        SpecialMethods.loadProgrammes(programmeSelector);
        SpecialMethods.setCourses(courseSelector);
        SpecialMethods.setLevel(studentSearchLevel);
        SpecialMethods.setLevel(levelSelector);

    }

    private boolean validateInputs() {
        return (studentNumberField.isInvalid() || fullnameField.isInvalid() || programmeSelector.isEmpty() || yearSelector.isEmpty() ||
                studentSearchLevel.isInvalid());
    }

    void resetFields() {
        String sub = studentNumberField.getValue().substring(0, 8);
        studentNumberField.setValue(sub);
        fullnameField.clear();
        // classSelector.clear();
        // programSelector.clear();
        // departmentSelector.clear();
    }

    /*--------------- */

    private FormLayout formDesign() {
        formLayout.setClassName("student-form-layout");
        // saveStudentButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
        studentSearchLevel.setLabel("Select Level");

        // earaseButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
        studentNumberField.setRequired(true);

        sectionPicker.setValue("A");
        sectionPicker.setWidthFull();
        levelSelector.addClassNames("item-picker");
        sectionPicker.addClassNames("item-picker");
//        sectionPicker.addThemeVariants(ComboBoxVariant.LUMO_SMALL);

        FlexLayout classAndLevelContainer = new FlexLayout(levelSelector, sectionPicker);
        classAndLevelContainer.addClassNames("leve-and-section-container");
        classAndLevelContainer.getStyle().set("gap", "10px");
        classAndLevelContainer.getStyle().setAlignItems(Style.AlignItems.CENTER);
        classAndLevelContainer.getStyle().setJustifyContent(Style.JustifyContent.SPACE_BETWEEN);
        classAndLevelContainer.setWidthFull();
        studentSearchLevel.setWidthFull();

        formLayout.add(
                studentNumberField, fullnameField,
                programmeSelector, yearSelector,
                classAndLevelContainer
        );

        return formLayout;
    }

    /*------------------------------------------------ */

    private void studentDialog() {
        Dialog studentFormDialog = new Dialog();
        Button saveStudentButton = new Button("Save");
        Button earaseButton = new Button("Clear");
        H5 dialogTitle = new H5("ADD NEW STUDENT");
        Span close = new Span(LineAwesomeIcon.WINDOW_CLOSE.create());
        HorizontalLayout hLayout = new HorizontalLayout(dialogTitle, close);

        SpecialMethods.setYear(yearSelector);
        yearSelector.setRequired(true);

        studentFormDialog.setClassName("student-dialog");
        dialogTitle.setClassName("dialog-title");
        close.setClassName("close-dialog-icon");
        hLayout.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        hLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);
        hLayout.setWidthFull();
        saveStudentButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        earaseButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

        // studentFormDialog.getHeader().removeAll();
        studentFormDialog.setCloseOnOutsideClick(false);
        studentFormDialog.getHeader().add(hLayout);
        studentFormDialog.getFooter().add(saveStudentButton, earaseButton);
        studentFormDialog.setDraggable(true);

        close.addClickListener(event -> {
            studentFormDialog.close();
        });

        earaseButton.addClickListener(event -> {
            resetFields();
        });

        /*------- events ---------- */
        saveStudentButton.addClickListener(event -> {
            UserConfirmDialogs dialogs = new UserConfirmDialogs("Save New Student", "Confirm to add student to selected class list.");
            boolean matchesIndexNo = false;
            String indexNumber = studentNumberField.getValue();
            for (StudentEntity item : STUDENT_SERVICE_OBJ.getStudentByStudentIndex(indexNumber)) {
                if (item.getIndexNumber().trim().equalsIgnoreCase(indexNumber.trim())) {
                    matchesIndexNo = true;
                }
            }
            if (validateInputs()) {
                dialogs.showError("Fill out all required fields");
            } else if (matchesIndexNo) {
                System.out.println(matchesIndexNo);
                new UserConfirmDialogs().showError("Index Number already exists, enter a unique index number");
                studentNumberField.setErrorMessage("Duplicate Index Number");
            } else {
                dialogs.saveDialog().addConfirmListener(click -> {

                    //feed entity instance with collected data.
                    STUDENT_ENTITY_OBJ.setIndexNumber(studentNumberField.getValue());
                    STUDENT_ENTITY_OBJ.setFullName(fullnameField.getValue());
                    STUDENT_ENTITY_OBJ.setLevel(levelSelector.getValue());
                    STUDENT_ENTITY_OBJ.setStudentClass(programmeSelector.getValue());
                    STUDENT_ENTITY_OBJ.setYearGroup(yearSelector.getValue());
                    STUDENT_ENTITY_OBJ.setProgramme(programmeSelector.getValue());
                    STUDENT_ENTITY_OBJ.setSection(sectionPicker.getValue());

                    if (STUDENT_SERVICE_OBJ.saveNewStudent(STUDENT_ENTITY_OBJ) > 0) {
                        getUI().get().access(() -> {
                            dialogs.showSuccess("Nice, student successfully added to class list");
                            resetFields();
                        });
                    } else {
                        dialogs.showError("Oops! failed to save student, index number may already exist");
                    }
                });
            }
        });
        studentFormDialog.add(formDesign());
        studentFormDialog.open();
    }

}// end of class...
