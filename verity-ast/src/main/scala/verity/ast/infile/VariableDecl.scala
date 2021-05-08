package verity.ast.infile

import verity.ast.*

import scala.collection.mutable.ListBuffer

/**
  * A variable declaration (local variable or field)
  */
trait VariableDecl extends Tree, HasText, HasType, NamedTree, HasModifiers {
  def name: String
  /**
    * What it gets initialized to, unless it's just declared
    * @return
    */
  def initExpr: Option[ResolvedOrUnresolvedExpr]

  /**
    * Whether or not this is simply a declaration
    * @return True if only a declaration, false if also intialized
    */
  def declarationOnly: Boolean = initExpr == None
}

class Field(
    val fieldName: Text,
    override val modifiers: ListBuffer[Modifier],
    var typ: Type,
    var initExpr: Option[ResolvedOrUnresolvedExpr] = None
) extends VariableDecl, ClassChild, HasModifiers, HasType {
  override def name: String = fieldName.text
  override def text: String = ???
  override def textRange = ???
}

class LocalVar(
    override val modifiers: Iterable[Modifier],
    val varName: Text,
    var typ: Type,
    var initExpr: Option[ResolvedOrUnresolvedExpr] = None,
    val endInd: Int) extends VariableDecl, Statement {

  override def equals(obj: Any): Boolean = obj match {
    case other: LocalVar => name == other.name && typ == other.typ && this.modifiers.toSet == other.modifiers.toSet
    case _ => false
  }
  override def name: String = varName.text
  override def text: String = {
    val sb = StringBuilder(HasText.seqText(modifiers)).append(typ.text).append(name)
    if (initExpr != None) sb.append('=').append(initExpr.get.text).append(';')
    sb.toString
  }
  override def textRange = ???
}
