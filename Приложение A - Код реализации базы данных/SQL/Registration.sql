USE ProjectL;
go

DROP PROC Registration;
go

CREATE PROC Registration
	@FirstName VARCHAR(255),
	@LastName VARCHAR(255),
	@MiddleName VARCHAR(255),
	@Login VARCHAR(255),
	@Password VARCHAR(255),
	@Mail VARCHAR(255),
	@Salt VARCHAR(3)
AS
	INSERT INTO Users (FirstName,MiddleName,LastName,Login,Password,Mail,Salt) 
		VALUES (@FirstName, @MiddleName, @LastName, @Login, @Password, @Mail ,@Salt)
	INSERT INTO user_role (user_id, role_id) 
		VALUES ((SELECT ID FROM Users WHERE Login = @Login),(SELECT ID FROM UserGroups WHERE Name = 'Worker')) 
	INSERT INTO Audit (TableName, ColumnName, Operation, OldValue, NewValue) 
		VALUES ('Users', NULL, 'INSERT', NULL, 'UserName = ' + @Login)
	
	