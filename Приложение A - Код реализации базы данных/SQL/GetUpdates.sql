USE ProjectL;
go

DROP FUNCTION GetUpdates
go

CREATE FUNCTION GetUpdates(@ID BIGINT)
RETURNS @result TABLE (TableName VARCHAR(255), ColumnName VARCHAR(255), Operation VARCHAR(255), OldValue VARCHAR(255), NewValue VARCHAR(255))
AS
BEGIN
	INSERT @result SELECT TableName, ColumnName, Operation, OldValue, NewValue FROM Audit WHERE ID > @ID
	
	UPDATE @result SET TableName = dbo.STR_1251_UTF8(TableName);
	UPDATE @result SET ColumnName = dbo.STR_1251_UTF8(ColumnName);
	UPDATE @result SET Operation = dbo.STR_1251_UTF8(Operation);
	UPDATE @result SET OldValue = dbo.STR_1251_UTF8(OldValue);
	UPDATE @result SET NewValue = dbo.STR_1251_UTF8(NewValue);
	
	RETURN
END