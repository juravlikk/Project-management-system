USE ProjectL;
go

DROP TRIGGER UPD_Tasks;
go

CREATE TRIGGER UPD_Tasks ON Tasks AFTER UPDATE
AS
	IF UPDATE(Completeness)
	BEGIN
		DECLARE @Type TINYINT;
		DECLARE @ID BIGINT;
		DECLARE @ParentTask BIGINT;
		DECLARE @ProjectID BIGINT;
		DECLARE @CompletenessIns FLOAT;
		DECLARE @CompletenessDel FLOAT;
		SELECT @Type = Type, @ID = ID, @CompletenessIns = Completeness, @ProjectID = ProjectID, @ParentTask = ParentTask FROM inserted;
		SELECT @CompletenessDel = Completeness FROM deleted;
		IF (@CompletenessIns > 100 or @CompletenessIns < 0) 
		BEGIN
			ROLLBACK TRANSACTION
			RETURN
		END
		IF EXISTS (SELECT * FROM Tasks WHERE Tasks.ID = @ParentTask)
			UPDATE Tasks SET Completeness = (SELECT SUM(Completeness)/COUNT(Completeness) FROM Tasks T WHERE Tasks.ID = T.ParentTask) WHERE Tasks.ID = @ParentTask;		
		UPDATE Project SET Completeness = (SELECT SUM(Completeness)/COUNT(Completeness) FROM Tasks WHERE ProjectID = @ProjectID) WHERE Project.ID = @ProjectID;
	END
	IF UPDATE(UserID)
	BEGIN
		DECLARE @UserIDOLD BIGINT;
		SELECT @UserIDOLD = UserID, @ID = ID FROM deleted;
		IF (@UserIDOLD IS NULL and (SELECT Type FROM deleted) != 1) UPDATE Tasks SET Start = GETDATE() WHERE ID = @ID ELSE
		BEGIN
			ROLLBACK TRANSACTION
			RETURN
		END		
	END
