package com.cass.views.manageusers;

import com.cass.data.LoadTableGrid;
import com.cass.data.UsersEntity;
import com.cass.dialogs.UserConfirmDialogs;
import com.cass.security.Encryption;
import com.cass.services.UserService;
import com.cass.special_methods.SpecialMethods;
import com.cass.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.OrderedList;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.Autocomplete;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.dom.Style.FlexWrap;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility.AlignItems;
import com.vaadin.flow.theme.lumo.LumoUtility.Display;
import com.vaadin.flow.theme.lumo.LumoUtility.FontSize;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import com.vaadin.flow.theme.lumo.LumoUtility.JustifyContent;
import com.vaadin.flow.theme.lumo.LumoUtility.ListStyleType;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.MaxWidth;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import com.vaadin.flow.theme.lumo.LumoUtility.TextColor;

import jakarta.annotation.security.RolesAllowed;

@PageTitle("Manage Users")
@Route(value = "manage-users", layout = MainLayout.class)
// @RolesAllowed({"ADMIN","USERS"})
@AnonymousAllowed
public class ManageUsersView extends VerticalLayout implements HasComponents, HasStyle {

    private OrderedList imageContainer;
    private Grid<UsersEntity> usersTable = new Grid<UsersEntity>();
    UserService SERVICE_OBJ = new UserService();

    public ManageUsersView() {
        add(renderPageHeader(), renderPageView());
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
     * CREATE A COMPONENT RENDERER TO ALLOW USER UPATE
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
            statusRadioGroup.setValue(users.getStatusValue().getText());

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
                USERS_ENTITY.setUsername(users.getUsername());
                String pwdValue = passwordTextField.isEmpty() ? users.getPassword() : Encryption.generateCipherText(passwordTextField.getValue());
                USERS_ENTITY.setPassword(pwdValue);
                byte roleId = (byte) (roleSelector.getValue().equals("Admin") ? 1 : 2);
                byte statusId = (byte) (statusRadioGroup.getValue().equals("active") ? 1 : 0);
                USERS_ENTITY.setStatus(statusId);
                USERS_ENTITY.setRoleId(roleId);

                new UserConfirmDialogs("UPDATE USER",
                        "Please confirm to update user's data else cancel to abort process")
                        .saveDialog().addConfirmListener(ex -> {
                            int responseStatus = SERVICE_OBJ.updateUser(USERS_ENTITY);
                            if (responseStatus > 0) {
                                getUI().get().getPage().reload();
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

        //ADD COMPONENTS TO LAYOUT
        layout.add(usernameField, userPasswordField, confirmPasswordField, userRolePicker, new Hr(), saveButton);
        dialog.add(layout);

        //ADD CLICK LISTENER TO BUTTON TO CHECK AND SAVE USER
        saveButton.addClickListener(click -> {
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
                            String cipherText = Encryption.generateCipherText(userPasswordField.getValue());
                            entity.setUsername(usernameField.getValue());
                            entity.setPassword(cipherText);
                            entity.setRoleId((byte) (userRolePicker.getValue().equals("Admin") ? 1 : 2));
                            int responseStatus = SERVICE_OBJ.saveUser(entity);

                            if (responseStatus > 0) {
                                popUp.showSuccess("Nice, new user successfully added to list");
                                userPasswordField.clear();
                                usernameField.clear();
                                confirmPasswordField.clear();
                                LoadTableGrid.loadTable(usersTable, SERVICE_OBJ.getAllUsers());
                            }
                        });
            }
        });
        dialog.open();
    }


}//end of class..
