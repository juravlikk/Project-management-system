<?php
function setWorker($login, $name) {
	if (is_null($login) or is_null($name)) {
		$res = json_encode(array("Error" => "Empty data"));
		return $res;		
	}
	$query = mssql_init("SetWorker");
	mssql_bind($query, "@login", $login, SQLVARCHAR, FALSE, FALSE, 255);
	mssql_bind($query, "@taskname", $name, SQLVARCHAR, FALSE, FALSE, 255);
	mssql_execute($query);
	$res = json_encode(array("Respond" => "Success"));
	return $res;	
}

function changeParent($parent, $name) {
	if (is_null($name)) {
		$res = json_encode(array("Error" => "Empty data"));
		return $res;		
	}
	if (checkParent($name, $parent) == 0) {
		$res = json_encode(array("Error" => "Invalid parent task. Tasks from different projects"));
		return $res;		
	}
	$query = mssql_init("ParentTaskChange");
	mssql_bind($query, "@parenttask", $parent, SQLVARCHAR, FALSE, FALSE, 255);
	mssql_bind($query, "@taskname", $name, SQLVARCHAR, FALSE, FALSE, 255);
	mssql_execute($query);
	$res = json_encode(array("Respond" => "Success"));
	return $res;	
}

function getProjects($login) {
	if (is_null($login)) {
		$res = json_encode(array("Error" => "Wrong options"));
		header("Content-type: application/json");
		exit($res);
	}

	$result = mssql_query("SELECT * FROM GetProjects('$login')");
	$row = array();	
	for ($i = 0; $i < mssql_num_rows($result); $i++) {
		mssql_data_seek($result, $i);
		$row[$i] = mssql_fetch_assoc($result);
	}
	$json = json_encode(array("Respond" => $row));
	return $json;
}

function getWorkers() {
	$result = mssql_query("SELECT * FROM GetListWorkers()");
	$row = array();	
	for ($i = 0; $i < mssql_num_rows($result); $i++) {
		mssql_data_seek($result, $i);
		$row[$i] = mssql_fetch_assoc($result);
	}
	$json = json_encode(array("Respond" => $row));
	return $json;
}

function createMilestone($login, $name, $deadline, $tasks) {
	$check = checkMilestone($name);
	if ($check != 0) {
		$res = json_encode(array("Error" => "Milestone with such name already exists"));
		return $res;
	}	
	foreach ($tasks as $key => $value) {
		if (checkOnWorker($value) != 0) {
			$res = json_encode(array("Error" => "One of the tasks already has Worker"));
			return $res;
		}
	}
	
	$query = mssql_init("CreateMilestone");
	mssql_bind($query, "@Login", $login, SQLVARCHAR, FALSE, FALSE, 255);
	mssql_bind($query, "@Name", $name, SQLVARCHAR, FALSE, FALSE, 255);
	mssql_bind($query, "@Deadline", $deadline, SQLVARCHAR, FALSE, FALSE, 255);
	mssql_execute($query);
	foreach ($tasks as $key => $value) {
		taskToMilestone($name, $value);
	}
	
	$res = json_encode(array("Respond" => "Success"));
	return $res;
}

function createProject($name, $deadline, $priority, $comments) {
	$chek = checkProject($name);
	if ($check != 0) {
		$res = json_encode(array("Error" => "Project with such name already exists"));
		return $res;
	}	
	
	$query = mssql_init("CreateProject");
	mssql_bind($query, "@Name", $name, SQLVARCHAR, FALSE, FALSE, 255);
	mssql_bind($query, "@Deadline", $deadline, SQLVARCHAR, FALSE, FALSE, 255);
	mssql_bind($query, "@Priority", $priority, SQLINT1, FALSE, FALSE);
	mssql_bind($query, "@Comments", $comments, SQLVARCHAR, FALSE, FALSE, 255);
	mssql_execute($query);

	$res = json_encode(array("Respond" => "Success"));
	return $res;
}

function createTask($project, $name, $priority, $status, $start, $scheduled, $type, $comments, $parent, $login) {
	$chek = checkTask($name);
	if ($check != 0) {
		$res = json_encode(array("Error" => "Task with such name already exists"));
		return $res;
	}
	
	$query = mssql_init("CreateTask");
	mssql_bind($query, "@project", $project, SQLVARCHAR, FALSE, FALSE, 255);
	mssql_bind($query, "@taskname", $name, SQLVARCHAR, FALSE, FALSE, 255);
	mssql_bind($query, "@priority", $priority, SQLINT1, FALSE, FALSE);
	mssql_bind($query, "@status", $status, SQLVARCHAR, FALSE, FALSE, 255);
	mssql_bind($query, "@start", $start, SQLVARCHAR, FALSE, FALSE, 255);
	mssql_bind($query, "@scheduledduration", $scheduled, SQLFLT4, FALSE, FALSE);
	mssql_bind($query, "@type", $type, SQLVARCHAR, FALSE, FALSE, 255);
	mssql_bind($query, "@comments", $comments, SQLVARCHAR, FALSE, FALSE, 255);
	mssql_execute($query);
	
	if (!is_null($login)) {
		setWorker($login, $name);
	}

	if (!is_null($parent)) {
		changeParent($parent, $name);
	}
	$res = json_encode(array("Respond" => "Success"));
	return $res;	
}

function taskToMilestone($milestone, $task) {
	$query = mssql_init("TaskToMilestone");
	mssql_bind($query, "@MilestoneName", $milestone, SQLVARCHAR, FALSE, FALSE, 255);
	mssql_bind($query, "@TaskName", $task, SQLVARCHAR, FALSE, FALSE, 255);
	mssql_execute($query);
}

function updateCompleteness($completeness, $task, $comments, $next) {
	if (is_null($completeness) or is_null($task)) {
		$res = json_encode(array("Error" => "Empty data"));
		return $res;		
	}
	if ($completeness > 100 or $completeness < 0) {
		$res = json_encode(array("Error" => "Wrong completeness"));
		return $res;		
	}
	$query = mssql_init("UpdateCompleteness");
	mssql_bind($query, "@Completeness", $completeness, SQLFLT4, FALSE, FALSE);
	mssql_bind($query, "@TaskName", $task, SQLVARCHAR, FALSE, FALSE, 255);
	mssql_bind($query, "@Next", $next, SQLVARCHAR, FALSE, FALSE, 255);
	mssql_bind($query, "@Comments", $comments, SQLTEXT, FALSE, FALSE, 4000);
	mssql_execute($query);
	$res = json_encode(array("Respond" => "Success"));
	return $res;	
}

function check($login, $mail) {
	$query = mssql_init("Checking");
	mssql_bind($query, "@login", $login, SQLVARCHAR, FALSE, FALSE, 255);
	mssql_bind($query, "@mail", $mail, SQLVARCHAR, FALSE, FALSE, 255);
	mssql_bind($query, "@result", $output, SQLINT4, true);
	mssql_execute($query);
	return $output;
}

function checkTask($task) {
	$query = mssql_init("CheckTask");
	mssql_bind($query, "@TaskName", $task, SQLVARCHAR, FALSE, FALSE, 255);
	mssql_bind($query, "@result", $output, SQLINT4, true);
	mssql_execute($query);
	return $output;
}

function checkParent($task, $parent) {
	$query = mssql_init("CheckParent");
	mssql_bind($query, "@TaskName", $task, SQLVARCHAR, FALSE, FALSE, 255);
	mssql_bind($query, "@ParentTask", $parent, SQLVARCHAR, FALSE, FALSE, 255);
	mssql_bind($query, "@result", $output, SQLINT1, true);
	mssql_execute($query);
	return $output;
}

function checkProject($project) {
	$query = mssql_init("CheckProject");
	mssql_bind($query, "@Name", $project, SQLVARCHAR, FALSE, FALSE, 255);
	mssql_bind($query, "@result", $output, SQLINT4, true);
	mssql_execute($query);
	return $output;
}

function checkMilestone($milestone) {
	$query = mssql_init("CheckMilestone");
	mssql_bind($query, "@Name", $milestone, SQLVARCHAR, FALSE, FALSE, 255);
	mssql_bind($query, "@result", $output, SQLINT4, true);
	mssql_execute($query);
	return $output;
}

function checkOnWorker($task) {
	$query = mssql_init("CheckOnWorker");
	mssql_bind($query, "@TaskName", $task, SQLVARCHAR, FALSE, FALSE, 255);
	mssql_bind($query, "@result", $output, SQLINT1, true);
	mssql_execute($query);
	return $output;
}
function registrationCorrect($login, $password, $mail, $firstname, $lastname, $middlename) {
	if ($login == "") return "Empty Login!";	
	if ($password == "") return "Empty Password!";	
	if ($mail == "") return "Empty E-mail!";
	if ($firstname == "") return "Empty First Name!";
	if ($lastname == "") return "Empty Last Name!";
	if ($middlename == "") return "Empty Middle Name!";	
	if (!preg_match('/^([a-z0-9])(\w|[.]|-|_)+([a-z0-9])@([a-z0-9])([a-z0-9.-]*)([a-z0-9])([.]{1})([a-z]{2,4})$/is', $mail)) return "Incorrect E-mail!";
	if (!preg_match('/^([a-zA-Z0-9])(\w|-|_)+([a-z0-9])$/is', $login)) return "Incorrect login";
	if (strlen($password) < 5) return "Incorrect password! Password must contains 5 or more symbols"; 	
	$output = check($login, $mail);
	if ($output == 1) return "Such login already exists!"; 
	elseif ($output == 2) return "Such mail already exists!";
	return "correct";
}

function registration($login, $password, $mail, $firstname, $lastname, $middlename) {
	$salt = mt_rand(100, 999);
	$password = md5(md5($password).$salt);

	$result = registrationCorrect($login, $password, $mail, $firstname, $lastname, $middlename);

	if ($result == "correct") {
		$query = mssql_init("Registration");
		mssql_bind($query, "@Login", $login, SQLVARCHAR, FALSE, FALSE, 255);
		mssql_bind($query, "@Password", $password, SQLVARCHAR, FALSE, FALSE, 255);
		mssql_bind($query, "@Mail", $mail, SQLVARCHAR, FALSE, FALSE, 255);
		mssql_bind($query, "@FirstName", $firstname, SQLVARCHAR, FALSE, FALSE, 255);
		mssql_bind($query, "@LastName", $lastname, SQLVARCHAR, FALSE, FALSE, 255);
		mssql_bind($query, "@MiddleName", $middlename, SQLVARCHAR, FALSE, FALSE, 255);
		mssql_bind($query, "@Salt", $salt, SQLVARCHAR, FALSE, FALSE, 3);
		mssql_execute($query);
			
		$res = json_encode(array("Respond" => "Success"));
		return $res;
	} else {
		$res = json_encode(array("Error" => $result));
		return $res;
	}
}

function login($login, $password) {
	$query = mssql_init("Signin");
	mssql_bind($query, "@login", $login, SQLVARCHAR, FALSE, FALSE, 255);
	mssql_bind($query, "@password", $password, SQLVARCHAR, FALSE, FALSE, 255);
	mssql_bind($query, "@result", $output, SQLINT4, true);
	mssql_execute($query);

	if ($output != -1) {
		$res = json_encode(array("Respond" => $output));
		return $res;
	} else {
		$res = json_encode(array("Error" => "Wrong login or password"));
		return $res;
	}
}

function getTasks($login, $project, $free) {
	if (is_null($project)) {
		if (is_null($login)) {
			$result = mssql_query("SELECT * FROM GetTasks(NULL, NULL, $free)");
		} else {
			$result = mssql_query("SELECT * FROM GetTasks('$login', NULL, $free)");
		}
	} else {
		if (is_null($login)) {
			$result = mssql_query("SELECT * FROM GetTasks(NULL, '$project', $free)");
		} else {
			$result = mssql_query("SELECT * FROM GetTasks('$login', '$project', $free)");
		}
	}
	$row = array();	
	for ($i = 0; $i < mssql_num_rows($result); $i++) {
		mssql_data_seek($result, $i);
		$row[$i] = mssql_fetch_assoc($result);
		$TaskName = $row[$i]['TaskName'];
		$periods = mssql_query("SELECT * FROM GetPeriods('$TaskName')");
		$period = array();
		for ($j = 0; $j < mssql_num_rows($periods); $j++) {
			mssql_data_seek($periods, $j);
			$period[$j] = mssql_fetch_assoc($periods);
		}
		$row[$i]['Periods'] = $period;
	}
	$json = json_encode(array("Respond" => $row));
	return $json;
}

function getMilestones($login) {
	if (is_null($login)) {
		$result = mssql_query("SELECT * FROM GetMilestones(NULL)");
	} else {
		$result = mssql_query("SELECT * FROM GetMilestones('$login')");
	}
	$row = array();	
	for ($i = 0; $i < mssql_num_rows($result); $i++) {
		mssql_data_seek($result, $i);
		$row[$i] = mssql_fetch_assoc($result);
		$MilestoneName = $row[$i]['Name'];
		$tasks = mssql_query("SELECT * FROM GetTaskMilestones('$MilestoneName')");
		$task = array();
		for ($j = 0; $j < mssql_num_rows($tasks); $j++) {
			mssql_data_seek($tasks, $j);
			$task[$j] = mssql_fetch_assoc($tasks);
		}
		$row[$i]['Tasks'] = $task;
	}
	$json = json_encode(array("Respond" => $row));
	return $json;
}

function getUpdates($id) {
	$result = mssql_query("SELECT * FROM GetUpdates('$id')");
	$row = array();	
	for ($i = 0; $i < mssql_num_rows($result); $i++) {
		mssql_data_seek($result, $i);
		$row[$i] = mssql_fetch_assoc($result);
	}
	$json = json_encode(array("Respond" => $row));
	return $json;
}

function getlastid() {
	$result = mssql_query("SELECT dbo.GetLastID()");
	$res = mssql_fetch_assoc($result);
	$json = json_encode(array("Respond" => $res['computed']));
	return $json;
}

function setLoadFactor($login, $load) {
	if ($load > 1 or $load < 0 or is_null($login)) {
		$res = json_encode(array("Error" => "Wrong options"));
		return $res;				
	}
	$query = mssql_init("SetLoadFactor");
	mssql_bind($query, "@Login", $login, SQLVARCHAR, FALSE, FALSE, 255);
	mssql_bind($query, "@LoadFactor", $load, SQLFLT4, FALSE, FALSE);
	mssql_execute($query);
	$json = json_encode(array("Respond" => "Success"));
	return $json;
}

function changePriority($task, $priority) {
	if ($priority < 0 or is_null($task)) {
		$res = json_encode(array("Error" => "Wrong options"));
		return $res;				
	}
	$query = mssql_init("changePriority");
	mssql_bind($query, "@TaskName", $task, SQLVARCHAR, FALSE, FALSE, 255);
	mssql_bind($query, "@Priority", $priority, SQLFLT4, FALSE, FALSE);
	mssql_execute($query);
	$json = json_encode(array("Respond" => "Success"));
	return $json;
}

function changePassword($new, $old, $login) {
	if ($load > 1 or $load < 0 or is_null($login)) {
		$res = json_encode(array("Error" => "Wrong options"));
		return $res;				
	}
	$query = mssql_init("ChangePassword");
	mssql_bind($query, "@Login", $login, SQLVARCHAR, FALSE, FALSE, 255);
	mssql_bind($query, "@Old", $old, SQLVARCHAR, FALSE, FALSE, 255);
	mssql_bind($query, "@New", $new, SQLVARCHAR, FALSE, FALSE, 255);
	mssql_bind($query, "@result", $output, SQLINT1, TRUE);
	mssql_execute($query);
	if ($output == 1) {
		$json = json_encode(array("Respond" => "Success"));
		return $json;
	} else {
		$res = json_encode(array("Error" => "Wrong options"));
		return $res;				
	}
}

function setManager($login) {
	if (is_null($login)) {
		$res = json_encode(array("Error" => "Wrong options"));
		return $res;				
	}
	$query = mssql_init("SetManager");
	mssql_bind($query, "@Login", $login, SQLVARCHAR, FALSE, FALSE, 255);
	mssql_execute($query);
	$json = json_encode(array("Respond" => "Success"));
	return $json;
}
?>
