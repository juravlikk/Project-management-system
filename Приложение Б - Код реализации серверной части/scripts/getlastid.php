<?php
require_once("db/mssql.php");
require_once("functions.php");

if (!isset($_POST['getlastid'])) exit;
$request = json_decode($_POST['getlastid']);

$res = getlastid();
header("Content-type: application/json");
exit($res);
?>
