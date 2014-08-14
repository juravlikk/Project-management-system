USE ProjectL;
go

DROP FUNCTION GetPeriods
go

CREATE FUNCTION GetPeriods(@TaskName VARCHAR(255))
RETURNS @result TABLE (StartPeriod DATETIME, EndPeriod DATETIME, CompletenessPart FLOAT, WorkTime FLOAT, Comments TEXT)
AS
BEGIN
	IF (@TaskName IS NULL) RETURN
	SET @TaskName = dbo.STR_UTF8_1251(@TaskName);
	INSERT @result SELECT StartPeriod, EndPeriod, CompletenessPart, WorkTime, Comments FROM Periods 
		WHERE TaskID = (SELECT ID FROM Tasks WHERE Name = @TaskName)
	
	UPDATE @result SET CompletenessPart = dbo.STR_1251_UTF8(CompletenessPart);
	UPDATE @result SET WorkTime = dbo.STR_1251_UTF8(WorkTime);	
	UPDATE @result SET Comments = dbo.STR_1251_UTF8(Comments);	
	
	RETURN
END
go