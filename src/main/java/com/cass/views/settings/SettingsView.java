package com.cass.views.settings;

import com.cass.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Section;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@PageTitle("Settings")
@Route(value = "view/settings", layout = MainLayout.class)
@AnonymousAllowed
public class SettingsView extends VerticalLayout implements BeforeEnterObserver {

    public SettingsView() {

    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        addClassNames("content-page", "settings-page");
    }



    private Component showStudentCheckListComponent() {
        Section layout = new Section();

        Checkbox allowView = new Checkbox("Enable Self Registration", true);

        allowView.addClassName("sel-registration-checkbox");



        return layout;
    }


}//end of class...
