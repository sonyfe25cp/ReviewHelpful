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
    </style>
</head>
<body class="container">
<table>
    <thead>
    <th>ID</th>
    <th>公司名</th>
    <th>评论数</th>
    <th>查看</th>
    </thead>
    <tbody>
    <#list statReviewCounts as statReviewCount>
    <tr>
        <td>${statReviewCount.id}</td>
        <td>${statReviewCount.name}</td>
        <td>${statReviewCount.count}</td>
        <td><a href="./${statReviewCount.id?c}-good-details.html">查看好评</a></td>
        <td><a href="./${statReviewCount.id?c}-bad-details.html">查看差评</a></td>
        <td><a href="./${statReviewCount.id?c}-nutral-details.html">查看建议</a></td>
        <td><a href="./${statReviewCount.id?c}-details.html">查看ALL</a></td>
    </tr>
    </#list>
    </tbody>
</table>
</body>
</html>