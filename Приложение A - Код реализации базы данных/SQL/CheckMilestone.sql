USE [ProjectL]
GO

DROP PROC CheckMilestone
go

CREATE PROCEDURE CheckMilestone
	@Name VARCHAR(255),
	@result INT OUTPUT
AS
	SELECT @result = COUNT(ID) FROM Milestones WHERE Name = @Name;