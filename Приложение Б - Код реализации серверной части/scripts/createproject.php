<?php
require_once("db/mssql.php");

require_once "db/role.php";
require_once "db/privilegedUser.php";

if (!isset($_POST['createproject'])) exit;
$request = json_decode($_POST['createproject'], true);
if (empty($request['name']) or empty($request['deadline']) or empty($request['priority']) or empty($request['comments'])) {
	$res = json_encode(array("Error" => "Wrong options"));
	header("Content-type: application/json");
	exit($res);	
}
$name = $request['name'];
$deadline = $request['deadline'];
$priority = $request['priority'];
$comments = $request['comments'];

if (isset($request['check'])) {
    $u = User::getByUsername($request['check']);
}

if ($u->hasPrivilege("createproject")) {
	require_once("functions.php");
	$res = createProject($name, $deadline, $priority, $comments);
} else {
	$res = json_encode(array("Error" => "Wrong options"));
	header("Content-type: application/json");
	exit($res);
}

header("Content-type: application/json");
exit($res);
?>
