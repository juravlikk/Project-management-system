<?php
require_once("db/mssql.php");

require_once "db/role.php";
require_once "db/privilegedUser.php";

if (!isset($_POST['setworker'])) exit;
$request = json_decode($_POST['setworker']);

if (is_null($request->{'login'}) or is_null($request->{'name'})) {
	$res = json_encode(array("Error" => "Wrong options"));
	header("Content-type: application/json");
	exit($res);
}

$login = $request->{'login'};
$anme = $request->{'name'};

if (isset($request->{'check'}) {
    $u = User::getByUsername($request->{'check'});
}

if ($u->hasPrivilege("setworker")) {
	require_once("functions.php");
	$res = setWorker($login, $name);
} else {
	$res = json_encode(array("Error" => "Wrong options"));
	header("Content-type: application/json");
	exit($res);
}

header("Content-type: application/json");
exit($res);
?>
