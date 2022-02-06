import java.util.*;
import java.io.*;
import java.lang.Thread;

public class Interpreter
{
	final static String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	static float[] mem = new float[4096];
	static HashMap<String, Float> vars = new HashMap<String, Float>();
	static Scanner sc = new Scanner(System.in);
	static ArrayList<AST> program = new ArrayList<AST>();
	static Stack<Integer> returnStack = new Stack<Integer>();
	static Random rd = new Random();
	static int currentLine = 0;

	public static void main(String[] args)
	{
		String input;

		FileInputStream fis;
		Scanner fin;
		boolean interactive = false;

		if (args.length < 1)
		{
			fin = sc;
			interactive = true;
		}
		else
		{
			try
			{
				fis = new FileInputStream(args[0]);
				fin = new Scanner(fis);
			}
			catch (Exception e)
			{
				System.out.println("Could not open " + args[0]);
				return;
			}
		}


		while (interactive ? true : fin.hasNextLine())
		{
			if (interactive)
			{
				System.out.print("\n> ");
			}

			input = fin.nextLine();

			Lexer l = new Lexer(input);
			
			ArrayList<Token> tokens;
			try
			{
				tokens = l.getTokens();
			}
			catch (LexerException e)
			{
				System.out.println(e.getMessage());
				System.out.println(input);
				if (!interactive)
				{
					System.exit(-1);
				}
				continue;
			}

			if (tokens.size() == 1)
			{
				continue;
			}

			Parser p = new Parser(tokens);
			AST tree;
			try
			{
				tree = p.parse();
			}
			catch (ParserException e)
			{
				System.out.println(e.getMessage());
				System.out.println(input);
				if (!interactive)
				{
					System.exit(-1);
				}
				continue;
			}

			if (!interactive)
			{
				program.add(tree);
			}
			else
			{
				try
				{
					postOrder(tree);
				}
				catch (Exception e)
				{
					System.out.println(e.getMessage());
				}
			}
			//RPN(tree);
			//System.out.println();
			//lisp(tree);
			//System.out.println();
			//System.out.println(result);
		}

		for (currentLine = 0; currentLine < program.size(); currentLine++)
		{
			try
			{
				postOrder(program.get(currentLine));
			}
			catch (Exception e)
			{
				System.out.println(e.getMessage());
				if (!interactive)
				{
					System.exit(0);
				}
			}
		}
	}

	public static void RPN(AST node)
	{
		if (node.getClass() == BinOp.class)
		{
			BinOp op = (BinOp)node;
			RPN(op.left);
			RPN(op.right);
			System.out.print(op.op.value + " ");
		}
		else if (node.getClass() == Leaf.class)
		{
			Leaf l = (Leaf)node;
			System.out.print(l.val.value + " ");
		}
	}

	public static void lisp(AST node)
	{
		if (node.getClass() == BinOp.class)
		{
			BinOp op = (BinOp)node;
			System.out.print("(" + op.op.value);
			lisp(op.left);
			lisp(op.right);
			System.out.print(")");
		}
		else if (node.getClass() == UnOp.class)
		{
			UnOp op = (UnOp)node;
			System.out.print("(" + op.op.value);
			lisp(op.child);
			System.out.print(")");
		}
		else if (node.getClass() == Leaf.class)
		{
			Leaf l = (Leaf)node;
			System.out.print(" " + l.val.value);
		}
	}

	public static float postOrder(AST node) throws BasicException
	{
		if (node.getClass() == BinOp.class)
		{
			BinOp op = (BinOp)node;
			switch(op.op.type)
			{
				case PLUS:
					return postOrder(op.left) + postOrder(op.right);
				case MINUS:
					return postOrder(op.left) - postOrder(op.right);
				case MULTIPLY:
					return postOrder(op.left) * postOrder(op.right);
				case DIVIDE:
					return postOrder(op.left) / postOrder(op.right);
				case POWER:
					return (float) Math.pow(postOrder(op.left), postOrder(op.right));
				case EQUAL:
					return postOrder(op.left) == postOrder(op.right) ? 1 : 0;
				case LT:
					return postOrder(op.left) < postOrder(op.right) ? 1 : 0;
				case GT:
					return postOrder(op.left) > postOrder(op.right) ? 1 : 0;
				case LTE:
					return postOrder(op.left) <= postOrder(op.right) ? 1 : 0;
				case GTE:
					return postOrder(op.left) >= postOrder(op.right) ? 1 : 0;
				case NE:
					return postOrder(op.left) != postOrder(op.right) ? 1 : 0;
				case IF:
					if (postOrder(op.left) == 1)
					{
						return postOrder(op.right);
					}
					break;
				case LET:
				{
					Leaf l = (Leaf)op.left;
					vars.put(l.val.value, postOrder(op.right));
					break;
				}
				case GET:
				{
					Leaf l = (Leaf)op.left;
					int i = (int) postOrder(op.right);
					if (i >= 0 && i < 4096)
					{
						vars.put(l.val.value, mem[i]);
					}
					else
					{
						throw new BasicException("Basic exception on line " + currentLine);	
					}
					break;
				}
				case PUT:
				{
					Leaf l = (Leaf)op.left;
					int i = (int) postOrder(op.right);
					if (i >= 0 && i < 4096)
					{
						mem[i] = vars.get(l.val.value);
					}
					else
					{
						throw new BasicException("Basic exception on line " + currentLine);	
					}
					break;
				}
					
			}
			return -1;
		}
		else if (node.getClass() == UnOp.class)
		{
			UnOp op = (UnOp)node;
			String add = "";
			switch (op.op.type)
			{
				case MINUS:
					return -1 * postOrder(op.child);
				case PRINTLN:
					add = "\n";
				case PRINT:
					if (op.child.getClass() == Leaf.class)
					{
						Leaf l = (Leaf)op.child;
						if (l.val.type == Tokens.STRING)
						{
							System.out.print(l.val.value + add);
						}
						else
						{
							System.out.print(postOrder(op.child) + add);
						}
					}
					else
					{
						System.out.print(postOrder(op.child) + add);
					}
					break;
				case INPUT:
				{
					Leaf l = (Leaf)op.child;
					vars.put(l.val.value, sc.nextFloat());
					sc.nextLine();
					break;
				}
				case GOTO:
				{
					Leaf l = (Leaf)op.child;
					int oldLine = currentLine;
					currentLine = findLabel(l.val.value);
					if (currentLine == -2)
					{
						throw new BasicException("Basic exception on line " + oldLine);
					}
					break;
				}
				case GOSUB:
				{
					Leaf l = (Leaf)op.child;
					int oldLine = currentLine;
					currentLine = findLabel(l.val.value);
					if (currentLine == -2)
					{
						throw new BasicException("Basic exception on line " + oldLine);
					}
					returnStack.push(oldLine);
					break;
				}
				case RND:
					return (float) rd.nextInt((int) postOrder(op.child));
				case SLEEP:
					try
					{
						Thread.sleep((int) postOrder(op.child));
					}
					catch (Exception e) {}
					break;
			}
			return -1;
		}
		else if (node.getClass() == Leaf.class)
		{
			Leaf l = (Leaf)node;
			switch (l.val.type)
			{
				case CLS:
					for (int i = 0; i < 200; i++)
					{
						System.out.println();
					}
					break;
				case END:
					System.exit(0);
				case RETURN:
					currentLine = returnStack.pop();
					break;
				case NUMBER:
					return Float.parseFloat(l.val.value); 
				case VARIABLE:
				{
					if (!vars.containsKey(l.val.value))
					{
						throw new BasicException("Basic exception on line " + currentLine);
					}
					return vars.get(l.val.value);
				}
			}
			return -1;

		}
		else
		{
			return -1;
		}
	}

	private static int findLabel(String label)
	{
		for (int i = 0; i < program.size(); i++)
		{
			AST a = program.get(i);

			if (a.getClass() == Leaf.class)
			{
				Leaf l = (Leaf)a;

				if (l.val.type == Tokens.LABEL && l.val.value.equals(label))
				{
					return i - 1;
				}
			}
		}
		return -2;
	}
}
