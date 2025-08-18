package com.cass.views.addstudent;

import com.cass.data.StudentEntity;
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
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
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
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@PageTitle("Add Student")
@Route("/rep-add-student")
public class RepAddStudentView extends VerticalLayout {
    private AtomicReference<String> activeUser, section, level, programme, studentYearGroup;
    private DAO DATA_SOURCE;

    public RepAddStudentView() {
        addClassName("content-page");
        try {
            DATA_SOURCE = new DAO();
            String sessionUser = SessionManager.getAttribute("activeUser").toString();
            activeUser = new AtomicReference<>(sessionUser.toUpperCase());
            studentYearGroup = new AtomicReference<>(SessionManager.getAttribute("yearGroup").toString());
            level = new AtomicReference<>(SessionManager.getAttribute("level").toString());
            programme = new AtomicReference<>(SessionManager.getAttribute("class").toString());
            section = new AtomicReference<>(SessionManager.getAttribute("section").toString());

        } catch (NullPointerException e) {
            UI.getCurrent().getPage().setLocation("/");
        }

    }

    @Override public void onAttach(AttachEvent event) {
        add(headerLayout(), bodyLayout());
    }

    private TextField nameField = new TextField("Student Name");
    private TextField indexNumberField = new TextField("Index Number");
    private Button saveButton = new Button("Save", LineAwesomeIcon.SAVE.create());

    private final Grid<StudentEntity> studentsGrid = new Grid<>(StudentEntity.class);


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
        layout.setSizeFull();
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

        VerticalLayout parent = new VerticalLayout(header, new Hr(), nameField, indexNumberField, new Hr(), saveButton);
        parent.setClassName("add-student-inner-layout");
        parent.setSpacing(false);
        parent.setWidth("29%");

        return parent;
    }

    private VerticalLayout gridSection() {
        var header = new H4("Total Students: " + totalStudentCount());
        header.addClassName("add-student-title");

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

        VerticalLayout parent = new VerticalLayout(header, new Hr(), filterField, configureStudentsGrid());
        parent.setSpacing(false);
        parent.setSizeFull();
        parent.setClassName("add-student-inner-layout");
        parent.setWidth("69%");
        return parent;
    }

//    COMPONENTS RENDERERS
    private final Grid<StudentEntity> configureStudentsGrid() {
        studentsGrid.setSizeFull();
        studentsGrid.addColumn(StudentEntity::getIndexNumber).setHeader("Index Number");
        studentsGrid.addColumn(StudentEntity::getFullName).setHeader("Full Name");
        studentsGrid.addColumn(StudentEntity::getStudentClass).setHeader("Class");
        studentsGrid.addColumn(studentStatus()).setHeader("Status");
        studentsGrid.getColumns().forEach(col -> {
            col.setAutoWidth(true);
            col.setResizable(true);
            col.setSortable(true);
        });
        studentsGrid.setItems(studentGridDataSource());
        studentsGrid.addClassName("student-grid");
        return studentsGrid;
    }

    private static Renderer<StudentEntity> studentStatus() {
        return new ComponentRenderer<>(data -> {
            Span badge = new Span();
            badge.setText(data.getStatus() == 1 ? "active" : "deactivated");
            badge.getElement().getThemeList().add(data.getStatus() == 1 ? "badge success pill" : "badge error pill");
            return badge;
        });
    }

    //REFERENCE METHODS IMPLEMENTATION
    private Collection<StudentEntity> studentGridDataSource() {
        return DATA_SOURCE.getStudentByClass(programme.get(), studentYearGroup.get(), level.get(), section.get());
    }

    private String totalStudentCount() {
        return String.valueOf(studentGridDataSource().size());
    }


}//end of class...
