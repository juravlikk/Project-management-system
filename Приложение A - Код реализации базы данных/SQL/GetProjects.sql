USE ProjectL;
go

DROP FUNCTION GetProjects
go

CREATE FUNCTION GetProjects (@login VARCHAR(255))
RETURNS @result TABLE (ProjectName VARCHAR(255), Start DATETIME, DeadLine DATETIME, Completeness FLOAT, Priority TINYINT, Comments TEXT, IsYour VARCHAR(20))
AS
BEGIN
	INSERT @result SELECT Project.Name AS ProjectName, Start, DeadLine, Completeness, Priority, Comments, 
	IsYour= CASE
				WHEN ID IN (SELECT ProjectID FROM Tasks JOIN Users ON UserID = Users.ID WHERE Users.Login = @login) THEN 'TRUE'
				ELSE 'FALSE'
			END
	FROM Project
	
	UPDATE @result SET ProjectName = dbo.STR_1251_UTF8(ProjectName);
	UPDATE @result SET Completeness = dbo.STR_1251_UTF8(Completeness);
	UPDATE @result SET Priority = dbo.STR_1251_UTF8(Priority);	
	UPDATE @result SET Comments = dbo.STR_1251_UTF8(Comments);	
	UPDATE @result SET IsYour = dbo.STR_1251_UTF8(IsYour);	
	
	RETURN	
END			 
go