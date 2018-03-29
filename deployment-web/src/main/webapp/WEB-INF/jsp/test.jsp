<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <%@ include file="/include/meta.html"%>
    <title>欢迎进入贝聊平台管理系统</title>
</head>

<body class="skin-blue sidebar-mini">
<div class="wrapper">

    <!-- header -->
    <jsp:include page="/include/header.jsp"/>

    <!-- sidebar -->
    <jsp:include page="/include/sidebar.jsp" />

    <!-- Content Wrapper. Contains page content -->
    <div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <section class="content-header">
            <h1>
                Welcome
                <small>to the world</small>
            </h1>
            <ol class="breadcrumb">
                <li><a href="#"><i class="fa fa-dashboard"></i> Level</a></li>
                <li class="active">Here</li>
            </ol>
        </section>

        <!-- Main content -->
        <section class="content">


            <input type="text" name="data" id="data" />
            <button onclick="send()">发送</button>

            <div id="dataContainer" style="width: 150px; height: 200px; overflow-x:auto; overflow-y:auto;">


            </div>




        </section><!-- /.content -->
    </div><!-- /.content-wrapper -->


    <!-- footer -->
    <jsp:include page="/include/footer.jsp"/>

    <!-- control sidebar -->
    <jsp:include page="/include/control-sidebar.jsp"/>
</div><!-- ./wrapper -->

<!-- bottom js -->
<%@ include file="/include/bottom-js.jsp"%>


<script>
    var websocket;
    var url = "ws://127.0.0.1:8081/admin/websocket/shellLog.do";
    if('WebSocket' in window) {
        websocket = new WebSocket(url);

    }else if('MozWebSocket' in window){
        websocket = new MozWebSocket(url);

    }else{
        websocket = new SockJS(url);
    }

    websocket.onopen = function(event){
        console.log('websocket.open...');
    }

    websocket.onmessage = function(event){
        console.log('websocket.onmessage...');
        $('#dataContainer').append('<p>' + event.data + "</p>");
        $('#data').val('')

    }

    websocket.onerror = function(event){
        console.log('websocket.onerror....');
    }

    websocket.onclose = function(event){
        console.log('websocket.onclose....');
    }

    function send(){
        console.log('发送...')
        websocket.send($('#data').val())
    }



</script>



</body>
</html>
