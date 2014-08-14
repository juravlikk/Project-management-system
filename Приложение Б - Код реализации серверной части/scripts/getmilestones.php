<?php
require_once("db/mssql.php");

require_once "db/role.php";
require_once "db/privilegedUser.php";

if (!isset($_POST['getmilestones'])) exit;
$request = json_decode($_POST['getmilestones']);

if (empty($request->{'login'})) {
	$login = NULL;
} else $login = $request->{'login'};

if (isset($request->{'check'})) {
    $u = User::getByUsername($request->{'check'});
}

if ($u->hasPrivilege("getmilestones")) {
	require_once("functions.php");
	$res = getMilestones($login);
} else {
	$res = json_encode(array("Error" => "Wrong options"));
	header("Content-type: application/json");
	exit($res);
}

header("Content-type: application/json");
exit($res);
?>
