<?php
class Role
{
    protected $permissions;

    protected function __construct() {
        $this->permissions = array();
    }

    // Возвращаем объект роли с соответствующими полномочиями
    public static function getRolePerms($role_id) {
        $role = new Role();
        $res = mssql_query("SELECT * FROM GetPermissions($role_id)");
	$row = array();	
	for ($i = 0; $i < mssql_num_rows($res); $i++) {
		mssql_data_seek($res, $i);
		$row[$i] = mssql_fetch_assoc($res);
		$role->permissions[$row[$i]['perm_desc']] = true;
	}

        return $role;
    }

    // Проверка установленных полномочий
    public function hasPerm($permission) {
        return isset($this->permissions[$permission]);
    }
}
