<!DOCTYPE html>
<html lang="en">
<head>

    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <link rel="stylesheet" href="/css/bootstrap.min.css">
    <link rel="stylesheet" href="/css/bootstrap-theme.min.css">
    <script src="/js/bootstrap.min.js"></script>
    <style type="text/css">
        .title {
            font-size: 24px;
            text-align: center;
        }
    </style>
</head>
<body class="container">

<a href="/prepare?sep=0">数据准备--好评差评建议合并</a>
<a href="/prepare?sep=1">数据准备--好评差评建议分开</a>

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

        <td>
            <a href="/showReviews?id=${statReviewCount.id?c}&goodOrBad=1">查看原始评论</a>
            <a href="/show?id=${statReviewCount.id?c}&goodOrBad=1">查看好评聚类</a>
            <a href="/show?id=${statReviewCount.id?c}&goodOrBad=2">查看差评聚类</a>
            <a href="/show?id=${statReviewCount.id?c}&goodOrBad=3">查看建议聚类</a>
        </td>
    <#--<td><a href="./${statReviewCount.id?c}-details.html">查看ALL</a></td>-->
    </tr>
    </#list>
    </tbody>
</table>
<#--<script src="/js/adaa.js"></script>-->
</body>
</html>