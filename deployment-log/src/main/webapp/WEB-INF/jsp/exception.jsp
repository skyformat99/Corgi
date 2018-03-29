<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<head>
<meta charset="utf-8">
<meta name="viewport"
	content="width=device-width,initial-scale=1, user-scalable=no, minimal-ui" />
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
<meta content="telephone=no,email=no" name="format-detection" />
<link rel="apple-touch-fullscreen" content="YES" />
<title>贝聊</title>
<style type="text/css">
p {
	font-size: 12px;
	color: #FF0000;
	padding-bottom: 10px;
}
</style>
</head>
<body>
	<h1>发生错误</h1>
	<p>${message}</p>
	<br />
	<input type="button" onclick="history.back()" value="返回上一页">
	</form>
</body>
</html>
