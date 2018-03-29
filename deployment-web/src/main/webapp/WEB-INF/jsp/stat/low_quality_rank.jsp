<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <%@ include file="/include/meta.html" %>
    <title>低质量发布统计</title>
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
                低质量发布统计
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
                        说明：统计低质量<b>模块</b>的发布情况，只统计生产环境。每天发布次数 <b>&gt;= 2</b> 的算低质量。
                    </p>
                </div>
            </div>
            <div class="row">
                <div class="col-xs-12">
                    <div class="box">
                        <div class="box-header">
                            <div class="col-sm-12">
                                <form class="form-inline" role="form">
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
            var separator = $.datePickerSperator;
            var dateArray = searchDateRange.split(separator);
            $.getJSON('/admin/stat/queryLowQualityRank', {
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
                        barColors: ['#3c8dbc'],
                        xkey: 'moduleName',
                        ykeys: ['deployTimes'],
                        labels: ['发布次数'],
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
