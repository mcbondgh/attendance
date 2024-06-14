package com.cass.views;

import com.cass.security.SessionManager;
//import com.cass.security.AuthenticatedUser;
import com.cass.views.addstudent.AddStudentView;
import com.cass.views.dashboard.DashboardView;
import com.cass.views.login.UserLoginView;
import com.cass.views.managecourse.ManageCourseView;
import com.cass.views.manageusers.ManageUsersView;
import com.cass.views.reports.ReportsView;
import com.cass.views.takeattendance.TakeAttendanceView;
import com.cass.views.reports.viewattendance.ViewAttendanceView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.vaadin.lineawesome.LineAwesomeIcon;

/**
 * The main view is a top-level placeholder for other views.
 */
public class MainLayout extends AppLayout implements BeforeEnterObserver {
    private H2 viewTitle;
    private H6 userNameLabel = new H6();

//    private AuthenticatedUser authenticatedUser;
//    private AccessAnnotationChecker accessChecker;

    public MainLayout() {

        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");

        viewTitle = new H2();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        addToNavbar(true, toggle, viewTitle);
    }

    private void addDrawerContent() {
        H3 appName = new H3("ATTENDANCE SHEET");
        appName.addClassNames(LumoUtility.FontSize.MEDIUM, LumoUtility.Margin.NONE);
        Header header = new Header(appName);
        
        appName.setClassName("app-name-text");

        Scroller scroller = new Scroller(createNavigation());

        addToDrawer(header, scroller, createFooter());
    }

    private Component createNavigation() {
        SideNav nav1 = new SideNav();
            nav1.addItem(new SideNavItem("Dashboard", DashboardView.class, LineAwesomeIcon.MDB.create()));
            nav1.addItem(new SideNavItem("Take Attendance", TakeAttendanceView.class,
                    LineAwesomeIcon.PENCIL_ALT_SOLID.create()));
            nav1.addItem(
                    new SideNavItem("Manage Students", AddStudentView.class, LineAwesomeIcon.PLUS_CIRCLE_SOLID.create()));
            nav1.addItem(new SideNavItem("Manage Activities", ManageCourseView.class, LineAwesomeIcon.USER.create()));
            nav1.addItem(
                    new SideNavItem("Manage Users", ManageUsersView.class, LineAwesomeIcon.USERS_COG_SOLID.create()));

        SideNav reportsNav = new SideNav("Reports");
        reportsNav.setCollapsible(true);
        reportsNav.setExpanded(false);

        reportsNav.addItem( new SideNavItem("Reports", ReportsView.class, LineAwesomeIcon.RECEIPT_SOLID.create()));
        reportsNav.addItem(new SideNavItem("View Attendance", ViewAttendanceView.class,
                LineAwesomeIcon.FILTER_SOLID.create()));
        // if (accessChecker.hasAccess(SetUpView.class)) {
        //     nav1.addItem(new SideNavItem("Set Up", SetUpView.class, LineAwesomeIcon.LIST_SOLID.create()));

        // }

        nav1.addClassNames("side-nav", "side-nav-one");
        reportsNav.addClassNames("side-nav", "reports-nav");
        VerticalLayout navLayout = new VerticalLayout(nav1, reportsNav);
        navLayout.setSpacing(false);
        navLayout.setSizeUndefined();

        nav1.setWidthFull();
        reportsNav.setWidthFull();
        return navLayout;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEvent) {
        try {
            String activeUser = SessionManager.getAttribute("activeUser").toString();
            userNameLabel.setText(activeUser);
        }catch(NullPointerException e){
            beforeEvent.forwardTo(UserLoginView.class);
        }
    }
    
    private Footer createFooter() {
        Footer layout = new Footer();
        layout.setClassName("footer-container");

        MenuBar menuBar = new MenuBar();
        MenuItem menuItem = menuBar.addItem(userNameLabel);
        Avatar avatar = new Avatar();
        avatar.setImage("icons/user-100.png");
        avatar.setClassName("avatar");
        menuItem.addComponentAsFirst(avatar);
        Anchor signoutLink = new Anchor("javascript:void(0)", "sign out");
        signoutLink.setWidthFull();

        //logout user...
        signoutLink.getElement().addEventListener("click", callBack -> {
            SessionManager.destroySession();
        });

        SubMenu subMenu = menuItem.getSubMenu();
        subMenu.addItem(signoutLink);

        menuBar.addThemeVariants(MenuBarVariant.LUMO_TERTIARY);
        menuBar.addClassName("signout-menu-bar");
        menuItem.addClassName("signout-menu-item");
        layout.add(menuBar);
        return layout;
    }


    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }

   

}
