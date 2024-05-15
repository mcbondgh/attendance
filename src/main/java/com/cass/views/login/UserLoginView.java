package com.cass.views.login;

import com.cass.dialogs.UserConfirmDialogs;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.UI;

import java.util.Map;

import org.vaadin.lineawesome.LineAwesomeIcon;

import com.cass.security.Encryption;
import com.cass.security.SessionManager;
import com.cass.services.DAO;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Section;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

import jakarta.annotation.security.PermitAll;

@PageTitle("login")
@Route(value = "login")
@RouteAlias(value = "")
// @PermitAll
public class UserLoginView extends VerticalLayout{
    DAO DAO_OBJECT = new DAO();

    public UserLoginView() {
        add(renderPage());
        addClassName("login-main-page-layout");
    }

    private Component renderPage() {
        VerticalLayout layout = new VerticalLayout();
        TextField usernameField = new TextField("Username");
        PasswordField passwordField = new PasswordField("Password");
        H3 title = new H3("ATTENDANCE SHEET");
        Paragraph paragraph = new Paragraph("Login to the attendance sheet");
        Button loginBtn = new Button("Login");
        Span errorText = new Span(" Invalid Username or password");
        Span footerText = new Span("Powered by Mc's Republic");
        footerText.addClassName("login-footer-text");
        
        Div div1 = new Div(title, paragraph);
        Div div2 = new Div(usernameField, passwordField, new Hr(), loginBtn, footerText);
        Div errorDiv = new Div(LineAwesomeIcon.EXCLAMATION_CIRCLE_SOLID.create(), errorText);
        Div mainDiv = new Div(div1, errorDiv, div2);

        div1.addClassNames("login-div", "login-header-div");
        div2.addClassNames("login-div", "login-input-div");
        layout.setClassName("login-layout");
        mainDiv.setClassName("login-main-div");
        errorDiv.setClassName("login-error-container");
        errorText.setClassName("login-errorText");
        usernameField.setClassName("login-input-fields");
        passwordField.setClassName("login-input-fields");
        loginBtn.addThemeVariants(ButtonVariant.LUMO_SMALL, 
                                ButtonVariant.MATERIAL_CONTAINED, 
                                ButtonVariant.LUMO_PRIMARY);

        usernameField.setRequired(true);
        passwordField.setRequired(true);
        usernameField.setInvalid(usernameField.isEmpty());
        passwordField.setInvalid(passwordField.isEmpty());
        errorDiv.setVisible(isAttached());

        // usernameField.setErrorMessage("required field");
        // passwordField.setErrorMessage("required field");

        layout.setSizeFull();
       
        layout.add(mainDiv);

        loginBtn.addClickShortcut(Key.ENTER);
        loginBtn.addClickListener(click -> {
           boolean usernameAndPassEmpty = usernameField.isEmpty() || passwordField.isEmpty();
           if (usernameAndPassEmpty) {
               usernameField.setErrorMessage("enter usernme");
               passwordField.setErrorMessage("enter password");
           } else {
                if(!authenticateUser(usernameField.getValue(), passwordField.getValue())) {
                    errorDiv.setVisible(true);
                } else {
                    SessionManager.setUsername(usernameField.getValue());
                    UI.getCurrent().getPage().setLocation("dashboard");
                }
           }
        });

        return layout;
    }


    /*********************************************************************
     * AUTHENTICATE USER TO GRANT OR DINY ACCESS
     *********************************************************************/
    private boolean authenticateUser(String username, String password) {
        boolean resultStatus = false;
        try {
            Map<String, String> userData = DAO_OBJECT.getUsersByUsername(username);

            if(userData.isEmpty()) {
                return resultStatus;
            }

            if(!userData.isEmpty()) {
                String name = userData.get("username");
                String passString = userData.get("password");
                String roleId = userData.get("roleId");

                //DECIPHER USERS PASSWORD 
                boolean passwordMatch = Encryption.getOriginalText(passString).equals(password);
            
                if(passwordMatch) {
                    resultStatus = true;
                }
            }
        }catch(NullPointerException ignore){
            new UserConfirmDialogs().showError("Database Connection Failed, please check connection..");
        }

        return resultStatus;

    }
    
}//end of class...
