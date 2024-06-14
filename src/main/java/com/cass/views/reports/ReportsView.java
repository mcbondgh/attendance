package com.cass.views.reports;

import com.cass.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.docx4j.model.properties.run.VerticalAlignment;

@PageTitle("Generate Reports")
@Route(value = "/reports", layout = MainLayout.class)
public class ReportsView extends VerticalLayout {
    public ReportsView() {

    }


}
