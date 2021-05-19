package verity.ast

import verity.ast.infile._

import java.io.File
import scala.collection.mutable

case class FileNode(
  name: String,
  packageRef: Option[PackageStmt],
  imports: Seq[ImportStmt],
  classlikes: Seq[Classlike],
  origFile: Option[File]
) {
  val textRanges: mutable.Map[Tree, TextRange] = mutable.HashMap()
  private[verity] var pkg: Pkg | Null = null
  private[verity] var resolvedImports: Iterable[Pkg.Importable] = List.empty

  /** Whether or not this is a source file or just a classfile that the project depends on.
    */
  def isSource: Boolean = origFile.nonEmpty
  def text =
    s"${packageRef.fold("")(_.text)}${imports.view.map(_.text).mkString}${classlikes.view.map(_.text).mkString}"
  override def toString = s"file $name"
}

/** @param path The path of the package this file is in
  * @param pkgTokStartOffset The start offset of the "package" token
  */
case class PackageStmt(val path: DotPath, val pkgTokStartOffset: Int) extends Tree, HasText {
  override def text: String = s"package ${path.text};"
}

/** @param path The path of the import (excluding the wildcard)
  * @param pkgTokStartOffset The start offset of the "import" token
  */
case class ImportStmt(path: DotPath, override val textRange: TextRange, wildCard: Boolean = false)
    extends Tree,
      HasText,
      HasTextRange {
  override def text = s"import ${path.text}${if (wildCard) ".*" else ""};"
}

case class DotPath(path: Iterable[(String, TextRange)]) extends Tree, HasTextRange {
  override def text = path.view.map(_._1).mkString(".")
  override def textRange = TextRange(path.head._2.start, path.last._2.end)
}
