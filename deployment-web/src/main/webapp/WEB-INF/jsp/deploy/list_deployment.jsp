<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.ibeiliao.deployment.common.enums.DeployStatus" %>
<%@ page import="com.ibeiliao.deployment.common.enums.DeployResult" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <%@ include file="/include/meta.html" %>
    <title>发布记录</title>
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
                发布记录
                <small></small>
            </h1>
            <ol class="breadcrumb">
                <li><a href="/admin/deploy/create.xhtml"><i class="fa fa-dashboard"></i> 创建上线单</a></li>
            </ol>
        </section>

        <!-- Main content -->
        <section class="content">
            <div class="row">
                <div class="col-md-12">
                    <div class="box">
                        <div class="box-header">
                            <label class="col-md-1 control-label text-right">环境</label>
                            <div class="col-md-2">
                                <div class="input-group input-group-sm">
                                    <select class="form-control" id="envId" name="envId">
                                        <option value="0">All</option>
                                        <c:forEach var="env" items="${envList}">
                                            <option value="${env.envId}">${env.envName}</option>
                                        </c:forEach>
                                    </select>

                                </div>
                            </div>
                            <div class="col-md-4">
                                <label class="col-md-4 control-label">项目</label>
                                <div class="col-md-6">
                                    <select id="projectId" name="projectId" class="form-control">
                                        <option value="0">全部</option>
                                        <c:forEach var="project" items="${projects}">
                                            <option value="${project.projectId}">${project.projectName}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                            </div>
                            <div class="col-md-4">
                                <label class="col-md-4 control-label">模块</label>
                                <div class="col-md-6">
                                    <select id="moduleId" name="moduleId" class="form-control">
                                        <option value="0">全部</option>
                                    </select>
                                </div>
                            </div>
                            <div class="col-md-1">
                               <span class="input-group-btn">
										<button type="button" class="btn btn-info btn-flat"
                                                onclick="search()">Go!</button>
									</span>
                            </div>
                        </div><!-- /.box-header -->
                        <div class="box-body">
                            <div class="row">
                                <div class="col-lg-12">
                                    <table id="listTable" class="table table-bordered table-hover"
                                           style="font-size:14px;">
                                        <thead>
                                        <tr>
                                            <th>ID</th>
                                            <th>项目/模块</th>
                                            <th>环境</th>
                                            <th>发布内容</th>
                                            <th>分支</th>
                                            <th>commit号</th>
                                            <th>发布者</th>
                                            <th>创建时间</th>
                                            <th>状态/操作</th>
                                        </tr>
                                        </thead>
                                        <script id="listTableTpl" type="text/html">
                                            {{each object as value i}}
                                            <tr>
                                                <td>
                                                    <a href="/admin/deploy/deploy.xhtml?historyId={{value.historyId}}">
                                                        {{value.historyId}}
                                                    </a>
                                                </td>
                                                <td>{{value.projectName}}/{{renderShortModuleName value.moduleName}}
                                                </td>
                                                <td>{{value.envName}}</td>
                                                <td>
                                                    <a href="/admin/deploy/view.xhtml?historyId={{value.historyId}}">
                                                        {{value.title}}
                                                    </a>
                                                </td>
                                                <td>
                                                    {{value.tagName}}
                                                </td>
                                                <td>{{value.versionNo}}</td>
                                                <td>{{value.realName}}</td>
                                                <td>{{formatDateTime value.createTime}}</td>
                                                <td>
                                                    {{if value.deployStatus
                                                    == <%=DeployStatus.WAITING_FOR_AUDIT.getValue()%>}}
                                                    {{if value.canAudit}}
                                                    <a class="btn btn-sm btn-primary" href="javascript:void(0)"
                                                       onclick="audit({{value.historyId}})">审核</a>
                                                    |
                                                    <a class="btn btn-sm btn-primary" href="javascript:void(0)"
                                                       onclick="reject({{value.historyId}})">拒绝</a>
                                                    {{/if}}
                                                    {{if !value.canAudit}}
                                                    等待审核
                                                    {{/if}}
                                                    {{if value.accountId == ${accountId}}}
                                                    <a class="btn btn-sm btn-primary" href="javascript:void(0)"
                                                       onclick="cancelDeploy({{value.historyId}})">
                                                        {{if value.isRollback == 0}}
                                                        取消发布
                                                        {{/if}}
                                                        {{if value.isRollback == 1}}
                                                        取消回滚
                                                        {{/if}}

                                                    </a>
                                                    {{/if}}
                                                    {{/if}}
                                                    {{if value.deployStatus
                                                    == <%=DeployStatus.WAITING_FOR_DEPLOYMENT.getValue()%> &&
                                                    value.isRestart == 0}}
                                                    {{if value.isRollback == 0}}
                                                    等待发布 |
                                                    {{/if}}
                                                    {{if value.isRollback == 1}}
                                                    等待回滚 |
                                                    {{/if}}
                                                    <a class="btn btn-sm btn-success"
                                                       href="/admin/deploy/deploy.xhtml?historyId={{value.historyId}}">
                                                        {{if value.isRollback == 0}}
                                                        发布
                                                        {{/if}}
                                                        {{if value.isRollback == 1}}
                                                        回滚
                                                        {{/if}}
                                                    </a>
                                                    {{if value.accountId == ${accountId}}}
                                                    | <a class="btn btn-sm btn-primary" href="javascript:void(0)"
                                                         onclick="cancelDeploy({{value.historyId}})">
                                                    {{if value.isRollback == 0}}
                                                    取消发布
                                                    {{/if}}
                                                    {{if value.isRollback == 1}}
                                                    取消回滚
                                                    {{/if}}

                                                </a>
                                                    {{/if}}
                                                    {{/if}}
                                                    {{if value.deployStatus
                                                    == <%=DeployStatus.AUDIT_REJECTED.getValue()%>}}
                                                    审核未通过
                                                    {{/if}}
                                                    {{if value.deployStatus == <%=DeployStatus.CANCELLED.getValue()%>}}
                                                    {{if value.isRollback == 0}}
                                                    发布已取消 |
                                                    {{/if}}
                                                    {{if value.isRollback == 1}}
                                                    回滚已取消 |
                                                    {{/if}}

                                                    {{/if}}
                                                    {{if value.deployStatus == <%=DeployStatus.DEPLOYED.getValue()%>}}
                                                    {{if value.result == <%=DeployResult.SUCCESS.getValue()%>}}
                                                    <span class="text-green">
                                                             {{if value.isRollback == 0}}
                                                                发布成功
                                                             {{/if}}
                                                             {{if value.isRollback == 1}}
                                                                回滚成功
                                                             {{/if}}
                                                        </span>
                                                    {{if value.isRestart == 0 && value.isRollback != 1}}
                                                    | <a class="btn btn-sm btn-primary" href="javascript:void(0)"
                                                         onclick="rollBack({{value.historyId}})" title="回滚到这个版本">回滚</a>
                                                    {{/if}}
                                                    {{/if}}
                                                    {{if value.result == <%=DeployResult.PARTIAL_SUCCESS.getValue()%>}}
                                                    <span class="text-warning">部分成功</span>
                                                    <%--| <a class="btn btn-sm btn-primary" href="javascript:void(0)" onclick="">再发布失败的服务器</a>--%>
                                                    {{/if}}
                                                    {{if value.result == <%=DeployResult.FAILURE.getValue()%>}}
                                                    <span class="text-danger">
                                                            {{if value.isRollback == 0}}
                                                                发布失败
                                                             {{/if}}
                                                             {{if value.isRollback == 1}}
                                                                回滚失败
                                                             {{/if}}

                                                        </span>
                                                    {{/if}}
                                                    {{/if}}
                                                    {{if value.deployStatus == <%=DeployStatus.DEPLOYING.getValue()%>}}
                                                    {{if value.isRollback == 0}}
                                                    正在发布 |
                                                    {{/if}}
                                                    {{if value.isRollback == 1}}
                                                    正在回滚 |
                                                    {{/if}}
                                                    {{/if}}
                                                </td>
                                            </tr>
                                            {{/each}}
                                        </script>
                                    </table>

                                </div>
                            </div>
                            <div class="row col-md-12" id="listPaginator">
                            </div>
                        </div><!-- /.box-body -->
                    </div><!-- /.box -->
                </div><!-- /.col -->
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
    $("#projectId").select2();

    // 初始化选择模块
    var projectInfo = ${projectJson};

    $('#projectId').change(function () {
        // 是0 全部的时候，清空
        var projectId = $('#projectId').val();
        if (projectId == 0) {
            $("#moduleId").html("<option value='0'>全部</option>");
        } else {
            for (var i = 0, length = projectInfo.length; i < length; i++) {
                if (projectInfo[i].projectId==projectId) {
                    var modules=projectInfo[i].projectModules;
                    var moduleHtml="<option value='0'>全部</option>";
                    for (var j = 0, mLength = modules.length; j < mLength; j++) {
                        moduleHtml=moduleHtml + "<option value='" + modules[j].moduleId +"'>" + modules[j].moduleNameZh + "</option>"
                    }
                    $("#moduleId").html(moduleHtml);
                    break;
                }
            }
        }
        // 非0 则将遍历获取
    });

    function search() {
        var envId = $('#envId').val();
        var projectId = $('#projectId').val();
        var moduleId = $('#moduleId').val();
        $('#listTable').artPaginate({
            // 获取数据的地址
            url: "/admin/deploy/list",
            // 显示页码的位置
            paginator: 'listPaginator',
            // 模版ID
            tpl: 'listTableTpl',
            // 请求的参数表，默认page=1, pageSize=20
            params: {'envId': envId, 'projectId': projectId, 'moduleId':moduleId}
        });
    }

    // 审核
    function audit(historyId) {
        BootstrapDialog.confirm('确认审核通过？', function (result) {
            if (result) {
                $.post('/admin/deploy/audit.do', {
                    historyId: historyId
                }, function (json) {
                    if (json.success) {
                        BootstrapDialog.alert(json.message, function () {
                            search();
                        });
                    } else {
                        BootstrapDialog.alert(json.message);
                    }
                }, 'json');
            }
        });
    }

    function cancelDeploy(historyId) {
        BootstrapDialog.confirm('确认取消发布？', function (result) {
            if (result) {
                $.post('/admin/deploy/cancel.do', {
                    historyId: historyId
                }, function (json) {
                    if (json.success) {
                        BootstrapDialog.alert(json.message, function () {
                            search();
                        });
                    } else {
                        BootstrapDialog.alert(json.message);
                    }
                }, 'json');
            }
        });

    }

    function reject(historyId) {
        BootstrapDialog.confirm('确认拒绝发布？', function (result) {
            if (result) {
                $.post('/admin/deploy/reject.do', {
                    historyId: historyId
                }, function (json) {
                    if (json.success) {
                        BootstrapDialog.alert(json.message, function () {
                            search();
                        });
                    } else {
                        BootstrapDialog.alert(json.message);
                    }
                }, 'json');
            }
        });
    }

    function rollBack(historyId) {
        BootstrapDialog.confirm('确认回滚到这个版本？', function (result) {
            if (result) {
                $.post('/admin/deploy/rollBack.do', {
                    historyId: historyId
                }, function (json) {
                    if (json.success) {
                        BootstrapDialog.alert(json.message, function () {
                            search();
                        });
                    } else {
                        BootstrapDialog.alert(json.message);
                    }
                }, 'json');
            }
        });
    }

    function loadDeployingNum() {
        $.getJSON('/admin/deploy/getDeployingNum', {}, function (json) {
            if (json.success) {
                $('#infoDiv').html('<p class="text-right">正在发布的数量：' + json.object + '</p>');
            }
        });
    }

    $(function () {
        search();

        loadDeployingNum();

        // 显示短的 moduleName，防止 moduleName太长影响界面美观
        template.helper('renderShortModuleName', function (content) {
            var pos = content.lastIndexOf("/");
            if (pos > 0) {
                return content.substring(pos + 1);
            }
            return content;
        });
    });
</script>
</body>
</html>
