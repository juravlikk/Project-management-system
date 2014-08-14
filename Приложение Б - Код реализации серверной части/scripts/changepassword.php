<?php
require_once("db/mssql.php");
require_once("functions.php");

if (!isset($_POST['changepassword'])) exit;
$request = json_decode($_POST['changepassword']);
if (empty($request->{'login'}) or empty($request->{'old'}) or empty($request->{'new'})) {
	$res = json_encode(array("Error" => "Wrong options"));
	header("Content-type: application/json");
	exit($res);
}
$login = $request->{'login'};
$old = $request->{'old'};
$new = $request->{'new'};

$res = changePassword($new, $old, $login);
header("Content-type: application/json");
exit($res);
?>
