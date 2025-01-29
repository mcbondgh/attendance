package com.cass.dialogs;

import com.cass.security.Encryption;
import com.cass.security.SessionManager;
import com.cass.services.UserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;

import java.util.Objects;

public class UpdatePasswordDialog extends UserService {

    private static final TextField usernameField = new TextField("Username");
    private static final PasswordField passwordField1 = new PasswordField("New Password");
    private static final PasswordField passwordField2 = new PasswordField("Confirm Password");
    private static final TextField roleField = new TextField("Role");
    private static final Button updateButton = new Button("Update Password");
    private static UserConfirmDialogs DIALOG;


    public UpdatePasswordDialog() {

    }

    public void openDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Update Your Password");

        FormLayout layout = new FormLayout();
        dialog.addClassName("user-view-add-dialog");
        layout.addClassName("user-view-dialog-layout");

        usernameField.setReadOnly(true);
        roleField.setReadOnly(true);
        passwordField1.setRequired(true);
        passwordField2.setRequired(true);
        updateButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);

        layout.add(usernameField, roleField, passwordField1, passwordField2, updateButton);

        passwordField1.setValueChangeMode(ValueChangeMode.EAGER);
        passwordField2.setValueChangeMode(ValueChangeMode.EAGER);

        boolean passwordsMatch = Objects.equals(passwordField1.getValue(), passwordField2.getValue());

        int roleId = Integer.parseInt(SessionManager.getAttribute("roleId").toString());
        String role = roleId == 1 ? "Admin" : roleId == 2 ? "Teaching Assistant" : "Class Rep";
        String username = SessionManager.getAttribute("activeUser").toString();

        usernameField.setValue(username);
        roleField.setValue(role);

        updateButton.addClickListener(e -> {
            if (passwordField2.isEmpty() || passwordField1.isEmpty()) {
                String errorMsg = "Password field cannot be empty";
                passwordField1.setErrorMessage(errorMsg);
                passwordField2.setErrorMessage(errorMsg);
                return;
            }
            if (!passwordsMatch) {
                String errorMsg = "Password fields do not match";
                passwordField1.setErrorMessage(errorMsg);
                passwordField2.setErrorMessage(errorMsg);
            }else {
                String encryptedPassword = Encryption.generateCipherText(passwordField1.getValue());
                if (updatePasswordOnly(username, encryptedPassword) > 0) {
                    DIALOG = new UserConfirmDialogs();
                    DIALOG.showSuccess("Password updated successfully");
                    passwordField1.clear();
                    passwordField2.clear();
                    dialog.close();
                }else {
                    new UserConfirmDialogs().showError("Oops! failed to update your password");
                    dialog.setHeaderTitle("Refresh page and try again");
                }
            }
        });

        dialog.add(layout);
        dialog.open();
    }
}
