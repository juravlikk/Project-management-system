USE [ProjectL]
GO

DROP PROC CheckProject
go

CREATE PROCEDURE CheckProject
	@Name VARCHAR(255),
	@result INT OUTPUT
AS
	SELECT @result = COUNT(ID) FROM Project WHERE Name = @Name;