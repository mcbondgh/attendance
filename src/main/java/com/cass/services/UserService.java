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


}
