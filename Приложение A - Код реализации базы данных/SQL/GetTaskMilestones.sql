USE ProjectL;
go

DROP FUNCTION GetTaskMilestones
go

CREATE FUNCTION GetTaskMilestones(@Milestone VARCHAR(255))
RETURNS @result TABLE (TaskName VARCHAR(255), Status VARCHAR(255))
AS
BEGIN
	IF (@Milestone IS NULL) RETURN
	SET @Milestone = dbo.STR_UTF8_1251(@Milestone);
	INSERT @result SELECT Tasks.Name AS TaskName, Status = 
		CASE
			WHEN Completeness = 100 THEN 'Completed'
			WHEN Completeness < 100 and DeadLine < GETDATE() THEN 'Incomplete'
			WHEN Completeness < 100 and DeadLine >= GETDATE() THEN 'Overdue'
		END
	FROM MilestonesTasks JOIN Milestones ON MilestoneID = Milestones.ID JOIN Tasks ON TaskID = Tasks.ID
	WHERE Milestones.Name = @Milestone
	
	UPDATE @result SET TaskName = dbo.STR_1251_UTF8(TaskName);
	UPDATE @result SET Status = dbo.STR_1251_UTF8(Status);
	
	RETURN
END