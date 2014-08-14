<?php
require_once("db/mssql.php");

require_once "db/role.php";
require_once "db/privilegedUser.php";

if (!isset($_POST['getworkers'])) exit;
$request = json_decode($_POST['getworkers']);

if (isset($request->{'check'})) {
    $u = User::getByUsername($request->{'check'});
}

if ($u->hasPrivilege("getworkers")) {
	require_once("functions.php");
	$res = getWorkers();
} else {
	$res = json_encode(array("Error" => "Wrong options"));
	header("Content-type: application/json");
	exit($res);
}

header("Content-type: application/json");
exit($res);
?>
