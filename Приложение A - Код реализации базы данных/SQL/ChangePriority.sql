USE ProjectL
go

DROP PROC ChangePriority
go

CREATE PROC ChangePriority
	@TaskName VARCHAR(255),
	@Priority TINYINT
AS
	INSERT INTO Audit (TableName, ColumnName, Operation, OldValue, NewValue) VALUES ('Tasks', 'Priority', 'UPDATE', (SELECT Priority FROM Tasks WHERE Name = @TaskName), @Priority)	

	UPDATE Tasks SET Priority = @Priority WHERE Name = @TaskName