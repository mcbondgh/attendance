package com.cass.services;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.cass.data.ActivitiesEntity;
import com.cass.data.AttendanceEntity;
import com.cass.data.AttendanceRecordsEntity;
import com.cass.data.StudentEntity;
import com.cass.data.UsersEntity;
import com.cass.security.Config;

public class DAO extends Config {
    public Collection<StudentEntity> getStudentByClass(String searchItem) {
        Collection<StudentEntity> data = new ArrayList<>();
        try {
            String query = "SELECT * FROM studentsList WHERE(class = ?)";
            prepare = getCon().prepareStatement(query);
            prepare.setString(1, searchItem);
            resultSet = prepare.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String indexNo = resultSet.getString("indexNumber");
                String fullName = resultSet.getString("fullName");
                String program = resultSet.getString("program");
                String stuClass = resultSet.getString("class");
                String department = resultSet.getString("department");
                byte status = resultSet.getByte("status");
                Timestamp dateAdded = resultSet.getTimestamp("dateAdded");
                Timestamp dateUpdated = resultSet.getTimestamp("dateUpdated");
                data.add(new StudentEntity(id, indexNo, fullName, program, stuClass, department, status, dateAdded, dateUpdated));
            }
            getCon().close();
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
        }
        return data;
    }

    public Collection<ActivitiesEntity> fetchClassListByClassName(String searchItem) {
        Collection<ActivitiesEntity> data = new ArrayList<>();
        try {
            String query = "SELECT * FROM studentsList WHERE(class = ?)";
            prepare = getCon().prepareStatement(query);
            prepare.setString(1, searchItem);
            resultSet = prepare.executeQuery();
            AtomicInteger counter = new AtomicInteger();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String indexNo = resultSet.getString("indexNumber");
                String fullName = resultSet.getString("fullName");
                String program = resultSet.getString("program");
                String stuClass = resultSet.getString("class");
                String department = resultSet.getString("department");
                byte status = resultSet.getByte("status");
                Timestamp dateAdded = resultSet.getTimestamp("dateAdded");
                Timestamp dateUpdated = resultSet.getTimestamp("dateUpdated");
                data.add(new ActivitiesEntity(id, fullName, indexNo, fullName));
            }
            getCon().close();
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
        }
        return data;
    }

    public Collection<StudentEntity> fetchAllStudents() {
        Collection<StudentEntity> data = new ArrayList<>();
        try {
            String query = "SELECT * FROM studentsList WHERE(status = 1)";
            prepare = getCon().prepareStatement(query);
            resultSet = prepare.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String indexNo = resultSet.getString("indexNumber");
                String fullName = resultSet.getString("fullName");
                String program = resultSet.getString("program");
                String stuClass = resultSet.getString("class");
                String department = resultSet.getString("department");
                byte status = resultSet.getByte("status");
                Timestamp dateAdded = resultSet.getTimestamp("dateAdded");
                Timestamp dateUpdated = resultSet.getTimestamp("dateUpdated");
                data.add(new StudentEntity(id, indexNo, fullName, program, stuClass, department, status, dateAdded, dateUpdated));
            }
            getCon().close();
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
        }
        return data;
    }

    public Collection<StudentEntity> getStudentByStudentIdex(String indexNumer) {
        Collection<StudentEntity> data = new ArrayList<>();
        try {
            String query = "SELECT * FROM studentsList WHERE(indexNumber = ?)";
            prepare = getCon().prepareStatement(query);
            prepare.setString(1, indexNumer);
            resultSet = prepare.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String indexNo = resultSet.getString("indexNumber");
                String fullName = resultSet.getString("fullName");
                String program = resultSet.getString("program");
                String stuClass = resultSet.getString("class");
                String department = resultSet.getString("department");
                byte status = resultSet.getByte("status");
                Timestamp dateAdded = resultSet.getTimestamp("dateAdded");
                Timestamp dateUpdated = resultSet.getTimestamp("dateUpdated");
                data.add(new StudentEntity(id, indexNo, fullName, program, stuClass, department, status, dateAdded, dateUpdated));
            }
            getCon().close();
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
        }
        return data;
    }

    public Collection<StudentEntity> fetchActiveStudentsForAttendanceTable(String studentClass){
        Collection<StudentEntity> data = new ArrayList<>();
        try{
            String query = "SELECT * FROM studentslist WHERE(status = 1 AND class = ?);";
            prepare = getCon().prepareStatement(query);
            prepare.setString(1, studentClass);
            resultSet = prepare.executeQuery();
            while(resultSet.next()) {
                int id = resultSet.getInt("id");
                String indexNo = resultSet.getString("indexNumber");
                String fullName = resultSet.getString("fullName");
                String stuClass = resultSet.getString("class");
                data.add(new StudentEntity(id, indexNo, fullName, stuClass));
            }
            getCon().close();
        }catch(SQLException ignore){}
        return data;
    }

    public boolean checkAttendanceByDate(Date dateToCheck, String programeName, String className) {
        boolean result = false;
        try {
            String query = "SELECT COUNT(id) AS result FROM attendance_records WHERE (attendanceDate = ? AND programeName = ? AND className = ?);";
            prepare = getCon().prepareStatement(query);
            prepare.setDate(1, dateToCheck);
            prepare.setString(2, programeName);
            prepare.setString(3, className);
            resultSet = prepare.executeQuery();
            if(resultSet.next()) {
                int counter = resultSet.getInt(1);
                result = counter > 0 ? true :false;
            }
            getCon().close();
        }catch(SQLException ignore){ ignore.printStackTrace();}
        return result;
    }

    public Collection<AttendanceRecordsEntity>fetchAttendanceRecords(Date startDate, Date endDate, String className, String programe) {
        Collection<AttendanceRecordsEntity> data = new ArrayList<>();
        try {
            String query = " SELECT\r\n" + //
                    "                ar.indexNumber,\r\n" + //
                    "                fullName,\r\n" + //
                    "                SUM(CASE WHEN attendanceValue = 'p' THEN 1 ELSE 0 END) AS present_count,\r\n" + //
                    "                SUM(CASE WHEN attendanceValue = 'a' THEN 1 ELSE 0 END) AS absent_count,\r\n" + //
                    "                SUM(CASE WHEN attendanceValue = 'excused' THEN 1 ELSE 0 END) AS excused_count,\r\n" + //
                    "                COUNT(*) AS total_attendance\r\n" + //
                    "            FROM\r\n" + //
                    "                attendance_records AS ar\r\n" + //
                    "            INNER JOIN studentslist AS sl\r\n" + //
                    "                ON ar.rowNumber = sl.id\r\n" + //
                    "            WHERE\r\n" + //
                    "                attendanceDate BETWEEN ? AND ? AND programeName = ? AND className = ?\r\n" + //
                    "            GROUP BY\r\n" + //
                    "                ar.indexNumber, fullName";
            prepare = getCon().prepareStatement(query);
            prepare.setDate(1, startDate);
            prepare.setDate(2, endDate);
            prepare.setString(3, programe);
            prepare.setString(4, className);
            
            resultSet = prepare.executeQuery();
            AtomicInteger index  = new AtomicInteger();
            while(resultSet.next()) {
                String indexNumber = resultSet.getString("indexNumber");
                String fullName = resultSet.getString("fullName");
                int presentCount = resultSet.getInt("present_count");
                int absentCount = resultSet.getInt("absent_count");
                int excusedCount = resultSet.getInt("excused_count");
                int totalAttendance = resultSet.getInt("total_attendance");
                data.add(new AttendanceRecordsEntity(index.incrementAndGet(), indexNumber, fullName, presentCount, absentCount, totalAttendance, excusedCount));
            }
            getCon().close();
        }catch(SQLException ignore) {}
        return data;
    }

    public Map<String, Object> getStudentCounter() {
       Map<String, Object> data = new HashMap<>();
        try {
            String query1 = """                        
                SELECT COUNT(id) AS total,
                (SELECT COUNT(id) FROM studentslist where class = 'Computer Sci 2') AS 'comp2_students',
                (SELECT COUNT(*) FROM studentslist WHERE class = 'Computer Sci 3')  AS 'comp3_students',
                (SELECT COUNT(*) FROM studentslist WHERE class = 'Network 2A') AS 'network2a_students',
                (SELECT COUNT(*) FROM studentslist WHERE class = 'Network 3A') AS 'network3a_students',
                (SELECT COUNT(*) FROM studentslist WHERE class = 'Network 2B') AS 'network2c_students',
                (SELECT COUNT(*) FROM studentslist WHERE class = 'Network 3B') AS 'network3c_students'
            FROM studentslist;
                    """;
            prepare = getCon().prepareStatement(query1);
            resultSet = prepare.executeQuery();
            if(resultSet.next()) {
                data.put("totalStudents", resultSet.getString("total"));
                data.put("comp2_students", resultSet.getString("comp2_students"));
                data.put("network2a_students", resultSet.getString("network2a_students"));
                data.put("network3a_students", resultSet.getString("network3a_students"));
                data.put("network2c_students", resultSet.getString("network2c_students"));
                data.put("network3c_students", resultSet.getString("network3c_students"));
                data.put("comp3_students", resultSet.getString("comp3_students"));
            }
            getCon().close();
        } catch (Exception e) {
            // TODO: handle exception
        }

        return data;
    }
    public Collection<ActivitiesEntity> fetchStudentActivities() {
        Collection<ActivitiesEntity> data = new ArrayList<>();
        try {
            String query = "SELECT ar.id, rowNumber, indexNumber, fullname, activityType, programe, className, maximumScore, score, activityDate, dateCreated \n" + //
                    "\tFROM class_attendance.activity_records AS ar\n" + //
                    "\tINNER JOIN studentslist AS sl\n" + //
                    "\tON ar.rowNumber = sl.id;";
            prepare = getCon().prepareStatement(query);
            resultSet = prepare.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("ar.id");
                String fullname = resultSet.getString("fullname");
                String indexNumber = resultSet.getString("indexNumber");
                int rowNo = resultSet.getInt("rowNumber");
                String acivityType = resultSet.getString("activityType");
                String programe = resultSet.getString("programe");
                String studentClass = resultSet.getString("className");
                double maxScore = resultSet.getDouble("maximumScore");
                double score = resultSet.getDouble("score");
                Date activityDate = resultSet.getDate("activityDate");
                data.add(new ActivitiesEntity(id, fullname, indexNumber, studentClass, acivityType, activityDate, maxScore, score, rowNo, programe));
            }
            getCon().close();
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
        }
        return data;
    }

    public Map<String, String> getUsersByUsername(String username) {
        Map<String, String> data = new HashMap<>();
        try {
            String query = "SELECT username, password, role_id FROM users WHERE username = '"+username+"';";
            prepare = getCon().prepareStatement(query);
            resultSet = prepare.executeQuery();
            if(resultSet.next()) {
                data.put("username", resultSet.getString("username"));
                data.put("password", resultSet.getString("password"));
                data.put("roleId", String.valueOf(resultSet.getInt("role_id")));
            }
        }catch(SQLException ignore) {ignore.printStackTrace();}


        return data;
    }
    public Collection<UsersEntity> getAllUsers() {
        Collection<UsersEntity> data = new ArrayList<>();
        try {
            String query = "SELECT * FROM users;"; 
            prepare = getCon().prepareStatement(query);
            resultSet = prepare.executeQuery();
            while(resultSet.next()) {
                int id = resultSet.getInt("id");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                byte role = resultSet.getByte("role_id");
                byte statusId = resultSet.getByte("status");

                data.add(new UsersEntity(id, username, password, role, statusId));
                getCon().close();
            }
        }catch(SQLException ignore) {ignore.printStackTrace();}


        return data;
    }


       
}//end of class
