package scalispa

import util.parsing.combinator.RegexParsers

import java.io.{ File, FileOutputStream, PrintStream }

import me.qmx.jitescript.{ JiteClass, CodeBlock }
import me.qmx.jitescript.util.CodegenUtils.{ p, ci, sig }

import org.objectweb.asm.Opcodes._

trait Node
case class SExp(l: List[Node]) extends Node
case class SInt(i: Int) extends Node
case class SIdent(s: String) extends Node

object Parser extends RegexParsers {
  def int = regex("""[0-9]+""".r) ^^ { (i: String) => SInt(i.toInt) }
  def ident = regex("""[A-Za-z+-/\*]+""".r) ^^ SIdent
  def sexp = "(" ~> rep(node) <~ ")" ^^ SExp
  def node: Parser[Node] = int | ident | sexp
  def apply(s: String) = parse(sexp, s)
}

object Evaluator {
  def compile(b: CodeBlock, n: Node) {
    n match {
      case SExp(Nil) => {}
      case SExp(x::xs) => {
        xs.foreach(compile(b, _))
        compile(b, x)
      }
      case SInt(x) => b.pushInt(x)
      case SIdent("+") => b.iadd()
      case SIdent("-") => b.isub()
      case SIdent("*") => b.imul()
      case SIdent("/") => b.idiv()
    }
  }

  def apply(program: SExp) {
    val b = new CodeBlock
    compile(b, program)
    b.iprintln().voidreturn()

    val name = "Lisp"
    val c = new JiteClass(name)
    c.defineMethod("main", ACC_PUBLIC | ACC_STATIC, sig(classOf[Unit], classOf[Array[String]]), b)

    val stream = new FileOutputStream(new File(name + ".class"))
    stream.write(c.toBytes)
    stream.close()
  }
}

object Main {
  def main(args: Array[String]) {
    Parser(args mkString " ") match {
      case Parser.Success(p, _) => Evaluator(p)
      case a => println(a)
    }
  }
}
