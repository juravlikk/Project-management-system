<?php
require_once("db/mssql.php");

require_once "db/role.php";
require_once "db/privilegedUser.php";

if (!isset($_POST['createmilestone'])) exit;
$request = json_decode($_POST['createmilestone'], true);
if (empty($request['name']) or empty($request['login']) or empty($request['deadline']) or empty($request['tasks'])) {
	$res = json_encode(array("Error" => "Wrong options"));
	header("Content-type: application/json");
	exit($res);	
}
$login = $request['login'];
$name = $request['name'];
$deadline = $request['deadline'];
$tasks = $request['tasks'];

if (isset($request['check'])) {
    $u = User::getByUsername($request['check']);
}

if ($u->hasPrivilege("createmilestone")) {
	require_once("functions.php");
	$res = createMilestone($login, $name, $deadline, $tasks);
} else {
	$res = json_encode(array("Error" => "Wrong options"));
	header("Content-type: application/json");
	exit($res);
}

header("Content-type: application/json");
exit($res);
?>
