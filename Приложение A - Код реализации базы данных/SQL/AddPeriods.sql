USE ProjectL;
go

DROP PROC AddPeriods
go

CREATE PROC AddPeriods
AS
	DECLARE @sum FLOAT;
	DECLARE @abs FLOAT;
	DECLARE @start DATETIME;
	DECLARE @end DATETIME;
	SELECT TOP 1 @start = StartPeriod, @end = EndPeriod FROM Pres;
	SET @abs = (SELECT MAX(CONVERT(FLOAT, (Start + ScheduledDuration - @start))) FROM Tasks WHERE Type = 2 AND (Start + ScheduledDuration > @start))
	SET @sum = (SELECT SUM(CompletenessPart*ScheduledDuration) FROM Pers JOIN Tasks ON Tasks.ID = TaskID)
	IF (@abs > CONVERT(FLOAT, @end - @start)) SET @abs = 0;
	UPDATE Pres SET WorkTime = CompletenessPart*@abs*(SELECT ScheduledDuration FROM Tasks WHERE ID = Pres.TaskID)/@sum;
	INSERT Periods SELECT TaskID, Comments, StartPeriod, EndPeriod, CompletenessPart, WorkTime FROM Pres
	TRUNCATE TABLE Pres
go