USE ProjectL;
go

DROP PROC UpdateCompleteness
go

CREATE PROC UpdateCompleteness
	@Completeness FLOAT,
	@TaskName VARCHAR(255),
	@Next VARCHAR(255),
	@Comments TEXT
AS
	DECLARE @TaskID BIGINT;
	SELECT @TaskID = ID FROM Tasks WHERE Name = dbo.STR_UTF8_1251(@TaskName)
	IF (@Completeness <= (SELECT Completeness FROM Tasks WHERE Tasks.ID = @TaskID) or @Completeness > 100 or (SELECT Type FROM Tasks WHERE ID = @TaskID) != 0) RETURN;
	DECLARE @START DATETIME;
	IF (EXISTS (SELECT * FROM Periods WHERE TaskID = (SELECT ID FROM Tasks WHERE UserID = (SELECT UserID FROM Tasks T WHERE T.ID = @TaskID))))
	BEGIN
		SELECT @START = EndPeriod FROM Periods 
			WHERE EndPeriod = (SELECT MAX(EndPeriod) FROM Periods WHERE TaskID = (SELECT ID FROM Tasks WHERE UserID = (SELECT UserID FROM Tasks T WHERE T.ID = @TaskID)))
	END ELSE
	BEGIN
		SELECT @START = Start FROM Tasks WHERE ID = @TaskID
	END
	IF (@Next IS NULL) 
	BEGIN
		IF (SELECT COUNT(*) FROM Pers) = 0 
		BEGIN
			INSERT INTO Periods (TaskID, Comments, StartPeriod, EndPeriod, CompletenessPart, WorkTime) VALUES
				(@TaskID, @Comments, @START, GETDATE(), @Completeness - (SELECT Completeness FROM Tasks WHERE Tasks.ID = @TaskID), CONVERT(FLOAT, GETDATE() - @START));
		END
		ELSE
		BEGIN
			INSERT INTO Pers (TaskID, Comments, StartPeriod, EndPeriod, CompletenessPart) VALUES
				(@TaskID, @Comments, (SELECT TOP(1)StartPeriod  FROM Pers), (SELECT TOP(1) EndPeriod FROM Pers), @Completeness - (SELECT Completeness FROM Tasks WHERE Tasks.ID = @TaskID));
		END
	END
	ELSE
	BEGIN
		IF (SELECT COUNT(*) FROM Pers) = 0 
		BEGIN
			INSERT INTO Pers (TaskID, Comments, StartPeriod, EndPeriod, CompletenessPart, WorkTime) VALUES
				(@TaskID, @Comments, @START, GETDATE(), @Completeness - (SELECT Completeness FROM Tasks WHERE Tasks.ID = @TaskID), CONVERT(FLOAT, GETDATE() - @START));
		END
		ELSE
		BEGIN
			INSERT INTO Pers (TaskID, Comments, StartPeriod, EndPeriod, CompletenessPart) VALUES
				(@TaskID, @Comments, (SELECT TOP(1) StartPeriod FROM Pers), (SELECT TOP(1) EndPeriod FROM Pers), @Completeness - (SELECT Completeness FROM Tasks WHERE Tasks.ID = @TaskID));
			EXEC AddPeriods
		END
	END

	INSERT INTO Audit (TableName, ColumnName, Operation, OldValue, NewValue) VALUES ('Tasks', 'Completeness', 'UPDATE', (SELECT Completeness FROM Tasks WHERE Tasks.ID = @TaskID), @Completeness)	

	UPDATE Tasks SET Completeness = @Completeness WHERE ID = @TaskID;

	