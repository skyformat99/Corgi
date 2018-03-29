<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<%@ include file="/include/meta.html"%>
	<title>查看全局配置</title>
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
			<h1 id="titleMessage">
				查看全局配置
				<small></small>
			</h1>
			<%--<ol class="breadcrumb">
				<li><a href="/admin/account/listAccount.xhtml"><i class="fa fa-dashboard"></i> 编辑</a></li>
			</ol>--%>
		</section>

		<!-- Main content -->
		<section class="content">
			<div class="row">
				<div class="col-md-12">
					<form id="editForm" class="form-horizontal" method="post" modelattribute="account"
						  onsubmit="return validateForm(this)" action="/admin/globalSetting/saveSetting.do">

						<div class="form-group">
							<label class="col-md-2 control-label">svn checkout目录</label>
							<div class="col-md-3">
								<input class="form-control" type="text" id="svnCheckoutDir"
									   name="svnCheckoutDir" title=" " placeholder="svn checkout目录"
									   maxlength="60" data-minlength="2" required="true">
								<input type="hidden" name="settingId" id="settingId" value=""/>
							</div>
						</div>
						<div class="form-group">
							<label class="col-md-2 control-label">目标服务器用户</label>
							<div class="col-md-3">
								<input class="form-control" type="text" id="targetServerUser"
									   name="targetServerUser" title="目标服务器用户" placeholder="目标服务器用户"
									   maxlength="30" data-minlength="2" required="true" >
							</div>
						</div>
						<div class="form-group">
							<label class="col-md-2 control-label">目标服务器目录</label>
							<div class="col-md-3">
								<input class="form-control" type="text" id="targetServerDir" name="targetServerDir"
									     placeholder="目标服务器目录" maxlength="60" data-minlength="1" required="true">
							</div>
						</div>


						<div class="form-group">
							<div class="col-md-offset-2">
								<button class="btn btn-primary"
									   type="button" id="submit" onclick="saveSetting()">提交</button>
							</div>
						</div>
					</form>
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
	$(function () {
		$.getJSON("/admin/globalSetting/getSetting", {}, function(json) {
			console.log(json);
			if (json.success) {
				var settingEnv = json.object;
				if (settingEnv.globalSetting) {
					$("#svnCheckoutDir").val(settingEnv.globalSetting.svnCheckoutDir);
					$("#targetServerUser").val(settingEnv.globalSetting.targetServerUser);
					$("#targetServerDir").val(settingEnv.globalSetting.targetServerDir);
				}
				$("#settingId").val(settingEnv.globalSetting.settingId);
			} else {
                BootstrapDialog.alert("加载全局配置失败");
			}
		});

	});


	//提交
	function saveSetting() {
		var validateResult = validateForm();
		if (validateResult != '') {
			BootstrapDialog.alert(validateResult);
			return;
		}
		$.post('/admin/globalSetting/saveSetting.do',
				{
					'svnCheckoutDir' : $("#svnCheckoutDir").val(),
					'targetServerUser' : $("#targetServerUser").val(),
					'targetServerDir' : $("#targetServerDir").val(),
					'settingId' : $("#settingId").val(),
				},
				function (data) {
					if ($(data.success)) {
						BootstrapDialog.alert("保存成功");
					} else {
						BootstrapDialog.alert("保存成功");
					}
				});
	}

	// 校验参数
	function validateForm() {
		var message = "";
		if ($("#svnCheckoutDir").val() == '') {
			message = 'svn 目录不能为空';
		} else  if ($("#targetServerDir").val() == '') {
			message = '请输入目标服务器目录';
		} else if ($("#targetServerUser").val() == '') {
			message = '请输入目标服务器用户'
		}
		return message;
	}

</script>
</body>
</html>
