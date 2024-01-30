package com.cass.dialogs;

import java.time.Duration;

import org.vaadin.lineawesome.LineAwesomeIcon;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.BoxSizing;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;


public class UserConfirmDialogs {
    private Button confirmBtn = new Button("confirm");
    private Button declineButton = new Button("decline");
    private String title;
    private String message;
    private ConfirmDialog dialog = new ConfirmDialog();
    private Notification notify = new Notification();


    public UserConfirmDialogs(){}
    public UserConfirmDialogs(String title, String message) {
        this.title = title;
        this.message = message;
    }
    
    public ConfirmDialog saveDialog() {
        dialog.setConfirmButton(confirmBtn);
        dialog.setCancelButton(declineButton);
        dialog.setCancelable(true);
        
        confirmBtn.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        declineButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

        dialog.setHeader(title);
        dialog.add(new Span(message));

        dialog.addCancelListener(ex -> {dialog.close();});
        dialog.open();
        return dialog;
    }

    public void showSuccess(String message) {
        HorizontalLayout layout = new HorizontalLayout();
        Span span = new Span(message);
        layout.add(LineAwesomeIcon.CHECK_CIRCLE_SOLID.create(), span);
        layout.setBoxSizing(BoxSizing.BORDER_BOX);
        layout.setAlignItems(Alignment.CENTER);
        layout.setJustifyContentMode(JustifyContentMode.BETWEEN);

        notify.setPosition(Position.TOP_END);
        notify.setDuration(3000);
        layout.getStyle().setColor("green");
        notify.add(layout);
        notify.open();
    }

    public void showError(String message) {
        HorizontalLayout layout = new HorizontalLayout();
        Span span = new Span(message);
        layout.add(VaadinIcon.EXCLAMATION_CIRCLE_O.create(), span);
        layout.setBoxSizing(BoxSizing.BORDER_BOX);
        layout.setAlignItems(Alignment.CENTER);
        layout.setJustifyContentMode(JustifyContentMode.BETWEEN);

        notify.setPosition(Position.TOP_END);
        layout.getStyle().setColor("RED");
        notify.setDuration(3000);
        notify.add(layout);
        notify.open();
    }
    
}
