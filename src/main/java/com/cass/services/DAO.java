package com.cass.services;

import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import com.cass.data.*;
import com.cass.security.Config;
import com.mysql.cj.protocol.Resultset;

public class DAO {

    public Collection<StudentEntity> getStudentByClass(String programme, String yearGroup, String level, String section) {
        Collection<StudentEntity> data = new ArrayList<>();
        try(Connection source = Config.getDataSource()) {
            String query = "SELECT * FROM studentsList WHERE(class = ? AND year_group = ? AND `level` = ? AND section = ?)";

            PreparedStatement prepare = source.prepareStatement(query);
            prepare.setString(1, programme);
            prepare.setString(2, yearGroup);
            prepare.setString(3, level);
            prepare.setString(4, section);

            ResultSet resultSet = prepare.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String indexNo = resultSet.getString("indexNumber");
                String fullName = resultSet.getString("fullName");
                String program = resultSet.getString("programme");
                String type = resultSet.getString("programme_type");
                String stuClass = resultSet.getString("class");
                String department = resultSet.getString("department");
                String stuSection = resultSet.getString("section");
                String year = resultSet.getString("year_group");
                String stuLevel = resultSet.getString("level");
                byte status = resultSet.getByte("status");
                Timestamp dateAdded = resultSet.getTimestamp("dateAdded");
                Timestamp dateUpdated = resultSet.getTimestamp("dateUpdated");
                data.add(new StudentEntity(id, indexNo, fullName, program, type, stuClass, department, status, year, stuSection, stuLevel, dateAdded, dateUpdated));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // TODO: handle exception
        }
        return data;
    }

    public Collection<ActivitiesEntity> fetchClassListByClassName(String programme, String sectionOrClass, String level) {
        Collection<ActivitiesEntity> data = new ArrayList<>();
        try(Connection source = Config.getDataSource()) {
            String query = "SELECT * FROM studentsList WHERE(class = ? AND year_group = YEAR(CURRENT_DATE()) AND section = ? AND level = ?)";

            PreparedStatement prepare = source.prepareStatement(query);
            prepare.setString(1, programme);
            prepare.setString(2, sectionOrClass);
            prepare.setString(3, level);
            ResultSet resultSet = prepare.executeQuery();
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
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
        }
        return data;
    }

    public Collection<StudentEntity> fetchAllStudents() {
        Collection<StudentEntity> data = new ArrayList<>();
        try(Connection source = Config.getDataSource()) {
            String query = "SELECT * FROM studentsList WHERE(status = 1)";
            PreparedStatement prepare = source.prepareStatement(query);
            ResultSet resultSet = prepare.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String indexNo = resultSet.getString("indexNumber");
                String fullName = resultSet.getString("fullName");
                String program = resultSet.getString("programme");
                String type = resultSet.getString("programme_type");
                String stuClass = resultSet.getString("class");
                String department = resultSet.getString("department");
                byte status = resultSet.getByte("status");
                String year = resultSet.getString("year_group");
                String section = resultSet.getString("section");
                String level = resultSet.getString("level");
                Timestamp dateAdded = resultSet.getTimestamp("dateAdded");
                Timestamp dateUpdated = resultSet.getTimestamp("dateUpdated");
                data.add(new StudentEntity(id, indexNo, fullName, program, type, stuClass, department, status, year, section, level, dateAdded, dateUpdated));
            }
        } catch (Exception e) {
           throw new RuntimeException(e);
            // TODO: handle exception
        }
        return data;
    }

    public Collection<StudentEntity> getStudentByStudentIndex(String indexNumber) {
        Collection<StudentEntity> data = new ArrayList<>();
        try(Connection source = Config.getDataSource()) {
            String query = "SELECT * FROM studentsList WHERE(indexNumber = ?)";
            PreparedStatement prepare = source.prepareStatement(query);
            prepare.setString(1, indexNumber);
            ResultSet resultSet = prepare.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String indexNo = resultSet.getString("indexNumber");
                String fullName = resultSet.getString("fullName");
                String program = resultSet.getString("programme");
                String type = resultSet.getString("programme_type");
                String stuClass = resultSet.getString("class");
                String department = resultSet.getString("department");
                byte status = resultSet.getByte("status");
                String year = resultSet.getString("year_group");
                String section = resultSet.getString("section");
                String level = resultSet.getString("level");
                Timestamp dateAdded = resultSet.getTimestamp("dateAdded");
                Timestamp dateUpdated = resultSet.getTimestamp("dateUpdated");
                data.add(new StudentEntity(id, indexNo, fullName, program, type, stuClass, department, status, year, section, level, dateAdded, dateUpdated));
            }

        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
        }
        return data;
    }

    public Map<String, Object> fetchViewAccessRoutes() {
        var map = new HashMap<String, Object>();
        String query = "SELECT * FROM view_access";
        try(Connection source = Config.getDataSource()) {
           ResultSet resultSet = source.prepareStatement(query).executeQuery();
             while (resultSet.next()) {
                 map.put("route", resultSet.getString("route"));
                 map.put("status", resultSet.getBoolean("is_enabled"));
             }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return map;
    }

    public Collection<StudentEntity> fetchActiveStudentsForAttendanceTable(String studentClass, String level, String programme, String programmeType, String section, String yearGroup) {
        Collection<StudentEntity> data = new ArrayList<>();
        try(Connection source = Config.getDataSource();) {

//            String query1 = "SELECT * FROM studentslist WHERE(status = 1 AND class = '" + studentClass + "' AND level = '" + level + "' " +
//                    "AND programme = '" + programme + "' AND section = '" + section + "' + AND year_group = '" + yearGroup + "');";
//
            PreparedStatement prepare;
            String preferredQuery;
                    //Objects.equals(studentClass, "All Classes") ? query : queryToUse;
            if (Objects.equals(section, "All Classes")) {
               preferredQuery = "SELECT * FROM studentslist WHERE (status = 1 AND level = '" + level + "' AND programme = '" + programme + "' AND programme_type = '" + programmeType + "' AND year_group = '" + yearGroup + "');";
               prepare = source.prepareStatement(preferredQuery);
            }else {
                preferredQuery = """
                    SELECT * FROM studentslist
                    WHERE(status = TRUE AND class = ? AND level = ? AND programme = ? AND programme_type = ? AND section = ? AND year_group = ?);
                    """;
                prepare = source.prepareStatement(preferredQuery);
                prepare.setString(1, studentClass);
                prepare.setString(2, level);
                prepare.setString(3, programme);
                prepare.setString(4, programmeType);
                prepare.setString(5, section);
                prepare.setString(6, yearGroup);
            }
            ResultSet resultSet = prepare.executeQuery();
//            resultSet = prepare.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String indexNo = resultSet.getString("indexNumber");
                String fullName = resultSet.getString("fullName");
                String stuClass = resultSet.getString("class");
                data.add(new StudentEntity(id, indexNo, fullName, stuClass));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return data;
    }

    public boolean checkAttendanceByDate(Date dateToCheck, String course, String programme, String level, String className, String yearGroup) {
        boolean result = false;
        try(Connection source = Config.getDataSource()) {
            String query = """
                    SELECT COUNT(id) AS result FROM attendance_records\s
                    WHERE (attendanceDate = ? AND course = ? AND programme = ? AND level = ? AND className = ? AND year_group = ?);
                    """;
            PreparedStatement prepare = source.prepareStatement(query);
            prepare.setDate(1, dateToCheck);
            prepare.setString(2, course);
            prepare.setString(3, programme);
            prepare.setString(4, level);
            prepare.setString(5, className);
            prepare.setString(6, yearGroup);

            ResultSet resultSet = prepare.executeQuery();
            if (resultSet.next()) {
                result = resultSet.getInt(1) > 0;
            }
            prepare.close();
        } catch (SQLException ignore) {
            ignore.printStackTrace();
        }
        return result;
    }

    public Collection<AttendanceRecordsEntity> fetchAttendanceRecords(Date startDate, Date endDate, String className, String programme, String yearGroup, String course) {
        Collection<AttendanceRecordsEntity> data = new ArrayList<>();
        try(Connection source = Config.getDataSource()) {
            String query = """
                    SELECT ar.indexNumber, fullName, ar.level,
                           SUM(CASE WHEN attendanceValue = 'p' THEN 1 ELSE 0 END) AS present_count,
                           SUM(CASE WHEN attendanceValue = 'a' THEN 1 ELSE 0 END) AS absent_count,
                           SUM(CASE WHEN attendanceValue = 'excused' THEN 1 ELSE 0 END) AS excused_count,
                           COUNT(*) AS total_attendance FROM attendance_records AS ar
                           INNER JOIN studentslist AS sl ON ar.rowNumber = sl.id
                           WHERE(attendanceDate BETWEEN ? AND ? AND ar.programme = ? AND className = ? AND ar.year_group = ? AND course = ?)
                           GROUP BY ar.indexNumber, fullName, level
                    """;
            PreparedStatement prepare = source.prepareStatement(query);
            prepare.setDate(1, startDate);
            prepare.setDate(2, endDate);
            prepare.setString(3, programme);
            prepare.setString(4, className);
            prepare.setString(5, yearGroup);
            prepare.setString(6, course);

            ResultSet resultSet = prepare.executeQuery();
            AtomicInteger index = new AtomicInteger();
            while (resultSet.next()) {
                String indexNumber = resultSet.getString("indexNumber");
                String fullName = resultSet.getString("fullName");
                int presentCount = resultSet.getInt("present_count");
                int absentCount = resultSet.getInt("absent_count");
                int excusedCount = resultSet.getInt("excused_count");
                int totalAttendance = resultSet.getInt("total_attendance");
                String level = resultSet.getString("level");
//                Date date = resultSet.getDate("attendanceDate");
                data.add(new AttendanceRecordsEntity(index.incrementAndGet(), indexNumber, fullName, level, presentCount, absentCount, totalAttendance, excusedCount));
            }
        } catch (SQLException ignore) {
            throw new RuntimeException(ignore);
        }
        return data;
    }

    public Map<String, Object> getStudentCounter() {
        Map<String, Object> data = new HashMap<>();
        try(Connection source = Config.getDataSource()) {
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
           PreparedStatement prepare = source.prepareStatement(query1);
           ResultSet resultSet = prepare.executeQuery();
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

        } catch (Exception e) {
            // TODO: handle exception
        }

        return data;
    }

    public Collection<ActivitiesEntity> getAggregatedStudentActivityRecords(Map<String, Object> parameters) {
        Collection<ActivitiesEntity> data = new ArrayList<>();
        try(Connection source = Config.getDataSource()) {
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
            PreparedStatement prepare = source.prepareStatement(query);
            prepare.setString(1, parameters.get("className").toString());
            prepare.setString(2, parameters.get("activityType").toString());
            prepare.setString(3, parameters.get("programme").toString());
            prepare.setString(4, parameters.get("yearGroup").toString());
            prepare.setString(5, parameters.get("course").toString());
            ResultSet resultSet = prepare.executeQuery();
            while (resultSet.next()) {
                String index = resultSet.getString("indexNumber");
                String name = resultSet.getString("fullname");
                double score = resultSet.getDouble("total_score");
                double maxScore = resultSet.getDouble("max_score");
                int activities = resultSet.getInt("activity_count");
                data.add(new ActivitiesEntity(counter.incrementAndGet(), name, index, score, maxScore, activities));
            }
        } catch (Exception ignore) {
            ignore.printStackTrace();
        }
        return data;
    }

    public Collection<ActivitiesEntity> fetchStudentActivities(String yearGroup) {
        Collection<ActivitiesEntity> data = new ArrayList<>();
        try(Connection source = Config.getDataSource()) {
            String query = """
                     SELECT ar.id, rowNumber, indexNumber, fullname, title, activityType, course, className, maximumScore, score, activityDate, dateCreated
                     FROM class_attendance.activity_records AS ar\s
                     INNER JOIN studentslist AS sl\s
                     ON ar.rowNumber = sl.id WHERE(status = 1 AND year_group = ?);
                    \s""";
            PreparedStatement prepare = source.prepareStatement(query);
            prepare.setString(1, yearGroup);
            ResultSet resultSet = prepare.executeQuery();
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
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
        }
        return data;
    }

    public Map<String, String> getUsersByUsername(String username) {
        Map<String, String> data = new HashMap<>();
        try(Connection source = Config.getDataSource()) {
            String query = "SELECT username, password, role_id, index_number FROM users WHERE username = ?;";
            PreparedStatement prepare = source.prepareStatement(query);
            prepare.setString(1, username);
            ResultSet resultSet = prepare.executeQuery();
            if (resultSet.next()) {
                data.put("username", resultSet.getString("username"));
                data.put("password", resultSet.getString("password"));
                data.put("roleId", String.valueOf(resultSet.getInt("role_id")));
                data.putIfAbsent("index_number", resultSet.getString("index_number") == null ? "0": resultSet.getString("index_number"));
            }
        } catch (SQLException ignore) {
            ignore.printStackTrace();
        }
        return data;
    }

    public Collection<UsersEntity> getAllUsers() {
        Collection<UsersEntity> data = new ArrayList<>();
        try(Connection source = Config.getDataSource()) {
            String query = "SELECT * FROM users WHERE status = TRUE ORDER BY username ASC;";
            PreparedStatement prepare = source.prepareStatement(query);
            ResultSet resultSet = prepare.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                byte role = resultSet.getByte("role_id");
                byte statusId = resultSet.getByte("status");
                String index = resultSet.getString("index_number");

                data.add(new UsersEntity(id, username, password, role, statusId, index));
            }
        } catch (SQLException ignore) {
            ignore.printStackTrace();
        }
        return data;
    }

    public Map<String, String> getClassRepInfo(String indexNumber) {
        Map<String, String> data = new HashMap<>();
        String query = """
                SELECT
                section, level, year_group, class
                FROM studentslist WHERE TRIM(indexNumber) = TRIM(?);
                """;
        try(Connection source = Config.getDataSource()) {
            PreparedStatement prepare = source.prepareStatement(query);
            prepare.setString(1, indexNumber);
            ResultSet resultSet = prepare.executeQuery();
            if (resultSet.next()) {
                data.put("section", resultSet.getString("section"));
                data.put("level", resultSet.getString("level"));
                data.put("year_group", resultSet.getString("year_group"));
                data.put("class", resultSet.getString("class"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }


    public List<ProgrammesEntity> getAllProgrammes() {
        List<ProgrammesEntity> data = new ArrayList<>();
        try(Connection source = Config.getDataSource()) {
            PreparedStatement prepare;
            ResultSet resultSet;
            //id, programme_name, programme_type, department, status, date_created
            String query = "SELECT * FROM programmes WHERE (status = TRUE) ORDER BY programme_name ASC;";
            prepare = source.prepareStatement(query);
            resultSet = prepare.executeQuery();
            //
            while (resultSet.next()) {
                data.add(
                        new ProgrammesEntity(
                                resultSet.getInt("id"),
                                resultSet.getString("programme_name"),
                                resultSet.getString("programme_type"),
                                resultSet.getBoolean("status"),
                                resultSet.getTimestamp("date_created"),
                                resultSet.getString("department")
                        )
                );

            }
        } catch (SQLException ignore) {
            ignore.printStackTrace();
        }
        return data;
    }

    public List<CourseRecord> getAllCourses() {
        List<CourseRecord> data = new ArrayList<>();
        try(Connection source = Config.getDataSource()) {
            PreparedStatement prepare;
            ResultSet resultSet;
            String query = """
                    SELECT course_id, course_name, course_code, programme_name, level, date_added, is_active FROM courses AS c
                    INNER JOIN programmes AS p
                    ON c.programme = p.id
                    WHERE is_active = TRUE;
                    """;
            resultSet = source.prepareStatement(query).executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("course_id");
                String name = resultSet.getString("course_name");
                String code = resultSet.getString("course_code");
                String programme = resultSet.getString("programme_name");
                String level = resultSet.getString("level");
                data.add(new CourseRecord(id, name, code, level, programme));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return data;
    }

    public List<UserLogsRecord> getSignLogs() {
        List<UserLogsRecord> data = new ArrayList<>();
        try(Connection source = Config.getDataSource()) {
            PreparedStatement prepare;
            ResultSet resultSet;
            String query = "SELECT * FROM signin ORDER BY id DESC LIMIT 20;";
            resultSet = source.prepareStatement(query).executeQuery();
            while (resultSet.next()) {
                int userId = resultSet.getInt(1);
                String username = resultSet.getString("username");
                String role = resultSet.getString("position");
                Timestamp date = resultSet.getTimestamp("signedin_at");
                data.add(new UserLogsRecord(userId, username, role, date));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return data;
    }
}//end of class
