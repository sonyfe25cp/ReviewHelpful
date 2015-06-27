<!DOCTYPE html>
<html lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap-theme.min.css">
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/js/bootstrap.min.js"></script>
    <style type="text/css">
        .title {
            font-size: 24px;
            text-align: center;
        }
        .item{
            border: dashed;
            border-color: gray;
            margin-bottom: 2px;
        }
    </style>
</head>
<body>
<div class="title">企业名称：${reviewObject.companyName}</div>

<div class="row">
    <div class="col-md-3" style="border:dotted; border-color: green">
        <div class="title">原始评论</div>
    <#list reviewObject.originReviews as review>
        <#include "item.ftl">
    </#list>
    </div>
</div>
</body>
</html>
