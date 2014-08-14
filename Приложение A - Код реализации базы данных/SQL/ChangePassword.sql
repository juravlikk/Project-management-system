USE ProjectL;
go

DROP PROC changePassword
go

CREATE PROC changePassword
	@Old VARCHAR(255),
	@New VARCHAR(255),
	@Login VARCHAR(255),
	@result TINYINT OUTPUT
AS
	DECLARE @Password VARCHAR(255);
	DECLARE @salt VARCHAR(3);
	SELECT @salt = salt, @Password = Password FROM Users WHERE Login = @login;
	DECLARE @res VARCHAR(255);
	EXEC MD5 @Old, @res OUTPUT;
	SET @Old = @res + @salt;
	EXEC MD5 @Old, @res OUTPUT;
	SET @Old = @res;
	IF (@Old != @Password) SET @result = -1 ELSE 
	BEGIN
		EXEC MD5 @New, @res OUTPUT;
		SET @New = @res + @salt;
		EXEC MD5 @New, @res OUTPUT;
		SET @New = @res;
		UPDATE Users SET Password = @New WHERE Login = @Login;
		SET @result = 1;
	END