package com.cass.documents;

import com.cass.data.ActivitiesEntity;
import com.cass.data.AttendanceRecordsEntity;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.server.StreamResource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class DocumentStreams{
    public static StreamResource createFileResource(String dataFormat, InputStream inputStream) {
        return new StreamResource(dataFormat, ()-> {
            try {
                return new ByteArrayInputStream(inputStream.readAllBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
