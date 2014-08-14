USE ProjectL;
go

DROP PROC SetWorker
go

CREATE PROC SetWorker
	@Login VARCHAR(255),
	@TaskName VARCHAR(255)
AS
	DECLARE @Task VARCHAR(255)
 	SET @Task = dbo.STR_UTF8_1251(@TaskName);
 	SET @TaskName = @Task
	IF ((SELECT Type FROM Tasks WHERE Name = @TaskName) = 1) RETURN
	INSERT INTO Audit (TableName, ColumnName, Operation, OldValue, NewValue) VALUES ('Tasks', 'Login', 'UPDATE', (SELECT UserID FROM Tasks WHERE Tasks.Name = @TaskName), @Login)	
	UPDATE Tasks SET UserID = (SELECT ID FROM Users WHERE Users.Login = @Login) WHERE Tasks.Name = @TaskName
	