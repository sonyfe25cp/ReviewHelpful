<!doctype html>
<html lang="en">
<head>
    <script type="text/javascript" src="/js/jquery-1.11.1.js"></script>
    <script type="text/javascript" src="/js/highcharts.js"></script>
    <script>
        $(function () {
            $('#container').highcharts({
                chart: {
                    type: 'scatter',
                    zoomType: 'xy'
                },
                title: {
                    text: '${method}距离聚类中心点情况'
                },
                subtitle: {
                    text: '${companyName}'
                },
                xAxis: {
                    title: {
                        enabled: true,
                        text: '密度'
                    },
                    startOnTick: true,
                    endOnTick: true,
                    showLastLabel: true
                },
                yAxis: {
                    title: {
                        text: '距离'
                    }
                },
                legend: {
                    layout: 'vertical',
                    align: 'left',
                    verticalAlign: 'top',
                    x: 100,
                    y: 70,
                    floating: true,
                    backgroundColor: '#FFFFFF',
                    borderWidth: 1
                },
                plotOptions: {
                    scatter: {
                        marker: {
                            radius: 5,
                            states: {
                                hover: {
                                    enabled: true,
                                    lineColor: 'rgb(100,100,100)'
                                }
                            }
                        },
                        states: {
                            hover: {
                                marker: {
                                    enabled: false
                                }
                            }
                        },
                        tooltip: {
                            headerFormat: '<b>{series.name}</b><br>',
                            pointFormat: '{point.extra}'
//                            pointFormat: '{point.x} cm, {point.y} kg'
                        }
                    }
                },
                series: [{
                    name: '评论',
                    color: 'rgba(223, 83, 83, .5)',
                    data: [${data}]
                }]
            });
        });
    </script>
    <style>
        .liebiao {
            float: left;
            width: 230px;
        }

        .item {
            margin-bottom: 5px;

        }

        .centroid {
            background-color: chartreuse;
        }
    </style>
</head>
<body>
<h3>${companyName}</h3>

<div id="container" style="min-width:700px;height:400px"></div>


<#if cluster?? && cluster?size != 0>
    <#list cluster?keys as key>
        <ul class="liebiao">
            ${key?string}
            <#list cluster[key] as review>
                <#if review.centroid>
                    <li class="centroid">
                <#else>
                    <li class="item">
                </#if>
                    ${review.text}
                </li>
        </#list>
    </ul>
    </#list>
<#else>
没有符合邻居数>${minRou}的聚类中心点
</#if>


</body>
</html>