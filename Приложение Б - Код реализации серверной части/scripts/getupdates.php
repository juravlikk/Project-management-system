<?php
require_once("db/mssql.php");
require_once("functions.php");

if (!isset($_POST['getupdates'])) exit;
$request = json_decode($_POST['getupdates']);
$id = $request->{'id'};

$res = getupdates($id);
header("Content-type: application/json");
exit($res);
?>
