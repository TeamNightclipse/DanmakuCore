package net.katsstuff.teamnightclipse.danmakucore.danmakucore.danmodelfromobj

import java.io.{ByteArrayOutputStream, DataOutput, DataOutputStream, IOException}
import java.nio.file.{Files, Paths}

import scala.collection.JavaConverters._
import scala.util.{Failure, Success, Try}

import com.mokiat.data.front.error.WFException
import com.mokiat.data.front.parser.{MTLMaterial, MTLParser, OBJMesh, OBJModel, OBJParser, OBJTexCoord}

object DanModelFromObj {

  def main(args: Array[String]): Unit = {
    Config.parser.parse(args, Config()) match {
      case Some(cfg) =>
        val usedCfg = if (cfg.out == Paths.get(".")) {
          val innName          = cfg.objFile.getFileName.toString
          val dotIndex         = innName.lastIndexOf('.')
          val withoutExtension = if (dotIndex != -1) innName.substring(0, dotIndex) else innName

          cfg.copy(out = Paths.get(s"$withoutExtension.danmodel"))
        } else cfg
        val tried = Try {
          val objParser = new OBJParser
          val mtlParser = new MTLParser
          val model     = objParser.parse(Files.newInputStream(usedCfg.objFile))

          val directory = usedCfg.objFile.toAbsolutePath.getParent
          val materials = model.getMaterialLibraries.asScala
            .map { str =>
              val mtlFile = directory.resolve(str)
              mtlParser.parse(Files.newInputStream(mtlFile))
            }
            .flatMap(_.getMaterials.asScala)
            .groupBy(_.getName)
            .map {
              case (name, innerMaterials) =>
                if (innerMaterials.lengthCompare(1) > 0) error("The specified model has materials with duplicate names")
                name -> innerMaterials.head
            }

          val data = createDanModel(usedCfg, model, materials)
          Files.write(usedCfg.out, data)
        }

        tried match {
          case Success(_)                                                           => println(s"Converted file to danobj and saved it at ${usedCfg.out}")
          case Failure(e @ (_: IOException | _: ConvertException | _: WFException)) => Console.err.println(e.getMessage)
          case Failure(e)                                                           => e.printStackTrace()
        }

      case None => sys.exit()
    }
  }

  def createDanModel(cfg: Config, model: OBJModel, materials: Map[String, MTLMaterial]): Array[Byte] = {
    val topBytes = new ByteArrayOutputStream()
    val topUs    = new DataOutputStream(topBytes)
    writeString("DanmakuCore DanModel", topUs)
    topUs.writeShort(1)

    writeString(cfg.name, topUs)
    writeString(cfg.description, topUs)
    writeString(cfg.author, topUs)

    val objects = model.getObjects.asScala

    val missingObjs = cfg.danmakuColorObj.map(name => name -> Option(model.getObject(name))).filter(_._2.isEmpty)
    if (missingObjs.nonEmpty) {
      error(s"No object found for ${missingObjs.map(_._1).mkString(", ")}")
    }

    val pieces = objects.flatMap(_.getMeshes.asScala).size
    topUs.writeInt(pieces)
    topUs.writeFloat(0.3F) //Danmaku alpha

    val dataBytes = new ByteArrayOutputStream()
    val dataUs    = new DataOutputStream(dataBytes)

    objects.foreach { obj =>
      val isDanmakuColor = cfg.danmakuColorObj.contains(obj.getName)
      obj.getMeshes.asScala.foreach { mesh =>
        val material = materials.getOrElse(
          mesh.getMaterialName,
          error(s"Found object ${obj.getName} where parts had no material. Tried to find ${mesh.getMaterialName}")
        )
        val (pieceData, glMode) = writeMesh(model, mesh, material, isDanmakuColor)

        dataUs.writeInt(glMode.int)
        dataUs.writeInt(mesh.getFaces.asScala.flatMap(_.getReferences.asScala).size)
        dataUs.writeBoolean(isDanmakuColor)
        dataUs.write(pieceData)
      }
    }

    topUs.writeInt(dataBytes.size())
    topUs.write(dataBytes.toByteArray)

    topBytes.toByteArray
  }

  def writeString(str: String, us: DataOutput): Unit = {
    val array = str.getBytes("UTF-8")
    us.writeShort(array.length)
    us.write(array)
  }

  def writeMesh(
      model: OBJModel,
      mesh: OBJMesh,
      material: MTLMaterial,
      isDanmakuColor: Boolean
  ): (Array[Byte], GLMode) = {
    val bytes          = new ByteArrayOutputStream()
    val us             = new DataOutputStream(bytes)
    val faceReferences = mesh.getFaces.asScala.map(_.getReferences.asScala)
    if (!faceReferences.forall(_.lengthCompare(faceReferences.head.size) == 0))
      error("Found model with varying amount of vertices for faces. Stick to one amount")

    val maxSides = faceReferences.head.size
    val glMode =
      if (maxSides == 3) GLMode.GLTriangles
      else if (maxSides == 4) GLMode.GLQuads
      else error("Found model with faces with more than 4 vertices. It's ineffcient. Fix it")

    for {
      face      <- faceReferences
      vertexRef <- face
    } {
      if (!vertexRef.hasVertexIndex) error("Found missing vertex info")
      if (!vertexRef.hasTexCoordIndex) error("Found missing uv info")
      if (!vertexRef.hasNormalIndex) error("Found missing normal info")

      val vertex  = model.getVertex(vertexRef)
      val uv      = model.getTexCoord(vertexRef)
      val normals = model.getNormal(vertexRef)

      if (uv.`type` != OBJTexCoord.Type.TYPE_2D) error("Found uv with unsupported type")

      us.writeDouble(vertex.x)
      us.writeDouble(vertex.y)
      us.writeDouble(vertex.z)
      us.writeDouble(uv.u)
      us.writeDouble(uv.v)

      if (!isDanmakuColor) {
        val color = material.getDiffuseColor
        us.writeFloat(color.r)
        us.writeFloat(color.g)
        us.writeFloat(color.b)
        us.writeFloat(1F)
      }

      us.writeFloat(normals.x)
      us.writeFloat(normals.y)
      us.writeFloat(normals.z)
    }

    (bytes.toByteArray, glMode)
  }

  def error(message: String): Nothing = throw ConvertException(message)
}
