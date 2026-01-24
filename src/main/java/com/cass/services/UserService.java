package com.cass.services;

import com.cass.data.UsersEntity;
import com.cass.security.Config;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class UserService extends DAO{

    public int saveUser(UsersEntity entity) {
        int status = 0;
        try(Connection source = Config.getDataSource()) {
            String query = "INSERT INTO users(username, password, role_id, index_number) VALUES(?, ?, ?, ?);";
           PreparedStatement prepare = source.prepareStatement(query);
            prepare.setString(1, entity.getUsername());
            prepare.setString(2, entity.getPassword());
            prepare.setByte(3, entity.getRoleId());
            prepare.setString(4, entity.getIndexNumber());
            status = prepare.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return status;
    }

    public int updateUser(UsersEntity entity) {
        String query = """
                UPDATE users SET username = ?, `password` = ?, role_id = ?, `status` = ?  WHERE id = ?
                """;
        try(Connection source = Config.getDataSource()) {
           PreparedStatement prepare = source.prepareStatement(query);
            prepare.setString(1, entity.getUsername());
            prepare.setString(2, entity.getPassword());
            prepare.setInt(3, entity.getRoleId());
            prepare.setInt(4, entity.getStatusId());
            prepare.setInt(5, entity.getId());
            return prepare.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int updatePasswordOnly(String username, String password) {
        String query = "UPDATE users SET `password` = ? WHERE username = ?";
        return LogUserActivity(password, username, query);
    }

    public int logUser(String username, String role) {
        String query = """
                INSERT INTO `class_attendance`.`signin` (`username`, `position`) VALUES (?, ?);
                """;
        return LogUserActivity(username, role, query);
    }

    private int LogUserActivity(String username, String role, String query) {
        try(Connection source = Config.getDataSource()) {
           PreparedStatement prepare = source.prepareStatement(query);
            prepare.setString(1, username);
            prepare.setString(2, role);
            return prepare.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


}
