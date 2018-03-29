<%@ page import="com.ibeiliao.deployment.common.enums.ModuleType" %>
<%@ page import="com.ibeiliao.deployment.common.enums.ModuleRepoType" %>
<%@ page import="com.ibeiliao.deployment.common.Constants" %>
<%@ page import="com.ibeiliao.deployment.cfg.Configuration" %>
<%@ page import="com.ibeiliao.deployment.common.util.JvmArgUtil" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <%@ include file="/include/meta.html" %>
    <title>编辑或添加模块</title>
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
                编辑或添加模块
                <small></small>
            </h1>
            <ol class="breadcrumb">
                <li><a id="viewProjectLink" href="#"><i class="fa fa-dashboard"></i> 返回项目</a></li>
            </ol>
        </section>

        <!-- Main content -->
        <section class="content">
            <div class="row">
                <section class="col-md-12">
                    <div class="box box-primary">
                        <div class="box-body">
                            <form class="form-horizontal">
                                <div class="row">
                                    <div class="form-group">
                                        <input type="hidden" value="" id="projectId"/>
                                        <input type="hidden" value="" id="moduleId"/>
                                        <label class="col-md-2 control-label text-right">项目</label>
                                        <div class="col-md-3 checkbox" id="projectName">
                                        </div>

                                        <label class="col-md-2 control-label text-right">负责人</label>
                                        <div class="col-md-3 checkbox" id="projectManagerName">
                                        </div>
                                    </div>
                                </div>

                                <div class="row">
                                    <div class="form-group">
                                        <label class="col-md-2 control-label text-right">模块名称</label>
                                        <div class="col-md-3">
                                            <input id="moduleNameZh" placeholder="模块中文名称，比如：优惠券" class="form-control"
                                                   type="text"/>
                                        </div>

                                        <label class="col-md-2 control-label text-right">类型</label>
                                        <div class="col-md-4">
                                            <div class="radio">
                                                <label><input type="radio" name="moduleType"
                                                              value="<%=ModuleType.WEB_PROJECT.getValue()%>"
                                                              onclick="setDefaultShell()"/><%=ModuleType.WEB_PROJECT.getName()%>
                                                </label>
                                                <label><input type="radio" name="moduleType"
                                                              value="<%=ModuleType.SERVICE.getValue()%>"
                                                              onclick="setDefaultShell()"/><%=ModuleType.SERVICE.getName()%>
                                                </label>
                                                <label><input type="radio" name="moduleType"
                                                              value="<%=ModuleType.STATIC.getValue()%>"
                                                              onclick="setDefaultShell()"/><%=ModuleType.STATIC.getName()%>
                                                </label>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <div class="row">
                                    <div class="form-group">
                                        <div class="col-md-1"></div>
                                        <div class="col-md-1 text-right">
                                            <select class="form-control" id="repoType">
                                                <option value=""></option>
                                                <option value="<%=ModuleRepoType.SVN.getValue()%>">SVN</option>
                                                <option value="<%=ModuleRepoType.GIT.getValue()%>">GIT</option>
                                            </select>
                                        </div>
                                        <div class="col-md-6">
                                            <input type="text" name="repoUrl" id="repoUrl" class="form-control" value=""
                                                   size="80"
                                                   placeholder="svn基地址,不要包含tags/、trunk/、branches"
                                                   title="svn基地址,不要包含tags/、trunk/、branches"/>
                                        </div>
                                        <div class="col-md-4">
                                            svn内网地址：https://svn-3c6c.ibeiliao.net/...<br/>
                                            git内网地址： http://gits-lan.ibeiliao.net/...
                                        </div>
                                    </div>
                                </div>


                                <div class="row">
                                    <div class="form-group">
                                        <label class="col-md-2 control-label text-right">模块</label>
                                        <div class="col-md-6">
                                            <input type="text" name="moduleName" id="moduleName" class="form-control"
                                                   value=""
                                                   placeholder="项目里的module名称，比如xxxx-impl"/>
                                        </div>
                                    </div>
                                </div>

                                <div class="row">
                                    <div class="form-group">
                                        <label class="col-md-2 control-label text-right">账号</label>
                                        <div class="col-md-2">
                                            <input type="text" name="svnAccount" class="form-control" id="svnAccount"
                                                   value=""
                                                   placeholder="svn/git账号" maxlength="32"/>
                                        </div>
                                        <label class="col-md-2 control-label text-right">密码</label>
                                        <div class="col-md-2">
                                            <input type="password" name="svnPassword" class="form-control"
                                                   id="svnPassword" value=""
                                                   placeholder="svn/git密码" maxlength="32"/>
                                        </div>
                                        <%--是否是发布上线 --%>
                                        <div class="col-md-2">
                                            <div class="checkbox">
                                                <label><input type="checkbox" name="needAudit" id="needAudit"
                                                              value="1"/>
                                                    发布审核(生产)</label>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </form>
                        </div>
                    </div>
                </section>
            </div>

            <div class="row">
                <section class="col-md-5">
                    <div class="box box-primary">
                        <div class="box-body">
                            <div class="form-group">
                                <label>pre deploy：</label> <br/>
                                <textarea id="preShell" class="form-control" placeholder="重启前执行"></textarea>

                            </div>
                            <div class="form-group">
                                <label>重启服务脚本或服务的Main Class：</label><br/>
                                比如: com.alibaba.dubbo.container.Main<br/>
                                <%--或者类似: &#36;{targetDir}/env/shell/service.sh restart XX<br/>--%>
                                /usr/local/resinpro/bin/resin.sh restart<br/>
                                <textarea class="form-control" id="restartShell"
                                          placeholder="com.alibaba.dubbo.container.Main" onblur="showJvmArgs()"
                                          onkeypress="changeRestartShell()"></textarea>
                            </div>
                            <div class="form-group">
                                <label>post deploy：</label> <br/>
                                <textarea class="form-control" id="postShell" placeholder="重启后执行"></textarea>
                            </div>
                            <div class="form-group">
                                <label>编译脚本(参考如下，<span class="text-danger">每行一条指令</span>)：</label> <br/>
                                mvn -P=&#36;{env} -Dmaven.test.skip=true -U clean install<br/>
                                cp -f &#36;{moduleDir}/target/*.<b>jar</b> &#36;{targetDir}<br/>
                                或 cp -f &#36;{moduleDir}/target/*.<b>war</b> &#36;{targetDir}<br/>
                                <textarea class="form-control" id="compileShell" placeholder="编译脚本"
                                          onkeypress="changeCompileShell()"></textarea>
                            </div>
                            <div class="form-group">
                                <label>停止服务脚本(重启脚本填了Main Class可忽略)：</label><br/>
                                比如: /usr/local/resinpro/bin/resin.sh stop<br/>
                                <textarea class="form-control" id="stopShell" placeholder="停止服务脚本"
                                          onkeypress="changeStopShell()"></textarea>
                            </div>
                            <%--<div class="form-group">--%>
                            <%--<h4>可用变量：</h4>--%>
                            <%--<!-- 请勿修改下面的空格 -->--%>
                            <%--<pre>--%>
                            <%--&#36;{moduleDir}：mvn编译完成后的模块目录，绝对路径，比如 /data/project/projectA/module1--%>
                            <%--&#36;{targetDir}：模块发布完成后所在的目录，绝对路径，比如 /data/project/projectA/module1--%>
                            <%--&#36;{env}：哪个环境，例如dev/test--%>
                            <%--</pre>--%>
                            <%--</div>--%>
                        </div>
                    </div>
                </section>

                <section class="col-md-7" style="padding-left: 0px;">
                    <%--环境与jvm参数--%>
                    <div class="box box-primary">
                        <div class="box-header with-border">
                            <h3 class="box-title">参数配置</h3>
                            <div class="box-tools pull-right">
                                <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i>
                                </button>
                            </div>
                        </div>
                        <div class="box-body">
                            <div class="row">
                                <div class="col-md-12">
                                    <ul class="nav nav-tabs">
                                        <li class="active"><a href="#jvmConf" data-toggle="tab" aria-expanded="true">JVM参数</a>
                                        </li>
                                        <li><a href="#resinConf" data-toggle="tab" aria-expanded="true">Resin参数</a></li>
                                    </ul>
                                    <%--内容--%>
                                    <div class="tab-content">
                                        <div class="tab-pane active" id="jvmConf">
                                            <div class="row checkbox" id="argTipDiv">
                                                <div class="col-md-12 text-danger">
                                                    &gt;&gt;「<%=ModuleType.SERVICE.getName()%>」自定义脚本
                                                    和「<%=ModuleType.STATIC.getName()%>」 不用填写JVM参数<br/>
                                                    &gt;&gt;「<%=ModuleType.WEB_PROJECT.getName()%>
                                                    」和「<%=ModuleType.SERVICE.getName()%>
                                                    」MainClass启动，系统默认添加以下JVM参数：<%=JvmArgUtil.getDefaultArgs()%>
                                                </div>
                                            </div>
                                            <form class="form-horizontal">
                                                <div id="jvmArgsContent">
                                                    <script id="jvmTpl" type="text/html">
                                                        {{each moduleJvms as value i}}
                                                        <div class="form-group" data-envId="{{value.envId}}"
                                                             data-envName="{{value.envName}}"
                                                             data-moduleJvmId="{{value.moduleJvmId}}">
                                                            <label class="col-md-1 control-label text-left">{{value.envName}}</label>
                                                            <div class="col-md-11">
                                                                <input type="text" class="form-control jvmArgs"
                                                                       placeholder="JVM参数表" value="{{value.jvmArgs}}"/>
                                                            </div>
                                                        </div>
                                                        {{/each}}
                                                    </script>
                                                </div>
                                            </form>
                                        </div>
                                        <div class="tab-pane" id="resinConf">
                                            <form class="form-horizontal checkbox">
                                                <div class="form-group">
                                                    <label class="col-md-2 control-label text-left">域名</label>
                                                    <div class="col-md-5">
                                                        <input id="domain" name="domain" type="text"
                                                               class="form-control" placeholder="不包含http/https"
                                                               title="不包含http/https" value=""/>
                                                    </div>
                                                    <div class="col-md-5">
                                                        <p class="text-left help-block">
                                                            比如pf.ibeiliao.net，不需要写dev/test</p>
                                                    </div>
                                                </div>
                                                <div class="form-group">
                                                    <label class="col-md-2 control-label text-left">域名别名</label>
                                                    <div class="col-md-5">
                                                        <input id="aliasDomain" name="aliasDomain" type="text"
                                                               class="form-control" placeholder="不包含http/https"
                                                               title="不包含http/https" value=""/>
                                                    </div>
                                                    <div class="col-md-5">
                                                        <p class="text-left help-block">域名别名，多个则用空格隔开，不需要写dev/test</p>
                                                    </div>
                                                </div>
                                                <div class="form-group">
                                                    <label class="col-md-2 control-label text-left">端口</label>
                                                    <div class="col-md-2">
                                                        <input id="httpPort" name="httpPort" type="text"
                                                               class="form-control" placeholder="HTTP端口" title="HTTP端口"
                                                               value=""/>
                                                    </div>
                                                    <div class="col-md-2">
                                                        <input id="serverPort" name="serverPort" type="text"
                                                               class="form-control" placeholder="Server端口"
                                                               title="Server端口" value=""/>
                                                    </div>
                                                    <div class="col-md-2">
                                                        <input id="watchdogPort" name="watchdogPort" type="text"
                                                               class="form-control" placeholder="Watchdog端口"
                                                               title="Watchdog端口" value=""/>
                                                    </div>
                                                    <div class="col-md-4">
                                                        <p class="text-left help-block">范围分别是808x/680x/660x</p>
                                                    </div>
                                                </div>
                                                <div class="form-group">
                                                    <label class="col-md-3 control-label text-left">thread-max</label>
                                                    <div class="col-md-2">
                                                        <input id="threadMax" name="threadMax" type="text"
                                                               class="form-control" placeholder="可以不填" value=""/>
                                                    </div>
                                                    <div class="col-md-6">
                                                        <p class="text-left help-block">默认不填写</p>
                                                    </div>
                                                </div>
                                                <div class="form-group">
                                                    <label class="col-md-3 control-label text-left">keepalive-max</label>
                                                    <div class="col-md-2">
                                                        <input id="keepaliveMax" name="keepaliveMax" type="text"
                                                               class="form-control" placeholder="可以不填" value=""/>
                                                    </div>
                                                    <div class="col-md-6">
                                                        <p class="text-left help-block">默认不填写</p>
                                                    </div>
                                                </div>
                                                <div class="form-group">
                                                    <label class="col-md-3 control-label text-left">keepalive-timeout</label>
                                                    <div class="col-md-2">
                                                        <input id="keepaliveTimeout" name="keepaliveTimeout" type="text"
                                                               class="form-control" placeholder="默认15s" value=""/>
                                                    </div>
                                                    <div class="col-md-6">
                                                        <p class="text-left help-block">单位：秒，默认15s</p>
                                                    </div>
                                                </div>
                                                <div class="form-group">
                                                    <label class="col-md-3 control-label text-left">socket-timeout</label>
                                                    <div class="col-md-2">
                                                        <input id="socketTimeout" name="socketTimeout" type="text"
                                                               class="form-control" placeholder="socketTimeout"
                                                               value=""/>
                                                    </div>
                                                    <div class="col-md-6">
                                                        <p class="text-left help-block">单位：秒，默认30s</p>
                                                    </div>
                                                </div>
                                                <div class="form-group">
                                                    <label class="col-md-3 control-label text-left">resin.xml</label>
                                                    <div class="col-md-4">
                                                        <label>
                                                            <input id="createEveryTime" name="createEveryTime"
                                                                   type="checkbox" checked/>每次发布都重新生成resin.xml</label>
                                                    </div>
                                                    <div class="col-md-4">
                                                        <p class="text-left help-block">如果为false，只生成一次</p>
                                                    </div>
                                                </div>
                                            </form>
                                        </div>

                                    </div>
                                </div>
                            </div>

                        </div>
                    </div>

                    <%--服务器组--%>
                    <div class="box box-primary">
                        <div class="box-body">
                            <div class="row">
                                <div class="col-md-12">
                                    <ul id="serverGroups" class="nav nav-tabs">
                                    </ul>
                                    <%--内容--%>
                                    <div id="groupContent" class="tab-content">

                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="box-footer">
                            <div class="row">
                                <div class="col-md-12">
                                    <h4>“编译脚本”可用变量：</h4>
                                    <!-- 请勿修改下面的空格 -->
                                    <pre>
&#36;{moduleDir}：mvn编译完成后的模块目录，绝对路径，比如 /data/project/projectA/module1
&#36;{targetDir}：模块发布完成后所在的目录，绝对路径，比如 /data/project/projectA/module1
&#36;{env}：哪个环境，例如dev/test
                                    </pre>
                                </div>
                            </div>
                        </div>
                    </div>

                </section>
            </div>

        </section>


        <div class="row" style="text-align: center; padding-bottom: 30px;">
            <button class="btn btn-primary"
                    type="button" id="submit" onclick="saveModule()">提交
            </button>
        </div>

        </section><!-- /.content -->
    </div><!-- /.content-wrapper -->

    <%--模态窗口 添加服务器组--%>
    <div class="modal" id="addGroup">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                            aria-hidden="true">&times;</span></button>
                    <h4 class="modal-title">添加服务器组</h4>
                </div>
                <div class="modal-body">
                    <div class="row">
                        <form class="form-horizontal col-md-12">
                            <input type="hidden" value="" id="currentGroupId"/>
                            <%--<input id="hiddenText" type="text" style="display:none" />--%>
                            <div class="form-group">
                                <label class="control-label col-sm-2"> 环境: </label>
                                <div class="col-md-6">
                                    <div id="envs" class="radio">

                                    </div>
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="control-label col-sm-2"> 名称: </label>
                                <div class="col-md-6">
                                    <input type="text" id="groupName" class="form-control" maxlength="45"/>
                                </div>
                            </div>

                        </form>
                    </div>
                </div>

                <div class="modal-footer">
                    <div class="col-md-3"></div>
                    <div class="col-md-2">
                        <button type="button" class="btn btn-primary" id="addBtn" onclick="addServerGroup()"
                                data-loading-text="添加中...">
                            保存
                        </button>
                    </div>
                    <div class="col-md-3">
                        <button type="button" class="btn" data-dismiss="modal">取消
                        </button>
                    </div>
                </div>
            </div><!-- /.modal-content -->
        </div><!-- /.modal-dialog -->
    </div><!-- /.modal -->

    <%--添加服务器模态窗口--%>
    <div class="modal" id="addServerModel">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                            aria-hidden="true">&times;</span></button>
                    <h4 class="modal-title">添加服务器</h4>
                </div>
                <div class="modal-body">
                    <div class="row">

                        <input type="hidden" id="currentServerId"/>

                        <form class="form-horizontal col-md-12">

                            <div class="form-group">
                                <label class="control-label col-sm-2"> 名称1: </label>
                                <div class="col-md-4">
                                    <input type="text" id="serverName1" value="" class="form-control" maxlength="45"
                                           onfocus="loadAllServers(this,false)" onkeyup="filterServer(this,false)"
                                           onblur="hideServerList(event)"/>
                                </div>
                                <label class="control-label col-sm-2"> IP1: </label>
                                <div class="col-md-4">
                                    <input type="text" id="serverIP1" value="" class="form-control" maxlength="45"
                                           onfocus="loadAllServers(this,true)" onkeyup="filterServer(this,true)"
                                           onblur="hideServerList(event)"/>
                                </div>
                            </div>

                            <div class="form-group moreIp">
                                <label class="control-label col-sm-2"> 名称2: </label>
                                <div class="col-md-4">
                                    <input type="text" id="serverName2" value="" class="form-control" placeholder="可以不填"
                                           maxlength="45" onfocus="loadAllServers(this)" onkeyup="filterServer(this)"
                                           onblur="hideServerList(event)"/>
                                </div>
                                <label class="control-label col-sm-2"> IP2: </label>
                                <div class="col-md-4">
                                    <input type="text" id="serverIP2" value="" class="form-control" placeholder="可以不填"
                                           maxlength="45" onfocus="loadAllServers(this)" onkeyup="filterServer(this)"
                                           onblur="hideServerList(event)"/>
                                </div>
                            </div>
                            <div class="form-group moreIp">
                                <label class="control-label col-sm-2"> 名称3: </label>
                                <div class="col-md-4">
                                    <input type="text" id="serverName3" value="" class="form-control" placeholder="可以不填"
                                           maxlength="45" onfocus="loadAllServers(this)" onkeyup="filterServer(this)"
                                           onblur="hideServerList(event)"/>
                                </div>
                                <label class="control-label col-sm-2"> IP3: </label>
                                <div class="col-md-4">
                                    <input type="text" id="serverIP3" value="" class="form-control" placeholder="可以不填"
                                           maxlength="45" onfocus="loadAllServers(this)" onkeyup="filterServer(this)"
                                           onblur="hideServerList(event)"/>
                                </div>
                            </div>
                            <div class="form-group moreIp">
                                <label class="control-label col-sm-2"> 名称4: </label>
                                <div class="col-md-4">
                                    <input type="text" id="serverName4" value="" class="form-control" placeholder="可以不填"
                                           maxlength="45" onfocus="loadAllServers(this)" onkeyup="filterServer(this)"
                                           onblur="hideServerList(event)"/>
                                </div>
                                <label class="control-label col-sm-2"> IP4: </label>
                                <div class="col-md-4">
                                    <input type="text" id="serverIP4" value="" class="form-control" placeholder="可以不填"
                                           maxlength="45" onfocus="loadAllServers(this)" onkeyup="filterServer(this)"
                                           onblur="hideServerList(event)"/>
                                </div>
                            </div>
                            <div class="form-group moreIp">
                                <label class="control-label col-sm-2"> 名称5: </label>
                                <div class="col-md-4">
                                    <input type="text" id="serverName5" value="" class="form-control" placeholder="可以不填"
                                           maxlength="45" onfocus="loadAllServers(this)" onkeyup="filterServer(this)"
                                           onblur="hideServerList(event)"/>
                                </div>
                                <label class="control-label col-sm-2"> IP5: </label>
                                <div class="col-md-4">
                                    <input type="text" id="serverIP5" value="" class="form-control" placeholder="可以不填"
                                           maxlength="45" onfocus="loadAllServers(this)" onkeyup="filterServer(this)"
                                           onblur="hideServerList(event)"/>
                                </div>
                            </div>
                        </form>
                    </div>


                </div>

                <div class="modal-footer">
                    <div class="col-md-3"></div>
                    <div class="col-md-2">
                        <button type="button" class="btn btn-primary" onclick="addServer()"
                                data-loading-text="添加中...">
                            确定
                        </button>
                    </div>
                    <div class="col-md-3">
                        <button type="button" class="btn" data-dismiss="modal">取消
                        </button>
                    </div>
                </div>
            </div><!-- /.modal-content -->
        </div><!-- /.modal-dialog -->
    </div><!-- /.modal -->

    <div id="serverListDiv" class="dropdown-menu"
         style="z-index:10000;display:none;width:300px;height:240px;overflow:scroll;border:1px solid #3c8dbc;">
        <table class="table table-bordered table-hover dataTable" role="grid" style="font-size: 12px;">
            <thead>
            <tr>
                <%--<td>区</td>--%>
                <td>名称</td>
                <td>内网IP</td>
            </tr>
            </thead>
            <tbody id="serverListDivContent">

            </tbody>
        </table>
    </div>
    <!-- footer -->
    <jsp:include page="/include/footer.jsp"/>

    <!-- control sidebar -->
    <jsp:include page="/include/control-sidebar.jsp"/>
</div><!-- ./wrapper -->

<!-- bottom js -->
<%@ include file="/include/bottom-js.jsp" %>
<script id="chooseServerTpl" type="text/html">

    {{each data as value i}}
    <tr title="公网IP: {{value.publicIpAddress}}, 规格: {{value.spec}}"
        onclick="chooseServer('{{value.instanceName}}','{{value.innerIpAddress}}')">
        <%--<td>{{value.regionId}}</td>--%>
        <td>{{value.instanceName}}</td>
        <td>{{value.innerIpAddress}}</td>
    </tr>
    {{/each}}

</script>
<script type="text/javascript">

    //全局的 环境列表
    var globalEnvs = "";
    var restartShellChanged = false;
    var stopShellChanged = false;
    var compileShellChanged = false;
    var pageX = 0;
    var pageY = 0;

    $(function () {
        loadModuleInfo();

        $(document).mousemove(function (e) {
            pageX = e.pageX;
            pageY = e.pageY;
        });
    });

    function loadModuleInfo() {
        var projectId, moduleId;
        if ($.getUrlParam("projectId")) {
            projectId = $.getUrlParam("projectId");
            getModuleBasicInfo(projectId);
            $('#viewProjectLink').prop('href', '/admin/project/viewProject.xhtml?projectId=' + projectId);
            return;
        }

        if ($.getUrlParam("moduleId")) {
            moduleId = $.getUrlParam("moduleId");
            getModuleInfo(moduleId);
        } else {
            BootstrapDialog.alert('请选择一个模块进行编辑');
        }
    }

    function changeRestartShell() {
        restartShellChanged = true;
    }

    function changeStopShell() {
        stopShellChanged = true;
    }

    function changeCompileShell() {
        compileShellChanged = true;
    }

    function showJvmArgs() {

    }

    function setDefaultShell() {
        var containerShell = '<%=Configuration.getWebContainerShell()%>';
        var moduleType = $('input:radio[name="moduleType"]:checked').val();

        if (moduleType) {
            var restartShell = $.trim($('#restartShell').val());
            var stopShell = $.trim($('#stopShell').val());
            var compileShell = $.trim($('#compileShell').val());
            if (moduleType == <%=ModuleType.WEB_PROJECT.getValue()%>) {
                if (restartShell == '' || !restartShellChanged) {
                    $('#restartShell').val("# web项目重启指令\n" + containerShell + " stop\n" + containerShell + " start");
                }
                if (stopShell == '' || !stopShellChanged) {
                    $('#stopShell').val("# web项目stop指令\n" + containerShell + " stop");
                }
                if (compileShell == '' || !compileShellChanged) {
                    $('#compileShell').val("mvn -P=$" + "{env} -Dmaven.test.skip=true -U clean install\ncp -f $" + "{moduleDir}/target/*.war $" + "{targetDir}");
                }
            } else if (moduleType == <%=ModuleType.SERVICE.getValue()%>) {
                if (restartShell == '' || !restartShellChanged) {
                    $('#restartShell').val("com.alibaba.dubbo.container.Main");
                }
                if (compileShell == '' || !compileShellChanged) {
                    $('#compileShell').val("mvn -P=$" + "{env} -Dmaven.test.skip=true -U clean install\ncp `find $" + "{moduleDir}/target/ -name \"*.jar\"` $" + "{targetDir}");
                }
                if (!stopShellChanged && isMainClass($('#restartShell').val())) {
                    $('#stopShell').val('');
                }
            } else if (moduleType == <%=ModuleType.STATIC.getValue()%>) {
                $('#restartShell').val("");
                $('#compileShell').val("cp -rf $" + "{moduleDir} $" + "{targetDir}");
                $('#stopShell').val('');
            }
        }

        showJvmArgs();
    }

    function isMainClass(restartShell) {
        var mainClassPattern = /^[A-Za-z0-9_$\.]+$/; // /([A-Za-z0-9_$]{1,40}\.?)+/;
        return (restartShell && restartShell.match(mainClassPattern));
    }

    function getModuleBasicInfo(projectId) {
        $.get("/admin/project/moduleBaseInfo", {'projectId': projectId}, function (json) {
            if (json.success) {
                var moduleDetail = json.object;
                var project = moduleDetail.project;
                if (project) {
                    $('#projectId').val(projectId);
                    $('#projectName').text(project.projectName);
                    $('#projectManagerName').text(project.managerName);
                }
                var serverGroups = moduleDetail.serverGroups;
                globalEnvs = moduleDetail.envs;
                initGroup(serverGroups);
                initEnv(moduleDetail.envs);
                initJvmArgs(moduleDetail);
            } else {
                BootstrapDialog.alert(json.message);
            }
        });
    }

    function initJvmArgs(moduleInfo) {
        var jvmHtml = template('jvmTpl', moduleInfo);
        $("#jvmArgsContent").html(jvmHtml);
        if (isMainClass(moduleInfo.restartShell)) {
            $('#jvmArgBox').show();
        }
    }

    function initEnv(envs) {
        for (var i in envs) {
            $("#envs").append("<label style='margin-right:5px;'><input type='radio' name='groupEnv'  value='" + envs[i].envId + "'/>" + envs[i].envName + "</label>");
        }
    }

    function initResinConf(projectModule) {
        if (projectModule.resinConf) {
            var resinConf = projectModule.resinConf;
            $('#domain').val(resinConf.domain);
            $('#aliasDomain').val(resinConf.aliasDomain);
            if (resinConf.httpPort > 0) {
                $('#httpPort').val("" + resinConf.httpPort);
            }
            if (resinConf.serverPort > 0) {
                $('#serverPort').val("" + resinConf.serverPort);
            }
            if (resinConf.watchdogPort > 0) {
                $('#watchdogPort').val("" + resinConf.watchdogPort);
            }
            if (resinConf.threadMax > 0) {
                $('#threadMax').val("" + resinConf.threadMax);
            }
            if (resinConf.keepaliveMax > 0) {
                $('#keepaliveMax').val("" + resinConf.keepaliveMax);
            }
            if (resinConf.keepaliveTimeout > 0) {
                $('#keepaliveTimeout').val("" + resinConf.keepaliveTimeout);
            }
            if (resinConf.socketTimeout > 0) {
                $('#socketTimeout').val("" + resinConf.socketTimeout);
            }
            if (resinConf.createEveryTime) {
                $('#createEveryTime').prop('checked', true);
            }
        }
    }

    function initGroup(serverGroups) {
        if (serverGroups) {
            var groupsHtml = "", groupContentHtml = "";
            for (var i in serverGroups) {
                var group = serverGroups[i];
                var contentId = "serverGroup" + group.groupId;
                //初始化服务器
                var serverHtml = "";
                for (var j in group.servers) {
                    var serverId = "oldServer_" + group.servers[j].serverId;
                    serverHtml = serverHtml + "<div style='margin-top: 10px;' class='row oldServer' data-serverId='" + group.servers[j].serverId +
                        "' data-serverName='" + group.servers[j].serverName +
                        "' data-groupId='" + group.servers[j].groupId +
                        "' data-serverIp='" + group.servers[j].ip +
                        "' id='" + serverId + "'>" +
                        "<div class='col-xs-6'>" + group.servers[j].serverName + "(" + group.servers[j].ip + ")</div>" +
                        "<div class='col-xs-3'><a  onclick='javascript:deleteServer(" + "\"" + serverId + "\"" + ");return false;' href='#' >删除</a></div>" +
                        "<div class='col-xs-3'><a onclick='javascript:editServer(" + "\"" + serverId + "\"" + ");return false;' href='#' >编辑</a></div>" +
                        "</div>";
                }

                var envName = getEnvName(group.envId);
                if (i == 0) {
                    groupsHtml = groupsHtml + "<li style='width: 120px; height: 59px;' id='group_" + group.groupId + "' data-groupId='" + group.groupId + "'data-groupname='" + group.groupName + "' data-groupenv='" + group.envId +
                        "'class='active'><a style='text-align: center;' href='#" + contentId + "' data-toggle='tab'><b class='groupEnvName'>" + group.groupName +
                        "</b><br/><span class='groupEnvChName' style='font-size: 9px;'>" + envName + "</span><br/>" +
                        "<span style='float: left;text-align: left;font-size: 9px;cursor: pointer;' onclick='editGroup(" + group.groupId + ");return false;' ><i class='fa fa-fw fa-edit'></i></span>" +
                        "<span style='float: right;text-align: left;font-size: 9px;cursor: pointer;' onclick='removeGroup(" + group.groupId + ");return false;' > <i class='fa fa-fw fa-remove'></i></span>" +
                        "</a>" +
                        "</a></li>"
                    groupContentHtml = groupContentHtml + "<div class='tab-pane fade in active' id='" + contentId + "'>" + serverHtml +
                        "<div class='row'  style='margin-top: 30px; margin-left: 10px;'>" +
                        "<button class='btn btn-primary' type='button' onclick='openAddServerModel();'>增加服务器</button>" +
                        "</div></div>";
                } else {
                    groupsHtml = groupsHtml + "<li  style='width: 120px; height: 59px;' id='group_" + group.groupId + "' data-groupId='" + group.groupId + "'data-groupname='" + group.groupName + "' data-groupenv='" + group.envId + "'><a style='text-align: center;' href='#" + contentId + "' data-toggle='tab'><b class='groupEnvName'>" + group.groupName +
                        "</b><br/><span class='groupEnvChName' style='font-size: 9px;'>" + envName + "</span><br/>" +
                        "<span style='float: left;text-align: left;font-size: 9px;cursor: pointer;' onclick='editGroup(" + group.groupId + ");return false;' ><i class='fa fa-fw fa-edit'></i></span>" +
                        "<span style='float: right;text-align: left;font-size: 9px;cursor: pointer;' onclick='removeGroup(" + group.groupId + ");return false;'> <i class='fa fa-fw fa-remove'></i></span>" +
                        "</a></a></li>"
                    groupContentHtml = groupContentHtml + "<div class='tab-pane fade' id='" + contentId + "'>" + serverHtml + "<div class='row' style='margin-top: 30px; margin-left: 10px;'>" +
                        "<button class='btn btn-primary' type='button' onclick='openAddServerModel();'>增加服务器</button>" +
                        "</div></div>";
                }
            }
            groupsHtml = groupsHtml + " <button class='btn'  id='addGroupTab' onclick='addNewGroup();'>添加组</button> "
            $("#serverGroups").html(groupsHtml);
            $("#groupContent").html(groupContentHtml);

        } else {
            $("#serverGroups").html(" <button class='btn'   id='addGroupTab' onclick='addNewGroup();'>添加组</button> ");
        }
    }

    function getEnvName(envId) {
        var envName = "";
        for (var i in globalEnvs) {
            if (globalEnvs[i].envId == envId) {
                envName = globalEnvs[i].envName;
            }
        }
        return envName;
    }

    function getModuleInfo(moduleId) {
        $.get("/admin/project/getModule", {'moduleId': moduleId}, function (json) {
            if (json.success) {
                var moduleDetail = json.object;
                var project = moduleDetail.project;
                if (project) {
                    $('#projectId').val(project.projectId);
                    $('#projectName').text(project.projectName);
                    $('#projectManagerName').text(project.managerName);
                    $('#viewProjectLink').prop('href', '/admin/project/viewProject.xhtml?projectId=' + project.projectId);
                }

                var projectModule = moduleDetail.projectModule;
                globalEnvs = moduleDetail.envs;
                if (projectModule) {
                    $('#moduleId').val(projectModule.moduleId);
                    $('#moduleNameZh').val(projectModule.moduleNameZh);
                    $('#moduleName').val(projectModule.moduleName);
                    $("input[name='moduleType'][value='" + projectModule.moduleType + "']").prop("checked", true);
                    $('#repoUrl').val(projectModule.repoUrl);
                    $('#srcPath').val(projectModule.srcPath);
                    $('#svnAccount').val(projectModule.svnAccount);
                    $('#svnPassword').val(projectModule.svnPassword);
                    $('#preShell').val(projectModule.preShell);
                    $('#restartShell').val(projectModule.restartShell);
                    $('#postShell').val(projectModule.postShell);
                    $('#compileShell').val(projectModule.compileShell);
                    $('#stopShell').val(projectModule.stopShell);
                    $('#repoType').val(projectModule.repoType);

                    if (projectModule.needAudit == <%=Constants.TRUE%>) {
                        $('#needAudit').prop("checked", 'checked');
                    }

                    initGroup(projectModule.serverGroups);
                    initJvmArgs(projectModule);
                    initResinConf(projectModule);
                }
                initEnv(moduleDetail.envs);

            } else {
                BootstrapDialog.alert(json.message);
            }
        });
    }

    //提交
    function saveModule() {
        var validateResult = validateForm();
        if (validateResult != '') {
            BootstrapDialog.alert(validateResult);
            return;
        }
        var postData = buildPostData();
        var occupyInfo = checkResinPort(postData);
        if (occupyInfo != '') {
            BootstrapDialog.alert(occupyInfo);
            return;
        }

        $.ajax({
            type: 'POST',
            url: '/admin/project/saveModule.do',
            data: JSON.stringify(postData),
            dataType: 'json',
            contentType: "application/json; charset=utf-8",
            success: function (data) {
                if (data.success) {
                    BootstrapDialog.alert("保存成功", function () {
                        window.location.href = "/admin/project/viewProject.xhtml?projectId=" + $("#projectId").val();
                    });
                } else {
                    BootstrapDialog.alert(data.message);
                }
            },
            error: function (data) {
                BootstrapDialog.alert("保存失败：" + data);
            }

        });

    }

    function buildModuleBaseInfo(projectModule) {
        projectModule.moduleId = $('#moduleId').val();
        projectModule.projectId = $('#projectId').val();
        projectModule.moduleNameZh = $('#moduleNameZh').val().trim();
        projectModule.moduleName = $('#moduleName').val().trim();
        projectModule.moduleType = $('input:radio[name="moduleType"]:checked').val();
        projectModule.repoUrl = $('#repoUrl').val().trim();
        projectModule.srcPath = $('#srcPath').val();
        projectModule.repoType = $('#repoType').val();

        projectModule.preShell = $('#preShell').val();
        projectModule.postShell = $('#postShell').val();
        projectModule.compileShell = $('#compileShell').val();
        projectModule.stopShell = $('#stopShell').val();
        projectModule.restartShell = $('#restartShell').val();

        projectModule.needAudit = $('#needAudit').is(':checked') ? 1 : 0;
        projectModule.svnAccount = $('#svnAccount').val().trim();
        projectModule.svnPassword = $('#svnPassword').val().trim();
    }
    function buildPostData() {
        var projectModule = {};
        buildModuleBaseInfo(projectModule);
        buildServerGroupData(projectModule);
        buildModuleJvmData(projectModule);
        buildResinConfData(projectModule);
        return projectModule;
    }

    function needJvmArgs() {
        var moduleType = $('input:radio[name="moduleType"]:checked').val();
        var restartShell = $.trim($('#restartShell').val());
        if ((moduleType && moduleType == <%=ModuleType.WEB_PROJECT.getValue()%>)
            || isMainClass(restartShell)) {
            return true;
        }
        return false;
    }

    function buildModuleJvmData(projectModule) {
        var moduleJvms = [];
        var moduleId = $("#moduleId").val();

        if (needJvmArgs()) {
            $("#jvmArgsContent").children(".form-group").each(function (index, e) {
                var moduleJvm = {};
                moduleJvm.moduleId = moduleId;
                moduleJvm.envId = $(this).attr("data-envId");
                moduleJvm.envName = $(this).attr("data-envName");
                moduleJvm.moduleJvmId = $(this).attr("data-moduleJvmId");
                moduleJvm.jvmArgs = $(this).find(".jvmArgs").first().val();
                moduleJvms.push(moduleJvm);
            });
        }
        projectModule.moduleJvms = moduleJvms;
    }

    function buildServerGroupData(module) {
        var groups = new Array();
        $("#serverGroups").children("li").each(function (index, e) {
            var group = {};
            group.envId = $(this).attr("data-groupenv");
            group.groupName = $(this).attr("data-groupname");
            if ($("#moduleId").val()) {
                group.moduleId = $("#moduleId").val();
            }
            if ($(this).attr("data-groupId")) {
                group.groupId = $(this).attr("data-groupId");
            }

            var targetId = $(this).children("a").attr("href");
            var serverId = targetId.substring(1, targetId.length);
            var servers = new Array();
            $("#" + serverId).children(".newServer").each(function () {
                var server = {};
                server.serverName = $(this).attr("data-servername");
                server.ip = $(this).attr("data-serverip");
                servers.push(server);
            });
            $("#" + serverId).children(".oldServer").each(function () {
                var server = {};
                server.serverId = $(this).attr("data-serverid");
                server.groupId = $(this).attr("data-groupId");
                server.serverName = $(this).attr("data-servername");
                server.ip = $(this).attr("data-serverip");
                servers.push(server);
            });
            group.servers = servers;
            groups.push(group);
        });
        module.serverGroups = groups;

    }

    function buildResinConfData(module) {
        var domain = $.trim($('#domain').val());
        var aliasDomain = $.trim($('#aliasDomain').val());
        var httpPort = $.trim($('#httpPort').val());
        var serverPort = $.trim($('#serverPort').val());
        var watchdogPort = $.trim($('#watchdogPort').val());
        var threadMax = $.trim($('#threadMax').val());
        var keepaliveMax = $.trim($('#keepaliveMax').val());
        var keepaliveTimeout = $.trim($('#keepaliveTimeout').val());
        var socketTimeout = $.trim($('#socketTimeout').val());
        var createEveryTime = $('#createEveryTime').prop('checked');
        var resinConf = {
            domain: domain,
            aliasDomain: aliasDomain,
            httpPort: (httpPort == '' ? '0' : httpPort),
            serverPort: (serverPort == '' ? '0' : serverPort),
            watchdogPort: (watchdogPort == '' ? '0' : watchdogPort),
            threadMax: (threadMax == '' ? '0' : threadMax),
            keepaliveMax: (keepaliveMax == '' ? '0' : keepaliveMax),
            keepaliveTimeout: (keepaliveTimeout == '' ? '0' : keepaliveTimeout),
            socketTimeout: (socketTimeout == '' ? '0' : socketTimeout),
            createEveryTime: (createEveryTime ? true : false)
        };
        module.resinConf = resinConf;
    }

    function ping(ip) {

        $.ajax({
            type: 'GET',
            url: '/admin/project/ping?ip=' + ip,
            contentType: "application/json; charset=utf-8",
            success: function (data) {
                if (!data.success) {
                    BootstrapDialog.alert("服务器 : " + ip + " ping不通,请检测是否正确");
                }
            }
        });
    }

    //检测resin的http端口是否被占用
    function checkResinPort(projectModule) {
        var occupyInfo = "";
        $.ajax({
            type: 'post',
            url: '/admin/project/checkResinPort',
            async: false,
            data: JSON.stringify(projectModule),
            dataType: 'json',
            contentType: "application/json; charset=utf-8",
            success: function (data) {
                if (!data.success) {
                    occupyInfo = data.message;
                }
            }
        });
        return occupyInfo;
    }
    // 校验参数
    function validateForm() {
        var message = "";
        if ($("#moduleNameZh").val() == '') {
            message = '模块名称不能为空';
        }
        if ($("#repoType").val() == '') {
            message = '版本管理类型不能为空，请选择是SVN或GIT';
        }
        if ($("#svnCheckoutDir").val() == '') {
            message = 'svn/git 地址不能为空';
        }
        if ($("#moduleName").val() == '') {
            message = 'module名称不能为空';
        }
        if ($("#svnAccount").val() == '') {
            message = 'svn/git账号不能为空';
        }
        if ($("#svnPassword").val() == '') {
            message = 'svn/git密码不能为空';
        }
        if (!$("input[name=moduleType]:checked").val()) {
            message = '请选择模块类型';
        }
        return message;
    }


    // 服务器组创建
    function addServerGroup() {
        //在tab创建一个 将数据存储在属性
        var groupEnv = $('input:radio[name="groupEnv"]:checked').val();
        if (!groupEnv) {
            BootstrapDialog.alert('请选择环境');
            return;
        }
        var envName = $('input:radio[name="groupEnv"]:checked').parent().text();
        var groupName = $("#groupName").val();
        var currentGroupId = $('#currentGroupId').val();

        if (currentGroupId) {
            // 编辑
            $('#' + currentGroupId).attr("data-groupenv", groupEnv);
            $('#' + currentGroupId).attr("data-groupname", groupName);
            $('#' + currentGroupId).find(".groupEnvName").text(groupName);
            $('#' + currentGroupId).find(".groupEnvChName").text(envName);

            $("#groupEnv").val("");
            $("#groupName").val("");
            $("#currentGroupId").val("");
            $('input:radio[name="groupEnv"]').removeAttr("checked");

            $("#addGroup").modal('hide');
        } else {
            // 新增
            var currentTime = (new Date()).getTime();
            var groupId = "newGroup_" + currentTime;
            var newGroupTab = "<li style='width: 120px; height: 59px;' id='group_" + currentTime + "' data-groupEnv='" + groupEnv + "' data-groupName='" + groupName + "' class='newGroup'>" +
                "<a style='text-align: center;' href='#" + groupId + "'  data-toggle='tab'><b class='groupEnvName'>" + groupName + "</b><br/><span class='groupEnvChName' style='font-size: 9px;'>" + envName + "</span><br/>" +
                "<span style='float: left;text-align: left;font-size: 9px;cursor: pointer;' onclick='editGroup(" + currentTime + ");return false;' ><i class='fa fa-fw fa-edit'></i></span>" +
                "<span style='float: right;text-align: left;font-size: 9px;cursor: pointer;' onclick='removeGroup(" + currentTime + ");return false;' > <i class='fa fa-fw fa-remove'></i></span>" +
                "</a></li>";
            $("#addGroupTab").before(newGroupTab);
            $("#groupContent").append("<div class='tab-pane fade' id='" + groupId + "'>" +
                "<div class='row'  style='margin-top: 30px; margin-left: 10px;'>" +
                "<button class='btn btn-primary' type='button' onclick='openAddServerModel();'>增加服务器</button>" +
                "</div></div>");
            //清空
            $("#groupEnv").val("");
            $("#groupName").val("");
            $("#currentGroupId").val("");
            $("#addGroup").modal('hide');
            $('input:radio[name="groupEnv"]').removeAttr("checked");

            // 跳转到新增的 tab
            $('#serverGroups li:last a').tab('show');
        }

    }
    //创建服务器
    function addServer() {

        var currentServerId = $("#currentServerId").val();

        if (currentServerId) {
            var serverIP = $.trim($("#serverIP1").val());
            var serverName = $.trim($("#serverName1").val());
            if (serverIP && serverName) {
                $("#" + currentServerId).attr("data-serverName", serverName);
                $("#" + currentServerId).attr("data-serverIP", serverIP);
                $("#" + currentServerId).children("div").first().text(serverName + "(" + serverIP + ")");
                $("#addServerModel").modal('hide');
                $("#serverIP1").val('');
                $("#serverName1").val('');
            } else {
                BootstrapDialog.alert('请输入完整的IP和名称, ip: ' + serverIP + ', 名称: ' + serverName);
            }
        } else if (checkServerNameAndIP()) {
            for (var i = 1; i <= 5; i++) {
                var serverIP = $.trim($("#serverIP" + i).val());
                var serverName = $.trim($("#serverName" + i).val());
                if (serverIP && serverName) {
                    var serverId = "newServer_" + (new Date()).getTime();
                    var serverHtml = "<div style='margin-top: 10px;' class='row newServer' data-serverName='" + serverName + "' data-serverIp='" + serverIP + "' id='" + serverId + "'>" +
                        "<div class='col-xs-6'>" + serverName + "(" + serverIP + ")</div>" +
                        "<div class='col-xs-3'><a  onclick='javascript:deleteServer(" + "\"" + serverId + "\"" + ");return false;' href='#' >删除</a></div>" +
                        "<div class='col-xs-3'><a onclick='javascript:editServer(" + "\"" + serverId + "\"" + ");return false;' href='#' >编辑</a></div>" +
                        "</div>";
                    $("#groupContent").children(".active").children("div").first().before(serverHtml);
                    $("#serverIP" + i).val('');
                    $("#serverName" + i).val('');
                }
            }
            $("#addServerModel").modal('hide');
        }

//        ping(serverIP);
    }

    function checkServerNameAndIP() {
        for (var i = 1; i <= 5; i++) {
            var serverIP = $.trim($("#serverIP" + i).val());
            var serverName = $.trim($("#serverName" + i).val());
            if ((serverIP == '' && serverName != '')
                || (serverIP != '' && serverName == '')) {
                BootstrapDialog.alert('请输入完整的IP和名称, ip: ' + serverIP + ", 名称: " + serverName);
                return false;
            }
        }
        return true;
    }

    function deleteServer(serverId) {
        $("#" + serverId).remove();
    }

    // 弹窗
    function addNewGroup() {
        $("#addGroup").modal('show');
        $("#addGroup").find('.modal-title').first().text("添加服务器组");
        $('#groupName').val("");
        $("#currentGroupId").val("");
        $(':radio[name="groupEnv"]').removeAttr("checked");
    }

    function editGroup(groupId) {
        $("#addGroup").modal('show');
        $("#addGroup").find('.modal-title').first().text("编辑服务器组");

        var env = $('#group_' + groupId).attr("data-groupenv");
        $(':radio[name="groupEnv"][value="' + env + '"]')[0].checked = true;
        var groupName = $('#group_' + groupId).attr("data-groupName");
        $('#groupName').val(groupName);
        $("#currentGroupId").val('group_' + groupId);
    }

    function removeGroup(groupId) {
        BootstrapDialog.confirm('确定要删除服务器组？', function (result) {
            if (result) {
                $("#group_" + groupId).remove();
                $("#serverGroup" + groupId).remove();
                // 新增的服务器组
                $("#newGroup_" + groupId).remove();
            }
        });
    }

    function editServer(serverId) {
        $(".moreIp").hide();
        $("#addServerModel").modal('show');
        $("#currentServerId").val(serverId);
        $("#serverName1").val($("#" + serverId).attr("data-serverName"));
        $("#serverIP1").val($("#" + serverId).attr("data-serverIP"));

    }
    function openAddServerModel() {
        $(".moreIp").show();
        $("#addServerModel").modal('show');
    }

    var _allServers = [];
    var _valueObj = null;
    // 加载服务器列表
    function loadAllServers(valueObj) {
        $.getJSON("/admin/project/getAllServers", {}, function (json) {
            if (json.success) {
                _allServers = json.object;
                filterServer(valueObj);
            } // end if
        });
    }

    function filterServer(valueObj) {
//        console.log('filterServer, value: ' + $(valueObj).val());
        if (_allServers && _allServers.length) {
            var value = $.trim($(valueObj).val());
            var filteredServers = [];
            for (var i = 0; i < _allServers.length; i++) {
                var server = _allServers[i];
                var match = true;
                if (value) {
                    match = (server.innerIpAddress.indexOf(value) >= 0) || (server.instanceName.indexOf(value) >= 0);
                }
                if (match) {
                    filteredServers.push(server);
                }
            }

            var data = {'data': filteredServers};
            var tmpHtml = template('chooseServerTpl', data);
            $('#serverListDivContent').html(tmpHtml);

            var offset = $(valueObj).offset();
            var top = offset.top;
            var left = offset.left;
            var height = $(valueObj).height() + 11;
            $('#serverListDiv').css({"position": "absolute", left: left, top: top + height})
                .show();

            _valueObj = valueObj;
        } // end if
    }

    function hideServerList(event) {
        var x = pageX;
        var y = pageY;
        var offset = $('#serverListDiv').offset();
        var x2 = offset.left + $('#serverListDiv').width();
        var y2 = offset.top + $('#serverListDiv').height();
        console.log('x: ' + x + ", y: " + y + ', left: ' + offset.left + ', top: ' + offset.top + ', x2: ' + x2 + ', y2: ' + y2);
        if (x < offset.left || y < offset.top || x > x2 || y > y2) {
            $('#serverListDiv').hide();
        }
    }

    // 选择服务器
    function chooseServer(instanceName, ip) {
        if (_valueObj) {
            var divObj = $(_valueObj).parent().parent();
            var inputs = $(divObj).find('input[type=text]');
            inputs[0].value = instanceName;
            inputs[1].value = ip;
            _valueObj = null;
            $('#serverListDiv').hide();
        }
    }
</script>
</body>
</html>
