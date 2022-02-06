import java.util.*;

public class Lexer
{
	ArrayList<Token> tokens;
	Token t;
	String input;
	int pos;

	public Lexer(String input)
	{
		this.tokens = new ArrayList<Token>();
		this.input = input;
		this.pos = 0;
	}
	
	public ArrayList<Token> getTokens() throws LexerException 
	{
		findNextToken();
		while (pos != -1)	
		{
			getNextToken();
			tokens.add(t);
			findNextToken();
		}

		t = new Token();
		t.type = Tokens.EOF;
		tokens.add(t);

		return tokens;
	}

	private Token getNextToken() throws LexerException
	{
		t = new Token();
		Character ch = input.charAt(pos);

		if (Character.isDigit(ch))
		{
			t.type = Tokens.NUMBER;
			t.value = eatNumber();
		}
		else if (ch == '"')
		{
			t.type = Tokens.STRING;
			t.value = eatString();
		}
		else if (Character.isAlphabetic(ch))
		{
			if (!eatKeyword())
			{
				t.type = Tokens.VARIABLE;
				t.value = eatLabelOrVariable();
			}
		}
		else if (ch == ':')
		{
			t.type = Tokens.LABEL;
			t.value = eatLabelOrVariable();
		}
		else if (ch == '=')
		{
			t.type = Tokens.EQUAL;
			t.value = "=";
			pos++;
		}
		else if (ch == '!')
		{
			if (pos + 1 < input.length() && input.charAt(pos + 1) == '=')
			{
				t.type = Tokens.NE;
				t.value = "!=";
				pos += 2;
			}
			else
			{
				throw new LexerException("Lexer error at " + pos);
			}
		}
		else if (ch == '<')
		{
			if (pos + 1 < input.length() && input.charAt(pos + 1) == '=')
			{
				t.type = Tokens.LTE;
				t.value = "<=";
				pos += 2;
			}
			else
			{
				t.type = Tokens.LT;
				t.value = "<";
				pos++;
			}
		}
		else if (ch == '>')
		{
			if (pos + 1 < input.length() && input.charAt(pos + 1) == '=')
			{
				t.type = Tokens.GTE;
				t.value = ">=";
				pos += 2;
			}
			else
			{
				t.type = Tokens.GT;
				t.value = ">";
				pos++;
			}
		}
		else if (ch == '+')
		{
			t.type = Tokens.PLUS;
			t.value = "+";
			pos++;
		}
		else if (ch == '-')
		{
			t.type = Tokens.MINUS;
			t.value = "-";
			pos++;
		}
		else if (ch == '*')
		{
			t.type = Tokens.MULTIPLY;
			t.value = "*";
			pos++;
		}
		else if (ch == '/')
		{
			t.type = Tokens.DIVIDE;
			t.value = "/";
			pos++;
		}
		else if (ch == '(')
		{
			t.type = Tokens.OPEN_PAR;
			t.value = "(";
			pos++;
		}
		else if (ch == ')')
		{
			t.type = Tokens.CLOSE_PAR;
			t.value = ")";
			pos++;
		}
		else if (ch == '^')
		{
			t.type = Tokens.POWER;
			t.value = "^";
			pos++;
		}
		else
		{
			throw new LexerException("Lexer error at " + pos);
		}

		return t;
	}

	private String eatNumber() throws LexerException
	{
		Character ch;
		int end = pos;
		boolean readPoint = false;
		do
		{
			end++;
			if (end >= input.length())
			{
				break;
			}
			ch = input.charAt(end);
			if (ch == '.')
			{
				if (readPoint)
				{
					throw new LexerException("Lexer error at " + pos);
				}
				readPoint = true;
			}
		} while (Character.isDigit(ch) || ch == '.');

		String value = input.substring(pos, end);
		pos = end;

		return value;
	}

	private String eatString()
	{
		Character ch;
		int end = pos;
		do
		{
			end++;
			if (end >= input.length())
			{
				break;
			}
			ch = input.charAt(end);
		} while (ch != '"');
		end++;

		String value = input.substring(pos + 1, end - 1);
		pos = end;

		return value;
	}

	private String eatLabelOrVariable()
	{
		Character ch;
		int end = pos;
		do
		{
			end++;
			if (end >= input.length())
			{
				break;
			}
			ch = input.charAt(end);
		} while (!Character.isWhitespace(ch));

		String value = input.substring(pos, end);
		pos = end;

		return value;
	}

	private boolean eatKeyword() throws LexerException
	{
		String sub = input.substring(pos);
		sub = sub.toUpperCase();
		int oldPos = pos;
		if (sub.startsWith("PRINTLN"))
		{
			t.type = Tokens.PRINTLN;
			t.value = "PRINTLN";
			pos += "PRINTLN".length();
		}
		else if (sub.startsWith("PRINT"))
		{
			t.type = Tokens.PRINT;
			t.value = "PRINT";
			pos += "PRINT".length();
		}
		else if (sub.startsWith("CLS"))
		{
			t.type = Tokens.CLS;
			t.value = "CLS";
			pos += "CLS".length();
		}
		else if (sub.startsWith("IF"))
		{
			t.type = Tokens.IF;
			t.value = "IF";
			pos += "IF".length();
		}
		else if (sub.startsWith("GOTO"))
		{
			t.type = Tokens.GOTO;
			t.value = "GOTO";
			pos += "GOTO".length();
		}
		else if (sub.startsWith("INPUT"))
		{
			t.type = Tokens.INPUT;
			t.value = "INPUT";
			pos += "INPUT".length();
		}
		else if (sub.startsWith("LET"))
		{
			t.type = Tokens.LET;
			t.value = "LET";
			pos += "LET".length();
		}
		else if (sub.startsWith("GET"))
		{
			t.type = Tokens.GET;
			t.value = "GET";
			pos += "GET".length();
		}
		else if (sub.startsWith("PUT"))
		{
			t.type = Tokens.PUT;
			t.value = "PUT";
			pos += "PUT".length();
		}
		else if (sub.startsWith("GOSUB"))
		{
			t.type = Tokens.GOSUB;
			t.value = "GOSUB";
			pos += "GOSUB".length();
		}
		else if (sub.startsWith("RETURN"))
		{
			t.type = Tokens.RETURN;
			t.value = "RETURN";
			pos += "RETURN".length();
		}
		else if (sub.startsWith("END"))
		{
			t.type = Tokens.END;
			t.value = "END";
			pos += "END".length();
		}
		else if (sub.startsWith("RND"))
		{
			t.type = Tokens.RND;
			t.value = "RND";
			pos += "RND".length();
		}
		else if (sub.startsWith("SLEEP"))
		{
			t.type = Tokens.SLEEP;
			t.value = "SLEEP";
			pos += "SLEEP".length();
		}
		if (t.type != Tokens.NOTHING)
		{
			if (pos >= input.length())
			{
				return true;
			}
			else if (!Character.isWhitespace(input.charAt(pos)))
			{
				throw new LexerException("Lexer error at " + pos);
			}
			return true;
		}
		return false;
	}
	
	private void findNextToken()
	{
		if (pos >= input.length())
		{
			pos = -1;
			return; 
		}
		Character ch = input.charAt(pos);

		while (Character.isWhitespace(ch))
		{
			pos++;
			if (pos >= input.length())
			{
				pos = -1;
				return; 
			}
			ch = input.charAt(pos);
		}
		if (ch == '#')
		{
			pos = -1;
		}
	}
}
