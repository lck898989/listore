<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>My JSP 'index.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
    <%
        out.write(request.getSession().getServletContext().getRealPath("/upload/"));
        String url = request.getRequestURL().toString();
        System.out.println("url is " + url);
        out.println("url :" + url + "<br>");
        //得到URI
        String uri = request.getRequestURI();
        System.out.println("uri is " + uri);
        out.println("uri is " + uri +"<br>");
        //得到请求方的完整主机名
        String remote_host = request.getRemoteHost();
        System.out.println("RemostHost is " + remote_host);
        out.println("RemotHost is " + remote_host + "<br>" );
        //得到请求地方的网络端口号
        int remote_port = request.getRemotePort();
        out.println("remote port is " + remote_port+ "<br>");
        
        //得到服务器本机使用的端口号
        int local_port = request.getLocalPort();
        out.println("local port is " + local_port+ "<br>");
        //得到服务器本机的IP地址
        String local_addr = request.getLocalAddr();
        out.println("local_addr is " + local_addr+ "<br>");
        //得到服务器主机的主机名
        String local_name = request.getLocalName();
        out.println("local_name is "+local_name+ "<br>");
        out.println(request.getSession().getServletContext().toString());
         
     %>
  </head>
  
  <body>
  	<form action="user/login" method="post">
			    用户名：<input type="text" name="username"/>
			    密    码：<input type="password" name="password"/>
			    <input type="submit" value="提交"/>
			    
    </form>
    springMvc文件上传
    <form action="manager/product/uploadFile" method="post" enctype="multipart/form-data">
           <input type="file" name="upload_fileName"/>
           <input type="submit" value="上传文件">
    
    </form>
    springMvc富文本文件上传
    <form action="manager/product/richText_img_Upload" method="post" enctype="multipart/form-data">
           <input type="file" name="upload_fileName"/>
           <input type="submit" value="富文本上传">
    
    </form>
  </body>
</html>
