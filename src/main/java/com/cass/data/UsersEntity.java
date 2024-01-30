package com.cass.data;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;

public class UsersEntity {
    private int id;
    private String username, password;
    private byte roleId;
    private byte statusId;
    private Span statusValue = new Span();
    private String roleName;
    private Button editBtn = new Button();

    public UsersEntity(int id, String username, String password, byte roleId, byte status) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.roleId = roleId;
        this.statusId = status;
        setParameters();
    }

    private void setParameters(){
        roleName = roleId == 1 ? "Admin" : "User";
       
        if (statusId == 1) {
            statusValue.setText( "Active");
            statusValue.addClassNames("status-label", "active");
        } else {
            statusValue.setText( "Active");
            statusValue.addClassNames("status-label", "inactive");
        }
    }

    public UsersEntity() {
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public byte getRoleId() {
        return roleId;
    }
    public void setRoleId(byte roleId) {
        this.roleId = roleId;
    }
    public byte getStatusId() {
        return statusId;
    }
    public void setStatus(byte status) {
        this.statusId = status;
    }
    public Span getStatusValue() {
        return statusValue;
    }
    public void setStatusValue(Span statusValue) {
        this.statusValue = statusValue;
    }

    public Button getEditBtn() {
        return editBtn;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public void setEditBtn(Button editBtn) {
        this.editBtn = editBtn;
    }

    
    

}
