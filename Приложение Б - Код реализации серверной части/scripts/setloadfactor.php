<?php
require_once("db/mssql.php");

require_once "db/role.php";
require_once "db/privilegedUser.php";

if (!isset($_POST['setloadfactor'])) exit;
$request = json_decode($_POST['setloadfactor'], true);
if (empty($request['loadfactor']) or empty($request['login'])) {
	$res = json_encode(array("Error" => "Wrong options"));
	header("Content-type: application/json");
	exit($res);	
}
$login = $request['login'];
$load = $request['loadfactor'];

if (isset($request['check'])) {
    $u = User::getByUsername($request['check']);
}

if ($u->hasPrivilege("setloadfactor")) {
	require_once("functions.php");
	$res = setLoadFactor($login, $load);
} else {
	$res = json_encode(array("Error" => "Wrong options"));
	header("Content-type: application/json");
	exit($res);
}

header("Content-type: application/json");
exit($res);
?>
