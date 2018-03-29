<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.ibeiliao.deployment.admin.websocket.request.WebSocketRequestType" %>
<%@ page import="com.ibeiliao.deployment.cfg.EncryptionPropertyPlaceholderConfigurer" %>
<%@ page import="com.ibeiliao.deployment.admin.enums.ServerStrategy" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <%@ include file="/include/meta.html" %>
    <title>发布</title>
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
            <h1 id="deployTitle">
                发布
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
                                <div class="col-md-12">
                                    <div class="container-fluid text-left" id="stepLogs">
                                        <c:if test="${history.isRollback == 0}">
                                            <p>请点击“开始发布”按钮 --&gt;&gt;</p>
                                        </c:if>
                                        <c:if test="${history.isRollback == 1}">
                                            <p>请点击“开始回滚”按钮 --&gt;&gt;</p>
                                        </c:if>

                                    </div>
                                </div>

                            </div>
                        </div><!-- /.box-body -->
                    </div><!-- /.box -->
                </section>
                <section class="col-md-8" style="padding-left: 0px;">
                    <div class="box box-primary">
                        <div class="box-body">
                            <div class="row form-group">
                                <label class="col-md-2 control-label text-right">发布者</label>
                                <div class="col-md-2">
                                    ${history.realName}
                                </div>
                                <div class="col-md-8">
                                    <c:if test="${canDeploy}">
                                        <button id="deployButton" class="btn btn-warning pull-right" type="button"
                                                onclick="startDeploy(${history.historyId})">
                                        <c:if test="${history.isRollback == 0}">
                                            开始发布
                                        </c:if>
                                        <c:if test="${history.isRollback == 1}">
                                            开始回滚
                                        </c:if>
                                        </button>
                                    </c:if>
                                </div>
                            </div>
                            <div class="row form-group">
                                <label class="col-md-2 control-label text-right">项目&模块</label>
                                <div class="col-md-10">
                                    ${history.projectName} , ${history.moduleName}
                                </div>
                            </div>
                            <div class="row form-group">
                                <label class="col-md-2 control-label text-right">Tag</label>
                                <div class="col-md-10">
                                    ${history.tagName}
                                </div>
                            </div>
                            <div class="row form-group">
                                <label class="col-md-2 control-label text-right">发布内容</label>
                                <div class="col-md-10">
                                    ${history.title}
                                </div>
                            </div>
                            <div class="row form-group">
                                <label class="col-md-2 control-label text-right">服务器发布异常处理</label>
                                <div class="col-md-10">
                                    <c:forEach var="item" items="<%= ServerStrategy.values() %>">
                                       <c:if test="${history.serverStrategy == item.value}">
                                           ${item.name}
                                       </c:if>
                                    </c:forEach>
                                </div>
                            </div>
                            <div class="row form-group">
                                <label class="col-md-2 control-label text-right">服务器组</label>
                                <div class="col-md-2">

                                </div>
                            </div>
                            <div class="row form-group">
                                <div class="col-md-12">
                                    <div class="nav-tabs-custom">
                                        <ul class="nav nav-tabs pull-left ui-sortable-handle" id="serverTab">
                                            <c:forEach var="server" items="${history.serverDeployHistories}"
                                                       varStatus="status">
                                                <li class="<c:if test="${status.index == 0}">active</c:if>"><a
                                                        href="#server-log-${server.id}"
                                                        data-toggle="tab">${server.serverIp}<br/><span
                                                        style="font-size: 9px;">${server.serverName}</span></a></li>
                                            </c:forEach>
                                        </ul>
                                        <div class="tab-content no-padding" id="serverTabContent">
                                            <c:forEach var="server" items="${history.serverDeployHistories}"
                                                       varStatus="status">
                                                <div data-ip="${server.serverIp}"
                                                     class="chart tab-pane <c:if test="${status.index == 0}">active</c:if>"
                                                     id="server-log-${server.id}"
                                                     style="position: relative; height: 360px;">
                                                    <c:choose>
                                                        <c:when test="${empty server.serverDeployLogs}">
                                                             <p>${server.serverIp} 等待发布</p>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <c:forEach var="log" items="${server.serverDeployLogs}" >
                                                                 <p>${log.shellLog}</p>
                                                            </c:forEach>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </div>
                                            </c:forEach>
                                        </div>
                                    </div>
                                </div>
                            </div>

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
<script src="//cdn.bootcss.com/sockjs-client/1.1.2/sockjs.js"></script>
<script type="text/javascript">

    var websocket;
    var timeInterval = null;

    $(function () {
        var url = "<%=EncryptionPropertyPlaceholderConfigurer.getConfig("websocket.url")%>";
        if ('WebSocket' in window) {
            websocket = new WebSocket(url);

        } else if ('MozWebSocket' in window) {
            websocket = new MozWebSocket(url);
        }

        if (websocket) {
            websocket.onopen = function (event) {
                console.log('websocket.open...');
                timeInterval = window.setInterval("sendHeartbeat()", 15000);
            };

            websocket.onmessage = function (event) {
                console.log('websocket.onmessage ... ' + event.data);
                eval('msg=' + event.data);
                if (msg.type == '<%=WebSocketRequestType.USER_IDENTITY.getName()%>') {
                    var data = {};
                    data['type'] = '<%=WebSocketRequestType.USER_IDENTITY.getName()%>';
                    data['cookies'] = document.cookie;
                    websocket.send(JSON.stringify(data));
                    console.log('login request sent ...');
                }
                else if (msg.type == '<%=WebSocketRequestType.USER_IDENTITY_SUCCESS.getName()%>') {
                    var data = {};
                    data['type'] = '<%=WebSocketRequestType.DEPLOY_SHELL_LOG.getName()%>';
                    data['serverDeployIdList'] = [
                        <c:forEach var="server" items="${history.serverDeployHistories}" varStatus="status">
                        <c:if test="${status.index > 0}">, </c:if>${server.id}
                        </c:forEach>
                    ];
                    websocket.send(JSON.stringify(data));
                    console.log('log request sent ...');
                }
                else if (msg.type == '<%=WebSocketRequestType.DEPLOY_SHELL_LOG.getName()%>') {
                    //displayServerLog(msg);
                    displayStepLog(msg)
                }
                else if (msg.type == '<%=WebSocketRequestType.DEPLOY_STEP_LOG.getName()%>') {
                    displayStepLog(msg);
                }

            };

            websocket.onerror = function (event) {
                console.log('websocket.onerror .... ' + event.code);
            };

            websocket.onclose = function (event) {
                console.log('websocket.onclose....');
                if (timeInterval) {
                    window.clearInterval(timeInterval);
                    timeInterval = null;
                }
            };
        }

    });

    var currentServerLogContent = "";
    function displayStepLog(msg) {
        var stepLogs = msg.stepLogs;
        if (stepLogs && stepLogs.length > 0) {
            $('#stepLogs').html(stepLogs.join('<p>'));
        }
        // 每台服务器的日志显示
        var serverDeployLogs = msg.serverDeployLogs;
        if (serverDeployLogs && serverDeployLogs.length > 0) {
            // 每次都先清空 tab内容,后补充
            $("#serverTabContent").children("div").each(function () {
                $(this).html("");
            });
            for (var i in serverDeployLogs) {
                var split = serverDeployLogs[i].split("\n");
                for (var j in split) {
                    if (split[j] != "") {
                        var split2 = split[j].split(" ");
                        var serverIp = split2[0];
                        $('div[data-ip="' + serverIp + '"]').append("<p>" + split[j].split(serverIp)[1].trim() + "</p>");
                    }
                }
            }
            // 填充完后,把空的补全为等待发布
            $("#serverTabContent").children("div").each(function () {
                var text = $(this).text();
                if (text.trim() == '') {
                    var ip = $(this).attr("data-ip");
                    $(this).html("<p>" + ip + " 等待发布</p>");
                }
            });
        }
    }

    function displayServerLog(msg) {
        /*var serverLogs = msg.serverLogs;
        if (serverLogs) {
            for (var i = 0; i < serverLogs.length; i++) {
                var log = serverLogs[i];
                $('#server-log-' + log.serverDeployId).append(log.log + '<br/>');
            }
        }*/
        if (msg.stepLogs) {
            displayStepLog(msg);
        }
    }

    function startDeploy(historyId) {
        $('#deployButton').prop('disabled', 'true');
        $.post('/admin/deploy/startDeploy.do', {
            'historyId': historyId
        }, function (json) {

            if (!json.success) {
                BootstrapDialog.alert(json.message);
                $('#deployButton').prop('disabled', 'false');
            }
        }, 'json');
    }

    function sendHeartbeat() {
        var data = {};
        data['type'] = '<%=WebSocketRequestType.HEARTBEAT.getName()%>';
        websocket.send(JSON.stringify(data));
//        console.log('heatbeat sent ...');
    }
</script>
</body>
</html>
