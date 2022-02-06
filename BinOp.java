public class BinOp extends AST
{
	public AST left, right;
	public Token op;

	public BinOp(AST left, Token op, AST right)
	{
		this.left = left;
		this.op = op;
		this.right = right;
	}
}
