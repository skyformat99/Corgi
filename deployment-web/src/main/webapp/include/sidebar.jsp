<%@ page import="com.ibeiliao.deployment.admin.context.AdminContext" %>
<%@ page import="com.ibeiliao.deployment.admin.context.AppConstants" %>
<%@ page import="com.ibeiliao.deployment.admin.service.account.MenuService" %>
<%@ page import="com.ibeiliao.deployment.admin.utils.HtmlMenuGenerator" %>
<%@ page import="com.ibeiliao.deployment.admin.utils.resource.MenuItem" %>
<%@ page import="com.ibeiliao.deployment.admin.utils.SpringContextUtil" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>

<!-- Left side column. contains the logo and sidebar -->
<aside class="main-sidebar" th:fragment="sidebar">

    <!-- sidebar: style can be found in sidebar.less -->
    <section class="sidebar">

        <!-- Sidebar user panel (optional) -->
        <div class="user-panel">
            <div class="pull-left image">
                <img src="/static/tpl/img/user2-160x160.jpg" class="img-circle" alt="User Image" />
            </div>
            <div class="pull-left info">

                <p><%=AdminContext.getName()%></p>
            </div>
        </div>

        <!-- Sidebar Menu -->
        <ul class="sidebar-menu">
            <li class="header">HEADER</li>
            <%
                MenuService menuService = SpringContextUtil.getBean(MenuService.class);
                MenuItem item = menuService.getMenuTree(AdminContext.getAccountId(), AppConstants.APP_ID_DEFAULT);
                String html = HtmlMenuGenerator.outputHtml(item);
            %>
            <%=html%>
        </ul><!-- /.sidebar-menu -->
    </section>
    <!-- /.sidebar -->
</aside>
