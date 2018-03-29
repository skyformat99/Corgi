<%@ page import="com.ibeiliao.deployment.admin.vo.account.AdminAccount" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<%@ include file="/include/meta.html"%>
	<title>环境列表</title>
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
				环境列表
				<small></small>
			</h1>
			<ol class="breadcrumb">
				<li><a href="javascript:void(0)" data-toggle="modal" data-target="#configModal" onclick="addEnv()"><i class="fa fa-dashboard"></i> 新增环境</a></li>
			</ol>
		</section>

		<!-- Main content -->
		<section class="content">
			<div class="row">
				<div class="col-xs-12">
					<div class="box">
						<div class="box-body">
							<div class="row">
								<div class="col-sm-12">
									<table id="listTable" class="table table-bordered table-hover">
										<thead>
										<tr>
											<th>ID</th>
											<th>环境名称</th>
											<th>是否是生产环境</th>
											<th>操作</th>
										</tr>
										</thead>
										<script id="listTableTpl" type="text/html">
											{{each object as value i}}
											<tr>
												<td>
													{{value.envId}}
												</td>
												<td>{{value.envName}}</td>
												<td>
													{{if value.onlineFlag == 1}}
														是
													{{else}}
														否
													{{/if}}
												</td>
												<td>
													<a href="javascript:void(0)" data-toggle="modal" data-target="#configModal"  onclick="editEnv({{value.envId}},'{{value.envName}}',{{value.onlineFlag}})">修改</a>
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
							<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
							<h4 class="modal-title" id="myModalLabel">新增环境</h4>
						</div>
						<div class="modal-body">
							<form class="form-horizontal">
								<div class="form-group">
									<label class="control-label col-sm-3">环境名称:</label>
									<div class="col-sm-9">
										<input type="hidden" name="envId" class="form-control" id="envId" placeholder="" value="0">
										<input type="text" name="envName" class="form-control" id="envName" placeholder="环境名称" maxlength="20">
									</div>
								</div>

								<div class="form-group">
									<label class="control-label col-sm-3">是否是生产环境:</label>
									<div class="col-sm-9">
										<div class="radio">
                                                <label><input type="radio" name="onlineFlag" value="0" checked="">否</label>
                                                <label><input type="radio" name="onlineFlag" value="1">是</label>
                                         </div>
									</div>
								</div>

							</form>
						</div>
						<div class="modal-footer">
							<button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
							<button type="button" class="btn btn-primary" onclick="save()">保存</button>
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
<%@ include file="/include/bottom-js.jsp"%>
<script type="text/javascript">
    function search() {
        $('#listTable').artPaginate({
            // 获取数据的地址
            url: "/admin/env/list",
            // 显示页码的位置
            paginator: 'listPaginator',
            // 模版ID
            tpl: 'listTableTpl',
            // 请求的参数表，默认page=1, pageSize=20
            params: {}
        });
    }

	function addEnv() {
        $('#myModalLabel').html('新增环境');
        $('#envId').val('0');
        $('#envName').val('');
    }
    // 修改
    function editEnv(envId, envName, onlineFlag) {
        $('#myModalLabel').html('编辑环境');
        $('#envId').val(envId);
        $('#envName').val(envName);
		$("input[name='onlineFlag'][value='" + onlineFlag + "']").prop("checked", true);
    }

    function save() {
        var envId = $('#envId').val();
        var envName = $('#envName').val();
		var onlineFlag = $('input:radio[name="onlineFlag"]:checked').val();
        $.post('/admin/env/save.do', {
            envId: envId,
            envName: envName,
			onlineFlag:onlineFlag
        }, function (json) {
            $('#configModal').modal('hide');
            if (json.success) {
                BootstrapDialog.alert(json.message, function () {
                    search();
                });
            } else {
                BootstrapDialog.alert(json.message);
            }
        }, 'json');
    }

	$(function(){
        search();
	});
</script>
</body>
</html>
