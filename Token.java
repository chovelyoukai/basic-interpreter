enum Tokens
{
	NOTHING,
	NUMBER,
	PLUS,
	MINUS,
	MULTIPLY,
	DIVIDE,
	OPEN_PAR,
	CLOSE_PAR,
	POWER,
	PRINT,
	PRINTLN,
	CLS,
	IF,
	GOTO,
	INPUT,
	LET,
	GET,
	PUT,
	GOSUB,
	RETURN,
	END,
	SLEEP,
	EQUAL,
	LT,
	GT,
	LTE,
	GTE,
	NE,
	RND,
	LABEL,
	VARIABLE,
	STRING,
	EOF
}

public class Token
{
	public Tokens type;
	public String value;

	public Token()
	{
		type = Tokens.NOTHING;
		value = "";
	}
}
