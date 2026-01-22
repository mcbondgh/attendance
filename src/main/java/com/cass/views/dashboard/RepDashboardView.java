package com.cass.views.dashboard;

import com.cass.security.SessionManager;
import com.cass.views.addstudent.RepAddStudentView;
import com.cass.views.login.UserLoginView;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.concurrent.atomic.AtomicReference;

@Route("/rep-dashboard")
@PageTitle("Rep Dashboard")
public class RepDashboardView extends VerticalLayout {

    private AtomicReference<String> activeUser;
    public RepDashboardView() {
        addClassName("content-page");
        try {
            String sessionUser = SessionManager.getAttribute("activeUser").toString();
            activeUser = new AtomicReference<>(sessionUser.toUpperCase());

        } catch (NullPointerException e) {
            UI.getCurrent().getPage().setLocation("/login");
        }
    }

    @Override
    public void onAttach(AttachEvent event) {
        add(
                headerLayout(),
                bodyView()
        );

    }

    private Component headerLayout() {
        FlexLayout layout = new FlexLayout();

        Anchor signoutLink = new Anchor("javascript:void(0)", "sign out");
        signoutLink.setWidthFull();
        signoutLink.addClassName("signout-link");

        //logout user...
        signoutLink.getElement().addEventListener("click", callBack -> {
            SessionManager.destroySession();
            UI.getCurrent().getPage().setLocation("/");
        });

        H4 headerTitle = new H4(activeUser.get());
        Div container = new Div(new Span("Welcome: "), headerTitle);
        container.addClassNames("rep-dashboard-title-div");
        container.getStyle().setAlignItems(Style.AlignItems.CENTER).setPadding("10px");


        layout.setClassName("rep-dashboard-header-container");
        layout.setJustifyContentMode(JustifyContentMode.BETWEEN);
        headerTitle.setClassName("dashboard-header-text");
        layout.setWidthFull();

        layout.add(container, signoutLink);
        return layout;
    }

    private Component bodyView() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.addClassName("rep-dashboard-layout");

        var takeAttendanceMenu = createManuCard("Take Attendance", "icons/icons8-calendar-100.png");
        var viewAttendanceMenu = createManuCard("View Attendance", "icons/icons8-report-file-100.png");
        var listMenu = createManuCard("Class List", "icons/icon.png");

        FlexLayout flexLayout = new FlexLayout(takeAttendanceMenu, viewAttendanceMenu, listMenu);
        flexLayout.addClassName("rep-dashboard-menu-layout");
        flexLayout.setWidthFull();

        var title = new H2("ATTENDANCE SHEET BOARD");
        title.setWidthFull();
        title.addClassNames("rep-dashboard-sheet-title", "dashboard-header-text");
        title.getStyle()
                .setJustifyContent(Style.JustifyContent.CENTER)
                .setAlignItems(Style.AlignItems.CENTER);

        layout.add(title, flexLayout);

        //set click listeners to menu buttons
        takeAttendanceMenu.getElement().addEventListener("click", e -> {
            Notification.show("Take Attendance Menu");
        });

        viewAttendanceMenu.getElement().addEventListener("click", e -> {
            Notification.show("View Attendance Menu");
        });

        listMenu.getElement().addEventListener("click", e -> {
            UI.getCurrent().navigate(RepAddStudentView.class);
        });

        return layout;
    }

    private Component createManuCard(String title, String imageSource) {
        Image image = new Image(imageSource, "IMG");
        var label = new H3(title);

        VerticalLayout layout = new VerticalLayout(image, label);
        layout.setWidthFull();
        layout.getStyle().setAlignItems(Style.AlignItems.CENTER).setJustifyContent(Style.JustifyContent.CENTER);
        layout.addClassName("menu-card-component");


        return layout;
    }


}//end of class...
