<?php
require_once("db/mssql.php");

require_once "db/role.php";
require_once "db/privilegedUser.php";

if (!isset($_POST['setmanager'])) exit;
$request = json_decode($_POST['setmanager']);
if (empty($request->{'login'})) {
	$res = json_encode(array("Error" => "Wrong options"));
	header("Content-type: application/json");
	exit($res);
}
$login = $request->{'login'};

if (isset($request->{'check'}) {
    $u = User::getByUsername($request->{'check'});
}

if ($u->hasPrivilege("setmanager")) {
	require_once("functions.php");
	$res = setManager($login);
} else {
	$res = json_encode(array("Error" => "Wrong options"));
	header("Content-type: application/json");
	exit($res);
}

header("Content-type: application/json");
exit($res);
?>
