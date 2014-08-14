<?php
class User
{
    private $roles;
    private $name;

    public function __construct() {
    }

    // Изменяем метод класса User
    public static function getByUsername($login) {
        $res = mssql_query("SELECT * FROM GetRoles('$login')");
        $result = mssql_fetch_assoc($res);

        if (!empty($result)) {
            $privUser = new User();
	    $privUser->name = $login;
            $privUser->initRoles();
            return $privUser;
        } else {
            return false;
        }
    }

    // Наполняем объект roles соответствующими разрешениями
    protected function initRoles() {
        $this->roles = array();
        $res = mssql_query("SELECT * FROM GetRoles('{$this->name}')");
	$row = array();	
	for ($i = 0; $i < mssql_num_rows($res); $i++) {
		mssql_data_seek($res, $i);
		$row[$i] = mssql_fetch_assoc($res);
		$this->roles[$row[$i]["role_name"]] = Role::getRolePerms($row[$i]["role_id"]);
	}
    }

    // Проверяем, обладет ли пользователь нужными разрешениями
    public function hasPrivilege($perm) {
        foreach ($this->roles as $role) {
            if ($role->hasPerm($perm)) {
                return true;
            }
        }
        return false;
    }
}
