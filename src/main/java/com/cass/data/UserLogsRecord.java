package com.cass.data;

import java.sql.Timestamp;

public record UserLogsRecord(int userId, String username, String role, Timestamp data) { }
