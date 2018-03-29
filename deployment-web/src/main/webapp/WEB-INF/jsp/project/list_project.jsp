<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.ibeiliao.deployment.admin.vo.account.AdminAccount" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <%@ include file="/include/meta.html" %>
    <title>项目列表</title>
</head>
<body class="skin-blue sidebar-mini">
<div class="wrapper">

    <!-- header -->
    <jsp:include page="/include/header.jsp"/>

    <!-- sidebar -->
    <jsp:include page="/include/sidebar.jsp"/>

    <!-- Content Wrapper. Contains page content -->
    <div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <section class="content-header">
            <h1>
                项目列表
                <small></small>
            </h1>
            <ol class="breadcrumb">
                <li><a href="javascript:void(0)" data-toggle="modal" data-target="#configModal"
                       onclick="addProject()"><i class="fa fa-dashboard"></i> 新增项目</a></li>
            </ol>
        </section>

        <!-- Main content -->
        <section class="content">
            <div class="row">
                <div class="col-xs-12">
                    <div class="box">
                        <div class="box-header">
                            <div class="col-sm-3">
                                <div class="input-group input-group-sm">
                                    <label class="control-label col-sm-6 text-right">项目名</label>
                                    <div class="input-group input-group-sm col-sm-6">
                                        <input type="text" id="searchProjectName" name="searchProjectName"
                                               maxlength="20" class="form-control" placeholder="项目名称">
                                    </div>
                                </div>
                            </div>

                            <div class="col-md-2">
                                <label class=" col-sm-6 control-label  text-right">语言</label>
                                <div class="input-group input-group-sm col-sm-6">
                                    <select class="form-control" id="projectLanguage" name="projectLanguage">
                                        <option value="">全部</option>
                                        <option value="java">java</option>
                                        <option value="HTML">HTMl</option>
                                    </select>
                                </div>
                            </div>

                            <div class="col-md-4">
                                <label class="control-label col-sm-6  text-right">负责人</label>
                                <div class="col-md-6 ">
                                    <select id="projectManagerId" name="projectManagerId" class="form-control">
                                        <option value="0">全部</option>
                                        <c:forEach var="projectManager" items="${projectManagers}">
                                            <option value="${projectManager.uid}">${projectManager.realname}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                            </div>

                            <div class="col-md-3">
                                <span class="input-group-btn">
										<button type="button" class="btn btn-info btn-flat"
                                                onclick="search()">Go!</button>
									</span>
                            </div>

                        </div><!-- /.box-header -->
                        <div class="box-body">
                            <div class="row">
                                <div class="col-sm-12">
                                    <table id="listTable" class="table table-bordered table-hover">
                                        <thead>
                                        <tr>
                                            <th>ID</th>
                                            <th>项目名称</th>
                                            <th>语言</th>
                                            <th>负责人</th>
                                            <th>参与者</th>
                                            <th>创建时间</th>
                                            <th>操作</th>
                                        </tr>
                                        </thead>
                                        <script id="listTableTpl" type="text/html">
                                            {{each object as value i}}
                                            <tr>
                                                <td>
                                                    {{value.projectId}}
                                                </td>
                                                <td>
                                                    <a href="/admin/project/viewProject.xhtml?projectId={{value.projectId}}">{{value.projectName}}
                                                        ({{value.projectNo}})</a></td>
                                                <td>{{value.programLanguage}}</td>
                                                <td>{{value.managers}}</td>
                                                <td>{{value.joinerNames}}</td>
                                                <td>{{formatDateTime value.createTime}}</td>
                                                <td>
                                                    <a href="javascript:void(0)" data-toggle="modal"
                                                       data-target="#configModal"
                                                       onclick="editProject({{value.projectId}})">修改</a>
                                                    <%--&nbsp; <a href="/admin/project/viewProject.xhtml?projectId={{value.projectId}}">项目详情</a>--%>
                                                </td>
                                            </tr>
                                            {{/each}}
                                        </script>
                                    </table>

                                </div>
                            </div>
                            <div class="row col-sm-12" id="listPaginator">
                            </div>
                        </div><!-- /.box-body -->
                    </div><!-- /.box -->
                </div><!-- /.col -->
            </div><!-- /.row -->

            <!-- Modal -->
            <div class="modal fade" id="configModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
                <div class="modal-dialog" role="document">
                    <div class="modal-content">
                        <div class="modal-header">
                            <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                                    aria-hidden="true">&times;</span></button>
                            <h4 class="modal-title" id="myModalLabel">新增项目</h4>
                        </div>
                        <form id="editForm" class="form-horizontal" method="post" modelattribute="projectDetailInfo"
                              onsubmit="return validateForm()" action="/admin/project/save.do">
                            <div class="modal-body">
                                <div class="form-group">
                                    <label class="col-md-2 control-label">项目名称</label>
                                    <div class="col-md-10">
                                        <input id="projectId" name="projectId" value="0" type="hidden"/>
                                        <input class="form-control" type="text" id="projectName"
                                               name="projectName" title="项目名称" placeholder="项目名称"
                                               maxlength="60">
                                    </div>
                                </div>
                                <div class="form-group">
                                    <label class="col-md-2 control-label">代号(唯一)</label>
                                    <div class="col-md-10">
                                        <input class="form-control" type="text" id="projectNo"
                                               name="projectNo" title="代号" placeholder="代号"
                                               maxlength="30">
                                    </div>
                                </div>
                                <div class="form-group">
                                    <label class="col-md-2 control-label">负责人</label>
                                    <div class="col-md-6">
                                        <select id="managers" name="managers" class="form-control" multiple="multiple">

                                        </select>
                                        <input type="hidden" id="projectAdminRelationJson"
                                               name="projectAdminRelationJson" value=""/>
                                    </div>
                                </div>
                                <div class="form-group">
                                    <label class="col-md-2 control-label">参与人</label>
                                    <div class="col-md-10">
                                        <select id="joiners" name="joiners" class="form-control" multiple="multiple">

                                        </select>
                                        <input type="hidden" id="projectAccountRelationJson"
                                               name="projectAccountRelationJson" value=""/>
                                    </div>
                                </div>
                                <div class="form-group">
                                    <label class="col-md-2 control-label">语言</label>
                                    <div class="col-md-6">
                                        <select id="programLanguage" name="programLanguage" class="form-control">
                                            <option value="java" selected="selected">Java</option>
                                            <option value="HTML">HTML</option>
                                            <%--<option value="python">Python</option>--%>
                                        </select>
                                    </div>
                                </div>

                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                                <button type="submit" id="submitButton" class="btn btn-primary">保存</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </section><!-- /.content -->
    </div><!-- /.content-wrapper -->

    <!-- footer -->
    <jsp:include page="/include/footer.jsp"/>

    <!-- control sidebar -->
    <jsp:include page="/include/control-sidebar.jsp"/>
</div><!-- ./wrapper -->

<!-- bottom js -->
<%@ include file="/include/bottom-js.jsp" %>
<script type="text/javascript">

    $('#projectManagerId').select2();
    function search() {
        var projectName = $.trim($('#searchProjectName').val());
        var projectLanguage = $.trim($('#projectLanguage').val());
        var projectManagerId = $.trim($('#projectManagerId').val());
        $('#listTable').artPaginate({
            // 获取数据的地址
            url: "/admin/project/list",
            // 显示页码的位置
            paginator: 'listPaginator',
            // 模版ID
            tpl: 'listTableTpl',
            // 请求的参数表，默认page=1, pageSize=20
            params: {
                'projectName': projectName,
                'projectManagerId': projectManagerId,
                'projectLanguage': projectLanguage
            }
        });
    }

    function addProject() {
        $('#projectId').val('0');
        $('#projectName').val('');
        $('#projectNo').val('');
        $('#projectAccountRelations').val('');
        $('#myModalLabel').html('新增项目');
        loadProject(0);
    }

    function editProject(projectId) {
        loadProject(projectId);
    }

    function loadProject(projectId) {

        $.getJSON("/admin/project/getProject", {'projectId': projectId}, function (json) {
            if (json.success) {
                var projectDetail = json.object;
                var project = projectDetail.project;
                initAllAccount(projectDetail.allAccounts);
                initJoiners(projectDetail);
                if (project && projectId > 0) {
                    $('#projectId').val(projectId);
                    $('#projectName').val(project.projectName);
                    $('#projectNo').val(project.projectNo);
                    $('#programLanguage').val(project.programLanguage);
                   // $('#managerId').val(project.managerId);
                    $('#myModalLabel').html('修改项目');
                }

                $("#joiners").select2();
                $('#managers').select2()
            } else {
                BootstrapDialog.alert(json.message);
            }
        });
    }

    function initJoiners(projectDetail) {
        var allAccounts = projectDetail.allAccounts;
        var relations = projectDetail.projectAccountRelations;
        var accountHtml = '';
        var managerHtml = '';
        for (var i in allAccounts) {
            var isJoin = checkIsJoin(relations, allAccounts[i].uid);
            if (isJoin) {
                accountHtml = accountHtml + "<option   value='" + allAccounts[i].uid + "' selected>" + allAccounts[i].realname + "</option>";
            } else {
                accountHtml = accountHtml + "<option   value='" + allAccounts[i].uid + "' >" + allAccounts[i].realname + "</option>";
            }
            var isManager = checkIsManager(relations, allAccounts[i].uid);
            if (isManager) {
                managerHtml = managerHtml + "<option   value='" + allAccounts[i].uid + "' selected>" + allAccounts[i].realname + "</option>";
            } else {
                managerHtml = managerHtml + "<option   value='" + allAccounts[i].uid + "' >" + allAccounts[i].realname + "</option>";
            }
        }
        $("#joiners").html(accountHtml);
        $('#managers').html(managerHtml);
    }

    function checkIsJoin(relations, accountId) {
        var isJoin = false;
        for (var i in relations) {
            if (relations[i].accountId == accountId && relations[i].isAdmin == 0) {
                isJoin = true;
                break;
            }
        }
        return isJoin;
    }
    function checkIsManager(relations, accountId) {
        var isManager = false;
        for (var i in relations) {
            if (relations[i].accountId == accountId && relations[i].isAdmin == 1) {
                isManager = true;
                break;
            }
        }
        return isManager;
    }

    function initAllAccount(allAccounts) {
        var accountHtml = '';
        for (var i in allAccounts) {
            accountHtml = accountHtml + "<option data-account='" + allAccounts[i].account
                + "' data-mobile='" + allAccounts[i].mobileNo +
                "' value='" + allAccounts[i].uid + "'>" + allAccounts[i].realname + "</option>";
        }
        $("#managerId").html(accountHtml);
    }

    function validateForm() {
        var message = '';
        if ($.trim($("#projectName").val()) == '') {
            message = '项目名称不能为空';
        }
        if ($.trim($("#projectNo").val()) == '') {
            message = '项目编号不能为空';
        }
        if ($("#programLanguage").val() == '') {
            message = '语言不能为空';
        }
        if ($("#managerId").val() == '') {
            message = '负责人不能为空';
        }
        if (message == '') {
            var projectAccountRelations = [];

            var joiners = $("#joiners").val();
            for (var i in joiners) {
                projectAccountRelations.push({'accountId': parseInt(joiners[i]), 'isAdmin': 0})
            }

            var managers = $("#managers").val();
            for (var i in managers) {
                projectAccountRelations.push({'accountId': parseInt(managers[i]), 'isAdmin': 1})
            }
            var relationJson = JSON.stringify(projectAccountRelations);
            $('#projectAccountRelationJson').val(relationJson);
        }
        else {
            BootstrapDialog.alert(message);
        }
        return (message == '');
    }

    $(function () {
        search();

        $('#editForm').ajaxForm({
            complete: function (xhr) {
                try {
                    eval('json=' + xhr.responseText);
                    BootstrapDialog.alert(json.message, function () {
                        if (json.success) {
                            $('#configModal').modal('hide');
                            search();
                        }
                        else {
                            $('#submitButton').removeAttr('disabled');
                        }
                    });

                } catch (e) {
                }
            }
        });
    });
</script>
</body>
</html>
