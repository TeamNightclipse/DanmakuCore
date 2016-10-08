/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.entity.danmaku.form.danobj

import scala.annotation.tailrec
import scala.util.parsing.combinator.JavaTokenParsers

import net.minecraft.util.ResourceLocation

object DanOBJParser extends JavaTokenParsers {

	override def skipWhitespace: Boolean = false

	def vector2: Parser[(Double, Double)] = decimalNumber ~ ' ' ~ decimalNumber ^^ {
		case x ~ _ ~ y => (x.toDouble, y.toDouble)
	}

	def vector3: Parser[(Double, Double, Double)] = decimalNumber ~ ' ' ~ decimalNumber ~ ' ' ~ decimalNumber ^^ {
		case x ~ _ ~ y ~ _ ~ z => (x.toDouble, y.toDouble, z.toDouble)
	}

	def vector4: Parser[(Double, Double, Double, Double)] = decimalNumber ~ ' ' ~ decimalNumber ~ ' ' ~ decimalNumber ~ ' ' ~ decimalNumber ^^ {
		case x ~ _ ~ y ~ _ ~ z ~ _ ~ w => (x.toDouble, y.toDouble, z.toDouble, w.toDouble)
	}

	def version: Parser[Int] = "version " ~> wholeNumber ^^ (_.toInt)
	def danmakuColor: Parser[Int] = "danmakuColor " ~> wholeNumber ^^ (_.toInt)
	def glowColor: Parser[Seq[Int]] = "glowColor " ~> repsep(wholeNumber, ' ') ^^ (_.map(_.toInt))

	def lowerCaseWord: Parser[String] = """^[a-z]+$""".r

	//Need something better here
	//http://stackoverflow.com/questions/169008/regex-for-parsing-directory-and-filename
	def path: Parser[String] = """((?:[^/]*/)*)(.*)""".r
	def texture: Parser[ResourceLocation] = "texture " ~> lowerCaseWord ~ ':' ~ path ^^ {
		case domain ~ _ ~ path => new ResourceLocation(domain, path)
	}

	def vertex: Parser[PositionData] = "v " ~> vector3 ^^ { case ((x, y, z)) => PositionData(x, y, z) }
	def uv: Parser[UVData] = "vt " ~> vector2 ^^ { case ((u, v)) => UVData(u, v) }
	def color: Parser[ColorData] = "vc " ~> vector4 ^^ { case ((r, g, b, a)) => ColorData(r.toFloat, g.toFloat, b.toFloat, a.toFloat) }
	def normal: Parser[NormalData] = "vn " ~> vector3 ^^ { case ((x, y, z)) => NormalData(x.toFloat, y.toFloat, z.toFloat) }

	type FaceInfo = (Int, Int, Int, Int)
	def faceInfo: Parser[FaceInfo] = wholeNumber ~ '/' ~ wholeNumber ~ '/' ~ wholeNumber ~ '/' ~ wholeNumber ^^ {
		case v ~ _ ~ vt ~ _ ~ vc ~ _ ~ vn => (v.toInt, vt.toInt, vc.toInt, vn.toInt)
	}

	def face: Parser[(FaceInfo, FaceInfo, FaceInfo)] =
		"f " ~> faceInfo ~ ' ' ~ faceInfo ~ ' ' ~ faceInfo ^^ {
			case f1 ~ _ ~ f2 ~ _ ~ f3 => (f1, f2, f3)
		}

	def allVertices: Parser[Seq[PositionData]] = repsep(vertex, '\n')
	def allTextures: Parser[Seq[UVData]] = repsep(uv, '\n')
	def allColors: Parser[Seq[ColorData]] = repsep(color, '\n')
	def allNormals: Parser[Seq[NormalData]] = repsep(normal, '\n')
	def allFaces: Parser[Seq[(FaceInfo, FaceInfo, FaceInfo)]] = repsep(face, '\n')

	def read(string: String): Either[String, (Seq[OptimizedTriangleData], ResourceLocation)] = {
		val triangles = parse(version, string).flatMapWithNext {
			case 1 => parse('\n' ~> danmakuColor, _)
				.flatMapWithNext(danmakuMarkerColor => parse(glowColor, _)
					.flatMapWithNext(glowMarkerColor => parse(texture, _)
						.flatMapWithNext(texture => parse(allVertices, _)
							.flatMapWithNext(pos => parse('\n' ~> allTextures, _)
								.flatMapWithNext(uv => parse('\n' ~> allColors, _)
									.flatMapWithNext(color => parse('\n' ~> allNormals, _)
										.flatMapWithNext(norm => parse('\n' ~> allFaces, _)
											.map(faces => {

												val mappedGlowColor = glowMarkerColor.map(color(_))

												@tailrec
												def inner(rest: Seq[(FaceInfo, FaceInfo, FaceInfo)], created: Seq[OptimizedTriangleData]): Seq[OptimizedTriangleData] = {
													if(rest == Nil) created
													else {
														val ((v1, vt1, vc1, vn1), (v2, vt2, vc2, vn2), (v3, vt3, vc3, vn3)) = rest.head
														val vert1 = VertexData(pos(v1), uv(vt1), color(vc1), norm(vn1))
														val vert2 = VertexData(pos(v3), uv(vt2), color(vc3), norm(vn2))
														val vert3 = VertexData(pos(v2), uv(vt3), color(vc2), norm(vn3))
														val triangle = TriangleData(vert1, vert2, vert3).optimize(mappedGlowColor, color(danmakuMarkerColor))

														inner(rest.tail, created :+ triangle)
													}
												}

												(inner(faces, Seq()), texture)
											}))))))))
			case unknown => Failure(s"Unknown .danobj version: $unknown", _)
		}

		triangles match {
			case Success(res, _) => Right(res)
			case none: NoSuccess => Left(none.msg)
		}
	}
}
