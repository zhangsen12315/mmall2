<%@page contentType = "text/html;charset=gb2312" %>

<html>
<body>
<h2>Tomcat1 World!</h2>
<h2>Tomcat1 World!</h2>
<h2>Tomcat1 World!</h2>
<meta charset="utf-8">

<form name="form1" action="/manage/product/upload.do" method="post" enctype="multipart/form-data">
    <input type="file" name="upload_file" />
    <input type="submit" value="springmvc�ϴ��ļ�" />
</form>

<form name="form2" action="/manage/product/richtext_img_upload.do" method="post" enctype="multipart/form-data">
    <input type="file" name="upload_file" />
    <input type="submit" value="���ı�ͼƬ�ϴ��ļ�" />
</form>

</body>
</html>
