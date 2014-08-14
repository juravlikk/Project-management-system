USE ProjectL;
go

DROP PROC TaskToMilestone
go

CREATE PROC TaskToMilestone
	@MilestoneName VARCHAR(255),
	@TaskName VARCHAR(255)
AS
	INSERT INTO MilestonesTasks (MilestoneID, TaskID) 
		VALUES ((SELECT ID FROM Milestones WHERE Name = dbo.STR_UTF8_1251(@MilestoneName)), (SELECT ID FROM Tasks WHERE Name = dbo.STR_UTF8_1251(@TaskName)));
	INSERT INTO Audit (TableName, ColumnName, Operation, NewValue, OldValue) 
		VALUES ('MilestonesTasks', NULL, 'INSERT', 'MilestoneName = ' + dbo.STR_UTF8_1251(@MilestoneName) + ', ' + 'TaskName = ' + dbo.STR_UTF8_1251(@TaskName), NULL)
go