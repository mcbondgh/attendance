package com.cass.views.manageusers;

import com.cass.data.UserLogsRecord;
import com.cass.services.DAO;
import com.cass.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@PageTitle("Manage Users")
@Route(value = "manage-users", layout = MainLayout.class)
// @RolesAllowed({"ADMIN","USERS"})
@AnonymousAllowed
public class UserLogsView extends VerticalLayout {

    Grid<UserLogsRecord> logsRecordGrid = new Grid<>();
    public UserLogsView() {
        setSizeFull();
        setClassName("content-page");
        add(configureGrid());
    }

    private Component configureGrid(){
        H3 headerText = new H3("Users Who Logged In");

        VerticalLayout layout = new VerticalLayout(headerText, logsRecordGrid);
        layout.addClassName("logs-container");
        layout.setSizeFull();
        logsRecordGrid.getStyle().setBoxSizing(Style.BoxSizing.BORDER_BOX);
        layout.setPadding(true);
        layout.getStyle().setBackgroundColor("#fff");
        layout.getStyle().setBorderRadius("5px");
        layout.getStyle().set("border", "1px solid #ddd");

        logsRecordGrid.addColumn(UserLogsRecord::userId).setHeader("ID");
        logsRecordGrid.addColumn(UserLogsRecord::username).setHeader("USERNAME");
        logsRecordGrid.addColumn(UserLogsRecord::role).setHeader("ROLE");
        logsRecordGrid.addColumn(UserLogsRecord::data).setHeader("DATE");

        logsRecordGrid.setItems(new DAO().getSignLogs());
        logsRecordGrid.setHeight("500px");
        return layout;
    }


}
