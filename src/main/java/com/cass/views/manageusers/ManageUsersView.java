package com.cass.views.manageusers;

import com.cass.data.LoadTableGrid;
import com.cass.data.UsersEntity;
import com.cass.dialogs.UserConfirmDialogs;
import com.cass.security.Encryption;
import com.cass.services.UserService;
import com.cass.special_methods.SpecialMethods;
import com.cass.views.MainLayout;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.Autocomplete;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

@PageTitle("User Logs")
@Route(value = "user-logs", layout = MainLayout.class)
// @RolesAllowed({"ADMIN","USERS"})
@AnonymousAllowed
public class ManageUsersView extends VerticalLayout implements HasComponents, HasStyle {

    private OrderedList imageContainer;
    private final Grid<UsersEntity> usersTable = new Grid<>(UsersEntity.class, false);
    UserService SERVICE_OBJ = new UserService();
    private TextField indexNumberField = new TextField("Index Number", "04/2020/0203");

    public ManageUsersView() {
        add(renderPageHeader(), renderPageView());
    }

    @Override
    public void onAttach(AttachEvent event) {
        indexNumberField.setRequired(true);
        indexNumberField.setMaxLength(20);
    }


    private Component renderPageHeader() {
        HorizontalLayout layout = new HorizontalLayout();
        H5 title = new H5("USERS & ROLES BOARD");
        Button addButton = new Button("Add New");

        layout.setWidthFull();
        layout.setAlignItems(Alignment.CENTER);
        layout.setJustifyContentMode(JustifyContentMode.BETWEEN);
        addButton.addThemeVariants(ButtonVariant.LUMO_SMALL);

        title.setClassName("dashboard-header-text");
        layout.addClassNames("dashboard-header-container", "view-header-containere");

        layout.add(title, addButton);

        //ADD CLICK LISTENER TO SHOW DIALOG BOX FOR ADDING NEW USERS
        addButton.addClickListener(action -> {
            constructDialog();
        });
        return layout;
    }

    /***********************************************************************************
     * RENDER PAGE VIEW
     ***********************************************************************************/
    private Component renderPageView() {
        VerticalLayout layout = new VerticalLayout();

        layout.add(createUsersTable());
        layout.addClassName("container");
        return layout;
    }


    /***********************************************************************************
     * CREATE AND DESIGN OF THE 'users Table'
     ***********************************************************************************/
    private Component createUsersTable() {
        usersTable.addColumn(UsersEntity::getId).setHeader("ROW ID");
        usersTable.addColumn(UsersEntity::getUsername).setHeader("USERNAME");
        usersTable.addColumn(UsersEntity::getRoleName).setHeader("ROLE");
        usersTable.addComponentColumn(UsersEntity::getStatusValue).setHeader("STATUS");
        LoadTableGrid.loadTable(usersTable, SERVICE_OBJ.getAllUsers());
        usersTable.setClassName("users-table");

        usersTable.setItemDetailsRenderer(createUserUpdateComponentRenderer());
        return usersTable;
    }

    /***********************************************************************************
     * CREATE A COMPONENT RENDERER TO ALLOW USER UPDATE
     ***********************************************************************************/
    private ComponentRenderer<Component, UsersEntity> createUserUpdateComponentRenderer() {
        return new ComponentRenderer<>(users -> {
            FormLayout formLayout = new FormLayout();
            TextField usernameTextField = new TextField("Username");
            PasswordField passwordTextField = new PasswordField("Password");
            PasswordField confirmPasswordField = new PasswordField("Confirm Password");
            ComboBox<String> roleSelector = new ComboBox<>("Select Role");
            Button updateButton = new Button("Update User");
            RadioButtonGroup<String> statusRadioGroup = new RadioButtonGroup<>("Set Status", "active", "inactive");
            H6 headerText = new H6("Update User Details");

            SpecialMethods.setUserRoles(roleSelector);
            usernameTextField.setValue(users.getUsername());
            roleSelector.setValue(users.getRoleName());
//            statusRadioGroup.setValue(users.getStatusValue().getText());

            usernameTextField.setRequired(true);
            passwordTextField.setRequired(true);
            confirmPasswordField.setRequired(true);
            roleSelector.setRequired(true);

            SpecialMethods.setUserRoles(roleSelector);

            statusRadioGroup.setValue(users.getStatusId() == 1 ? "active" : "invacive");

            //<theme-editor-local-classname>
            statusRadioGroup.addClassName("manage-users-view-radio-group-1");
            formLayout.addClassName("update-user-formlayout");
            updateButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_PRIMARY);

            updateButton.addClickListener(e -> {
                UsersEntity USERS_ENTITY = new UsersEntity();
                USERS_ENTITY.setId(users.getId());
                USERS_ENTITY.setUsername(usernameTextField.getValue());
                String pwdValue = passwordTextField.isEmpty() ? users.getPassword() : Encryption.generateCipherText(passwordTextField.getValue());
                USERS_ENTITY.setPassword(pwdValue);
                byte roleId = (byte) (roleSelector.getValue().equals("Admin") ? 1 : 2);
                byte statusId = (byte) (statusRadioGroup.getValue().equals("active") ? 1 : 0);
                USERS_ENTITY.setStatus(statusId);
                USERS_ENTITY.setRoleId(roleId);

                new UserConfirmDialogs("UPDATE USER", "Please confirm to update user's data else cancel to abort process")
                        .saveDialog().addConfirmListener(ex -> {
                            int responseStatus = SERVICE_OBJ.updateUser(USERS_ENTITY);
                            if (responseStatus > 0) {
                                Notification.show("User data successfully updated", 3000, Notification.Position.TOP_END);
                            }
                        });
            });

            formLayout.add(headerText, usernameTextField, passwordTextField, confirmPasswordField, roleSelector, statusRadioGroup, updateButton);

            formLayout.setResponsiveSteps(
                    new FormLayout.ResponsiveStep("0", 1),
                    new FormLayout.ResponsiveStep("600px", 2)
            );
            formLayout.setColspan(headerText, 2);
            formLayout.setColspan(usernameTextField, 2);
            return formLayout;
        });

    }


    /***********************************************************************************
     * POP UP DIALOG FOR ADDING NEW USERS 
     ***********************************************************************************/
    private void constructDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Add New User");
        TextField usernameField = new TextField("Username");
        PasswordField userPasswordField = new PasswordField("Password");
        PasswordField confirmPasswordField = new PasswordField("Confirm Password");
        ComboBox<String> userRolePicker = new ComboBox<>("Select Role");
        Button saveButton = new Button("Save User");
        Autocomplete autocomplete = Autocomplete.ADDRESS_LEVEL4;
        usernameField.setAutocomplete(autocomplete);

        FormLayout layout = new FormLayout();

        //load userRoleSelector with data
        SpecialMethods.setUserRoles(userRolePicker);

        dialog.addClassName("user-view-add-dialog");
        layout.addClassName("user-view-dialog-layout");
        usernameField.setPlaceholder("Username");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
        saveButton.setWidthFull();

        //set required fields.
        usernameField.setRequired(true);
        userPasswordField.setRequired(true);
        confirmPasswordField.setRequired(true);
        userRolePicker.setRequired(true);
        usernameField.setInvalid(isVisible());
        userPasswordField.setInvalid(isVisible());
        confirmPasswordField.setInvalid(isVisible());
        userRolePicker.setInvalid(isAttached());
        indexNumberField.setVisible(false);

        indexNumberField.setValueChangeMode(ValueChangeMode.LAZY);

        //check from users table if index number already exists.
        indexNumberField.addValueChangeListener(event -> {

        });

        userRolePicker.addValueChangeListener(e -> {
            indexNumberField.setVisible(e.getValue().equals("Class Rep"));
        });

        //ADD COMPONENTS TO LAYOUT
        layout.add(usernameField, userPasswordField, confirmPasswordField, userRolePicker, indexNumberField, new Hr(), saveButton);
        dialog.add(layout);

        //ADD CLICK LISTENER TO BUTTON TO CHECK AND SAVE USER
        saveButton.addClickListener(click -> {

            if (userRolePicker.getValue().equals("Class Rep")) {

            }
            UserConfirmDialogs popUp = new UserConfirmDialogs();
            UserService SERVICE_OBJ = new UserService();
            UsersEntity entity = new UsersEntity();
            boolean passwordMatches = userPasswordField.getValue().equals(confirmPasswordField.getValue());
            boolean invalidFields = userPasswordField.isEmpty() || userRolePicker.isEmpty() || confirmPasswordField.isEmpty();

            if (!passwordMatches) {
                popUp.showError("Password fields do not match");
            } else if (invalidFields) {
                popUp.showError("Fill out all required fields");
            } else {
                new UserConfirmDialogs("SAVE USER", "Do you wish add current record to your list of users?").
                        saveDialog().addConfirmListener(confirm -> {
                            UI.getCurrent().access(() -> {

                                String cipherText = Encryption.generateCipherText(userPasswordField.getValue());
                                entity.setUsername(usernameField.getValue());
                                entity.setPassword(cipherText);
                                entity.setIndexNumber(indexNumberField.getValue());

                                var roleId = new AtomicReference<Byte>();
                                var userRole = userRolePicker.getValue();
                                if (Objects.equals(userRole, "Admin")) {
                                    roleId.set((byte) 1);
                                } else if (Objects.equals(userRole, "Teaching Assistant")) {
                                    roleId.set((byte) 2);
                                } else roleId.set((byte) 3);

                                entity.setRoleId(roleId.get());
//                            entity.setRoleId((byte) (userRolePicker.getValue().equals("Admin") ? 1 : 2));
                                int responseStatus = SERVICE_OBJ.saveUser(entity);

                                if (responseStatus > 0) {
                                    popUp.showSuccess("Nice, new user successfully added to list");
                                    userPasswordField.clear();
                                    usernameField.clear();
                                    confirmPasswordField.clear();
                                    LoadTableGrid.loadTable(usersTable, SERVICE_OBJ.getAllUsers());
                                }
                            });
                        });
            }
        });
        dialog.open();
    }


}//end of class..
