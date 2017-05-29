package net.katsstuff.danmakucore.danmodelfromobj

import java.io.File
import java.nio.file.{Path, Paths}

case class Config(objFile: Path = Paths.get("."), danmakuColorObj: Seq[String] = Seq.empty, out: Path = Paths.get("."), name: String = "", description: String = "", author: String = "")
object Config {
  val parser = new scopt.OptionParser[Config]("danmodelfromobj") {
    head("DanModelFromObj Converts an obj and mtl file to the danmodel format")

    opt[File]('i', "objFile").action((f, c) => c.copy(objFile = f.toPath)).required().valueName("file")
    opt[Seq[String]]("danmakuColorObjs").action((objs, c) => c.copy(danmakuColorObj = objs)).valueName("names...")
    opt[File]('o', "output").action((f, c) => c.copy(out = f.toPath)).valueName("file")

    opt[String]("name").action((s, c) => c.copy(name = s))
    opt[String]("description").action((s, c) => c.copy(description = s))
    opt[String]("author").action((s, c) => c.copy(author = s))
  }
}