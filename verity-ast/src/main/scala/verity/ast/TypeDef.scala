package verity.ast

import verity.ast.*

trait TypeDef extends NamedTree {

  /** Get a reference to this type parameter
    */
  def makeRef(path: Seq[Text], typeArgs: TypeArgList): ResolvedTypeRef =
    ResolvedTypeRef(path, typeArgs, this)

  def makeRef(path: Seq[Text]): ResolvedTypeRef =
    ResolvedTypeRef(path, TypeArgList(typeParams.params.map(_.makeRef)), this)

  private lazy val cachedTypeRef: Option[ResolvedTypeRef] =
    Option.when(typeParams.params.isEmpty)(_makeRef)
  private def _makeRef: ResolvedTypeRef =
    makeRef(
      Text(this.name) :: Nil,
      TypeArgList(typeParams.params.map(_.makeRef))
    )
  def makeRef: Type = cachedTypeRef.getOrElse(_makeRef)

  def typeParams: TypeParamList
}

object TypeDef {
  val placeholder = new TypeDef {
    def name: String = "PLACEHOLDER"
    def typeParams: TypeParamList = TypeParamList(Nil)
  }
}

object BuiltinTypes {
  private[verity] var objectTypeDef: TypeDef = TypeDef.placeholder
  private[verity] var stringTypeDef: TypeDef = TypeDef.placeholder

  def objectType: Type = objectTypeDef.makeRef
  def stringType: Type = stringTypeDef.makeRef

  def refreshBuiltins(rootPkg: RootPkg): Unit = {
    for {
      java <- rootPkg.subPkgs.find(_.name == "java")
      lang <- java.subPkgs.find(_.name == "lang")
    } {
      // println("found package java.lang!")
      lang.classlikes.find(_.name == "Object").foreach { objectClsDef =>
        this.objectTypeDef = objectClsDef
      // println("reset java.lang.Object!")
      }

      lang.classlikes.find(_.name == "String").foreach { stringClsDef =>
        this.stringTypeDef = stringClsDef
      // println("reset String!")
      }
    }

    import scala.collection.mutable.ArrayBuffer
    val verityPkg = rootPkg.subPkgs.find(_.name == "verity").getOrElse {
      val newPkg = PkgNode("verity", ArrayBuffer.empty, ArrayBuffer.empty, rootPkg)
      rootPkg.subPkgs += newPkg
      newPkg
    }
    val langPkg = verityPkg.subPkgs.find(_.name == "lang").getOrElse {
      val newPkg = PkgNode("lang", ArrayBuffer.empty, ArrayBuffer.empty, verityPkg)
      verityPkg.subPkgs += newPkg
      newPkg
    }

    langPkg.files += FileNode("Magic.verity", None, Nil, Seq(NotGivenDef, NotProvenDef), None, Nil)
  }

}
