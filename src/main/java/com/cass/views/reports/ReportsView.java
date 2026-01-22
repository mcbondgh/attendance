package com.cass.views.reports;

import com.cass.data.ActivitiesEntity;
import com.cass.documents.DocumentGenerator;
import com.cass.documents.DocumentStreams;
import com.cass.services.DAO;
import com.cass.special_methods.SpecialMethods;
import com.cass.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import org.jetbrains.annotations.NotNull;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@PageTitle("Generate Reports")
@Route(value = "/reports", layout = MainLayout.class)
public class ReportsView extends VerticalLayout {
    public ReportsView() {
        add(
                pageHeaderSection(),
                pageBody()
        );
        populateFields();

        //<theme-editor-local-classname>
        addClassName("reports-view-vertical-layout-1");
    }

    DAO DAO = new DAO();

    private final Grid<ActivitiesEntity> reportsTable = new Grid<>();
    private final ComboBox<String> programmePicker = new ComboBox<>("Select Programme");
    private final ComboBox<String> coursePicker = new ComboBox<>("Select Course");
    private final ComboBox<String> yearGroup = new ComboBox<>("Select Year Group");
    private final Button generateReportButton = new Button("Generate");
    private final ComboBox<String> typePicker = new ComboBox<>("Select Activity");
    private final ComboBox<String> classPicker = new ComboBox<>("Select Section");

    private final TextField filterField = new TextField();
    private final Anchor pdfLink = new Anchor("#", "PDF");
    private final Anchor csvLink = new Anchor("#", "CSV");

    void populateFields() {
        SpecialMethods.setProgramme(programmePicker);
        SpecialMethods.setCourses(coursePicker);
        SpecialMethods.setYear(yearGroup);
        SpecialMethods.setClassSections(classPicker);
        SpecialMethods.setActivityTypes(typePicker);
    }

    /*******************************************************************************************************************
     * RENDER PAGE BODY
     *******************************************************************************************************************/
    private VerticalLayout pageBody() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.addClassNames("activity-report-view", "report-view");
        layout.getStyle().set("background-color", "#fff");

        HorizontalLayout hLayout = new HorizontalLayout( sideBarContainer(), gridLayout());
        hLayout.setSizeFull();
        hLayout.setClassName("reports-view-horizontal-layout");
        layout.add(hLayout);

        return layout;
    }

    /*******************************************************************************************************************
     * RENDER PAGE HEADER SECTION
     *******************************************************************************************************************/
    private HorizontalLayout pageHeaderSection() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setClassName("reports-header-section");
        layout.setWidthFull();
        H5 headerText = new H5("STUDENTS ACTIVITY HISTORY");
        headerText.addClassNames("dashboard-header-text", "dashboard-header-container");

        layout.add(headerText);
        return layout;
    }

    /*******************************************************************************************************************
     * CREATE A MINI SIDE VIEW FOR COMPONENT FILTER.
     *******************************************************************************************************************/
    private VerticalLayout sideBarContainer() {
        VerticalLayout layout = new VerticalLayout();
        layout.addClassNames("reports-div", "container-one");
        generateReportButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_PRIMARY);

        generateReportButton.setClassName("generate-report-button");
        programmePicker.addClassNames("item-picker","class-picker");
        coursePicker.addClassNames("item-picker", "course-picker");
        typePicker.addClassNames("item-picker", "type-picker");
        yearGroup.addClassNames("item-picker", "year-group-picker");
        classPicker.addClassNames("item-picker", "semester-picker");
        pdfLink.addClassNames("anchor-link", "pdf-button");
        csvLink.addClassNames("anchor-link", "csv-button");

        programmePicker.setRequired(true);
        coursePicker.setRequired(true);
        typePicker.setRequired(true);
        yearGroup.setRequired(true);
        classPicker.setRequired(true);

        programmePicker.setInvalid(programmePicker.isEmpty());
        coursePicker.setInvalid(coursePicker.isEmpty());
        typePicker.setInvalid(typePicker.isEmpty());
        yearGroup.setInvalid(yearGroup.isEmpty());
        classPicker.setInvalid(classPicker.isEmpty());

        //Disable button if either of the selectors are empty
        layout.getElement().addEventListener("mouseover", event -> {
           generateReportButton.setEnabled(
                   !(programmePicker.isInvalid() || coursePicker.isInvalid() || typePicker.isInvalid() || yearGroup.isInvalid())
           );
        });

        /**************************************************************************************************************
         * IMPLEMENTATION OF BUTTON CLICK EVENT
        **************************************************************************************************************/

        //FILL THE LIST_DATA_PROVIDER WITH THE DATABASE VALUES AND LOAD THE TABLE ONCE THE BUTTON IS CLICKED.
        generateReportButton.addClickListener(click-> {
            Map<String, Object> parameters = getParameters();
            reportsTable.setItems( new ListDataProvider<>(DAO.getAggregatedStudentActivityRecords(parameters)));
            this.downloadReport();
        });

        layout.add(programmePicker, coursePicker, classPicker, typePicker, yearGroup, new Hr(), generateReportButton );
        return layout;
    }

    /*******************************************************************************************************************
     * CREATE THE TABLE VIEW TO DISPLAY RESULT.
     *******************************************************************************************************************/
    private VerticalLayout gridLayout() {
        VerticalLayout layout = new VerticalLayout();
        layout.addClassNames("reports-div", "container-two");

        configureGrid();

        FlexLayout menuBar = new FlexLayout(pdfLink, csvLink);
        Div filterTextContainer = new Div(filterField, menuBar);
        filterField.setPrefixComponent(LineAwesomeIcon.SEARCH_SOLID.create());

        filterTextContainer.addClassNames("filter-field-container");
        filterField.addClassName("filter-field");
        filterTextContainer.setWidthFull();
        filterField.setWidthFull();
        filterField.setPlaceholder("Filter table with key words");
        menuBar.addClassNames("reports-menu-bar");

        //ADD A MOUSE_MOVE EVENT TO CHECK IF TABLE IS EMPTY, IF TRUE, DISABLE THE EXPORT BUTTONS ELSE ENABLE THEM
        layout.getElement().addEventListener("mouseover", event -> {
           menuBar.setEnabled(reportsTable.getListDataView().getItems().findAny().isPresent());
        });

        //IMPLEMENT FILTER FOR TABLE ITEMS
        filterField.setValueChangeMode(ValueChangeMode.EAGER);
        filterField.addValueChangeListener(listener -> {
            UI.getCurrent().access(() -> {
                reportsTable.getListDataView().setFilter(filter -> {
                    if (filterField.isEmpty()) {
                        return true;
                    }
                        boolean matchesIndex = filter.getIndexNumber().toLowerCase().contains(listener.getValue().toLowerCase());
                        boolean matchesName = filter.getFullname().toLowerCase().contains(listener.getValue().toLowerCase());
                    return matchesName || matchesIndex;
                }).refreshAll();
            });
        });
        layout.add(filterTextContainer, reportsTable);
        return layout;
    }

    /******************************************************************************************************************
     * CREATE AND POPULATE A MAP TO STORE THE VARIOUS REQUIRED PARAMETERS.
     ******************************************************************************************************************/
    private @NotNull Map<String, Object> getParameters() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("className", classPicker.getValue());
        parameters.put("programme", programmePicker.getValue());
        parameters.put("activityType", typePicker.getValue());
        parameters.put("course", coursePicker.getValue());
        parameters.put("yearGroup", yearGroup.getValue());
        return parameters;
    }

    /*******************************************************************************************************************
     * CONFIGURE GRID TABLE COLUMNS
     *******************************************************************************************************************/
    void configureGrid() {
        reportsTable.addColumn(ActivitiesEntity::getId).setHeader("ID").setClassName("header-id");
        reportsTable.addColumn(ActivitiesEntity::getIndexNumber).setHeader("INDEX NUMBER");
        reportsTable.addColumn(ActivitiesEntity::getFullname).setHeader("FULL NAME");
        reportsTable.addColumn(ActivitiesEntity::getScore).setHeader("TOTAL SCORE");
        reportsTable.addColumn(ActivitiesEntity::getmaximumScore).setHeader("MAX SCORE");
        reportsTable.addColumn(ActivitiesEntity::getActivityCount).setHeader("ACTIVITY COUNT");
        reportsTable.getColumns().forEach(each -> each.setAutoWidth(true));
        reportsTable.setRowsDraggable(true);
    }

    /*******************************************************************************************************************
     * ACTION EVENT TO DOWNLOAD DATA
     *******************************************************************************************************************/
    void downloadReport() {
//        StreamResource resource = new StreamResource("Assessment Report.pdf", ()-> {
//            try {
//                return new ByteArrayInputStream(DocumentGenerator.generateActivityReportPDF(
//                        classPicker.getValue(), coursePicker.getValue(), reportsTable).readAllBytes());
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        });
//        pdfLink.setHref(resource);
//        pdfLink.setTarget("_blank");
            String className = programmePicker.getValue();
            String course = coursePicker.getValue();

            //create pdf downloadable file.
            InputStream pdfStream = DocumentGenerator.generateActivityReportPDF(className, course, reportsTable);
            StreamResource pdfResource = DocumentStreams.createFileResource("Assessment Report.pdf", pdfStream);
            pdfLink.setHref(pdfResource);
            pdfLink.setTarget("_blank");

            //create csv downloadable file.
            InputStream csvStream = DocumentGenerator.generateCSVFile(className, course, reportsTable);
            StreamResource csvResource = DocumentStreams.createFileResource("Assessment Report.xlsx", csvStream);
            csvLink.setHref(csvResource);
    }



}//END OF CLASS...
