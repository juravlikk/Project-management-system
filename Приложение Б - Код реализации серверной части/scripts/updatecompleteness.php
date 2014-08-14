<?php
require_once("db/mssql.php");

require_once "db/role.php";
require_once "db/privilegedUser.php";

if (!isset($_POST['updatecompleteness'])) exit;
$request = json_decode($_POST['updatecompleteness']);
if (empty($request->{'task'}) or empty($request->{'completeness'}) or empty($request->{'completeness'})) {
	$res = json_encode(array("Error" => "Wrong options"));
	header("Content-type: application/json");
	exit($res);
}
if (empty($request->{'next'})) {
	$next = NULL;	
} else {
	$next = $request->{'next'};
}
$task = $request->{'task'};
$comments = $request->{'comments'};
$completeness = $request->{'completeness'};

if (isset($request->{'check'})) {
    $u = User::getByUsername($request->{'check'});
}

if ($u->hasPrivilege("updatecompleteness")) {
	require_once("functions.php");
	$res = updateCompleteness($completeness, $task, $comments, $next);
} else {
	$res = json_encode(array("Error" => "Wrong options"));
	header("Content-type: application/json");
	exit($res);
}

header("Content-type: application/json");
exit($res);
?>
