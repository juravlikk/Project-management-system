USE ProjectL;
go

DROP PROC MD5
go

CREATE PROC MD5
	@string VARCHAR(255),
	@result VARCHAR(255) OUTPUT
AS
	SELECT @result = SUBSTRING(sys.fn_sqlvarbasetostr(HASHBYTES('MD5',  @string)),3,32);
go