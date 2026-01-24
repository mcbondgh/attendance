package com.cass.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

import com.cass.data.AttendanceEntity;
import com.cass.data.StudentEntity;
import com.cass.security.Config;


public class StudentService extends  DAO{

    public int saveNewStudent(StudentEntity entity) {
        int status = 0;
        try(Connection source = Config.getDataSource()) {
            PreparedStatement prepare;
            String query = """
                    INSERT INTO studentslist(indexNumber, fullName, class, year_group, level, section, programme, programme_type)
                    VALUES(?, ?, ?, ?, ?, ?, ?, ?);
                    """;
            prepare = source.prepareStatement(query);
            prepare.setString(1, entity.getIndexNumber());
            prepare.setString(2, entity.getFullName());
            prepare.setString(3, entity.getStudentClass());
            prepare.setString(4, entity.getYearGroup());
            prepare.setString(5, entity.getLevel());
            prepare.setString(6, entity.getSection());
            prepare.setString(7, entity.getProgramme());
            prepare.setString(8, entity.getProgrammeType());
            status = prepare.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return status;
    }

    public int updateStudentData(StudentEntity entity) {
        int status = 0;
        try(Connection source = Config.getDataSource()) {
            PreparedStatement prepare;
            String query = """
                    UPDATE studentslist SET indexNumber = ?, fullName = ?, programme = ?, `class` = ?,
                    `level` = ?, section = ?, year_group = ?, `status` = ?, dateUpdated = ?, programme_type = ?
                    WHERE(id = ?);
                    """;
            prepare = source.prepareStatement(query);
            prepare.setString(1, entity.getIndexNumber());
            prepare.setString(2, entity.getFullName());
            prepare.setString(3, entity.getProgramme());
            prepare.setString(4, entity.getStudentClass());
            prepare.setString(5, entity.getLevel());
            prepare.setString(6, entity.getSection());
            prepare.setString(7, entity.getYearGroup());
            prepare.setByte(8, entity.getStatus());
            prepare.setTimestamp(9, entity.getDateUpdated());
            prepare.setString(10, entity.getProgrammeType());
            prepare.setInt(11, entity.getId());
            status = prepare.executeUpdate();
        } catch (SQLException ignored) {

        }
        return status;
    }

    public int saveAttendance(AttendanceEntity entity) {
        int status = 0;
        try(Connection source = Config.getDataSource()) {
            PreparedStatement prepare;
            ResultSet resultSet;
            String query = """
                    INSERT INTO attendance_records(rowNumber, indexNumber, programme, course, level,
                    className, year_group, attendanceValue, attendanceDate)
                    VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?);
                   """;
            prepare = source.prepareStatement(query);
            prepare.setInt(1, entity.getRowNumber());
            prepare.setString(2, entity.getIndexNumber());
            prepare.setString(3, entity.getProgramme());
            prepare.setString(4, entity.getCourse());
            prepare.setString(5, entity.getLevel());
            prepare.setString(6, entity.getClassName());
            prepare.setString(7, entity.getYearGroup());
            prepare.setString(8, entity.getAttendanceValue());
            prepare.setDate(9, entity.getAttendanceDate());
            status = prepare.executeUpdate();
        } catch (SQLException ignore) {
            ignore.printStackTrace();
        }

        return status;
    }

    public int removeStudent(int studentRowNumber) {
        AtomicInteger responseStatus = new AtomicInteger();
        try(Connection source = Config.getDataSource()) {
            PreparedStatement prepare;
            ResultSet resultSet;
            String query = "DELETE FROM studentslist WHERE id = '" + studentRowNumber + "'";
            prepare = source.prepareStatement(query);
            responseStatus.getAndSet(prepare.executeUpdate());
        } catch (SQLException ignore) {
        }
        ;
        return responseStatus.get();
    }


}//end of class...
