USE ProjectL
go

DROP FUNCTION GetPermissions
go

CREATE FUNCTION GetPermissions(@role_id INTEGER)
	RETURNS TABLE
AS
	RETURN SELECT T2.perm_desc FROM role_perm AS T1 JOIN dbo.[permissions] AS T2 ON T1.perm_id = T2.perm_id WHERE T1.role_id = @role_id
go