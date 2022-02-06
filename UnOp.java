public class UnOp extends AST
{
	public AST child;
	public Token op;

	public UnOp(Token op, AST child)
	{
		this.op = op;
		this.child = child;
	}
}
