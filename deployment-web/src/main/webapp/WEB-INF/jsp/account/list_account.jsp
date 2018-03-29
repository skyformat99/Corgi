<%@ page import="com.ibeiliao.deployment.admin.vo.account.AdminAccount" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<%@ include file="/include/meta.html"%>
	<title>管理员列表</title>
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
				管理员列表
				<small></small>
			</h1>
			<ol class="breadcrumb">
				<li><a href="/admin/account/editAccount.xhtml"><i class="fa fa-dashboard"></i> 新增管理员</a></li>
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
									<input type="text" id="keyword" name="keyword" maxlength="30" class="form-control">
									<span class="input-group-btn">
										<button type="button" class="btn btn-info btn-flat" onclick="search()">Go!</button>
									</span>
								</div>
							</div>
							<div class="col-sm-9" style="text-align: right;">

							</div>
						</div><!-- /.box-header -->
						<div class="box-body">
							<div class="row">
								<div class="col-sm-12">
									<table id="listTable" class="table table-bordered table-hover">
										<thead>
										<tr>
											<th>ID</th>
											<th>帐号</th>
											<th>角色</th>
											<th>名字</th>
											<th>手机号码</th>
											<th>操作</th>
										</tr>
										</thead>
										<script id="listTableTpl" type="text/html">
											{{each object as value i}}
											<tr>
												<td>
													{{value.uid}}
												</td>
												<td>{{value.account}}</td>
												<td>{{value.roleName}}</td>
												<td>{{value.realname}}</td>
												<td>{{value.mobileNo}}</td>
												<td>{{formatDateTime value.createTime}}</td>
												<td>
													{{if value.accountStatus == <%=AdminAccount.NOMAL%>}}
													<a href="javascript:modifyStatus({{value.uid}})">冻结</a>
													{{/if}}
													{{if value.accountStatus == <%=AdminAccount.LOCKED%>}}
													<a href="javascript:modifyStatus({{value.uid}});">解冻</a>
													{{/if}}
													&nbsp; <a href="/admin/account/editAccount.xhtml?uid={{value.uid}}">修改</a>
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
	function search(iPage) {
		var page = iPage || 1;
		var keyword = $('#keyword').val();
		$('#listTable').artPaginate({
			// 获取数据的地址
			url: "/admin/account/queryAccount",
			// 显示页码的位置
			paginator: 'listPaginator',
			// 模版ID
			tpl: 'listTableTpl',
			// 请求的参数表，默认page=1, pageSize=20
			params: {'keyword': keyword, 'page' : page}
		});
	}

	// 修改帐号状态：冻结、解冻
	function modifyStatus(uid) {
		$.post("/admin/account/lockOrUnlockAccount.do", {'uid':uid}, function(json) {
			BootstrapDialog.alert(json.message, function() {
				//window.location.href = '/admin/account/listAccount.xhtm';
				var page = $('#listTable').artPage();
				search(page);
			});
		});
	}

	$(function(){
		search();
	});
</script>
</body>
</html>
