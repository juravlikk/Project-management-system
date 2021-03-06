IF OBJECT_ID('STR_UTF8_1251') IS NOT NULL
DROP FUNCTION dbo.STR_UTF8_1251
GO
CREATE FUNCTION dbo.STR_UTF8_1251
(
	@s varchar(8000)
)
RETURNS VARCHAR(8000)
AS

BEGIN
    DECLARE @i int,
            @c int,
            @byte2 int,
            @c1 int,
            @new_c1 int,
            @new_i int,
            @new_c2 int,
            @out_i int
    DECLARE @out varchar(8000),
            @a int 
    SET @i = 1
    SET @byte2 = 0
    SET @out = ''
    WHILE (@i<=len(@s))
    BEGIN
        SET @c = ascii(SUBSTRING(@s,@i,1))
        
        IF (@c<=127 )
            SET @out = @out+SUBSTRING(@s,@i,1)
        
        IF (@byte2>0 )
        BEGIN
            SET @new_c2 = (@c1&3)*64+(@c&63)
            
            --Right shift @new_c1 2 bits
            		SET @new_c1 = CAST(@c1/2 AS INT)
            		SET @new_c1 = (CAST(@new_c1/2 AS INT))&5
            		SET @new_i = @new_c1*256+@new_c2
            		IF (@new_i=1025 )
            		    SET @out_i = 168
            		IF (@new_i=1105 )
            		    SET @out_i = 184
            		IF (@new_i<>1025 AND @new_i<>1105 )
            		    SET @out_i = @new_i-848
            		
            		SET @out = @out + char(@out_i)
            		SET @byte2 = 0
        END
        
        --Right shift @c 5 bits
        	     SET @a = CAST(@c/2 AS INT)
        	     SET @a = CAST(@a/2 AS INT)
        	     SET @a = CAST(@a/2 AS INT)
        	     SET @a = CAST(@a/2 AS INT)
        	     SET @a = CAST(@a/2 AS INT)
        	     
        	     IF (@a=6 )
        	     BEGIN
        	         SET @c1 = @c
        	         SET @byte2 = 1
        	     END
        	     SET @i = @i+1
    END
    RETURN @out
END
GO

--DECLARE @ss NVARCHAR(4000)
--select @ss=  dbo.STR_1251_UTF8('parent')
--SELECT @ss

IF OBJECT_ID('STR_1251_UTF8') IS NOT NULL
DROP FUNCTION dbo.STR_1251_UTF8
GO
CREATE FUNCTION STR_1251_UTF8(@s varchar(8000))
RETURNS VARCHAR(8000)
AS
BEGIN
	DECLARE
		@c int			,
		@t varchar(8000)	
	SELECT @t = ''
	WHILE LEN(@s) > 0
	BEGIN
		SELECT @c = ASCII(SUBSTRING(@s,1,1))
		IF (@c >= 0x80 AND @c <= 0xFF)
		BEGIN
			IF (@c >= 0xF0)
			BEGIN
				SELECT @t = @t + char(0xD1) + char(@c-0x70)
			END
			ELSE
			IF (@c >= 0xC0)
			BEGIN
				SELECT @t = @t + char(0xD0) + char(@c-0x30)
			END
			ELSE
			BEGIN
				IF (@c = 0xA8) SELECT @t = @t + char(0xD0) + char(0x81) -- �
				ELSE
				IF (@c = 0xB8) SELECT @t = @t + char(0xD1) + char(0x91) -- �
				ELSE
				-- ���������� �������
				IF (@c = 0xA1) SELECT @t = @t + char(0xD0) + char(0x8E) -- � (�)
				ELSE
				IF (@c = 0xA2) SELECT @t = @t + char(0xD1) + char(0x9E) -- � (�)
				ELSE 
				IF (@c = 0xAA) SELECT @t = @t + char(0xD0) + char(0x84) -- // � (�)
				ELSE 
				IF (@c = 0xAF) SELECT @t = @t + char(0xD0) + char(0x87) -- // � (I..)
				ELSE 
				IF (@c = 0xB2) SELECT @t = @t + char(0xD0) + char(0x86) -- // I (I)
				ELSE 
				IF (@c = 0xB3) SELECT @t = @t + char(0xD1) + char(0x96) -- // i (i)
				ELSE 
				IF (@c = 0xBA) SELECT @t = @t + char(0xD1) + char(0x94) -- // � (�)
				ELSE 
				IF (@c = 0xBF) SELECT @t = @t + char(0xD1) + char(0x97) -- // � (i..)
				ELSE 
				-- ��������� �������
				IF (@c = 0x8C) SELECT @t = @t + char(0xD3) + char(0x90) -- // &#1232; (A)
				ELSE 
				IF (@c = 0x8D) SELECT @t = @t + char(0xD3) + char(0x96) -- // &#1238; (E)
				ELSE 
				IF (@c = 0x8E) SELECT @t = @t + char(0xD2) + char(0xAA) -- // &#1194; (�)
				ELSE 
				IF (@c = 0x8F) SELECT @t = @t + char(0xD3) + char(0xB2) -- // &#1266; (�)
				ELSE 
				IF (@c = 0x9C) SELECT @t = @t + char(0xD3) + char(0x91) -- // &#1233; (�)
				ELSE 
				IF (@c = 0x9D) SELECT @t = @t + char(0xD3) + char(0x97) -- // &#1239; (�)
				ELSE 
				IF (@c = 0x9E) SELECT @t = @t + char(0xD2) + char(0xAB) -- // &#1195; (�)
				ELSE 
				IF (@c = 0x9F) SELECT @t = @t + char(0xD3) + char(0xB3) -- // &#1267; (�)
				ELSE 
				-- chars
				IF (@c = 0xB9) SELECT @t = @t + char(0xE2) + char(0x84) + char(0x96) -- // � (No)				ELSE
				ELSE					
				SELECT @t = @t + '?'
			END
  END
  ELSE
   SELECT @t = @t + CHAR(@c)
  
  SELECT @s = SUBSTRING(@s,2,LEN(@s)-1)
 END
 RETURN @t
 END
go
--select ASCII('�')