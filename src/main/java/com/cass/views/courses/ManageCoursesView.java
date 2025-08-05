package com.cass.views.courses;

import com.cass.data.CourseRecord;
import com.cass.dialogs.UserConfirmDialogs;
import com.cass.services.CoursesModel;
import com.cass.services.DAO;
import com.cass.special_methods.SpecialMethods;
import com.cass.views.MainLayout;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.shared.Registration;

@PageTitle("Manage Course")
@Route(value = "courses", layout = MainLayout.class)
// @RolesAllowed({ "ADMIN", "USERS" })
@AnonymousAllowed
public class ManageCoursesView extends VerticalLayout implements BeforeEnterObserver {

    private final Grid<CourseRecord>coursesGrid = new Grid<>(CourseRecord.class, false);
    public ManageCoursesView() {
        setSpacing(true);
        setSizeFull();
        addClassName("content-page");
        add(renderPageHeader(),renderPageBody());
    }
    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {

    }

    @Override
    public Registration addAttachListener(ComponentEventListener<AttachEvent> listener) {
        return super.addAttachListener(listener);
    }


    /*******************************************************************************************************************
                                VIEWS
     *******************************************************************************************************************/
    private Component renderPageHeader() {
        HorizontalLayout layout = new HorizontalLayout();
        H5 headerTitle = new H5("ADD OR UPDATE EXISTING COURSE");
        Button viewButton = new Button("Add New Course");

        layout.addClassNames("dashboard-header-container", "view-header-container");
        headerTitle.setClassName("dashboard-header-text");
        viewButton.addClassName("activity-button");
        viewButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_CONTRAST);

        layout.add(headerTitle, viewButton);

        //navigate to 'ActivitiesView.class' on button click.
        viewButton.getElement().addEventListener("click", callBack -> {
            addCourseDialog();
        });
        return layout;
    }
    private Component renderPageBody() {
        VerticalLayout layout = new VerticalLayout(configureGrid());
        layout.addClassName("page-content-container");
//        layout.setSpacing(false);
        layout.setSizeFull();
        return layout;
    }


    /*******************************************************************************************************************
                                IMPLEMENTATION OF OTHER METHODS...
     *******************************************************************************************************************/

    public ListDataProvider<CourseRecord> loadCourseData() {
        return new ListDataProvider<>(new DAO().getAllCourses());
    }

    private Component configureGrid() {
        coursesGrid.addColumn(CourseRecord::id).setHeader("ID");
        coursesGrid.addColumn(CourseRecord::name).setHeader("Course Name");
        coursesGrid.addColumn(CourseRecord::code).setHeader("Course Code");
        coursesGrid.addColumn(CourseRecord::programme).setHeader("Programme");
        coursesGrid.addColumn(CourseRecord::level).setHeader("Level");
        coursesGrid.addThemeVariants(GridVariant.LUMO_COMPACT);
        coursesGrid.addClassNames("default-grid-style");
        coursesGrid.getColumns().forEach(col -> {
            col.setAutoWidth(true);
            col.setResizable(true);
        });
        coursesGrid.setSizeFull();
        coursesGrid.setItems(loadCourseData());
        return coursesGrid;
    }

    private void addCourseDialog() {
        H5 headerText = new H5("Add New Course");
        headerText.addClassName("header-text");

        TextField courseNameField = new TextField("Course Name", "eg Project Management");
        TextField courseCodeField = new TextField("Course Code", "CSD 300");
        ComboBox<String> levelSelector = new ComboBox<>("Level", "Level 100", "Leve 200", "Level 300", "Level 400");
        ComboBox<String> programSelector = new ComboBox<>("Programme");
        SpecialMethods.setProgramme(programSelector);

        courseNameField.setRequired(true);
        courseCodeField.setRequired(true);
        levelSelector.setRequired(true);
        programSelector.setRequired(true);

        courseNameField.setValueChangeMode(ValueChangeMode.EAGER);
        courseCodeField.setValueChangeMode(ValueChangeMode.EAGER);

        Button addButton = new Button("Add Course", VaadinIcon.PLUS_CIRCLE_O.create());
        addButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_PRIMARY);

        Div formContainer = new Div(courseNameField, courseCodeField, levelSelector, programSelector, addButton);
        formContainer.addClassNames("dialog-inner-container");

        formContainer.getElement().addEventListener("mouseover", e -> {
            String errorMsg = "Please fill field";
            courseNameField.setErrorMessage(errorMsg);
            courseCodeField.setErrorMessage(errorMsg);
            programSelector.setErrorMessage(errorMsg);
            levelSelector.setErrorMessage(errorMsg);
            boolean validateField = courseNameField.isInvalid() || courseCodeField.isInvalid() || programSelector.isEmpty() || levelSelector.isEmpty();
            addButton.setEnabled(!validateField);
        });

        formContainer.getChildren().forEach(item -> {
            Element component = item.getElement();
            component.getStyle().setWidth("100%");
        });

        addButton.addClickListener(e -> {
            CourseRecord.addCourseRecord formData = new CourseRecord.addCourseRecord(courseNameField.getValue(),
                    courseCodeField.getValue(), levelSelector.getValue(), programSelector.getValue());

           var dialog = new UserConfirmDialogs("SAVE COURSE", "Please confirm to save course else cancel to abort");
           dialog.saveDialog().addConfirmListener(confirmEvent -> {
               int responseStatus = new CoursesModel().createCourse(formData);
               if (responseStatus > 0) {
                   courseCodeField.clear();
                   courseNameField.clear();
                   coursesGrid.setItems(loadCourseData());
               }
           });
        });

        Dialog dialog = new Dialog();
        dialog.getHeader().add(headerText);
        dialog.add(formContainer);
        dialog.addClassNames("display-dialog", "add-course-dialog");
        dialog.open();
    }

}//end of class...
