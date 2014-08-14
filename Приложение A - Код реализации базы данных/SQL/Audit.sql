USE ProjectL;
go

DROP VIEW ProjectL_Tables;
go

CREATE VIEW ProjectL_Tables AS 
	SELECT ROW_NUMBER() OVER (ORDER BY TABLE_NAME) AS ID, TABLE_NAME FROM information_schema.tables WHERE TABLE_TYPE = 'base table' and TABLE_NAME != 'systranschemas' and TABLE_NAME != 'Audit' GROUP BY TABLE_NAME
go

DECLARE @numb INT;
SELECT @numb = COUNT(TABLE_NAME) FROM information_schema.tables WHERE TABLE_TYPE = 'base table' and TABLE_NAME != 'systranschemas'

/*
DECLARE @i INT;
DECLARE @TableName VARCHAR(255);
SET @i = 0;
WHILE @i < @numb
BEGIN
	SET @TableName = (SELECT TABLE_NAME FROM ProjectL_Tables WHERE ID = @i)
	SET @i = @i + 1;
END
*/
go

CREATE TRIGGER Tasks_Trigger ON Tasks FOR INSERT, UPDATE, DELETE
AS
BEGIN
	IF ((SELECT COUNT(*) FROM inserted) = 0) INSERT INTO Audit (TableName, ColumnName, Operation, OldValue, NewValue) VALUES
		('Tasks', NULL, 'DELETE', (SELECT 'ID = ' + ID AS ID FROM deleted), NULL)
END
