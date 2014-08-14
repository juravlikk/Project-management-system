USE [ProjectL]
GO

DROP PROC CheckTask
go

CREATE PROCEDURE CheckTask
	@TaskName VARCHAR(255),
	@result INT OUTPUT
AS
	SELECT @result = COUNT(ID) FROM Tasks WHERE Name = @TaskName;