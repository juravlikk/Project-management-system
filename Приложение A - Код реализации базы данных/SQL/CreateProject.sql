USE ProjectL;
go

DROP PROC CreateProject
go

CREATE PROC CreateProject
	@Name VARCHAR(255),
	@Deadline VARCHAR(255),
	@Priority TINYINT,
	@Comments VARCHAR(255) = NULL
AS
	IF (CAST(@DeadLine AS DATETIME)< GETDATE()) RETURN
	INSERT INTO Project (Name, DeadLine, Priority) 
	VALUES (@Name, CAST(@DeadLine AS DATETIME), @Priority);
	IF (@Comments IS NOT NULL) UPDATE Project SET Comments = @Comments WHERE Name = @Name
	INSERT INTO Audit (TableName, ColumnName, Operation, OldValue, NewValue) 
		VALUES ('Projects', NULL, 'INSERT', NULL, 'ProjectName = ' + @Name)
	