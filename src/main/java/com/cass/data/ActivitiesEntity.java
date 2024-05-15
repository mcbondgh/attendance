package com.cass.data;

import java.sql.Date;
import java.sql.Timestamp;

import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.dom.Style.AlignItems;
import com.vaadin.flow.dom.Style.TextAlign;

public class ActivitiesEntity {
    private int id;
    private String indexNumber, name, className, activityType;
    private String activityTitle;
    private Date activityDate;
    private double maximumScore, score;
    private String fullname;
    private int rowNumber;
    private String programe;
    private Timestamp dateCreated;
    private NumberField scoreField = new NumberField();

    public ActivitiesEntity() {
    
    }


    public ActivitiesEntity(int id, String fullname, String indexNumber, String name) {
        this.id = id;
        this.fullname = fullname;
        this.indexNumber = indexNumber;
        this.name = name;
        indicators();
    }

    

    public ActivitiesEntity(String activityType, String activityTitle, Date activityDate, double maximumScore, double score, String programe) {
        this.activityType = activityType;
        this.activityTitle = activityTitle;
        this.activityDate = activityDate;
        this.maximumScore = maximumScore;
        this.score = score;
        this.programe = programe;
    }

    public ActivitiesEntity(int id, String fullname, String indexNumber,String className, String title, String activityType, Date activityDate, double maximumScore,
            double score, int rowNo, String programe) {
        this.id = id;
        this.className = className;
        this.fullname = fullname;
        this.indexNumber = indexNumber;
        this.activityType = activityType;
        this.activityTitle = title;
        this.activityDate = activityDate;
        this.maximumScore = maximumScore;
        this.score = score;
        this.rowNumber = rowNo;
        this.programe = programe;
    }


    private void indicators() {
        scoreField.setRequired(true);
        scoreField.setValue(0.0);
        scoreField.setClassName("score-field");
        scoreField.setAllowedCharPattern("[0-9.]");
        scoreField.getStyle().setAlignItems(AlignItems.CENTER);

    }

    public void setMaxSocreValue(double scoreValue) {
        scoreField.setMax(scoreValue);
    }


    public Date getActivityDate() {
        return activityDate;
    }


    public void setActivityDate(Date activityDate) {
        this.activityDate = activityDate;
    }


    public double getmaximumScore() {
        return maximumScore;
    }


    public void setMaximumScore(double maximumScore) {
        this.maximumScore = maximumScore;
    }


    public int getRowNumber() {
        return rowNumber;
    }


    public void setRowNumber(int rowNumber) {
        this.rowNumber = rowNumber;
    }


    public String getFullname() {
        return fullname;
    }


    public String getActivityTitle() {
        return activityTitle;
    }


    public void setActivityTitle(String activityTitle) {
        this.activityTitle = activityTitle;
    }


    public void setFullname(String fullname) {
        this.fullname = fullname;
    }


    public String getPrograme() {
        return programe;
    }


    public void setPrograme(String programe) {
        this.programe = programe;
    }


    public Timestamp getDateCreated() {
        return dateCreated;
    }


    public void setDateCreated(Timestamp dateCreated) {
        this.dateCreated = dateCreated;
    }


    public String getIndexNumber() {
        return indexNumber;
    }

    public void setIndexNumber(String indexNumber) {
        this.indexNumber = indexNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public Date getactivityDate() {
        return activityDate;
    }

    public void setactivityDate(Date activityDate) {
        this.activityDate = activityDate;
    }

    public double getMaximumSocre() {
        return maximumScore;
    }

      public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public NumberField getScoreField() {
        return scoreField;
    }

    public void setScoreField(NumberField scoreField) {
        this.scoreField = scoreField;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    
}
