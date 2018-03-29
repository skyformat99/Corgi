<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <%@ include file="/include/meta.html" %>
    <title>欢迎进入贝聊平台管理系统</title>
</head>

<body class="skin-blue sidebar-mini">
<div class="wrapper">


    <!-- Content Wrapper. Contains page content -->

    <section class="content" style='margin-top:200px;'>
        <div class="row">
            <div class="col-md-4 col-md-offset-4">
                <!-- Horizontal Form -->
                <div class="box box-info">
                    <div class="box-header with-border">
                        <h3 class="box-title">登录</h3>
                    </div>
                    <!-- /.box-header -->
                    <!-- form start -->
                    <form class="form-horizontal">
                        <div class="box-body">
                            <div class="form-group">
                                <label class="col-sm-2 control-label">邮箱</label>

                                <div class="col-sm-10">
                                    <input type="text" class="form-control" id="account" placeholder="Email" value="">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">密码</label>

                                <div class="col-sm-10">
                                    <input type="password" class="form-control" id="password" placeholder="Password">
                                </div>
                            </div>
                        </div>
                        <!-- /.box-body -->
                        <div class="box-footer">
                            <button class="btn btn-primary pull-right" type="button" id="submit" onclick="doLogin()">
                                提交
                            </button>
                        </div>
                        <!-- /.box-footer -->
                    </form>
                </div>

            </div>
        </div>
        <!-- /.row -->
    </section>


    <!-- footer -->

</div><!-- ./wrapper -->

<!-- bottom js -->
<script src="/static/js/bootstrap/bootstrap.min.js" type="text/javascript"></script>
<script src="/static/js/json2.js" type="text/javascript"></script>
<!-- AdminLTE App -->
<script src="/static/tpl/js/app.min.js" type="text/javascript"></script>
<!-- DATA TABES SCRIPT -->
<script src="/static/js/template.js" type="text/javascript"></script>
<script src="/static/plugins/select2/select2.min.js" type="text/javascript"></script>
<script src="/static/plugins/jquery.form.js" type="text/javascript"></script>
<script src="/static/plugins/bootstrap-dialog/js/bootstrap-dialog.min.js"></script>
<script src="/static/js/paginator.js?v=7" type="text/javascript"></script>
<script src="/static/plugins/daterangepicker/moment.js" type="text/javascript"></script>
<script src="/static/plugins/daterangepicker/daterangepicker.js" type="text/javascript"></script>
<script src="/static/plugins/jquery.cookie.js" type="text/javascript"></script>

<script src="/static/plugins/datatables/jquery.dataTables.min.js" type="text/javascript"></script>
<script src="/static/plugins/datatables/dataTables.bootstrap.js" type="text/javascript"></script>
<script src="/static/js/datatable-custom.js" type="text/javascript"></script>

<script src="/static/js/pnotify.custom.min.js" type="text/javascript"></script>

<script type="text/javascript">
    function doLogin() {
        if ($("#account").val() == '') {
            BootstrapDialog.alert("邮箱不能为空");
            return;
        }
        if ($("#password").val() == '') {
            BootstrapDialog.alert("密码不能为空");
            return;
        }


        $.post("/admin/login.do", {account: $("#account").val(), password: $("#password").val()},
            function (data) {
                if (data.code == 1) {
                    window.location.href = "/admin/welcome.xhtml";
                } else {
                    BootstrapDialog.alert("账号或者密码错误");
                }
            });

    }
</script>
</body>
</html>
