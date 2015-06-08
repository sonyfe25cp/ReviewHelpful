<!doctype html>
<html lang="en">
<head>
    <script type="text/javascript" src="/js/jquery-1.11.1.js"></script>
    <script type="text/javascript" src="/js/highcharts.js"></script>

</head>
<body>
<h3>${companyName}</h3>

<#if differentResults??>

    <#list differentResults?keys as key>

    <script>
        $(function () {
            $('#${key}').highcharts({
                chart: {
                    type: 'scatter',
                    zoomType: 'xy'
                },
                title: {
                    text: '${key}距离聚类中心点情况'
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
                    data: [${differentResults[key]}]
                }]
            });
        });
    </script>
    <a href="/showDetails?id=${id}&goodOrBad=${goodOrBad}&method=${key}&minRou=5">${key}距离</a>
    <div id="${key}" style="min-width:700px;height:400px"></div>

    </#list>

</#if>






</body>
</html>