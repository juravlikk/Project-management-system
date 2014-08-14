<?php
	$HostName = "projectl.no-ip.org";
	$UserName = "alex";
	$Password = "projectl";
	$DBName = "ProjectL";

	if(!mssql_connect($HostName, $UserName, $Password)) {
		exit("Can not connect to MSSQL server");
	} else {
		mssql_select_db($DBName);
	}
?>
