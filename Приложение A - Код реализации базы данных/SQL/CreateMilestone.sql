USE ProjectL;
go

DROP PROC CreateMilestone
go

CREATE PROC CreateMilestone
	@Login VARCHAR(255),
	@Name VARCHAR(255),
	@Deadline VARCHAR(255)
AS
	IF (CAST(@DeadLine AS DATETIME) < GETDATE()) RETURN
	INSERT INTO Milestones (Name, UserID, DeadLine) 
		VALUES (@Name, (SELECT ID FROM Users WHERE Login = @Login), CAST(@DeadLine AS DATETIME));
	INSERT INTO Audit (TableName, ColumnName, Operation, OldValue, NewValue) 
		VALUES ('Milestones', NULL, 'INSERT', NULL, 'MilestoneName = ' + @Name)
	