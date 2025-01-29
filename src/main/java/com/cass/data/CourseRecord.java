package com.cass.data;

public record CourseRecord(int id, String name, String code, String level, String programme) {
    public record addCourseRecord(String name, String code, String level, String programme){}
}
