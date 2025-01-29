package com.cass.services;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

import com.cass.data.AttendanceEntity;
import com.cass.data.StudentEntity;


public class StudentService extends DAO {

    public int saveNewStudent(StudentEntity entity) {
        int status = 0;
        try {
            String query = """
                    INSERT INTO studentslist(indexNumber, fullName, class, year_group, level, section, programme)
                    VALUES(?, ?, ?, ?, ?, ?, ?);
                    """;
            prepare = getCon().prepareStatement(query);
            prepare.setString(1, entity.getIndexNumber());
            prepare.setString(2, entity.getFullName());
            prepare.setString(3, entity.getStudentClass());
            prepare.setString(4, entity.getYearGroup());
            prepare.setString(5, entity.getLevel());
            prepare.setString(6, entity.getSection());
            prepare.setString(7, entity.getProgramme());
            status = prepare.executeUpdate();
            getCon().close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return status;
    }

    public int updateStudentData(StudentEntity entity) {
        int status = 0;
        try {
            String query = """
                    UPDATE studentslist SET indexNumber = ?, fullName = ?, programme = ?, `class` = ?,
                    `level` = ?, section = ?, year_group = ?, `status` = ?, dateUpdated = ?
                    WHERE(id = ?);
                    """;
            prepare = getCon().prepareStatement(query);
            prepare.setString(1, entity.getIndexNumber());
            prepare.setString(2, entity.getFullName());
            prepare.setString(3, entity.getProgramme());
            prepare.setString(4, entity.getStudentClass());
            prepare.setString(5, entity.getLevel());
            prepare.setString(6, entity.getSection());
            prepare.setString(7, entity.getYearGroup());
            prepare.setByte(8, entity.getStatus());
            prepare.setTimestamp(9, entity.getDateUpdated());
            prepare.setInt(10, entity.getId());
            status = prepare.executeUpdate();
            getCon().close();
        } catch (SQLException ignore) {
        }
        return status;
    }

    public int saveAttendance(AttendanceEntity entity) {
        int status = 0;
        try {
            String query = """
                    INSERT INTO attendance_records(rowNumber, indexNumber, className, programeName, attendanceValue, attendanceDate, year_group)
                    VALUES(?, ?, ?, ?, ?, ?, ?);
                    """;
            prepare = getCon().prepareStatement(query);
            prepare.setInt(1, entity.getRowNumber());
            prepare.setString(2, entity.getIndexNumber());
            prepare.setString(3, entity.getclassName());
            prepare.setString(4, entity.getprogrameName());
            prepare.setString(5, entity.getAttendanceValue());
            prepare.setDate(6, entity.getAttendanceDate());
            prepare.setString(7, entity.getYearGroup());
            status = prepare.executeUpdate();
            getCon().close();
        } catch (SQLException ignore) {
            ignore.printStackTrace();
        }

        return status;
    }

    public int removeStudent(int studentRowNumber) {
        AtomicInteger responseStatus = new AtomicInteger();
        try {
            String query = "DELETE FROM studentslist WHERE id = '" + studentRowNumber + "'";
            prepare = getCon().prepareStatement(query);
            responseStatus.getAndSet(prepare.executeUpdate());
            getCon().close();
        } catch (SQLException ignore) {
        }
        ;
        return responseStatus.get();
    }


}//end of class...
