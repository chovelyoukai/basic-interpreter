import java.util.*;

public class Parser
{
	ArrayList<Token> tokens;
	int pos;

	public Parser(ArrayList<Token> tokens)
	{
		this.tokens = tokens;
		this.pos = 0;
	}

	public AST parse() throws ParserException
	{
		return statement();
	}

	private AST statement() throws ParserException
	{
		Token t = getToken();
	
		if (t.type == Tokens.PRINT || t.type == Tokens.PRINTLN)
		{
			advance();
			Token t2 = getToken();
			if (t2.type == Tokens.STRING)
			{
				return new UnOp(t, new Leaf(t2));
			}
			else
			{
				return new UnOp(t, expr());
			}
		}
		if (t.type == Tokens.SLEEP)
		{
			advance();
			return new UnOp(t, expr());
		}
		else if (t.type == Tokens.CLS ||
			t.type == Tokens.RETURN ||
			t.type == Tokens.END ||
			t.type == Tokens.LABEL) 
		{
			return new Leaf(t);
		}
		else if (t.type == Tokens.IF)
		{
			advance();
			AST left = condition();
			AST right = statement();
			return new BinOp(left, t, right);
		}
		else if (t.type == Tokens.GOTO || t.type == Tokens.GOSUB)
		{
			advance();
			return new UnOp(t, label());
		}
		else if (t.type == Tokens.INPUT)
		{
			advance();
			return new UnOp(t, variable());
		}
		else if (t.type == Tokens.LET || t.type == Tokens.GET || t.type == Tokens.PUT)
		{
			advance();
			AST left = variable();
			advance();
			AST right = expr();
			return new BinOp(left, t, right);
		}
		else
		{
			throw new ParserException("Parser error at " + pos);
		}
	}

	private AST condition() throws ParserException
	{
		AST left = expr();
		Token t = getToken();
		if (t.type != Tokens.EQUAL &&
			t.type != Tokens.LT &&
			t.type != Tokens.GT &&
			t.type != Tokens.LTE &&
			t.type != Tokens.GTE &&
			t.type != Tokens.NE)
		{
			throw new ParserException("Parser error at " + pos);
		}
		advance();
		AST right = expr();

		return new BinOp(left, t, right);
	}

	private AST label() throws ParserException
	{
		Token t = getToken();
		if (t.type == Tokens.LABEL)
		{
			return new Leaf(t);
		}
		throw new ParserException("Parser error at " + pos);
	}
	
	private AST variable() throws ParserException
	{
		Token t = getToken();
		if (t.type == Tokens.VARIABLE)
		{
			return new Leaf(t);
		}
		throw new ParserException("Parser error at " + pos);
	}

	private AST expr() throws ParserException
	{
		Token t;
		AST node = term();

		t = getToken();
		if (t.type == Tokens.PLUS)
		{
			advance();
			node = new BinOp(node, t, expr());
		}
		else if (t.type == Tokens.MINUS)
		{
			advance();
			node = new BinOp(node, t, expr());
		}
		return node;
	}

	private AST term() throws ParserException
	{
		Token t;
		AST node = factor();

		t = getToken();
		if (t.type == Tokens.MULTIPLY)
		{
			advance();
			node = new BinOp(node, t, term());
		}
		else if (t.type == Tokens.DIVIDE)
		{
			advance();
			node = new BinOp(node, t, term());
		}
		return node;
	}

	private AST factor() throws ParserException
	{
		Token t;
		AST node = primary();

		t = getToken();
		if (t.type == Tokens.POWER)
		{
			advance();
			node = new BinOp(node, t, factor());
		}
		return node;
	}

	private AST primary() throws ParserException
	{
		Token t = getToken();
		if (t.type == Tokens.NUMBER)
		{
			Leaf node = new Leaf(t);
			advance();
			return node;
		}
		else if (t.type == Tokens.VARIABLE)
		{
			Leaf node = new Leaf(t);
			advance();
			return node;
		}
		else if (t.type == Tokens.RND)
		{
			return random();
		}
		else if (t.type == Tokens.MINUS)
		{
			advance();
			return new UnOp(t, primary());
		}
		else if (t.type == Tokens.OPEN_PAR)
		{
			advance();
			AST node = expr();
			t = getToken();
			if (t.type != Tokens.CLOSE_PAR)
			{
				throw new ParserException("Parser error at " + pos);
			}
			advance();
			return node;
		}
		else
		{
			throw new ParserException("Parser error at " + pos);
		}
	}

	private AST random() throws ParserException
	{
		Token t = getToken();

		if (t.type == Tokens.RND)
		{
			advance();
			return new UnOp(t, expr());
		}
		
		throw new ParserException("Parser error at " + pos);
	}

	private Token getToken()
	{
		Token t = tokens.get(pos);
		return t;
	}
	
	private void advance()
	{
		pos++;
	}
}
