<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <%@ include file="/include/meta.html" %>
    <title>编辑或添加项目</title>
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
            <h1 id="titleMessage">
                编辑或添加项目
                <small></small>
            </h1>
        </section>

        <!-- Main content -->
        <section class="content">
            <div class="row">
                <div class="col-md-12">
                    <form id="editForm" class="form-horizontal" method="post" modelattribute="account"
                          onsubmit="" action="/admin/project/saveProject.do">

                        <div class="form-group">
                            <label class="col-md-2 control-label">项目名称</label>
                            <div class="col-md-3">
                                <input id="projectId" value="0" type="hidden"/>
                                <input class="form-control" type="text" id="projectName"
                                       name="projectName" title="项目名称" placeholder="项目名称"
                                       maxlength="30" data-minlength="2" required="true">
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-md-2 control-label">代号(唯一)</label>
                            <div class="col-md-3">
                                <input class="form-control" type="text" id="projectNo"
                                       name="projectNo" title="代号" placeholder="代号"
                                       maxlength="30" data-minlength="2" required="true">
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-md-2 control-label">负责人</label>
                            <div class="col-md-3">
                                <select id="managerId">

                                </select>
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-md-2 control-label">参与人</label>
                            <div class="col-md-3">
                                <select id="joiners" name="joiners" class="form-control" multiple="multiple">

                                </select>
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-md-2 control-label">语言</label>
                            <div class="col-md-3">
                                <select id="language">
                                    <option></option>
                                    <option value="java">java</option>
                                    <option value="php">php</option>
                                    <option value="python">python</option>
                                </select>
                            </div>
                        </div>

                        <div class="form-group">
                            <div class="col-md-offset-2">
                                <button class="btn btn-primary"
                                        type="button" id="submit" onclick="saveProject()">提交
                                </button>
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
<%@ include file="/include/bottom-js.jsp" %>
<script type="text/javascript">

    $(function () {
        loadProject();
    });
    function loadProject() {
        var projectId;
        if ($.getUrlParam("projectId")) {
            projectId = $.getUrlParam("projectId");
        } else {
            projectId = 0;
            BootstrapDialog.alert('请选择一个项目进行编辑');
        }
        $.get("/admin/project/getProject", {'projectId': projectId}, function (json) {
            if (json.success) {
                var projectDetail = json.object;
                var project = projectDetail.project;
                if (project) {
                    $('#projectId').val(projectId);
                    $('#projectName').val(project.projectName);
                    $('#projectNo').val(project.projectNo);
                    $('#language').val(project.language);
                    $('#projectId').val(project.projectId);
                }
                initAllAccount(projectDetail.allAccounts);
                initJoiners(projectDetail);
                $("#joiners").select2();
            } else {
                BootstrapDialog.alert(json.message);
            }
        });
    }

    function initJoiners(projectDetail) {
        var allAccounts = projectDetail.allAccounts;
        var relations = projectDetail.projectAccountRelations;
        var accountHtml;
        for (var i in allAccounts) {
            var isJoin = checkIsJoin(relations, allAccounts[i].uid);
            if (isJoin) {
                accountHtml = accountHtml + "<option   value='" + allAccounts[i].uid + "' selected>" + allAccounts[i].realname + "</option>";
            } else {
                accountHtml = accountHtml + "<option   value='" + allAccounts[i].uid + "' >" + allAccounts[i].realname + "</option>";
            }
        }
        $("#joiners").html(accountHtml);
    }

    function checkIsJoin(relations, accountId) {
        var isJoin = false;
        for(var i in relations) {
            if (relations[i].accountId == accountId && relations[i].isAdmin == 0) {
                isJoin = true;
                break;
            }
        }
        return isJoin;
    }

    function initAllAccount(allAccounts) {
        var accountHtml;
        for (var i in allAccounts) {
            accountHtml = accountHtml + "<option data-account='"+ allAccounts[i].account
                    + "' data-mobile='"+ allAccounts[i].mobileNo +
                    "' value='" + allAccounts[i].uid + "'>" + allAccounts[i].realname + "</option>";
        }
        $("#managerId").html(accountHtml);
    }


    //提交
    function saveProject() {
        var validateResult = validateForm();
        if (validateResult != '') {
            BootstrapDialog.alert(validateResult);
            return;
        }
        var postData = buildPostData();
        $.ajax({
            type: 'POST',
            url: '/admin/project/save',
            data:  JSON.stringify(postData),
            dataType: 'json',
            contentType: "application/json; charset=utf-8",
            success: function(data){
                 if ($(data.success)) {
                        BootstrapDialog.alert("保存成功");
                    } else {
                        BootstrapDialog.alert("保存失败");
                    }
            },
            error: function(data){
                 BootstrapDialog.alert("保存失败");
            }

        });

    }

    function buildPostData() {
        var projectDetailInfo = {}, project = {}, projectAccountRelations = new Array();
        project.projectName = $('#projectName').val();
        project.projectNo = $('#projectNo').val();
        project.language = $('#language').val();
        project.projectId = $('#projectId').val();
        project.managerPhone = $('#managerId option:selected').attr("data-mobile");
        project.managerEmail = $('#managerId option:selected').attr("data-account");
        project.managerName = $('#managerId option:selected').text();
        project.managerId = $('#managerId option:selected').val();
        projectDetailInfo.project = project;

        var managerId = parseInt($("#managerId").val());
        projectAccountRelations.push({'accountId': managerId,'isAdmin':1});
        var joiners = $("#joiners").val();
        for(var i in joiners) {
            projectAccountRelations.push({'accountId': parseInt(joiners[i]),'isAdmin':0})
        }
        projectDetailInfo.projectAccountRelations = projectAccountRelations;

        return projectDetailInfo;
    }

    // 校验参数
    function validateForm() {
        var message = "";
        if ($("#projectName").val() == '') {
            message = '项目名称不能为空';
        }
        if ($("#projectNo").val() == '') {
            message = '项目编号不能为空';
        }
        if ($("#language").val() == '') {
            message = '语言不能为空';
        }
        if ($("#managerId").val() == '') {
            message = '负责人不能为空';
        }
        return message;
    }

</script>
</body>
</html>
