<?php
require_once('db/mssql.php');
require_once('functions.php');

if (!isset($_POST['registration'])) exit;
$request = json_decode($_POST['registration']);
$login = $request->{'login'};
$password = $request->{'password'};
$mail = $request->{'mail'};
$firstname = $request->{'firstname'};
$middlename = $request->{'middlename'};
$lastname = $request->{'lastname'};

$result = registration($login, $password, $mail, $firstname, $lastname, $middlename);
header("Content-type: application/json");
exit($result);
?>
