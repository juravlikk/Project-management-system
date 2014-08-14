USE ProjectL;
go

DROP PROC Signin
go

CREATE PROC Signin
	@login VARCHAR(255),
	@password VARCHAR(255),
	@result INT OUTPUT
AS
	DECLARE @check INT;
	EXEC Checking @login, DEFAULT, @check OUTPUT;
	IF (@check=1) BEGIN
		DECLARE @salt VARCHAR(3);
		DECLARE @passwd VARCHAR(255); 
		SELECT @salt = salt, @passwd = "password" FROM Users WHERE Login=@login;
	END
	DECLARE @res VARCHAR(255);
	EXEC MD5 @password, @res OUTPUT;
	SET @password = @res + @salt;
	EXEC MD5 @password, @res OUTPUT;
	SET @password = @res;
	IF (@passwd = @password) SET @result = 1 ELSE SET @result = -1;
	IF (EXISTS (SELECT Name FROM UserGroups JOIN user_role ON role_id = UserGroups.ID WHERE Name = 'Manager' AND user_id = (SELECT Users.ID FROM Users WHERE Login = @login))) SELECT @result = 2;
go