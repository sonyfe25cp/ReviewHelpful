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
                    text: 'Jaccard距离聚类中心点情况'
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
            $('#container2').highcharts({
                chart: {
                    type: 'scatter',
                    zoomType: 'xy'
                },
                title: {
                    text: 'cosine距离聚类中心点情况'
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
                        }
                    }
                },
                series: [{
                    name: '评论',
                    color: 'rgba(223, 83, 83, .5)',
                    data: [${data2}]
                }]
            });
            $('#container3').highcharts({
                chart: {
                    type: 'scatter',
                    zoomType: 'xy'
                },
                title: {
                    text: '基于标签距离的聚类中心点情况'
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
                        }
                    }
                },
                series: [{
                    name: '评论',
                    color: 'rgba(223, 83, 83, .5)',
                    data: [${data3}]
                }]
            });
        });
    </script>
</head>
<body>
<h3>${companyName}</h3>
<a href="/showDetails?id=${id}&goodOrBad=${goodOrBad}&method=Jaccard&minRou=5">Jaccard距离</a>
<div id="container" style="min-width:700px;height:400px"></div>

<a href="/showDetails?id=${id}&goodOrBad=${goodOrBad}&method=Cosine&minRou=5">cosine距离</a>
<div id="container2" style="min-width:700px;height:400px"></div>

<a href="/showDetails?id=${id}&goodOrBad=${goodOrBad}&method=Customed&minRou=5">基于标签的距离</a>
<div id="container3" style="min-width:700px;height:400px"></div>
</body>
</html>