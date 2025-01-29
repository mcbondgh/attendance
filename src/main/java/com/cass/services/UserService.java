package com.cass.services;

import com.cass.data.UsersEntity;

public class UserService extends DAO{
    
    public int saveUser(UsersEntity entity) {
        int status = 0;
        try {
            String query = "INSERT INTO users(username, password, role_id) VALUES(?, ?, ?);";
            prepare = getCon().prepareStatement(query);
            prepare.setString(1, entity.getUsername());
            prepare.setString(2, entity.getPassword());
            prepare.setByte(3, entity.getRoleId());
            status = prepare.executeUpdate();
            getCon().close();
        }catch(Exception e) {
            e.printStackTrace();
        }
        return status;
    }

    public int updateUser(UsersEntity entity) {
        String query = """
                UPDATE users SET username = ?, `password` = ?, role_id = ?, `status` = ?  WHERE user_id = ?
                """;
        try {
            prepare = getCon().prepareStatement(query);
            prepare.setString(1, entity.getUsername());
            prepare.setString(2, entity.getPassword());
            prepare.setInt(3, entity.getRoleId());
            prepare.setInt(4, entity.getStatusId());
            prepare.setInt(5, entity.getId());
            return prepare.executeUpdate();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int updatePasswordOnly(String username, String password) {
        String query = "UPDATE users SET `password` = ? WHERE username = ?";
        try {
            prepare = getCon().prepareStatement(query);
            prepare.setString(1, password);
            prepare.setString(2, username);
            return prepare.executeUpdate();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }




}
