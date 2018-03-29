<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="content-type" content="text/html;charset=UTF-8">
    <meta content='width=device-width, initial-scale=1, user-scalable=yes' name='viewport'>
    <title>编译日志</title>
    <script src="/static/plugins/jQuery/jQuery-2.1.4.min.js"></script>
    <style type="text/css">
        body {
            font-size: 14px;
            font-family: 'Source Sans Pro', 'Helvetica Neue', Helvetica, Arial, sans-serif;
        }
    </style>
</head>
<body>
<h3>编译中……</h3>
<div id="logBody">
</div>
<script type="text/javascript">
    var interval = 1000;
    var historyId = 0;
    var offset = 0;


    $.getUrlParam = function (name) {
        var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
        var r = window.location.search.substr(1).match(reg);
        if (r != null) return unescape(r[2]);
        return null;
    };

    $(function () {
        historyId = $.getUrlParam('historyId');
        if (historyId && historyId > 0) {
            window.setTimeout(loadLog, interval);
        }
    });

    function loadLog() {
        $.getJSON("/admin/deploy/getCompileLog", {'historyId': historyId, "offset": offset}, function (data) {
            var logs = data.logs;
            offset = data.offset;
            if (logs && logs.length && logs.length > 0) {
                var logBody = $('#logBody');
                for (var i = 0; i < logs.length; i++) {
                    var log = logs[i];
                    log = log.replace(/\n/gi, '<br/>');
                    logBody.append(log + '<br/>');
                }
                $(document.body).scrollTop($(document.body)[0].scrollHeight);
            }
            window.setTimeout(loadLog, interval);
        });
    }
</script>
</body>
</html>
