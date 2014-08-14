<?php
require_once("db/mssql.php");

require_once "db/role.php";
require_once "db/privilegedUser.php";

if (!isset($_POST['gettasks'])) exit;
$request = json_decode($_POST['gettasks']);
if (empty($request->{'login'})) {
	$login = NULL;
} else $login = $request->{'login'}; 
if (empty($request->{'project'})) { 
	$project = NULL;
} else $project = $request->{'project'};
$free = $request->{'free'};

if (!is_null($login) and $free == 1) {
	$res = json_encode(array("Error" => "Wrong options"));
	header("Content-type: application/json");
	exit($res);
}

if (isset($request->{'check'})) {
    $u = User::getByUsername($request->{'check'});
}

if ($u->hasPrivilege("gettasks")) {
	require_once("functions.php");
	$res = gettasks($login, $project, $free);
} else {
	$res = json_encode(array("Error" => "Wrong options"));
	header("Content-type: application/json");
	exit($res);
}

header("Content-type: application/json");
exit($res);
?>
