USE ProjectL;
go

DROP PROC ParentTaskChange
go

CREATE PROC ParentTaskChange
	@ParentTask VARCHAR(255) = NULL,
	@TaskName VARCHAR(255)
AS
	DECLARE @ParentT VARCHAR(255);
	DECLARE @Task VARCHAR(255);
	SET @ParentT = dbo.STR_UTF8_1251(@ParentTask);
	SET @Task = dbo.STR_UTF8_1251(@TaskName);
	SET @ParentTask = @ParentT
	SET @TaskName = @Task
	IF (SELECT ProjectID FROM Tasks WHERE Name = @TaskName) != (SELECT ProjectID FROM Tasks WHERE ParentTask = @ParentTask) RETURN
	DECLARE @Type TINYINT;
	SELECT @Type = Type FROM Tasks WHERE Name = @TaskName

	INSERT INTO Audit (TableName, ColumnName, Operation, OldValue, NewValue) VALUES ('Tasks', 'ParentTask', 'UPDATE', (SELECT ParentTask FROM Tasks WHERE Tasks.Name = @TaskName), @ParentTask)	

	IF (@ParentTask IS NULL)
		IF (@Type != 2 and (SELECT ParentTask FROM Tasks WHERE Tasks.Name = @TaskName) IS NOT NULL)
			DECLARE @Parent BIGINT
			SELECT @Parent = ID FROM Tasks WHERE Name = @TaskName
			UPDATE Tasks SET ParentTask = ISNULL((SELECT ParentTask FROM Tasks T WHERE T.ID = Tasks.ParentTask AND Tasks.Name = @TaskName), NULL) WHERE Name = @TaskName
			IF (@Parent IS  NOT NULL)
			UPDATE Tasks SET Completeness = (SELECT SUM(Completeness)/COUNT(Completeness) FROM Tasks T WHERE T.ParentTask = @Parent) WHERE Tasks.ID = @Parent
	IF (@ParentTask IS NOT NULL)
	BEGIN
		SELECT @Type = Type FROM Tasks WHERE Name = @ParentTask
		IF (@Type = 0 and (SELECT ParentTask FROM Tasks WHERE Tasks.Name = @TaskName) IS NULL)
		BEGIN
			INSERT INTO Tasks (ProjectID, Name, ParentTask, Start, Type, Comments) 
			VALUES ((SELECT ProjectID FROM Tasks WHERE Tasks.Name = @ParentTask),
					(SELECT Name + ' (Parent)' FROM Tasks WHERE Tasks.Name = @ParentTask),
					(SELECT ParentTask FROM Tasks WHERE Tasks.Name = @ParentTask),
					(SELECT Start FROM Tasks WHERE Tasks.Name = @ParentTask),
					1,
					(SELECT Comments FROM Tasks WHERE Tasks.Name = @ParentTask))
			UPDATE Tasks SET ParentTask = (SELECT ID FROM Tasks T WHERE T.Name = @ParentTask + ' (Parent)') WHERE Name = @ParentTask;
			UPDATE Tasks SET ParentTask = (SELECT ID FROM Tasks T WHERE T.Name = @ParentTask + ' (Parent)') WHERE Name = @TaskName;
			UPDATE Tasks SET Completeness = (SELECT SUM(Completeness)/COUNT(Completeness) FROM Tasks T WHERE T.ParentTask = (SELECT ID FROM Tasks T WHERE T.Name = @ParentTask + ' (Parent)')) WHERE Tasks.Name = @ParentTask + ' (Parent)';
		END
		IF (@Type = 1 and (SELECT ParentTask FROM Tasks WHERE Tasks.Name = @TaskName) = 0)
		BEGIN
			UPDATE Tasks SET ParentTask = (SELECT ID FROM Tasks T WHERE T.Name = @ParentTask) WHERE Tasks.Name =@TaskName
			UPDATE Tasks SET Completeness = (SELECT SUM(Completeness)/COUNT(Completeness) FROM Tasks T WHERE T.ParentTask = (SELECT ID FROM Tasks T WHERE T.Name = @ParentTask)) WHERE Tasks.Name = @ParentTask;
		END
	END