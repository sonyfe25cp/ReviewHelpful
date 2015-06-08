<!DOCTYPE HTML>
<html>
<head>
	<meta http-equiv="content-type" content="text/html; charset=UTF-8" />
	<title>Bootstrap 实例</title>
   <meta name="viewport" content="width=device-width, initial-scale=1">
   <link href="../css/bootstrap.min.css" type="text/css" rel="stylesheet" />
   <link href="../css/mainpage.css" type="text/css" rel="stylesheet" />
   <script type="text/javascript" src="../js/jquery-1.11.0.min.js"></script>
   <script type="text/javascript" src="../js/bootstrap.min.js"></script>
   <script type="text/javascript" src="../js/mainpage.js"></script>
</head>
<body>
<div class="container">
	<div class="row page_header">
		<div class="col-md-6"></div>
		<div class="col-md-6">
			<div class="row">
				<form class="form-inline">
					<div class="form-group" style="width: 175px">
						<input type="text" class="form-control" placeholder="请搜索"/ style="width: 70%;height: 28px">
						<button type="button" class="btn btn-default btn-sm form-control" style="height: 28px">
			         	<span class="glyphicon glyphicon-search"></span>
			         </button>
		         </div>
		         <div class="form-group" style="width: 115px">
		         	<label class="" style="color: #FFF;font-size: 16px;margin: 0px">
		         		<span class="glyphicon glyphicon-user"></span>欢迎您admin
		         	</label>
		         </div>
		         <div class="form-group" style="margin-left:5px;width: 85px">
		         	<a style="color: #FFF;font-size: 16px ">
		         		<span class="glyphicon glyphicon-folder-open"></span>个人中心
		         	</a>
		         </div>
		         <div class="form-group" style="width: 85px">
		         	<a style="color: #FFF;font-size: 16px ">
		         		<span class="glyphicon glyphicon-cog"></span>系统管理
		         	</a>
		         </div>
		         <div class="form-group" style="width: 85px">
		         	<a style="color: #FFF;font-size: 16px ">
		         		<span class="glyphicon glyphicon-off"></span>注销登录
		         	</a>
		         </div>
		      </form>
			</div>
		</div>
	</div>
	<div class="row" style="margin-top: 6px">
		<nav class="navbar navbar-default col-md-3 sidebar">
			<div class="nav_header">
				<a href="#nav_list1" class="" data-toggle="collapse" style="color: #333;">
					国内行业动态<span class="glyphicon glyphicon-minus nav_header_label" style="float: right;line-height: 2.3em"></span>
				</a>
			</div>
			<div>
				<ul id="nav_list1" class="collapse in nav nav-list">
					<li>
						<a href="javascript:void(0)" onclick="loadPage('./index')">手工业发展</a>
					</li>
					<li>
						<a href="javascript:void(0)" onclick="loadPage('subpage2.html')">商业发展</a>
					</li>
					<li>
						<a href="javascript:void(0)" onclick="">饮食业发展</a><li>
						<a href="" onclick="loadPage('loadtest.html')">手工业发展</a>
					</li>
					<li>
						<a href="">商业发展</a>
					</li>
					<li>
						<a href="">饮食业发展</a>
					</li>
				</ul>
			</div>
			<div class="nav_header">
				<a href="#nav_list2" class="" data-toggle="collapse" style="color: #333;">
					海外情报<span class="glyphicon glyphicon-plus nav_header_label" style="float: right;line-height: 2.3em"></span>
				</a>
			</div>
			<div>
				<ul id="nav_list2" class="collapse nav nav-list">
					<li>
						sddd
					</li>
					<li>
						sddd
					</li>
					<li>
						sddd
					</li>
				</ul>
			</div>
			<div class="nav_header">
				<a href="#nav_list3" class="" data-toggle="collapse" style="color: #333;">
					科技动态<span class="glyphicon glyphicon-plus nav_header_label" style="float: right;line-height: 2.3em"></span>
				</a>
			</div>
			<div>
				<ul id="nav_list3" class="collapse nav nav-list">
					<li>
						sddd
					</li>
					<li>
						sddd
					</li>
					<li>
						sddd
					</li>
				</ul>
			</div>
			<div class="nav_header">
				<a href="#nav_list4" class="" data-toggle="collapse" style="color: #333;">
					政策法规<span class="glyphicon glyphicon-plus nav_header_label" style="float: right;line-height: 2.3em"></span>
				</a>
			</div>
			<div>
				<ul id="nav_list4" class="collapse nav nav-list">
					<li>
						sddd
					</li>
					<li>
						sddd
					</li>
					<li>
						sddd
					</li>
				</ul>
			</div>
			<div class="nav_header">
				<a href="#nav_list5" class="" data-toggle="collapse" style="color: #333;">
					展会咨询<span class="glyphicon glyphicon-plus nav_header_label" style="float: right;line-height: 2.3em"></span>
				</a>
			</div>
			<div>
				<ul id="nav_list5" class="collapse nav nav-list">
					<li>
						sddd
					</li>
					<li>
						sddd
					</li>
					<li>
						sddd
					</li>
				</ul>
			</div>
		</nav>
		<!-- 主体内容动态加载 -->
        <div class="contentwindow col-md-9" id="loadframe" style="height: 580px">

        </div>
    </div>

    <hr/>
    <!-- FOOTER -->
    <footer>
    	<p>&copy; 2013 BIT DLDE</p>
    </footer>
   </div>
 </body>
</html>