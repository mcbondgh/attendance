package com.cass.views.viewattendance;

import com.cass.data.AttendanceEntity;
import com.cass.data.AttendanceRecordsEntity;
import com.cass.data.LoadTableGrid;
import com.cass.services.DAO;
import com.cass.special_methods.SpecialMethods;
import com.cass.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.BoxSizing;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.progressbar.ProgressBarVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.DataCommunicator.Filter;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import jakarta.annotation.security.PermitAll;

import java.sql.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.scheduling.config.Task;
import org.vaadin.lineawesome.LineAwesomeIcon;

@PageTitle("View Attendance")
@Route(value = "view-attendance", layout = MainLayout.class)
@AnonymousAllowed
@Uses(Icon.class)
public class ViewAttendanceView extends VerticalLayout {

    DAO DAO_OBJECT = new DAO();

    private Grid<AttendanceRecordsEntity> grid = new Grid<>();
    private Filter filterObj;
    private ComboBox<String> classPicker = new ComboBox<>("Select Class");
    private DatePicker startDatePicker = new DatePicker("Start Date");
    private DatePicker endDatePicker = new DatePicker("End Date");
    private ComboBox<String> programPicker = new ComboBox<>("Select Program");
    private Button generateButton = new Button("Generate");
    private TextField filterField = new TextField();

    public ViewAttendanceView() {
        // set component class names
        generateButton.setClassName("generate-attendance-view-button");
        generateButton.setEnabled(false);
        filterField.addClassName("filter-field");

        requiredFields();
        add(renderViewHeader(), renderTableContent());

    }

    /*************************************************************************************
     * SET REQUIRED FIELDS
     **************************************************************************************/

    private void requiredFields() {
        startDatePicker.setInvalid(startDatePicker.isEmpty());
        endDatePicker.setRequired(true);
        endDatePicker.setInvalid(endDatePicker.isEmpty());
        classPicker.setInvalid(classPicker.isEmpty());
        programPicker.setInvalid(programPicker.isEmpty());
        programPicker.setRequired(true);
        classPicker.setRequired(true);
    }

    private void isFieldEmpty(Button button) {
        button.setEnabled(!(startDatePicker.isInvalid() || endDatePicker.isInvalid() ||
                classPicker.isInvalid() || programPicker.isInvalid()));
    }

    /*************************************************************************************
     * CREATE AND RENDER PAGE HEADER WITH THE FILTER COMPONENTS
     **************************************************************************************/
    private Component renderViewHeader() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.addClassName("attendance-header-layout");
        layout.setBoxSizing(BoxSizing.BORDER_BOX);

        HorizontalLayout hLayout = new HorizontalLayout(startDatePicker, new H3("-"), endDatePicker);
        hLayout.setClassName("attendance-date-picker-container");
        hLayout.setSpacing(false);

        generateButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
        SpecialMethods.setClasses(classPicker);
        SpecialMethods.setPrograme(programPicker);

        classPicker.setClassName("take-attendance-combobox");
        programPicker.setClassName("take-attendance-combobox");
        generateButton.setClassName("generate-button");
        // add components to layout
        layout.add(hLayout, classPicker, programPicker, generateButton);
        layout.setAlignSelf(Alignment.END, generateButton);

        // add eventlistner to layout to check if field is empty and enable button same.
        layout.getElement().addEventListener("mousemove", callBack -> {
            isFieldEmpty(generateButton);
        });

        // LOAD TABLE DATA
        generateButton.addClickListener(click -> {

            // Timer timer = new Timer();
            // timer.scheduleAtFixedRate(task, 1000, 1000);
            Date start = Date.valueOf(startDatePicker.getValue());
            Date end = Date.valueOf(endDatePicker.getValue());
            LoadTableGrid.loadTable(
                    grid,
                    DAO_OBJECT.fetchAttendanceRecords(start, end, classPicker.getValue(), programPicker.getValue()));
            AtomicInteger presentCounter = new AtomicInteger(0);
            AtomicInteger abscentCounter = new AtomicInteger(0);
            AtomicInteger excusedCounter = new AtomicInteger(0);

            grid.getListDataView().getItems().forEach(eachItem -> {
                if (eachItem.getPresent() >= 1) {
                    presentCounter.incrementAndGet();
                }
                if (eachItem.getAbscent() >= 1) {
                    abscentCounter.incrementAndGet();
                }
                if (eachItem.getExcused() >= 1) {
                    excusedCounter.incrementAndGet();
                }
            });
            grid.getColumnByKey("absentColumn").setFooter(new Span("TOTAL: " + abscentCounter.get()));
            grid.getColumnByKey("presentColumn").setFooter(new Span("TOTAL: " + presentCounter.get()));
            grid.getColumnByKey("excusedColumn").setFooter(new Span("TOTAL: " + excusedCounter.get()));
        });


        return layout;
    }

    /*************************************************************************************
     * CREATE AND RENDER PAGE BODY WITH THE GRID COMPONENTS
     **************************************************************************************/

    private Component renderTableContent() {
        VerticalLayout layout = new VerticalLayout();
        layout.setClassName("container");

        Div filterContainer = new Div(filterField);
        filterContainer.setClassName("view-attendance-filter-container");
        filterField.setPlaceholder("filter by index number or name");
        filterField.setPrefixComponent(LineAwesomeIcon.SEARCH_SOLID.create());
        filterField.setClearButtonVisible(true);
        filterField.addClassName("filter-text-field");

        filterField.setValueChangeMode(ValueChangeMode.EAGER);
        filterField.addValueChangeListener(change -> {
            // add filter to table
            grid.getListDataView().setFilter(filter -> {
                if (filterField.isEmpty()) {
                    return true;
                }
                boolean matchesName = filter.getFullname().toLowerCase().contains(change.getValue().toLowerCase());
                boolean matchesIndex = filter.getIndexNumber().toLowerCase().contains(change.getValue().toLowerCase());

                return matchesName || matchesIndex;
            });
            grid.getListDataView().refreshAll();
        });

        layout.add(filterContainer, createGrid());
        return layout;
    }

    /*************************************************************************************
     * VIEW ATTENDANCE GRID IMPLEMENTATION
     *************************************************************************************/

    private Grid<AttendanceRecordsEntity> createGrid() {

        grid.setClassName("view-attendance-grid");

        // SET GRID COLUMNS
        grid.addColumn(AttendanceRecordsEntity::getId).setHeader("NO.");
        grid.addColumn(AttendanceRecordsEntity::getIndexNumber).setHeader("INDEX NUMBER");
        grid.addColumn(AttendanceRecordsEntity::getFullname).setHeader("FULL NAME");
        grid.addComponentColumn(item -> {return item.getPresentLabel();}).setHeader("PRESENT").setKey("presentColumn");
        grid.addComponentColumn(item -> {return item.getAbscentLabel();}).setHeader("ABSENT").setKey("absentColumn");
        grid.addComponentColumn(item -> {return item.getExcusedLabel();}).setHeader("EXCUSED").setKey("excusedColumn");
        grid.addComponentColumn(item -> {return item.getTotalAttendanceLabel();}).setHeader("TOTAL ATTENDANCE");

        grid.getColumns().forEach(each -> each.setAutoWidth(true));
        grid.getColumns().forEach(each -> each.setSortable(true));
        grid.getColumns().forEach(each -> each.setTextAlign(ColumnTextAlign.START));

        return grid;
    }

    /*************************************************************************************
     * VIEW ATTENDANCE GRID IMPLEMENTATION
     *************************************************************************************/
    TimerTask task = new TimerTask() {
        ProgressBar progressBar = new ProgressBar();
        AtomicInteger counter = new AtomicInteger(3);
        

        @Override
        public void run() {
            progressBar.setIndeterminate(true);
            progressBar.addThemeVariants(ProgressBarVariant.LUMO_SUCCESS);
            counter.decrementAndGet();
            progressBar.setVisible(counter.get() == 0);
            System.out.println(counter.get());
            if (counter.get() == 0) {
                this.cancel();
            }
        }
    };

}// end of class...
