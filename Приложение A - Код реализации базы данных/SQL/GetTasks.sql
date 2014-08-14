USE ProjectL;
go

DROP FUNCTION GetTasks
go

CREATE FUNCTION GetTasks (@login VARCHAR(255), @project VARCHAR(255), @free BIT)
RETURNS @result TABLE(TaskName VARCHAR(255) PRIMARY KEY, ProjectName VARCHAR(255), UserName VARCHAR(255), Completeness FLOAT, Priority TINYINT, Start DATETIME, ScheduledDuration FLOAT, Duration FLOAT, Type VARCHAR(50), ParentTask VARCHAR(255), Status VARCHAR(255), Comments TEXT)
AS
BEGIN
	IF (@login IS NOT NULL and @free = 1) RETURN;
	INSERT @result SELECT Tasks.Name AS TaskName, Project.Name AS ProjectName, 
			UserName = 
			CASE
				WHEN UserID IS NULL THEN 'NONE'
				WHEN UserID IS NOT NULL THEN (SELECT Login FROM Users WHERE UserID = Users.ID)
			END,
			Tasks.Completeness, Tasks.Priority, Tasks.Start, Tasks.ScheduledDuration, Tasks.Duration,  
			Type = 
			CASE Tasks.Type
				WHEN 0 THEN 'Static'
				WHEN 1 THEN 'Dynamic' 
				WHEN 2 THEN 'Fixed' 
			END,
			ParentTask =  
			CASE 
				WHEN Tasks.ParentTask IS NOT NULL THEN (SELECT T.Name FROM Tasks T WHERE T.ID = Tasks.ParentTask)
				WHEN Tasks.ParentTask IS NULL THEN 'NONE' 
			END,
			(SELECT Status.Name FROM Status WHERE Status.ID = Tasks.Status) AS Status,			
			Tasks.Comments
	FROM Tasks JOIN Project ON ProjectID = Project.ID;
	IF (@login IS NOT NULL) DELETE FROM @result WHERE UserName != @login;
	IF (@free = 1) DELETE FROM @result WHERE UserName IS NOT NULL;
	IF (@project IS NOT NULL) DELETE FROM @result WHERE ProjectName != @Project;
	
	UPDATE @result SET TaskName = dbo.STR_1251_UTF8(TaskName);
	UPDATE @result SET ProjectName = dbo.STR_1251_UTF8(ProjectName);
	UPDATE @result SET UserName = dbo.STR_1251_UTF8(UserName);
	UPDATE @result SET Completeness = dbo.STR_1251_UTF8(Completeness);
	UPDATE @result SET Priority = dbo.STR_1251_UTF8(Priority);
	UPDATE @result SET Type = dbo.STR_1251_UTF8(Type);	
	UPDATE @result SET ScheduledDuration = dbo.STR_1251_UTF8(ScheduledDuration);
	UPDATE @result SET Duration = dbo.STR_1251_UTF8(Duration);
	UPDATE @result SET ParentTask = dbo.STR_1251_UTF8(ParentTask);
	UPDATE @result SET Status = dbo.STR_1251_UTF8(Status);
	UPDATE @result SET Comments = dbo.STR_1251_UTF8(Comments);

	RETURN
END
go