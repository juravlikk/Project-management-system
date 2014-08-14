USE ProjectL;
go

DROP PROC SetLoadFactor;
go

CREATE PROC SetLoadFactor
	@Login VARCHAR(255),
	@LoadFactor FLOAT
AS
	IF (@LoadFactor < 0 or @LoadFactor > 1) RETURN

	INSERT INTO Audit (TableName, ColumnName, Operation, OldValue, NewValue) VALUES ('Users', 'LoadFactor', 'UPDATE', (SELECT LoadFactor FROM Users WHERE Login = @Login), @LoadFactor)	

	UPDATE Users SET LoadFactor = @LoadFactor WHERE Users.Login = @Login;