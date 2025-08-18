package com.cass.data;

import java.util.Collection;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;

public class LoadTableGrid {

    public static <T> GridListDataView<T> loadTable(String searchParameter, Grid<T> tableName, Collection<T> dataCollection) {
        GridListDataView<T> data = tableName.setItems(dataCollection);
        return data;
    }
    public static <T> void loadTable(Grid<T> tableName, Collection<T> dataCollection) {
        tableName.setItems(dataCollection);
    }
        
}
