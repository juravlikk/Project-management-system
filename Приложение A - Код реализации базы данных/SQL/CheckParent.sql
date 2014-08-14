USE ProjectL;
go

DROP PROC CheckParent
go

CREATE PROC CheckParent
	@TaskName VARCHAR(255),
	@ParentTask VARCHAR(255),
	@result TINYINT OUTPUT
AS
	SELECT @ParentTask = dbo.STR_UTF8_1251(@ParentTask);
	SELECT @TaskName = dbo.STR_UTF8_1251(@TaskName);
	IF (SELECT ProjectID FROM Tasks WHERE Name = @TaskName) != (SELECT ProjectID FROM Tasks WHERE ParentTask = @ParentTask)
		SET @result = 0
	ELSE SET @result = 1
