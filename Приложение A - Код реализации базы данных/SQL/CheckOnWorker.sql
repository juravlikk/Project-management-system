USE ProjectL;
go

DROP PROC CheckOnWorker;
go

CREATE PROC CheckOnWorker
	@TaskName VARCHAR(255),
	@result TINYINT OUTPUT
AS
	SELECT @result = COUNT(*) FROM Tasks WHERE Tasks.Name = @TaskName and Tasks.Type = 0 and UserID IS NULL
go