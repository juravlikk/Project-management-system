<?php
require_once("db/mssql.php");

require_once "db/role.php";
require_once "db/privilegedUser.php";

if (!isset($_POST['createtask'])) exit;
$request = json_decode($_POST['createtask']);
if (empty($request->{'project'}) or empty($request->{'name'}) or empty($request->{'priority'}) or empty($request->{'status'}) or empty($request->{'sheduled'}) or empty($request->{'type'})) {
	$res = json_encode(array("Error" => "Wrong options"));
	header("Content-type: application/json");
	exit($res);	
}
if (empty($request->{'login'}) {
	$login = NULL;
} else $login = $request->{'login'};
if (empty($request->{'login'}) {
	$parent = NULL;
} else $parent = $request->{'parent'};
$project = $request->{'project'};
$name = $request->{'name'};
$priority = $request->{'priority'};
$status = $request->{'status'};
$start = $request->{'start'};
$scheduled = $request->{'scheduled'};
$type = $request->{'type'};
$comments = $request->{'comments'};

if (isset($request->{'check'})) {
    $u = User::getByUsername($request->{'check'});
}

if ($u->hasPrivilege("createtask")) {
	require_once("functions.php");
	$res = createTask($project, $name, $priority, $status, $start, $scheduled, $type, $comments, $parent, $login);
} else {
	$res = json_encode(array("Error" => "Wrong options"));
	header("Content-type: application/json");
	exit($res);
}

header("Content-type: application/json");
exit($res);
?>
