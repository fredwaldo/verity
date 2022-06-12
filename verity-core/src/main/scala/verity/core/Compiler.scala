package verity.core

//import scala.language.unsafeNulls

import verity.ast.*
import verity.checks.InitialPass
import verity.core.resolve
import verity.codegen.OutputJava
import verity.util.*
import verity.parser.Parser
import verity.readbytecode.ReadBytecode
//import com.typesafe.scalalogging.Logger

import java.io.{File, FileFilter, FileInputStream}
import java.nio.file.{Files, Path, Paths}
import scala.collection.mutable.ArrayBuffer
import java.io.FilenameFilter

object Compiler {
  def compile(pkgs: Iterable[File], files: Iterable[File], options: Options): Unit = {
//    given Logger = Logger(".")
    given rootPkg: Package = Package(ArrayBuffer.empty, ArrayBuffer.empty)

    //Load JDK classes such as java.lang.Object
    ReadBytecode.readJdk(rootPkg, Paths.get(options.jdkDir).unsafeNN, options.modulesToRead)
    //Set the Object and String builtins
    verity.ast.BuiltinTypes.refreshBuiltins(rootPkg)

    //Parse Verity sources
    parsePkg(pkgs, files, rootPkg)

    //Do some initial checks (see if modifiers are okay, resolve imports, references to types, etc.)
    verity.checks.InitialPass.initialPass(rootPkg)
    //Resolve expressions inside methods
    var allPassed = true
    for ((file -> res) <- resolve.resolveAndCheck(rootPkg)) {
      allPassed = allPassed && res._1
    }

    if (allPassed) {
      //Code generation
      OutputJava.outputJavaPkg(rootPkg, options.javaOutputDir)
    }
  }

  def parsePkg(
    pkgs: Iterable[File],
    files: Iterable[File],
    parent: Package
  ): Unit = {
    parent.files ++= files
      .map { file =>
        Parser.parseFile(file.getName.unsafeNN, file) match {
          case e @ Left((errorMsg, offset)) =>
            //todo
            println(s"Error while parsing ${file.getName}: $errorMsg")
            e
          case s => s
        }
      }
      .collect { case Right(file) => file }

    pkgs.foreach { pkg =>
      val pkgNode = Package(pkg.getName.unsafeNN, ArrayBuffer.empty, ArrayBuffer.empty, parent)
      parent.subPkgs += pkgNode

      val foobar: File = pkg
      val (subPkgs, allFiles) = foobar
        .listFiles(new FilenameFilter {
          override def accept(file: File, name: String) =
            name.endsWith(".verity") || file.isDirectory
        })
        .unsafeNN
        .view
        .partition(_.unsafeNN.isDirectory)

//      println(s"subPkgs=$subPkgs, allFiles=$allFiles")

      parsePkg(
        subPkgs.filterNotNull,
        allFiles.asInstanceOf[Iterable[File]],
        pkgNode
      )
    }
  }
}
