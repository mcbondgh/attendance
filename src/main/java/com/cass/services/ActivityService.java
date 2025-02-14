package com.cass.services;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

import com.cass.data.ActivitiesEntity;

public class ActivityService extends DAO {

    public int saveAcademicActivity(ActivitiesEntity entity) {

        try {
            String query = """
                INSERT INTO activity_records(rowNumber, level, programme, activityType, course, className, maximumScore, score, activityDate)
                VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?);
            """;
            prepare = getCon().prepareStatement(query);
            prepare.setInt(1, entity.getRowNumber());
            prepare.setString(2, entity.getLevel());
            prepare.setString(3, entity.getPrograme());
            prepare.setString(4, entity.getActivityType());
            prepare.setString(5, entity.getCourse());
            prepare.setString(6, entity.getClassName());
            prepare.setDouble(7, entity.getMaximumSocre());
            prepare.setDouble(8, entity.getScore());
            prepare.setDate(9, entity.getActivityDate());
            return prepare.executeUpdate();
        } catch (SQLException ignore) { }
        return 0;
    }

}
