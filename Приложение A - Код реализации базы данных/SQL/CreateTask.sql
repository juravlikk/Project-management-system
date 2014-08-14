USE ProjectL;
go

DROP PROC CreateTask
go

CREATE PROC CreateTask
	@ProjectName VARCHAR(255),
	@TaskName VARCHAR(255),
	@Priority TINYINT,
	@Status VARCHAR(255),
	@Start VARCHAR(255),
	@ScheduledDuration FLOAT,
	@Type VARCHAR(255),
	@Comments TEXT = NULL
AS
	DECLARE @ProjectID BIGINT;
	SELECT @ProjectID = ID FROM Project WHERE Name = @ProjectName;
	DECLARE @TaskStatus TINYINT;
	SELECT @TaskStatus = ID FROM Status WHERE Name = @Status;
	DECLARE @TaskType TINYINT;
	SET @TaskType=CASE @Type
							WHEN 'Static' THEN 0
							WHEN 'Dynamic' THEN 1 
							WHEN 'Fixed' THEN 2 
						END;
	INSERT INTO Tasks (ProjectID, Name, Priority, Start, Type) 
	VALUES(@ProjectID, @TaskName, @Priority, @Start, @TaskType);
	IF (@TaskType = 0) UPDATE Tasks SET Status = @Status WHERE Name = @TaskName;
	IF (@TaskType != 1) UPDATE Tasks SET ScheduledDuration = @ScheduledDuration WHERE Name = @TaskName;
	IF (@TaskType != 1) UPDATE Tasks SET Priority = @Priority WHERE Name = @TaskName;
	IF (@Comments IS NOT NULL) UPDATE Tasks SET Comments = @Comments  WHERE Name = @TaskName;
	INSERT INTO Audit (TableName, ColumnName, Operation, NewValue, OldValue) 
		VALUES ('Tasks', NULL, 'INSERT', 'TaskName = ' + @TaskName, NULL)
go