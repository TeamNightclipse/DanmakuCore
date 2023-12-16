package net.katsstuff.danmakucore.danmaku.behaviors

import net.minecraft.resources.ResourceLocation

class MandatoryEndBehavior extends Behavior[Unit] {
  override def extraColumns: Seq[ResourceLocation] = Seq()

  override def requiredMainColumns: Seq[MainColumns.RequiredColumns] = Nil

  override def transferData(objData: Unit, extraData: Array[Array[Float]], index: Int): Unit = ()

  override def noOpData: Unit = ()

  override def act(mainColumns: MainColumns, extraData: Array[Array[Float]], size: Int): Unit = {
    val ticksExisted = mainColumns.ticksExisted
    val endTime      = mainColumns.endTime
    val dead         = mainColumns.dead
    val nextStage    = mainColumns.nextStage
    val parent       = mainColumns.parent
    val posX         = mainColumns.posX
    val posY         = mainColumns.posY
    val posZ         = mainColumns.posZ
    val orientation  = mainColumns.orientation
    val scaleX       = mainColumns.scaleX
    val scaleY       = mainColumns.scaleY
    val scaleZ       = mainColumns.scaleZ

    {
      var i: Int = 0
      while (i < size) {
        ticksExisted(i) = (ticksExisted(i) + 1).toShort
        i += 1
      }
    }

    {
      var i: Int = 0
      while (i < size) {
        val thisDead = ticksExisted(i) > endTime(i)
        if (thisDead && !mainColumns.currentDead.contains(i)) {
          mainColumns.currentDead += i
          var nextStages = nextStage(i)
          if(nextStages == null) {
            nextStages = Nil
          }

          val transformedNextStages = nextStages.map { spawnData =>
            spawnData.copy(
              pos = spawnData.pos.add(posX(i), posY(i), posZ(i)),
              orientation = (orientation(i) * spawnData.orientation).asImmutable,
              shotData = spawnData.shotData.copy(
                sizeX = scaleX(i) * spawnData.shotData.sizeX,
                sizeY = scaleY(i) * spawnData.shotData.sizeY,
                sizeZ = scaleZ(i) * spawnData.shotData.sizeZ
              ),
              parent = Option(parent(i))
            )
          }

          transformedNextStages match {
            case Seq()          =>
            case Seq(nextSpawn) => mainColumns.addSpawn(nextSpawn, i)
            case nextSpawns     => mainColumns.addSpawns(nextSpawns)
          }
        }

        dead(i) = dead(i) || thisDead
        i += 1
      }
    }
  }
}
