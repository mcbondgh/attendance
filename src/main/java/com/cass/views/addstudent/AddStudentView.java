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
import com.vaadin.flow.component.HasText.WhiteSpace;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.dom.Style.AlignItems;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import jakarta.annotation.security.RolesAllowed;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.vaadin.lineawesome.LineAwesomeIcon;

@PageTitle("Add Student")
@Route(value = "add-student", layout = MainLayout.class)
// @RolesAllowed("ADMIN")
@AnonymousAllowed
public class AddStudentView extends Composite<VerticalLayout> {

    StudentEntity STUDENT_ENTITY_OBJ = new StudentEntity();
    StudentService STUDENT_SERVICE_OBJ = new StudentService();

    private VerticalLayout pageLayout = new VerticalLayout();
    private FormLayout formLayout = new FormLayout();
    private TextField studentNumberField = new TextField("Student Number");
    private TextField fullnameField = new TextField("Student Name");
    private Select<String> classSelector = new Select<String>();
    private Select<String> programSelector = new Select<>();
    private Select<String> departmentSelector = new Select<>();
    private Button addNewStudentBtn = new Button("Add Student");
    private Grid<StudentEntity> studentsTable = new Grid<>();
    

    public AddStudentView() {
        pageLayout.setClassName("container");
        setClassName("page-container");

        pageLayout.add(renderPageHeader(), studentGrid());
        pageLayout.setSizeFull();
        setRequiredFields();

        getContent().add(pageLayout);
    }

    private Select<String> filterSelector = new Select<>();
    private Component renderPageHeader() {
        HorizontalLayout layout = new HorizontalLayout();
        //<theme-editor-local-classname>
        layout.addClassName("add-student-view-horizontal-layout-1");
        //<theme-editor-local-classname>
        filterSelector.setOverlayClassName("add-student-view-select-1");
        //<theme-editor-local-classname>
        filterSelector.addClassName("add-student-view-select-1");
        Button filterButton = new Button("Get Students"); 

        SpecialMethods.setClasses(filterSelector);

        Div filterDiv = new Div(filterSelector, filterButton);

        layout.add(filterDiv, addNewStudentBtn);

        addNewStudentBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addNewStudentBtn.addThemeVariants(ButtonVariant.LUMO_SMALL);

        addNewStudentBtn.setClassName("add-button");
        layout.setClassName("header-container");
        filterDiv.setClassName("header-filter-container");

        filterSelector.setClassName("filter-selector");

        // filterButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        filterButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
        filterSelector.setPlaceholder("Select Class");
        

        //ACTION EVENTS IMPLEMENTATION
        addNewStudentBtn.addClickListener(event -> {
            studentDialog();
        });

        filterButton.addClickListener(event -> {
            if(filterSelector.isEmpty()) {
                filterSelector.setInvalid(true);
                filterSelector.setErrorMessage("Select Program");
                
            } else {
                tableData(filterSelector.getValue());
                int tableSize = studentsTable.getListDataView().getItemCount();
                H6 tableLabel = new H6("TOTAL STUDENTS " + tableSize);
                tableLabel.setClassName("table-counter");
                studentsTable.getColumnByKey("indexNumberColumn").setFooter(tableLabel);
            }
        });
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
        studentsTable.addComponentColumn(item -> {return studentStatus(item.getStatus());}).setHeader("STATUS");

        // studentsTable.addColumn(StudentEntity::getPrograme).setHeader("PROGRAM");
        // studentsTable.addComponentColumn(item -> {return editButton(item.getId());}).setHeader("ACTION");
        studentsTable.setItemDetailsRenderer(showStudentDetails());
        studentsTable.getColumns().forEach(col -> col.setAutoWidth(true));
        studentsTable.getColumns().forEach(col -> col.setSortable(true));

        VerticalLayout layout = new VerticalLayout(filterField, studentsTable);
        layout.setSpacing(false);
        layout.setSizeFull();

        //filter table values 
        filterField.setValueChangeMode(ValueChangeMode.EAGER);
        filterField.addValueChangeListener(action -> {
            tableData(filterSelector.getValue()).addFilter(filter ->{
                String filterText = filterField.getValue().toLowerCase();
               boolean matchesIndexNo = filter.getIndexNumber().toLowerCase().contains(filterText);
               boolean matchesName = filter.getFullName().toLowerCase().contains(filterText);

               return matchesIndexNo || matchesName;
            }).refreshAll();
        });

        return layout;
    }

    //STUDENT STABLE DATA PROVIDER.
    protected GridListDataView<StudentEntity> tableData(String searchParameter) {
        GridListDataView<StudentEntity> data = studentsTable.setItems(STUDENT_SERVICE_OBJ.getStudentByClass(searchParameter));
        return data;
    }
        
    //TABLE BUTTON IMPLEMEMTATION.
    protected Span editButton(int studentId) {
        Span span = new Span(LineAwesomeIcon.PEN_SOLID.create());
        span.setClassName("action-button");

        span.addClickListener(action-> {
            
        });
        return span;
    }
    protected Span studentStatus(byte status) {
        Span badge = new Span();
        badge.setText(status == 1 ? "active" : "inactive");
        badge.getElement().getThemeList().add(status == 1 ? "badge success" : "badge error");
        return badge;
    }

    //CREATE TABLE DETAILED CONTENT VIA A COMPONENT RENDERER
    private ComponentRenderer<FormLayout, StudentEntity> showStudentDetails() {
        return new ComponentRenderer<>(student-> {
            FormLayout form = new FormLayout();
            TextField nameField = new TextField("Name");
            TextField studentNumberField = new TextField("Index Number");
            RadioButtonGroup<String> checkbox = new RadioButtonGroup<>("Student Status", "active","inactive");
            TextField rowNumber = new TextField("Roll Number");
            Select<String> studentClass = new Select<>();
            Div div1 = new Div(new H5("Update Student Data"));


            //<theme-editor-local-classname>
            rowNumber.addClassName("add-student-view-select-2");
            //<theme-editor-local-classname>
            studentClass.setOverlayClassName("add-student-view-select-3");
            //<theme-editor-local-classname>
            studentClass.addClassName("add-student-view-select-3");
            Button updateButton = new Button("Update");
            Button removeButton = new Button("Remove");


            //set component class names
            div1.setClassName("form-header-div");
            form.setClassName("form-layout");

            checkbox.setValue(student.getStatus() == 1 ? "active" : "inactive");
            studentClass.setLabel("Class");
            checkbox.setLabel("Student Status");
            updateButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
            removeButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
            
            SpecialMethods.setClasses(studentClass);

            nameField.setValue(student.getFullName());
            studentNumberField.setValue(student.getIndexNumber());
            rowNumber.setValue(String.valueOf(student.getId()));
            rowNumber.setReadOnly(true);
            studentClass.setValue(student.getStudentClass());

            //set all fields as required
            nameField.setRequired(true);
            studentNumberField.setRequired(true);
        
            form.setResponsiveSteps(
                    new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
                    new FormLayout.ResponsiveStep("600px", 4, FormLayout.ResponsiveStep.LabelsPosition.TOP)
            );

            form.setColspan(div1, 4);
            form.setColspan(nameField, 2);
            form.add(div1, nameField, studentNumberField, rowNumber, studentClass,  checkbox, updateButton, removeButton);

            //ACTION EVENTS 
            updateButton.addClickListener(event -> {
                                
                if (studentNumberField.isEmpty() || nameField.isEmpty()) {
                   studentNumberField.setErrorMessage("required field");
                   nameField.setErrorMessage("required field");
                }else {
                    STUDENT_ENTITY_OBJ.setId(student.getId());
                    STUDENT_ENTITY_OBJ.setFullName(nameField.getValue());
                    STUDENT_ENTITY_OBJ.setIndexNumber(studentNumberField.getValue());
                    STUDENT_ENTITY_OBJ.setStudentClass(studentClass.getValue());
                    STUDENT_ENTITY_OBJ.setStatus((byte) (checkbox.getValue().equals("active")? 1 : 0));
                    STUDENT_ENTITY_OBJ.setDateUpdated(Timestamp.valueOf(LocalDateTime.now()));
                    
                    new UserConfirmDialogs("UPDATE STUDENT DATA", "Do you wish to updated student data to current values").
                    saveDialog().addConfirmListener(evet -> {
                        if(STUDENT_SERVICE_OBJ.updateStudentData(STUDENT_ENTITY_OBJ) > 0) {
                            UI.getCurrent().access(()-> {
                                new UserConfirmDialogs().showSuccess("Nice, Student data successfully updated.");
                                studentsTable.getListDataView().refreshAll();
                                LoadTableGrid.loadTable(studentsTable, STUDENT_SERVICE_OBJ.getStudentByClass(studentClass.getValue()));
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
                    LoadTableGrid.loadTable(studentsTable, STUDENT_SERVICE_OBJ.getStudentByClass(studentClass.getValue()));
                }
               });
            });
            return form;
        });
        
    }
    
    /*----------------------------------------*/

    private void setRequiredFields() {
        studentNumberField.setRequired(true);
        fullnameField.setRequired(true);
        classSelector.setEmptySelectionAllowed(false);
        classSelector.setRequiredIndicatorVisible(true);
        programSelector.setEmptySelectionAllowed(false);
        departmentSelector.setEmptySelectionAllowed(false);
        studentNumberField.setInvalid(studentNumberField.isEmpty());
        fullnameField.setInvalid(fullnameField.isEmpty());
    

        departmentSelector.setItems("Computer Science");

        SpecialMethods.setClasses(classSelector);
        SpecialMethods.setPrograme(programSelector);
        SpecialMethods.setDepartment(departmentSelector);

    }

    private boolean validateInputs() {
        return (studentNumberField.isInvalid() || fullnameField.isInvalid() || classSelector.isEmpty());
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

        classSelector.setLabel("Select Class");
        programSelector.setLabel("Select Program");
        departmentSelector.setLabel("Select Department");

        // earaseButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
        studentNumberField.setRequired(true);

        formLayout.add(studentNumberField, fullnameField,
                classSelector);

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

        earaseButton.addClickListener(event ->{
            resetFields();
        });

        /*------- events ---------- */
        saveStudentButton.addClickListener(event -> {
            UserConfirmDialogs dialogs = new UserConfirmDialogs("Save New Student", "Confirm to add student to selected class list.");
            boolean matchesIndexNo = false;
                String indexNumber = studentNumberField.getValue();
                for(StudentEntity item : STUDENT_SERVICE_OBJ.getStudentByStudentIdex(indexNumber)) {
                    if(item.getIndexNumber().equalsIgnoreCase(indexNumber)) {
                        matchesIndexNo = true;
                    }
                }
            if (validateInputs()) {
                dialogs.showError("Fill out all required fields");
            } else if (matchesIndexNo) {
                System.out.println(matchesIndexNo);
                new UserConfirmDialogs().showError("Index Number already exists, enter a unique index number");
                studentNumberField.setErrorMessage("Duplicate Index Number");
            }else {
                dialogs.saveDialog().addConfirmListener(click -> {

                    //feed entity instance with collected data.
                    STUDENT_ENTITY_OBJ.setIndexNumber(studentNumberField.getValue());
                    STUDENT_ENTITY_OBJ.setFullName(fullnameField.getValue());
                    // STUDENT_ENTITY_OBJ.setDepartment(departmentSelector.getValue());
                    STUDENT_ENTITY_OBJ.setStudentClass(classSelector.getValue());
                    // STUDENT_ENTITY_OBJ.setPrograme(programSelector.getValue());

                    if (STUDENT_SERVICE_OBJ.saveNewStudent(STUDENT_ENTITY_OBJ) > 0) {
                        getUI().get().access(()-> {
                            dialogs.showSuccess("Nice, student successfully added to class list");
                            resetFields();
                        });
                    }
                });
            }
        });
        studentFormDialog.add(formDesign());
        studentFormDialog.open();
    }

}// end of class...
