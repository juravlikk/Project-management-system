USE [ProjectL]
GO

DROP PROC Checking
go

CREATE PROCEDURE Checking
	@login NVARCHAR(255),
	@mail NVARCHAR(255) = 'NONE',
	@result INT OUTPUT
AS
	IF (@mail = 'NONE')	SELECT @result = COUNT(ID) FROM Users WHERE Login = @login;
	ELSE
	BEGIN 
		IF (EXISTS(SELECT * FROM Users WHERE Login = @login)) SET @result = 1 ELSE
		IF (EXISTS(SELECT * FROM Users WHERE Mail = @mail)) SET @result = 2 ELSE
		SET @result = 0;		
	END