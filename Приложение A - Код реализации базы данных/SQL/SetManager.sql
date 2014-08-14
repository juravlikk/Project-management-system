USE ProjectL;
go

DROP PROC SetManager;
go

CREATE PROC SetManager
	@Login VARCHAR(255)
AS
	IF (@Login IS NULL OR EXISTS(SELECT * FROM UserGroups JOIN user_role ON role_id = ID WHERE user_id = (SELECT Users.ID FROM Users WHERE Login = @Login) AND Name = 'Manager')) RETURN
	INSERT INTO Audit (TableName, ColumnName, Operation, OldValue, NewValue) VALUES ('Users', 'UPDATE', 'GroupID','1', '2');
	UPDATE user_role SET role_id = (SELECT ID FROM UserGroups WHERE Name = 'Manager') WHERE user_id = (SELECT Users.ID FROM Users WHERE Login = @Login)
go