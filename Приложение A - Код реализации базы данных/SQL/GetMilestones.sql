USE ProjectL;
go

DROP FUNCTION GetMilestones
go

CREATE FUNCTION GetMilestones(@Login VARCHAR(255))
RETURNS @result TABLE(Name VARCHAR(255) PRIMARY KEY, UserName VARCHAR(255), DeadLine DATETIME, Status VARCHAR(255))
AS
BEGIN
	INSERT @result SELECT Name, UserName = (SELECT Login FROM Users WHERE Users.ID = UserID), DeadLine, Status = 
		CASE
			WHEN (SELECT COUNT(Status) FROM GetTaskMilestones(Name) WHERE Status = 'Overdue') > 0 THEN 'Overdue'
			WHEN (SELECT COUNT(Status) FROM GetTaskMilestones(Name) WHERE Status = 'Overdue') = 0 and (SELECT COUNT(Status) FROM GetTaskMilestones(Name) WHERE Status = 'Incomlete') > 0 THEN 'Incomplete'
			WHEN (SELECT COUNT(Status) FROM GetTaskMilestones(Name) WHERE Status = 'Overdue') = 0 and (SELECT COUNT(Status) FROM GetTaskMilestones(Name) WHERE Status = 'Incomlete') = 0 THEN 'Completed'
		END
	FROM Milestones
	IF (@Login IS NOT NULL) DELETE FROM @result WHERE UserName != @Login;

	UPDATE @result SET Name = dbo.STR_1251_UTF8(Name);
	UPDATE @result SET UserName = dbo.STR_1251_UTF8(UserName);
	UPDATE @result SET Status = dbo.STR_1251_UTF8(Status);

	RETURN
END