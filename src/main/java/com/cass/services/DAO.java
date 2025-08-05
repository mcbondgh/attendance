package com.cass.services;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import com.cass.data.*;
import com.cass.security.Config;
import org.apache.fop.pdf.ObjectStream;

public class DAO extends Config {
    public Collection<StudentEntity> getStudentByClass(String programme, String yearGroup, String level, String section) {
        Collection<StudentEntity> data = new ArrayList<>();
        try {
            String query = "SELECT * FROM studentsList WHERE(class = ? AND year_group = ? AND `level` = ? AND section = ?)";
            prepare = getCon().prepareStatement(query);
            prepare.setString(1, programme);
            prepare.setString(2, yearGroup);
            prepare.setString(3, level);
            prepare.setString(4, section);
            resultSet = prepare.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String indexNo = resultSet.getString("indexNumber");
                String fullName = resultSet.getString("fullName");
                String program = resultSet.getString("programme");
                String stuClass = resultSet.getString("class");
                String department = resultSet.getString("department");
                String stuSection = resultSet.getString("section");
                String year = resultSet.getString("year_group");
                String stuLevel = resultSet.getString("level");
                byte status = resultSet.getByte("status");
                Timestamp dateAdded = resultSet.getTimestamp("dateAdded");
                Timestamp dateUpdated = resultSet.getTimestamp("dateUpdated");
                data.add(new StudentEntity(id, indexNo, fullName, program, stuClass, department, status, year, stuSection, stuLevel, dateAdded, dateUpdated));
            }
            getCon().close();
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
        }
        return data;
    }

    public Collection<ActivitiesEntity> fetchClassListByClassName(String programme, String sectionOrClass, String level) {
        Collection<ActivitiesEntity> data = new ArrayList<>();
        try {
            String query = "SELECT * FROM studentsList WHERE(class = ? AND year_group = YEAR(CURRENT_DATE()) AND section = ? AND level = ?)";
            prepare = getCon().prepareStatement(query);
            prepare.setString(1, programme);
            prepare.setString(2, sectionOrClass);
            prepare.setString(3, level);
            resultSet = prepare.executeQuery();
            AtomicInteger counter = new AtomicInteger();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String indexNo = resultSet.getString("indexNumber");
                String fullName = resultSet.getString("fullName");
                String program = resultSet.getString("programme");
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
                String year = resultSet.getString("year_group");
                String section = resultSet.getString("section");
                String level = resultSet.getString("level");
                Timestamp dateAdded = resultSet.getTimestamp("dateAdded");
                Timestamp dateUpdated = resultSet.getTimestamp("dateUpdated");
                data.add(new StudentEntity(id, indexNo, fullName, program, stuClass, department, status, year, section, level, dateAdded, dateUpdated));
            }
            getCon().close();
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
        }
        return data;
    }

    public Collection<StudentEntity> getStudentByStudentIndex(String indexNumber) {
        Collection<StudentEntity> data = new ArrayList<>();
        try {
            String query = "SELECT * FROM studentsList WHERE(indexNumber = ?)";
            prepare = getCon().prepareStatement(query);
            prepare.setString(1, indexNumber);
            resultSet = prepare.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String indexNo = resultSet.getString("indexNumber");
                String fullName = resultSet.getString("fullName");
                String program = resultSet.getString("program");
                String stuClass = resultSet.getString("class");
                String department = resultSet.getString("department");
                byte status = resultSet.getByte("status");
                String year = resultSet.getString("year_group");
                String section = resultSet.getString("section");
                String level = resultSet.getString("level");
                Timestamp dateAdded = resultSet.getTimestamp("dateAdded");
                Timestamp dateUpdated = resultSet.getTimestamp("dateUpdated");
                data.add(new StudentEntity(id, indexNo, fullName, program, stuClass, department, status, year, section, level, dateAdded, dateUpdated));
            }
            getCon().close();
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
        }
        return data;
    }

    public Collection<StudentEntity> fetchActiveStudentsForAttendanceTable(String studentClass, String level, String programme, String section) {
        Collection<StudentEntity> data = new ArrayList<>();
        try {
            String query = "SELECT * FROM studentslist WHERE (status = 1 AND level = '" + level + "' AND programme = '" + programme + "')";
            String query1 = "SELECT * FROM studentslist WHERE(status = 1 AND class = '" + studentClass + "' AND level = '" + level + "' " +
                    "AND programme = '" + programme + "' AND section = '" + section + "');";
            String preferredQuery = Objects.equals(studentClass, "All Classes") ? query : query1;
            prepare = getCon().prepareStatement(preferredQuery);
            resultSet = prepare.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String indexNo = resultSet.getString("indexNumber");
                String fullName = resultSet.getString("fullName");
                String stuClass = resultSet.getString("class");
                data.add(new StudentEntity(id, indexNo, fullName, stuClass));
            }
            getCon().close();
        } catch (SQLException ignore) {
        }
        return data;
    }

    public boolean checkAttendanceByDate(Date dateToCheck, String course, String programme, String level, String className) {
        boolean result = false;
        try {
            String query = """
                    SELECT COUNT(id) AS result FROM attendance_records\s
                    WHERE (attendanceDate = ? AND course = ? AND programme = ? AND level = ? AND className = ?);
                    """;
            prepare = getCon().prepareStatement(query);
            prepare.setDate(1, dateToCheck);
            prepare.setString(2, course);
            prepare.setString(3, programme);
            prepare.setString(4, level);
            prepare.setString(5, className);
            resultSet = prepare.executeQuery();
            if (resultSet.next()) {
                int counter = resultSet.getInt(1);
                result = counter > 0;
            }
            prepare.close();
        } catch (SQLException ignore) {
            ignore.printStackTrace();
        }
        return result;
    }

    public Collection<AttendanceRecordsEntity> fetchAttendanceRecords(Date startDate, Date endDate, String className, String programme, String yearGroup, String course) {
        Collection<AttendanceRecordsEntity> data = new ArrayList<>();
        try {
            String query = """
                    SELECT ar.indexNumber, fullName,
                           SUM(CASE WHEN attendanceValue = 'p' THEN 1 ELSE 0 END) AS present_count,
                           SUM(CASE WHEN attendanceValue = 'a' THEN 1 ELSE 0 END) AS absent_count,
                           SUM(CASE WHEN attendanceValue = 'excused' THEN 1 ELSE 0 END) AS excused_count,
                           COUNT(*) AS total_attendance FROM attendance_records AS ar
                           INNER JOIN studentslist AS sl ON ar.rowNumber = sl.id
                           WHERE(attendanceDate BETWEEN ? AND ? AND ar.programme = ? AND className = ? AND ar.year_group = ? AND course = ?)
                           GROUP BY ar.indexNumber, fullName
                    """;
            prepare = getCon().prepareStatement(query);
            prepare.setDate(1, startDate);
            prepare.setDate(2, endDate);
            prepare.setString(3, programme);
            prepare.setString(4, className);
            prepare.setString(5, yearGroup);
            prepare.setString(6, course);

            resultSet = prepare.executeQuery();
            AtomicInteger index = new AtomicInteger();
            while (resultSet.next()) {
                String indexNumber = resultSet.getString("indexNumber");
                String fullName = resultSet.getString("fullName");
                int presentCount = resultSet.getInt("present_count");
                int absentCount = resultSet.getInt("absent_count");
                int excusedCount = resultSet.getInt("excused_count");
                int totalAttendance = resultSet.getInt("total_attendance");
//                Date date = resultSet.getDate("attendanceDate");
                data.add(new AttendanceRecordsEntity(index.incrementAndGet(), indexNumber, fullName, presentCount, absentCount, totalAttendance, excusedCount));
            }
            getCon().close();
        } catch (SQLException ignore) { ignore.printStackTrace();
        }
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
                        (SELECT COUNT(*) FROM studentslist WHERE class = 'Network 3B') AS 'network3c_students',
                         (SELECT COUNT(*) FROM studentslist WHERE class = 'BTECH Computer Sci') AS 'btech_students'
                    FROM studentslist;
                    """;
            prepare = getCon().prepareStatement(query1);
            resultSet = prepare.executeQuery();
            if (resultSet.next()) {
                data.put("totalStudents", resultSet.getString("total"));
                data.put("comp2_students", resultSet.getString("comp2_students"));
                data.put("network2a_students", resultSet.getString("network2a_students"));
                data.put("network3a_students", resultSet.getString("network3a_students"));
                data.put("network2c_students", resultSet.getString("network2c_students"));
                data.put("network3c_students", resultSet.getString("network3c_students"));
                data.put("comp3_students", resultSet.getString("comp3_students"));
                data.put("btech", resultSet.getString("btech_students"));
            }
            getCon().close();
        } catch (Exception e) {
            // TODO: handle exception
        }

        return data;
    }

    public Collection<ActivitiesEntity> getAggregatedStudentActivityRecords(Map<String, Object> parameters) {
        Collection<ActivitiesEntity> data = new ArrayList<>();
        try {
            AtomicInteger counter = new AtomicInteger(0);
            String query = """
                    SELECT\s
                    	indexNumber,\s
                        fullname,
                    	SUM(score) AS total_score,
                        SUM(maximumScore) AS `max_score`,
                        COUNT(activityType) AS 'activity_count'
                    FROM activity_records AS ar\s
                    	INNER JOIN studentslist AS sl
                        ON ar.rowNumber = sl.id
                    WHERE(className = ? AND\s
                    	activityType = ? and ar.programme = ? AND year_group = ? AND ar.course = ? )
                    GROUP BY indexNumber, fullname ORDER BY indexNumber;
                    """;
            prepare = getCon().prepareStatement(query);
            prepare.setString(1, parameters.get("className").toString());
            prepare.setString(2, parameters.get("activityType").toString());
            prepare.setString(3, parameters.get("programme").toString());
            prepare.setString(4, parameters.get("yearGroup").toString());
            prepare.setString(5, parameters.get("course").toString());
            resultSet = prepare.executeQuery();
            while (resultSet.next()) {
                String index = resultSet.getString("indexNumber");
                String name = resultSet.getString("fullname");
                double score = resultSet.getDouble("total_score");
                double maxScore = resultSet.getDouble("max_score");
                int activities = resultSet.getInt("activity_count");
                data.add(new ActivitiesEntity(counter.incrementAndGet(), name, index, score, maxScore, activities));
            }
        } catch (Exception ignore) { ignore.printStackTrace();
        }
        return data;
    }

    public Collection<ActivitiesEntity> fetchStudentActivities(String yearGroup) {
        Collection<ActivitiesEntity> data = new ArrayList<>();
        try {
            String query = """
                    SELECT ar.id, rowNumber, indexNumber, fullname, title, activityType, course, className, maximumScore, score, activityDate, dateCreated
                    FROM class_attendance.activity_records AS ar\s
                    INNER JOIN studentslist AS sl\s
                    ON ar.rowNumber = sl.id WHERE(status = 1 AND year_group = ?);
                   \s""";
            prepare = getCon().prepareStatement(query);
            prepare.setString(1, yearGroup);
            resultSet = prepare.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("ar.id");
                String fullname = resultSet.getString("fullname");
                String indexNumber = resultSet.getString("indexNumber");
                int rowNo = resultSet.getInt("rowNumber");
                String title = resultSet.getString("title");
                String activityType = resultSet.getString("activityType");
                String course = resultSet.getString("course");
                String studentClass = resultSet.getString("className");
                double maxScore = resultSet.getDouble("maximumScore");
                double score = resultSet.getDouble("score");
                Date activityDate = resultSet.getDate("activityDate");
                data.add(new ActivitiesEntity(id, fullname, indexNumber, studentClass, title, activityType, activityDate, maxScore, score, rowNo, course));
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
            String query = "SELECT username, password, role_id FROM users WHERE username = '" + username + "';";
            prepare = getCon().prepareStatement(query);
            resultSet = prepare.executeQuery();
            if (resultSet.next()) {
                data.put("username", resultSet.getString("username"));
                data.put("password", resultSet.getString("password"));
                data.put("roleId", String.valueOf(resultSet.getInt("role_id")));
            }
            getCon().close();
        } catch (SQLException ignore) {
            ignore.printStackTrace();
        }


        return data;
    }

    public Collection<UsersEntity> getAllUsers() {
        Collection<UsersEntity> data = new ArrayList<>();
        try {
            String query = "SELECT * FROM users;";
            prepare = getCon().prepareStatement(query);
            resultSet = prepare.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                byte role = resultSet.getByte("role_id");
                byte statusId = resultSet.getByte("status");

                data.add(new UsersEntity(id, username, password, role, statusId));
            }
            getCon().close();
        } catch (SQLException ignore) {
            ignore.printStackTrace();
        }
        return data;
    }


    public Collection<StudentClassesEntity> getAllClasses() {
        Collection<StudentClassesEntity> data = new ArrayList<>();
        try {
//            id, className, creditHours, department, dateCreated
            String query = "SELECT * FROM student_classes;";
            prepare = getCon().prepareStatement(query);
            resultSet = prepare.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String className = resultSet.getString("className");
                byte creditHours = resultSet.getByte("creditHours");
                String dept = resultSet.getString("department");
                boolean status = resultSet.getBoolean("status");
                data.add(new StudentClassesEntity(id, className, creditHours, dept, status));
            }
            getCon().close();
        } catch (SQLException ignore) {
            ignore.printStackTrace();
        }
        return data;
    }

    public List<CourseRecord> getAllCourses() {
        List<CourseRecord> data = new ArrayList<>();
        try {
            String query = "SELECT * FROM courses";
            resultSet = getCon().prepareStatement(query).executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                String name = resultSet.getString("course_name");
                String code = resultSet.getString("course_code");
                String programme = resultSet.getString("programme");
                String level = resultSet.getString("level");
                data.add(new CourseRecord(id, name, code, level, programme));
            }
            resultSet.close();
            getCon().close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return data;
    }

    public List<UserLogsRecord> getSignLogs() {
       List<UserLogsRecord> data = new ArrayList<>();
        try {
            String query = "SELECT * FROM signin ORDER BY id DESC LIMIT 20;";
            resultSet = getCon().prepareStatement(query).executeQuery();
            while (resultSet.next()) {
                int userId = resultSet.getInt(1);
               String username = resultSet.getString("username");
               String role = resultSet.getString("position");
               Timestamp date = resultSet.getTimestamp("signedin_at");
               data.add(new UserLogsRecord(userId, username, role, date));
            }
            resultSet.close();
            getCon().close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return data;
    }
}//end of class
