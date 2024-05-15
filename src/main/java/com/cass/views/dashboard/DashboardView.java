package com.cass.views.dashboard;

import java.util.Map;

import org.vaadin.lineawesome.LineAwesomeIcon;

import com.cass.services.DAO;
import com.cass.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@PageTitle("Dashboard")
@Route(value = "dashboard", layout = MainLayout.class)
// @RouteAlias(value = "dashboard", layout = MainLayout.class)
@AnonymousAllowed
public class DashboardView extends VerticalLayout {

    private VerticalLayout pageContainer = new VerticalLayout();

    public DashboardView() {
        add(pageHeaderContainer(), pageContentContainer());
    }

    /***********************************************************************************
     * CREATE PAGE HEADER WITH TITLE 
     ************************************************************************************/

     private Component pageHeaderContainer() {
        HorizontalLayout layout = new HorizontalLayout();
        H4 headerTitle = new H4("ATTENDANCE SHEET BOARD");

        layout.setClassName("dashboard-header-container");
        headerTitle.setClassName("dashboard-header-text");

        layout.add(headerTitle);
        return layout;
     }


     /***********************************************************************************
     * CREATE PAGE CONTENT ITEMS
     ************************************************************************************/
     private Component pageContentContainer() {
        Map<String, Object> dataCounter = new DAO().getStudentCounter();

        HorizontalLayout hLayout = new HorizontalLayout();

        FormLayout layout = new FormLayout();
        Span title1 = new Span("BTech Computer Sci");
        Span title2 = new Span("TOTAL STUDENT");
        Span title3 = new Span("TOTAL CLASSES");
        Span title4 = new Span("Computer Science 2");
        Span title5 = new Span("Network 2A");
        Span title6 = new Span("Network 2B");
        Span title7 = new Span("Computer Science 3");
        Span title8 = new Span("Network 3A");
        Span title9 = new Span("Network 3B");

        H5 content1 = new H5(dataCounter.get("btech").toString());
        content1.addComponentAsFirst(LineAwesomeIcon.PROJECT_DIAGRAM_SOLID.create());
        H5 content2 = new H5(dataCounter.get("totalStudents").toString());
        content2.addComponentAsFirst(LineAwesomeIcon.GRADUATION_CAP_SOLID.create());
        H5 content3 = new H5("7");
        content3.addComponentAsFirst(LineAwesomeIcon.SCHOOL_SOLID.create());
        H5 content4 = new H5(dataCounter.get("comp2_students").toString());
        H5 content5 = new H5(dataCounter.get("network2a_students").toString());
        H5 content6 = new H5(dataCounter.get("network2c_students").toString());
        H5 content7 = new H5(dataCounter.get("comp3_students").toString());
        H5 content8 = new H5(dataCounter.get("network3a_students").toString());
        H5 content9 = new H5(dataCounter.get("network3c_students").toString());

        Div div1 = new Div(title1, content1);
        Div div2 = new Div(title2, content2);
        Div div3 = new Div(title3, content3);
        Div div4 = new Div(title4, content4);
        Div div5 = new Div(title5, content5);
        Div div6 = new Div(title6, content6);
        Div div7 = new Div(title7, content7);
        Div div8 = new Div(title8, content8);
        Div div9 = new Div(title9, content9);
        
        layout.add(div2, div3, div1, div4, div5, div6, div7, div8, div9);

        layout.setClassName("dashboard-form-layout");

        layout.setResponsiveSteps(
            new FormLayout.ResponsiveStep("0", 1),
            new FormLayout.ResponsiveStep("600px", 3)
        );
        return layout;
     }

}
