<%@ page import="com.ibeiliao.deployment.admin.enums.ServerStrategy" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <%@ include file="/include/meta.html" %>
    <title>创建上线单</title>
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
                创建上线单
                <small></small>
            </h1>
            <ol class="breadcrumb">
                <li><a href="/admin/deploy/list.xhtml"><i class="fa fa-dashboard"></i> 发布列表</a></li>
            </ol>
        </section>

        <!-- Main content -->
        <section class="content">
            <div class="row">
                <section class="col-md-4">
                    <div class="box box-primary">
                        <div class="box-body table-responsive no-padding" style="overflow:hidden;">
                            <div class="row">
                                <div class="col-md-6">
                                    <ul class="nav nav-pills nav-stacked" style="border-right:1px solid #eeeeee;">
                                        <c:forEach var="project" items="${projects}">
                                            <li id="project${project.projectId}"
                                                class="${projectId == project.projectId ? "active" : ""}"><a href="#"
                                                                                                             onclick="chooseProject(${project.projectId})">${project.projectName}
                                                    ${projectId == project.projectId ? "<i class=\"fa fa-fw fa-check-square-o pull-right\"></i>":""}
                                            </a></li>
                                        </c:forEach>
                                    </ul>
                                </div>
                                <div class="col-md-6">
                                    <ul class="nav nav-pills nav-stacked" id="moduleListTable"
                                        style="border-left:1px solid #eeeeee;">
                                        <c:forEach var="module" items="${projectModules}">
                                            <li id="module${module.moduleId}"
                                                class="${moduleId == module.moduleId ? "active" : ""}"><a href="#"
                                                                                                          onclick="chooseModule(${module.moduleId})">${module.moduleNameZh}
                                                    ${moduleId == module.moduleId ? "<i class=\"fa fa-fw fa-check-square-o pull-right\"></i>":""}
                                            </a></li>
                                        </c:forEach>
                                    </ul>
                                </div>
                            </div>
                        </div><!-- /.box-body -->
                    </div><!-- /.box -->
                </section>
                <section class="col-md-8" style="padding-left: 0px;">
                    <div class="box box-primary">
                        <div class="box-body">
                            <form id="editForm" class="form-horizontal" method="post" modelattribute="order"
                                  onsubmit="return validateForm(this)" action="/admin/deploy/create.do">
                                <div class="form-group">
                                    <label class="col-md-2 control-label">环境</label>
                                    <div class="col-md-8">
                                        <%--<select class="form-control" id="envId" name="envId" onchange="changeEnv()">--%>
                                        <%--<c:forEach var="env" items="${envList}">--%>
                                        <%--<option value="${env.envId}">${env.envName}</option>--%>
                                        <%--</c:forEach>--%>
                                        <%--</select>--%>
                                        <c:forEach var="env" items="${envList}" varStatus="status">
                                            <label><input type="radio" name="envId" value="${env.envId}"
                                                          onclick="changeEnv()"
                                                          <c:if test="${status.index == 0}">checked</c:if> />${env.envName}
                                            </label> &nbsp;
                                        </c:forEach>
                                        <input type="hidden" id="projectId" name="projectId" value="${projectId}"/>
                                        <input type="hidden" id="moduleId" name="moduleId" value="${moduleId}"/>
                                        <input type="hidden" id="versionNo" name="versionNo" value=""/>
                                    </div>
                                </div>
                                <div class="form-group">
                                    <label class="col-md-2 control-label" id="repoType"></label>
                                    <div class="col-md-10 text-left" id="divRepoUrl">
                                        ${repoUrl}
                                    </div>
                                </div>
                                <div class="form-group">
                                    <label class="col-md-2 control-label">分支</label>
                                    <div class="col-md-5">
                                        <input type="text" class="form-control" name="tagName" id="tagName"
                                               maxlength="60"/>
                                    </div>
                                    <div class="col-md-5 input-group">
                                        <select class="form-control" id="tagList" name="tagList" onchange="selectTag()">
                                        </select>
                                        <span class="input-group-btn">
										    <button id="refreshRepoBtn" type="button" class="btn btn-info btn-flat"
                                                    onclick="refreshRepository()"><i
                                                    class="fa fa-fw fa-refresh"></i></button>
									    </span>
                                    </div>
                                </div>
                                <div class="form-group">
                                    <label class="col-md-2 control-label">内容</label>
                                    <div class="col-md-9">
                                        <input type="text" class="form-control" name="title" id="title" maxlength="80"/>
                                    </div>
                                </div>
                                <div class="form-group">
                                    <label class="col-md-2 control-label">分批发布服务器</label>
                                    <div class="col-md-2">
                                        <select class="form-control" id="concurrentServerPercentage"
                                                name="concurrentServerPercentage">
                                            <option value="35">35%</option>
                                            <option value="10">10%</option>
                                            <option value="20">20%</option>
                                            <option value="50">50%</option>
                                            <option value="5">5%</option>
                                            <option value="1">1%</option>
                                        </select>
                                    </div>
                                    <label class="col-md-2 control-label">批次发布间隔</label>
                                    <div class="col-md-2">
                                        <select class="form-control" id="deployTimeInterval" name="deployTimeInterval">
                                            <option value="5">5秒</option>
                                            <option value="20">20秒</option>
                                            <option value="30">30秒</option>
                                            <option value="10">10秒</option>
                                            <option value="45">45秒</option>
                                            <option value="60">60秒</option>
                                            <option value="90">90秒</option>
                                            <option value="120">120秒</option>
                                            <option value="1">1秒</option>
                                        </select>
                                    </div>
                                </div>

                                <div class="form-group">
                                    <label class="col-md-2 control-label">服务器发布异常处理：</label>
                                    <div class="col-md-8">
                                        <select class="form-control" id="serverStrategy" name="serverStrategy">
                                            <c:forEach var="item" items="<%= ServerStrategy.values() %>">
                                                <option value="${item.value}">${item.name}</option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                </div>

                                <div class="form-group">
                                    <label class="col-md-2 control-label">强制编译</label>
                                    <div class="col-md-2">
                                        <label><input type="radio" name="forceCompile" value="1"/>是</label> &nbsp;
                                        <label><input type="radio" name="forceCompile" value="0" checked/>否</label>
                                    </div>
                                    <div class="col-md-8 text-danger">
                                        trunk、branch每次都会重新编译，tag默认不会强制编译
                                    </div>
                                </div>
                                <div class="form-group">
                                    <label class="col-md-2 control-label text-warning">发布提示</label>
                                    <div class="col-md-10 text-warning">
                                        灰度发布的项目，需要创建<b class="text-danger">2次</b>上线单才能够完成发布（<strong>不要漏了第二批</strong>）。
                                        <br/>
                                        第一批发完后，建议检测日志来确认是否项目已经发布成功，再来发第二批服务器。
                                    </div>
                                </div>

                                <div class="form-group">
                                    <div class="col-md-12">
                                        <div class="nav-tabs-custom">
                                            <ul class="nav nav-tabs pull-left ui-sortable-handle" id="serverGroupTab">
                                            </ul>
                                        </div>
                                        <div class="tab-content no-padding" id="serverGroupTabContent">
                                        </div>
                                    </div>
                                </div>
                                <div class="form-group">
                                    <div class="col-md-offset-2">
                                        <button class="btn btn-primary" type="submit" id="submitButton">创建上线单</button>
                                    </div>
                                </div>
                            </form>
                        </div><!-- /.box-body -->
                    </div><!-- /.box -->
                </section>
            </div><!-- /.row -->
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
    var allModules = ${allModules};
    var moduleServerGroups = [];
    var moduleServers = [];
    var envMap = {};
    <c:forEach var="env" items="${envList}">
    envMap[${env.envId}] = '${env.envName}';
    </c:forEach>
    var loadingReposotory = false;

    function chooseProject(projectId) {
        var oldProjectId = $('#projectId').val();
        var content = '';
        var count = 0;
        for (var i = 0; i < allModules.length; i++) {
            var module = allModules[i];
            if (module.projectId == projectId) {
                var checked = '';
                var icon = '';
                if (count == 0) {
                    checked = 'active';
                    icon = '<i class="fa fa-fw fa-check-square-o pull-right"></i>';
                    chooseModule(module.moduleId);
                }
                content += '<li class="' + checked + '" id="module' + module.moduleId + '"><a href="#" onclick="chooseModule(' + module.moduleId + ')">' + module.moduleNameZh + icon + '</a></li>';
                count++;
            }
        }
        $('#projectId').val(projectId);
        $('#moduleListTable').html(content);
        if (oldProjectId) {
            $('#project' + oldProjectId).removeClass('active');
            $('#project' + oldProjectId).find('i').remove();
        }
        $('#project' + projectId).addClass('active');
        $('#project' + projectId).children('a').append('<i class="fa fa-fw fa-check-square-o pull-right"></i>');
    }

    function chooseModule(moduleId) {
        var oldModuleId = $('#moduleId').val();
        for (var i = 0; i < allModules.length; i++) {
            var module = allModules[i];
            if (module.moduleId == moduleId) {
                $('#divRepoUrl').html(module.repoUrl);
                if (module.repoType == 1) {
                    $('#repoType').html("SVN");
                } else {
                    $('#repoType').html("GIT");
                }

                loadModuleServers(moduleId);
                break;
            }
        }
        $('#moduleId').val(moduleId);
        if (oldModuleId) {
            $('#module' + oldModuleId).removeClass('active');
            $('#module' + oldModuleId).find('i').remove();
        }
        $('#module' + moduleId).addClass('active');
        $('#module' + moduleId).children('a').append('<i class="fa fa-fw fa-check-square-o pull-right"></i>');
    }

    function changeEnv() {
        var moduleId = $('#moduleId').val();
        if (moduleId) {
            loadModuleServers(moduleId);
        }
    }

    function loadModuleServers(moduleId) {
        var envId = $('input[name=envId]:checked').val();
        $.getJSON("/admin/deploy/queryModuleServer", {'moduleId': moduleId, 'envId': envId}, function (json) {
            if (json.success) {
                moduleServerGroups = json.object.groups;
                moduleServers = json.object.servers;
                renderGroups();
            }
            else {
                BootstrapDialog.alert(json.message);
            }
        });
    }

    function renderGroups() {
        if (moduleServerGroups && moduleServerGroups.length && moduleServerGroups.length > 0) {
            var groupHtml = '';
            var groupServerHtml = '';
            var envId = $('input[name=envId]:checked').val();
            for (var i = 0; i < moduleServerGroups.length; i++) {
                var group = moduleServerGroups[i];
                if (group['envId'] == envId) {
                    var envName = envMap[group['envId']];
                    var active = (i == 0 ? 'active' : '');
                    var groupId = group['groupId'];
                    groupHtml += '<li class="' + active + '"><a href="#server-group-' + groupId + '" data-toggle="tab">' + group['groupName'] + '<br/><span style="font-size: 9px;">' + envName + '</span></a></li>';
                    groupServerHtml += '<div class="chart tab-pane ' + active + '" id="server-group-' + groupId + '" style="position: relative; height: 216px;">' + generateGroupServerHtml(groupId) + '</div>';
                }
            }
            $('#serverGroupTab').html(groupHtml);
            $('#serverGroupTabContent').html(groupServerHtml);
            refreshRepository();
        }
        else {
            $('#serverGroupTab').html('');
            $('#serverGroupTabContent').html('');
            var tagList = $('#tagList');
            tagList.empty();
        }
    }

    function generateGroupServerHtml(groupId) {
        var serverHtml = '';
        for (var i = 0; i < moduleServers.length; i++) {
            var server = moduleServers[i];
            if (server['groupId'] == groupId) {
                serverHtml += '<div class="col-md-4"><label><input type="checkbox" name="serverId" style="margin: 4px 0 0;" value="' + server['serverId'] + '"/><span style="font-weight: normal;font-size: 16px;">' + server['ip'] + ' (' + server['serverName'] + ')</span></label></div>';
            }
        }
        return serverHtml;
    }

    // 校验参数
    function validateForm(form) {
        var tagName = $.trim($('#tagName').val());
        var title = $.trim($('#title').val());
        var serverId = $('input[name=serverId]:checked');
        var message = '';
        if (tagName == '') {
            message = '请输入或选择 tag/分支';
        } else if (title == '') {
            message = '请输入发布内容';
        } else if (!(serverId && serverId.length > 0)) {
            message = '请选择要发布的服务器';
        }
        // 检测发布的服务器组，每次只能发一组
        var tbs = $("#serverGroupTabContent").children(".tab-pane");
        var selectGroupNum = 0;
        tbs.each(function (e) {
            var selectServerNum = $(this).find('input[name=serverId]:checked').length;
            if (selectServerNum > 0) {
                selectGroupNum++;
            }
        });
        if (selectGroupNum > 1) {
            message = '每次只能发布一组服务器';
        }

        if (message != '') {
            BootstrapDialog.alert(message);
        }
        else {
            $('#submitButton').prop('disabled', 'true');
        }
        return (message == '');
    }

    function refreshRepository() {
        if (!loadingReposotory) {
            loadingReposotory = true;
            $('#refreshRepoBtn').html('<i class="fa fa-fw fa-spinner"></i>');
            $.getJSON("/admin/deploy/listRepository", {'moduleId': $('#moduleId').val()}, function (json) {
                if (json.success) {
                    var tagList = $('#tagList');
                    tagList.empty();
                    tagList.append('<option value="0"></option>');
                    for (var i in json.object) {
                        var row = json.object[i];
                        tagList.append('<option value="' + row['version'] + '">' + row['url'] + '</option>')
                    }
                }
                loadingReposotory = false;
                $('#refreshRepoBtn').html('<i class="fa fa-fw fa-refresh"></i>');
            });
        }
    }

    function selectTag() {
        var tagName = $('#tagList').find("option:selected").text();
        if (tagName) {
            $('#tagName').val(tagName);
            $('#versionNo').val($('#tagList').val());
        }
    }

    $(function () {
        $('#editForm').ajaxForm({
            complete: function (xhr) {
                try {
                    eval('json=' + xhr.responseText);
                    BootstrapDialog.alert(json.message, function () {
                        if (json.success) {
                            window.location.href = "/admin/deploy/list.xhtml";
                        }
                        else {
                            $('#submitButton').removeAttr('disabled');
                        }
                    });

                } catch (e) {
                }
            }
        });

        var moduleId = ${moduleId};
        if (moduleId > 0) {
            loadModuleServers(moduleId);

            $('#moduleId').val(moduleId);
            refreshRepository();
        }
    });
</script>
</body>
</html>
