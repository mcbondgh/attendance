package com.cass.services;

import com.cass.data.CourseRecord;

public class CoursesModel extends DAO {

    public int createCourse(CourseRecord.addCourseRecord data) {
        try {
            String query = """
                    INSERT INTO courses(course_name, course_code, programme, `level`)
                    VALUES(?, ?, ?, ?)
                    """;
            prepare = getCon().prepareStatement(query);
            prepare.setString(1, data.name());
            prepare.setString(2, data.code());
            prepare.setString(3, data.programme());
            prepare.setString(4, data.level());
            return prepare.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int updateCourse(CourseRecord data) {
        try {
            String query = """
                    
                    """;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


}//end of class...
