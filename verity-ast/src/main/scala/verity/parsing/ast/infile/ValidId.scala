package verity.parsing.ast.infile

import verity.parsing.TextRange
import verity.parsing.ast.infile.expr.{Assignable, Expr}

case class ValidId(name: String, override val textRange: TextRange) extends Expr with Assignable {
  // def unapply(): (CharSequence, Int, Int) = ???
//  def startOffset: Int = ???
//  def endOffset: Int = ???
  override def text: String = name
}