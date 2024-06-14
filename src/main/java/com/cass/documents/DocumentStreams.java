package com.cass.documents;

import com.cass.data.AttendanceRecordsEntity;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.server.StreamResource;

public class DocumentStreams extends DocumentGenerator{

    public static StreamResource attendanceResource(String var1, String var2, Grid<AttendanceRecordsEntity> grid) {
        String[] data = {var1, var2};
//        return new StreamResource("attendance-report.pdf", () -> DocumentGenerator.generateAttendancePdf(data, grid));
        return null;
    }
}
