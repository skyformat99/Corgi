<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.ibeiliao.deployment.common.enums.DeployStatus" %>
<%@ page import="com.ibeiliao.deployment.common.enums.ServerDeployResult" %>
<%@ page import="com.ibeiliao.deployment.common.Constants" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <%@ include file="/include/meta.html" %>
    <title>发布详情</title>
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
                发布详情
                <small></small>
            </h1>
            <ol class="breadcrumb">
                <li><a href="/admin/deploy/list.xhtml"><i class="fa fa-dashboard"></i> 发布记录</a></li>
            </ol>
        </section>

        <!-- Main content -->
        <section class="content">
            <div class="row">
                <div class="col-md-12">
                    <div class="box">
                        <div class="box-body">
                            <div class="row" id="projectInfoDiv">

                            </div>

                            <div class="row">
                                <div class="col-sm-12">
                                    <table id="listTable" class="table table-bordered table-hover">
                                        <thead>
                                        <tr>
                                            <th>ID</th>
                                            <th>服务器</th>
                                            <th>完成时间</th>
                                            <th>结果</th>
                                            <th>操作</th>
                                        </tr>
                                        </thead>
                                        <script id="listTableTpl" type="text/html">
                                            {{each serverDeployHistories as value i}}
                                            <tr>
                                                <td>
                                                    {{value.id}}
                                                </td>
                                                <td>
                                                    {{value.serverIp}}
                                                    {{if value.serverName != ''}}
                                                        （{{value.serverName}}）
                                                    {{/if}}
                                                </td>
                                                <td>
                                                    {{if value.startupTime == null}}
                                                    -
                                                    {{/if}}
                                                    {{if value.startupTime != null}}
                                                        {{formatDateTime value.startupTime}}
                                                    {{/if}}
                                                </td>
                                                <td>
                                                    {{if value.deployStatus == <%=ServerDeployResult.WAITING_FOR_DEPLOYMENT.getValue()%>}}
                                                    等待发布
                                                    {{/if}}
                                                    {{if value.deployStatus == <%=ServerDeployResult.SUCCESS.getValue()%>}}
                                                    <span class="text-green">发布成功</span>
                                                    {{/if}}
                                                    {{if value.deployStatus == <%=ServerDeployResult.FAILURE.getValue()%>}}
                                                    <span class="text-red">发布失败</span>
                                                    {{/if}}
                                                </td>
                                                <td>
                                                    <a href="javascript:void(0)" onclick="viewLog({{value.id}})">查看日志</a>
                                                </td>
                                            </tr>
                                            {{/each}}
                                        </script>
                                        <tbody id="listTableBody">

                                        </tbody>
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
            <div class="modal fade" id="serverLogModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
                <div class="modal-dialog" role="document">
                    <div class="modal-content" style="width: 640px;">
                        <div class="modal-header">
                            <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                            <h4 class="modal-title" id="myModalLabel">发布日志</h4>
                        </div>
                        <div class="modal-body" style="height: 400px;overflow: scroll;">
                            <table id="logListTable" class="table table-bordered table-hover">
                                <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>时间</th>
                                    <th>日志</th>
                                </tr>
                                </thead>
                                <script id="logListTableTpl" type="text/html">
                                    {{each object as value i}}
                                    <tr style="font-size: 14px;">
                                        <td>
                                            {{value.logId}}
                                        </td>
                                        <td>
                                            {{formatDateTime value.createTime}}
                                        </td>
                                        <td>
                                            {{renderLogContent value.shellLog}}
                                        </td>
                                    </tr>
                                    {{/each}}
                                </script>
                                <tbody id="logListTableBody">

                                </tbody>
                            </table>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                        </div>
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
<script id="projectInfoTemplate" type="text/html">
    <div class="col-md-12">
        <label class="col-md-2 control-label text-right">项目</label>
        <div class="col-md-4">
            {{projectName}}
        </div>
        <label class="col-md-2 control-label text-right">模块</label>
        <div class="col-md-4">
            {{moduleName}}
        </div>
    </div>
    <div class="col-md-12">
        <label class="col-md-2 control-label text-right">环境</label>
        <div class="col-md-4">
            {{envName}}
        </div>
        <label class="col-md-2 control-label text-right">服务器数量</label>
        <div class="col-md-4">
            {{deployServers}}
        </div>
    </div>
    <div class="col-md-12">
        <label class="col-md-2 control-label text-right">类型</label>
        <div class="col-md-10">
            {{if isRestart == <%=Constants.TRUE%>}}
                重启服务
                <%--，<a href="/admin/deploy/view.xhtml?historyId={{rollbackToDeployId}}" target="_blank">查看上次发布</a>--%>
            {{/if}}
            {{if isRollback == <%=Constants.TRUE%>}}
            回滚版本，<a href="/admin/deploy/view.xhtml?historyId={{rollbackToDeployId}}" target="_blank">查看上次发布</a>
            {{/if}}
            {{if isRestart != <%=Constants.TRUE%> && isRollback != <%=Constants.TRUE%>}}
            发布版本
            {{/if}}
        </div>
    </div>
    <div class="col-md-12">
        <label class="col-md-2 control-label text-right">分支</label>
        <div class="col-md-10">
            {{tagName}}
        </div>
    </div>
    <div class="col-md-12">
        <label class="col-md-2 control-label text-right">内容</label>
        <div class="col-md-10">
            {{title}}
        </div>
    </div>
    <div class="col-md-12">
        <label class="col-md-2 control-label text-right">发布者</label>
        <div class="col-md-10">
            {{realName}}
        </div>
    </div>
    <div class="col-md-12">
        <label class="col-md-2 control-label text-right">创建时间</label>
        <div class="col-md-10">
            {{formatDateTime createTime}}
        </div>
    </div>
    <div class="col-md-12">
        <label class="col-md-2 control-label text-right">审核</label>
        <div class="col-md-10">
            {{if deployStatus == <%=DeployStatus.WAITING_FOR_AUDIT.getValue()%>}}
            等待审核
            {{/if}}
            {{if deployStatus != <%=DeployStatus.WAITING_FOR_AUDIT.getValue()%>}}
            审核者ID: {{auditorId}} - {{formatDateTime deployTime}}
            {{/if}}
        </div>
    </div>
    <div class="col-md-12">
        <label class="col-md-2 control-label text-right">结果</label>
        <div class="col-md-10">
            {{if deployStatus == <%=DeployStatus.WAITING_FOR_AUDIT.getValue()%>}}
            等待审核
            {{/if}}
            {{if deployStatus == <%=DeployStatus.WAITING_FOR_DEPLOYMENT.getValue()%>}}
            等待发布
            {{/if}}
            {{if deployStatus == <%=DeployStatus.AUDIT_REJECTED.getValue()%>}}
            <span class="text-red">审核未通过</span>
            {{/if}}
            {{if deployStatus == <%=DeployStatus.CANCELLED.getValue()%>}}
            已取消发布
            {{/if}}
            {{if deployStatus == <%=DeployStatus.DEPLOYED.getValue()%>}}
                {{if successCount == deployServers}}
                <span class="text-green">成功</span>
                {{/if}}
                {{if successCount != deployServers}}
                <span class="text-green">成功 {{successCount}}</span>，<span class="text-red">失败 {{deployServers - successCount}}</span>
                {{/if}}
            {{/if}}

        </div>
    </div>
</script>
<script type="text/javascript">

    $(function () {
        template.helper('renderLogContent', function (value) {
            value = value.replace(/\n/, '<br/>');
            return value;
        });

        var historyId = $.getUrlParam('historyId');
        if (historyId && historyId > 0) {
            $.getJSON("/admin/deploy/getDeployHistory", {'historyId': historyId}, function (json) {
                if (json.success) {
                    var history = json.object;
                    var htmlTxt = template('projectInfoTemplate', history);
                    $('#projectInfoDiv').html(htmlTxt);
                    htmlTxt = template('listTableTpl', history);
                    $('#listTableBody').html(htmlTxt);
                }
                else {
                    BootstrapDialog.alert(json.message);
                }
            });
        }
        else {
            BootstrapDialog.alert('请选择发布记录查看');
        }
    });

    function viewLog(id) {
        $.getJSON("/admin/deploy/getServerDeployLog", {'serverDeployId': id}, function (json) {
            if (json.success) {
                var htmlTxt = template('logListTableTpl', json);
                $('#logListTableBody').html(htmlTxt);
                $('#serverLogModal').modal('show');
            }
            else {
                BootstrapDialog.alert(json.message);
            }
        });
    }
</script>
</body>
</html>
