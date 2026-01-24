package com.cass.services;

import com.cass.data.CourseRecord;
import com.cass.security.Config;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CoursesModel {

    public int createCourse(CourseRecord.addCourseRecord data) {
        try(Connection source = Config.getDataSource()) {
            PreparedStatement prepare;
            ResultSet resultSet;
            String query = """
                    INSERT INTO courses(course_name, course_code, programme, `level`)
                    VALUES(?, ?, ?, ?)
                    """;
            prepare = source.prepareStatement(query);
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
