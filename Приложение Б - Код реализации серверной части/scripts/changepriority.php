<?php
require_once("db/mssql.php");

require_once "db/role.php";
require_once "db/privilegedUser.php";

if (!isset($_POST['changepriority'])) exit;
$request = json_decode($_POST['changepriority'], true);
if (empty($request['priority']) or empty($request['task'])) {
	$res = json_encode(array("Error" => "Wrong options"));
	header("Content-type: application/json");
	exit($res);	
}
$task = $request['task'];
$priority = $request['priority'];

if (isset($request['check'])) {
    $u = User::getByUsername($request['check']);
}

if ($u->hasPrivilege("changepriority")) {
	require_once("functions.php");
	$res = changePriority($task, $priority);
} else {
	$res = json_encode(array("Error" => "Wrong options"));
	header("Content-type: application/json");
	exit($res);
}

header("Content-type: application/json");
exit($res);
?>
