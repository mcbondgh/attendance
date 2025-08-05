package com.cass.views.addstudent;

import com.cass.security.SessionManager;
import com.cass.views.dashboard.RepDashboardView;
import com.cass.views.login.UserLoginView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.concurrent.atomic.AtomicReference;

@PageTitle("Add Student")
@Route("/rep-add-student")
public class RepAddStudentView extends VerticalLayout {
    AtomicReference<String> activeUser;

    public RepAddStudentView() {
        addClassName("content-page");
        try {
            String sessionUser = SessionManager.getAttribute("activeUser").toString();
            activeUser = new AtomicReference<>(sessionUser.toUpperCase());
        } catch (NullPointerException e) {
            UI.getCurrent().getPage().setLocation("/");
        }

        add(headerLayout(), bodyLayout());
    }

    private TextField nameField = new TextField("Student Name");
    private TextField indexNumberField = new TextField("Index Number");
    private Select<String> programSelector = new Select<>();
    private final ComboBox<String> yearGroup = new ComboBox<>();
    private final ComboBox<String> yearSelector = new ComboBox<>("Year Group");
    ComboBox<String> studentSearchLevel = new ComboBox<>();


    private Component headerLayout() {
        FlexLayout layout = new FlexLayout();

        Button backButton = new Button("Back", VaadinIcon.ARROW_BACKWARD.create(), e -> {
            e.getSource().getUI().get().navigate(RepDashboardView.class);
        });

        backButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
        backButton.addClassName("back-button");


        H4 headerTitle = new H4(activeUser.get());
        Div container = new Div(new Span("Welcome: "), headerTitle);
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

        VerticalLayout parent = new VerticalLayout(header);
        parent.setClassName("add-student-inner-layout");
        parent.setWidth("29%");

        return parent;
    }

    private Component gridSection() {
        var header = new H4("Class List");
        header.addClassName("add-student-title");

        VerticalLayout parent = new VerticalLayout(header);
        parent.setClassName("add-student-inner-layout");
        parent.setWidth("69%");
        return parent;
    }


}//end of class...
