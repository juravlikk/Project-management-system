<?php
require_once("db/mssql.php");
require_once("functions.php");

if (!isset($_POST['login'])) exit;
$request = json_decode($_POST['login']);
if (empty($request->{'login'}) or empty($request->{'password'})) {
	$res = json_encode(array("Error" => "Wrong options"));
	header("Content-type: application/json");
	exit($res);
}
$login = $request->{'login'};
$password = $request->{'password'};

$res = login($login, $password);
header("Content-type: application/json");
exit($res);
?>
