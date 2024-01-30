package com.cass.services;

import java.sql.SQLException;

import com.cass.data.AttendanceEntity;
import com.cass.data.StudentEntity;


public class StudentService extends DAO{

    public int saveNewStudent(StudentEntity entity) {
        int status = 0;
            try {
                String query = """
                        INSERT INTO studentslist(indexNumber, fullName, class)
                        VALUES(?, ?, ?);
                        """;
                prepare = getCon().prepareStatement(query);
                prepare.setString(1, entity.getIndexNumber());
                prepare.setString(2, entity.getFullName());
                prepare.setString(3, entity.getStudentClass());
                // prepare.setString(4, entity.getStudentClass());
                // prepare.setString(5, entity.getDepartment());
                status = prepare.executeUpdate();
                getCon().close();
            }catch(SQLException ignore){}
        return status;
    }

    public int updateStudentData(StudentEntity entity) {
        int status = 0;
            try {
                String query = """
                    UPDATE studentsList 
                    SET indexNumber = ?, fullName = ?, class = ?, status = ?, dateUpdated = ?
                    WHERE (id = ?);
                        """;
                prepare = getCon().prepareStatement(query);
                prepare.setString(1, entity.getIndexNumber());
                prepare.setString(2, entity.getFullName());
                // prepare.setString(3, entity.getPrograme());
                prepare.setString(3, entity.getStudentClass());
                prepare.setByte(4, entity.getStatus());
                prepare.setTimestamp(5, entity.getDateUpdated());
                prepare.setInt(6, entity.getId());
                status = prepare.executeUpdate();
                getCon().close();
            }catch(SQLException ignore){}
        return status;
    }

    public int saveAttendance(AttendanceEntity entity) {
        int status = 0;
        try{
            String query = """
                INSERT INTO attendance_records(rowNumber, indexNumber, className, programeName, attendanceValue, attendanceDate)
                VALUES(?, ?, ?, ?, ?, ?);
                """;
                prepare = getCon().prepareStatement(query);
                prepare.setInt(1, entity.getRowNumber());
                prepare.setString(2, entity.getIndexNumber());
                prepare.setString(3, entity.getclassName());
                prepare.setString(4, entity.getprogrameName());
                prepare.setString(5, entity.getAttendanceValue());
                prepare.setDate(6, entity.getAttendanceDate());
                status = prepare.executeUpdate();
                getCon().close();
        }catch(SQLException ignore){ignore.printStackTrace();}

        return status;
    }
    
    
}//end of class...
