package com.cass.data;

public class StudentClassesEntity extends UsersEntity {

    private int id;
    private String className, dept;
    private byte creditHours;
    private boolean status;

    public StudentClassesEntity(int id, String className, byte creditHours, String dept, boolean status) {
        this.id = id;
        this.className = className;
        this.creditHours = creditHours;
        this.dept = dept;
        this.status = status;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public byte getCreditHours() {
        return creditHours;
    }

    public void setCreditHours(byte creditHours) {
        this.creditHours = creditHours;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
