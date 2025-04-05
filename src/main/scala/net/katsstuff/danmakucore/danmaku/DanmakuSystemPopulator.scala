package net.katsstuff.danmakucore.danmaku

import net.katsstuff.danmakucore.danmaku.form.Form
import net.minecraft.core.RegistryAccess

import scala.reflect.ClassTag

class DanmakuSystemPopulator(
    vectorMap: Map[String, Int],
    vectorDefaults: Map[String, Float],
    compiledSystem: CompiledDanmakuSystem
) {

  private val intMapValues = Map(
    "endTime"        -> (0, 80),
    "mainColor"      -> (0, 0xFFFFFFFF),
    "secondaryColor" -> (0, 0xFF000000)
  )

  private inline def repeatWithFirst[A: ClassTag](seq: IArray[A], n: Int): IArray[A] = {
    val first = seq.head
    IArray.fill(seq.length - n)(first) ++ seq
  }

  def populate(evalState: DanmakuInstantiation.EvaluationState, forms: Seq[Form]): Unit = {
    val count              = evalState.maxLength
    val arrElems           = vectorMap.size
    val (floatArr, intArr) = compiledSystem.getAddValuesArr(count)

    vectorMap.foreach { case (k, idx) =>
      repeatWithFirst(evalState.floats.getOrElse(k, IArray(vectorDefaults(k))), count).zipWithIndex.foreach { case (v, i) =>
        floatArr(idx + arrElems * i) = v
      }
    }

    intMapValues.foreach { case (k, (idx, default)) =>
      repeatWithFirst(evalState.ints.getOrElse(k, IArray(default)), count).zipWithIndex.foreach { case (v, i) =>
        intArr(idx + arrElems * i) = v
      }
    }

    compiledSystem.addNew(floatArr, intArr, forms.toArray, count)
  }

  def populateWithInstantiation(
      evalState: DanmakuInstantiation.EvaluationState,
      instantiation: DanmakuInstantiation
  )(implicit registryAccess: RegistryAccess): Unit = populate(instantiation.evalutate(evalState), Seq.fill(evalState.maxLength)(instantiation.form))
}
