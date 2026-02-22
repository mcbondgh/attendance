package com.cass.views.dashboard;

import com.cass.dialogs.UserConfirmDialogs;
import com.cass.security.Encryption;
import com.cass.security.SessionManager;
import com.cass.services.UserService;
import com.cass.views.addstudent.RepAddStudentView;
import com.cass.views.login.UserLoginView;
import com.cass.views.reports.viewattendance.RepAttendanceView;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.vaadin.lineawesome.LineAwesomeIcon;

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
        signoutLink.addComponentAsFirst(VaadinIcon.SIGN_OUT.create());
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

    private void resetPasswordDialog() {
        Dialog passwordDialog = new Dialog();
        passwordDialog.setWidth("400px");
        passwordDialog.setHeight("400px");
        passwordDialog.addClassNames("reset-dialog");

        var h6 = new H6("Reset Password");
        var span = new Span("You are about to change your password. By confirming this process, your password shall be changed your preferred new password.");
        var rowOne = new Div(h6, span);
        rowOne.setWidthFull();
        rowOne.getStyle().setAlignItems(Style.AlignItems.CENTER);
        rowOne.addClassName("reset-dialog-row-one");

        PasswordField newPasswordField = new PasswordField("New Password", "***********");
        PasswordField confirmPasswordField = new PasswordField("Confirm Password", "*********");

        newPasswordField.setRequired(true);
        confirmPasswordField.setRequired(true);
        newPasswordField.setErrorMessage("Please enter a valid password");
        confirmPasswordField.setErrorMessage("Please enter a valid password");

        confirmPasswordField.setValueChangeMode(ValueChangeMode.EAGER);

        var button = new Button("Reset Password", VaadinIcon.CHECK.create(), e -> {
            var hashedPassword = Encryption.generateCipherText(newPasswordField.getValue());
            UserService userService = new UserService();
            if (userService.updatePasswordOnly(activeUser.get(), hashedPassword) > 0) {
                passwordDialog.close();
                new UserConfirmDialogs().showSuccess("Password successfully changed.");
            } else new UserConfirmDialogs().showError("An error occurred while changing your password.");
        });

        button.setWidthFull();
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
        button.addClassNames("default-button-style");
        button.setEnabled(false);

        confirmPasswordField.addValueChangeListener(e -> {
            boolean passwordMatches = newPasswordField.getValue().matches(confirmPasswordField.getValue());
            if (!passwordMatches) {
                confirmPasswordField.setErrorMessage("Passwords do not match");
                button.setEnabled(false);
            } else {
                button.setEnabled(true);
                confirmPasswordField.setErrorMessage(null);
            }
        });

        var rowTwo = new Div(newPasswordField, confirmPasswordField, button);
        rowTwo.setWidthFull();
        rowTwo.addClassName("reset-dialog-row-two");

        var parent = new Div(rowOne, rowTwo);
        parent.setWidthFull();
        parent.setClassName("reset-dialog-parent");
        passwordDialog.add(parent);
        passwordDialog.open();
    }

    private Component bodyView() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.addClassName("rep-dashboard-layout");

        var passwordMenu = createManuCard("Change Password", "icons/icons8-lock-30.png");
        var takeAttendanceMenu = createManuCard("Take Attendance", "icons/icons8-calendar-100.png");
        var viewAttendanceMenu = createManuCard("View Attendance", "icons/icons8-report-file-100.png");
        var listMenu = createManuCard("Class List", "icons/icon.png");

        FlexLayout flexLayout = new FlexLayout(passwordMenu, takeAttendanceMenu, viewAttendanceMenu, listMenu);
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
        passwordMenu.getElement().addEventListener("click", e -> {
            resetPasswordDialog();
        });

        takeAttendanceMenu.getElement().addEventListener("click", e -> {
            Notification.show("This feature is currently under development");
        });

        viewAttendanceMenu.getElement().addEventListener("click", e -> {
            UI.getCurrent().navigate(RepAttendanceView.class);
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
