<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <%@ include file="/include/meta.html" %>
    <title>按项目统计</title>
    <link href="/static/plugins/morris/morris.css" rel="stylesheet" type="text/css" />
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
                按项目统计
                <small></small>
            </h1>
            <ol class="breadcrumb">
            </ol>
        </section>

        <!-- Main content -->
        <section class="content">
            <div class="row">
                <div class="col-md-12">
                    <p class="text-left">
                        说明：统计每个项目的发布情况。未开始发布的不计算在内，stop/restart的不计算在内。
                    </p>
                </div>
            </div>
            <div class="row">
                <div class="col-xs-12">
                    <div class="box">
                        <div class="box-header">
                            <div class="col-sm-12">
                                <form class="form-inline" role="form">
                                    <select class="form-control" id="envId" name="envId" onchange="changeEnv()">
                                        <c:forEach var="env" items="${envList}">
                                            <option value="${env.envId}">${env.envName}</option>
                                        </c:forEach>
                                    </select>
                                    <div class="form-group col-md-5">
                                        <label>日期：</label>
                                        <div class="input-group" style="width:300px;">
                                            <div class="input-group-addon">
                                                <i class="fa fa-calendar"></i>
                                            </div>
                                            <input type="text" class="form-control pull-right active" name="searchDateRange"
                                                   id="searchDateRange"/>
                                        </div>
                                    </div>
                                    <button type="button" class="btn btn-info btn-flat" onclick="query()">查询
                                    </button>
                                </form>
                            </div>
                        </div><!-- /.box-header -->
                        <div class="box-body chart-responsive">
                            <div class="chart" id="bar-chart" style="height: 300px;"></div>
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
<script type="text/javascript" src="/static/js/raphael-min.js"></script>
<script type="text/javascript" src="/static/plugins/morris/morris.min.js"></script>
<script type="text/javascript">
    function query() {
        var searchDateRange = $.trim($('#searchDateRange').val());
        if (searchDateRange) {
            var envId = $('#envId').val();
            var separator = $.datePickerSperator;
            var dateArray = searchDateRange.split(separator);
            $.getJSON('/admin/stat/queryStatProject', {
                'envId' : envId,
                'start' : dateArray[0],
                'end'   : dateArray[1]
            }, function (json) {
                if (json.success) {
                    // CHART
                    $('#bar-chart').html('');
                    var bar = new Morris.Bar({
                        element: 'bar-chart',
                        resize: true,
                        data: json.object,
                        barColors: ['#3c8dbc', '#00a65a', '#f56954'],
                        xkey: 'projectName',
                        ykeys: ['deployTimes', 'success', 'failure'],
                        labels: ['发布次数', '成功', '失败'],
                        hideHover: 'auto'
                    });

                } else {
                    BootstrapDialog.alert(json.message);
                }
            });
        } else {
            BootstrapDialog.alert('请选择日期范围');
        }
    }

    $(function () {
        // 选择时间范围控件
        $.datePicker('#searchDateRange', true);

        query();
    });
</script>
</body>
</html>
