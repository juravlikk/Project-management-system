USE ProjectL
go

DROP FUNCTION GetRoles
go

CREATE FUNCTION GetRoles(@login VARCHAR(255))
	RETURNS TABLE
AS
	RETURN SELECT t1.role_id, t2.Name AS role_name FROM user_role AS t1 JOIN UserGroups AS t2 ON t1.role_id = t2.ID WHERE t1.user_id = (SELECT ID FROM Users WHERE Login = @Login)
go