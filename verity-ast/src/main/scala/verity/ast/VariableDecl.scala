package verity.ast

import verity.ast.*

import scala.collection.mutable.ArrayBuffer

/** A variable declaration (local variable or field)
  */
trait VarDef extends Tree, HasText, HasType, NamedTree, HasModifiers {
  def name: String

  /** What it gets initialized to, unless it's just declared
    * @return
    */
  def initExpr: Option[Expr]

  /** Whether or not this is simply a declaration
    * @return
    *   True if only a declaration, false if also intialized
    */
  def declarationOnly: Boolean = initExpr == None

  def isFinal: Boolean
}

class Field(
  val fieldName: Text,
  override val modifiers: ArrayBuffer[Modifier],
  var typ: Type,
  var initExpr: Option[Expr] = None,
  override val isFinal: Boolean
) extends VarDef, HasModifiers, HasType {
  override def name = fieldName.text
}

class LocalVar(
  override val modifiers: Iterable[Modifier],
  val varName: Text,
  var typ: Type,
  var initExpr: Option[Expr] = None,
  val endInd: Int,
  override val isFinal: Boolean
) extends VarDef {

  override def equals(obj: Any): Boolean = obj match {
    case other: LocalVar =>
      name == other.name && typ == other.typ && this.modifiers.toSet == other.modifiers.toSet
    case _ => false
  }
  override def name: String = varName.text
}
