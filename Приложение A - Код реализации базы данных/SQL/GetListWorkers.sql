USE ProjectL;
go

DROP FUNCTION GetListWorkers;
go

CREATE FUNCTION GetListWorkers()
RETURNS @result TABLE (FirstName VARCHAR(255), LastName VARCHAR(255), MiddleName VARCHAR(255), Login VARCHAR(255), LoadFactor FLOAT, Mail VARCHAR(255))
AS
BEGIN
	INSERT @result SELECT FirstName, LastName, MiddleName, Login, LoadFactor, Mail FROM Users 
	WHERE ID = (SELECT user_id FROM user_role JOIN UserGroups ON role_id = UserGroups.ID WHERE Name = 'Worker')
	
	UPDATE @result SET FirstName = dbo.STR_1251_UTF8(FirstName);
	UPDATE @result SET LastName = dbo.STR_1251_UTF8(LastName);
	UPDATE @result SET MiddleName = dbo.STR_1251_UTF8(MiddleName);
	UPDATE @result SET Login = dbo.STR_1251_UTF8(Login);
	UPDATE @result SET LoadFactor = dbo.STR_1251_UTF8(LoadFactor);
	UPDATE @result SET Mail = dbo.STR_1251_UTF8(Mail);	
	
	RETURN
END
go