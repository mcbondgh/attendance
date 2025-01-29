SELECT * FROM studentslist WHERE DATE(dateAdded) = DATE(NOW());

-- INSERT INTO attendance_records(rowNumber, indexNumber, className, programeName, attendanceValue, attendanceDate)
-- VALUES(427, "04/2022/1250D", "Network 2A", "SYSTEMS A. & DESIGN", "P", "2024-02-29"),
-- (405, "04/2022/1161D", "Network 2A", "SYSTEMS A. & DESIGN", "P", "2024-02-29"),
-- (429, "04/2022/1162D", "Network 2A", "SYSTEMS A. & DESIGN", "P", "2024-02-29");
-- (423, "04/2022/1237D", "Network 2A", "SYSTEMS A. & DESIGN", "P", "2024-02-29"),
-- (418, "04/2022/1256D", "Network 2A", "SYSTEMS A. & DESIGN", "P", "2024-02-29"),
-- (415, "04/2022/1188D", "Network 2A", "SYSTEMS A. & DESIGN", "P", "2024-02-29"),
-- (443, "04/2022/1281D", "Network 2A", "SYSTEMS A. & DESIGN", "P", "2024-02-29"),
-- (353, "04/2022/1471D", "Network 2A", "SYSTEMS A. & DESIGN", "P", "2024-02-29"),
-- (444, "04/2022/1159D", "Network 2A", "SYSTEMS A. & DESIGN", "P", "2024-02-29"),
-- (442, "04/2022/1259D", "Network 2A", "SYSTEMS A. & DESIGN", "P", "2024-02-29"),
-- (368, "04/2022/1198D", "Network 2A", "SYSTEMS A. & DESIGN", "P", "2024-02-29");
-- (638, "04/2021/4188D", "Network 3A", "ITCM", "P", "2024-02-27"),
-- (639, "04/2021/4134D", "Network 3A", "ITCM", "P", "2024-02-27"),
-- (640, "04/2021/4205d", "Network 3A", "ITCM", "P", "2024-02-27"),
-- (641, "04/2021/4174D", "Network 3A", "ITCM", "P", "2024-02-27"),
-- (642, "04/2021/4129D", "Network 3A", "ITCM", "P", "2024-02-27"),
-- (643, "04/2021/4220D", "Network 3A", "ITCM", "P", "2024-02-27"),
-- (644, "04/2021/4111D", "Network 3A", "ITCM", "P", "2024-02-27"),
-- (645, "04/2021/4216d", "Network 3A", "ITCM", "P", "2024-02-27"),
-- (646, "04/2021/4119D", "Network 3A", "ITCM", "P", "2024-02-27"),
-- (647, "04/2021/4141D", "Network 3A", "ITCM", "P", "2024-02-27"),
-- (648, "04/2021/4219D", "Network 3A", "ITCM", "P", "2024-02-27"),
-- (649, "04/2021/4243D", "Network 3A", "ITCM", "P", "2024-02-27"),
-- (650, "04/2021/4109d", "Network 3A", "ITCM", "P", "2024-02-27"),
-- (650, "04/2021/4124D", "Network 3A", "ITCM", "P", "2024-02-27"),
-- (651, "04/2021/4209D", "Network 3A", "ITCM", "P", "2024-02-27"),
-- (652, "04/2021/4109d", "Network 3A", "ITCM", "P", "2024-02-27"),
-- (653, "04/2021/4181D", "Network 3A", "ITCM", "P", "2024-02-27");


-- 12 /05 / 2024
ALTER TABLE student_classes
CHANGE COLUMN creditHours  creditHours TINYINT DEFAULT 2;
ALTER TABLE student_classes 
MODIFY COLUMN department VARCHAR(50) DEFAULT 'COMPUTER SCIENCE DEPT';

SELECT * FROM attendance_records WHERE programeName = 'Project Management';

SELECT rowNumber, fullname, st.indexNumber, className, programeName, attendanceValue, attendanceDate
-- (SELECT COUNT(id) FROM attendance_records WHERE attendanceDate = current_date()) AS total
 FROM attendance_records AS ar
INNER JOIN studentslist AS st
ON ar.rowNumber = st.id 
WHERE attendanceDate = CURRENT_DATE() AND className = 'Network 3B';
-- 2024-06-12 07:36:22 3A
-- 2024-06-12 13:18:40 3B
-- DELETE FROM attendance_records WHERE programeName = 'PROJECT MANAGEMENT';

UPDATE attendance_records 
SET dateRecorded = '2024-06-12 13:18:40', attendanceDate = '2024-06-12'
WHERE programeName = 'PROJECT MANAGEMENT' AND className = 'NETWORK 3B';

SELECT * FROM studentsList WHERE(class = "NETWORK 3A" and title = "SEMESTER 1");

SELECT
 ar.indexNumber, attendanceDate,
SUM(CASE WHEN attendanceValue = 'p' THEN 1 ELSE 0 END) AS present_count,
SUM(CASE WHEN attendanceValue = 'a' THEN 1 ELSE 0 END) AS absent_count,
SUM(CASE WHEN attendanceValue = 'excused' THEN 1 ELSE 0 END) AS excused_count,
COUNT(*) AS total_attendance
FROM attendance_records AS ar
    INNER JOIN studentslist AS sl
     ON ar.rowNumber = sl.id
    WHERE programeName = "PROJECT MANAGEMENT" AND className = 'COMPUTER SCI 3'
  GROUP BY
  ar.indexNumber, attendanceDate;
  
  
  SELECT ar.id, rowNumber, indexNumber, fullname, 
  title, activityType, programe, className, maximumScore, score, activityDate, dateCreated
        FROM class_attendance.activity_records AS ar
        INNER JOIN studentslist AS sl
        ON ar.rowNumber = sl.id WHERE(status = 1 AND TITLE = 'SEMESTER 1');
