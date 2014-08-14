<?php
require_once("db/mssql.php");

require_once "db/role.php";
require_once "db/privilegedUser.php";

if (!isset($_POST['changeparent'])) exit;
$request = json_decode($_POST['changeparent']);

if (is_null($request->{'name'})) {
	$res = json_encode(array("Error" => "Wrong options"));
	header("Content-type: application/json");
	exit($res);
}

$parent = $request->{'parent'};
$anme = $request->{'name'};

if (isset($request->{'check'})) {
    $u = User::getByUsername($request->{'check'});
}

if ($u->hasPrivilege("changeparent")) {
	require_once("functions.php");
	$res = changeParent($parent, $name) ;
} else {
	$res = json_encode(array("Error" => "Wrong options"));
	header("Content-type: application/json");
	exit($res);
}

header("Content-type: application/json");
exit($res);
?>
