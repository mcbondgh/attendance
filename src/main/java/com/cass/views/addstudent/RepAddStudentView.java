package com.cass.views.addstudent;

import com.cass.data.StudentEntity;
import com.cass.documents.DocumentGenerator;
import com.cass.documents.DocumentStreams;
import com.cass.security.SessionManager;
import com.cass.services.DAO;
import com.cass.views.dashboard.RepDashboardView;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@PageTitle("Class List")
@Route("/rep-add-student")
public class RepAddStudentView extends VerticalLayout {
    private AtomicReference<String> activeUser, section, level, programme, studentYearGroup, programmeType;
    private DAO DATA_SOURCE;
    private final Grid<StudentEntity> studentsGrid = new Grid<>();

    public RepAddStudentView() {
        addClassName("content-page");
        try {
            DATA_SOURCE = new DAO();
            String sessionUser = SessionManager.getAttribute("activeUser").toString();
            activeUser = new AtomicReference<>(sessionUser.toUpperCase());
            studentYearGroup = new AtomicReference<>(SessionManager.getAttribute("yearGroup").toString());
            level = new AtomicReference<>(SessionManager.getAttribute("level").toString());
            programme = new AtomicReference<>(SessionManager.getAttribute("class").toString());
            programmeType = new AtomicReference<>(SessionManager.getAttribute("programmeType").toString());
            section = new AtomicReference<>(SessionManager.getAttribute("section").toString());

        } catch (NullPointerException e) {
            UI.getCurrent().getPage().setLocation("/");
        }

    }

    @Override
    public void onAttach(AttachEvent event) {
        add(headerLayout(), bodyLayout());
    }

    private TextField nameField = new TextField("Student Name");
    private TextField indexNumberField = new TextField("Index Number");
    private Button saveButton = new Button("Save", LineAwesomeIcon.SAVE.create());


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


    private Component bodyLayout() {
        FlexLayout layout = new FlexLayout();
        layout.setWidthFull();
        layout.addClassName("add-student-body-container");

        layout.add(formSection(), gridSection());
        return layout;
    }

    private Component formSection() {
        var header = new H4("Add New Student");
        header.addClassName("add-student-title");

        saveButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_PRIMARY);
        saveButton.setWidthFull();
        saveButton.addClassName("default-button-style");
        saveButton.setEnabled(false);

        nameField.setWidthFull();
        indexNumberField.setWidthFull();

        VerticalLayout parent = new VerticalLayout(header, new Hr(), nameField, indexNumberField, new Hr(), saveButton);
        parent.setClassName("add-student-inner-layout");
        parent.setSpacing(false);
        parent.getStyle().setMarginTop("0");
        parent.setWidth("20%");

        return parent;
    }

    private VerticalLayout gridSection() {
        var header = new H5("Total Students: " + totalStudentCount());
        header.addClassName("add-student-title");

        configureStudentsGrid();

        var filterField = new TextField("", "filter by student name or index number");
        filterField.setClassName("filter-field");
        filterField.setClearButtonVisible(true);

        //filter table values
        filterField.setValueChangeMode(ValueChangeMode.EAGER);
        filterField.addValueChangeListener(action -> {
            studentsGrid.getListDataView().addFilter(filter -> {
                String filterText = filterField.getValue().toLowerCase();
                boolean matchesIndexNo = filter.getIndexNumber().toLowerCase().contains(filterText);
                boolean matchesName = filter.getFullName().toLowerCase().contains(filterText);
                return matchesIndexNo || matchesName;
            });
            studentsGrid.getListDataView().refreshAll();
        });

        Anchor exportLink = new Anchor();
        exportLink.setText("Export");
        exportLink.addClassName("export-link");

        //check and set the export button to active if the grid is not empty to allow table to be exported.
        if (studentsGrid.getListDataView().getItems().findAny().isPresent()) {
            String filename = programme.get() + "_class_list.xlsx";
            var stream = DocumentGenerator.generateStudentList(programme.get(), studentsGrid);
            exportLink.setHref(DocumentStreams.createFileResource(filename, stream));
        }

        FlexLayout flexLayout = new FlexLayout(filterField, exportLink);
        flexLayout.setAlignItems(Alignment.CENTER);
        flexLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);
        flexLayout.setWidthFull();
        flexLayout.addClassNames("filter-export-container");

        VerticalLayout parent = new VerticalLayout(header, new Hr(), flexLayout, studentsGrid);
        parent.setSpacing(false);
        parent.setSizeFull();
        parent.setClassName("add-student-inner-layout");
        parent.setWidth("79%");
        return parent;
    }

    //    COMPONENTS RENDERERS
    private void configureStudentsGrid() {
        studentsGrid.setSizeUndefined();
        studentsGrid.addColumn(StudentEntity::getIndexNumber).setHeader("Index Number");
        studentsGrid.addColumn(StudentEntity::getFullName).setHeader("Full Name");
        studentsGrid.addColumn(StudentEntity::getStudentClass).setHeader("Programme");
//        studentsGrid.addColumn(StudentEntity::getYearGroup).setHeader("Year");
        studentsGrid.addColumn(StudentEntity::getLevel).setHeader("Level");
        studentsGrid.addColumn(StudentEntity::getSection).setHeader("Group");
        studentsGrid.addColumn(studentStatus()).setHeader("Status");
        studentsGrid.getColumns().forEach(col -> {
            col.setAutoWidth(true);
            col.setResizable(true);
            col.setSortable(true);
        });
        studentsGrid.setItems(studentGridDataSource());
        studentsGrid.addClassName("student-grid");

    }

    private static Renderer<StudentEntity> studentStatus() {
        return new ComponentRenderer<>(data -> {
            Span badge = new Span();
            badge.getStyle().setFontSize("smaller");
            badge.setText(data.getStatus() == 1 ? "active" : "deactivated");
            badge.getElement().getThemeList().add(data.getStatus() == 1 ? "badge success pill" : "badge error pill");
            return badge;
        });
    }

    //REFERENCE METHODS IMPLEMENTATION
    private Collection<StudentEntity> studentGridDataSource() {
        return DATA_SOURCE.getStudentByClass(programme.get(), programmeType.get(), studentYearGroup.get(), level.get(), section.get());
    }

    private String totalStudentCount() {
        return String.valueOf(studentGridDataSource().size());
    }


}//end of class...
