package com.cass.services;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

import com.cass.data.ActivitiesEntity;

public class ActivityService extends DAO {

    public int saveAcademicActivity(ActivitiesEntity entity) {
        AtomicInteger status = new AtomicInteger();
        try {
            String query = """
                INSERT INTO activity_records(rowNumber, title, activityType, programe, className, maximumScore, score, activityDate)
                VALUES(?, ?, ?, ?, ?, ?, ?, ?);
            """;
            prepare = getCon().prepareStatement(query);
            prepare.setInt(1, entity.getRowNumber());
            prepare.setString(2, entity.getActivityTitle());
            prepare.setString(3, entity.getActivityType());
            prepare.setString(4, entity.getPrograme());
            prepare.setString(5, entity.getClassName());
            prepare.setDouble(6, entity.getMaximumSocre());
            prepare.setDouble(7, entity.getScore());
            prepare.setDate(8, entity.getActivityDate());
            status.set(prepare.executeUpdate());
            getCon().close();
        } catch (SQLException igonre) { igonre.printStackTrace();
        }
        return status.get();
    }

}
