package net.katsstuff.danmakucore.client.gui

import net.minecraft.client.Minecraft
import net.minecraftforge.event.TickEvent
import net.minecraftforge.event.TickEvent.RenderTickEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import org.lwjgl.glfw.GLFW.{GLFW_ARROW_CURSOR, GLFW_IBEAM_CURSOR, GLFW_POINTING_HAND_CURSOR, GLFW_RESIZE_ALL_CURSOR, GLFW_RESIZE_EW_CURSOR, GLFW_RESIZE_NS_CURSOR, glfwCreateStandardCursor, glfwDestroyCursor, glfwSetCursor}

object CursorShapes:
  private var arrowCursor        = 0L
  private var ibeamCursor        = 0L
  private var resizeEwCursor     = 0L
  private var resizeNsCursor     = 0L
  private var resizeAllCursor    = 0L
  private var pointingHandCursor = 0L
  private var window             = 0L

  private var activeCursor = 0L

  private var activeCursorRefreshed: Boolean = true

  def init(): Unit =
    window = Minecraft.getInstance().getWindow.getWindow
    arrowCursor = glfwCreateStandardCursor(GLFW_ARROW_CURSOR)
    ibeamCursor = glfwCreateStandardCursor(GLFW_IBEAM_CURSOR)
    resizeEwCursor = glfwCreateStandardCursor(GLFW_RESIZE_EW_CURSOR)
    resizeNsCursor = glfwCreateStandardCursor(GLFW_RESIZE_NS_CURSOR)
    resizeAllCursor = glfwCreateStandardCursor(GLFW_RESIZE_ALL_CURSOR)
    pointingHandCursor = glfwCreateStandardCursor(GLFW_POINTING_HAND_CURSOR)

  private def setCursor(cursor: Long): Unit =
    activeCursorRefreshed = true
    if cursor != activeCursor && cursor != 0 then
      glfwSetCursor(window, cursor)
      activeCursor = cursor

  def setArrow(): Unit        = setCursor(arrowCursor)
  def setIBeam(): Unit        = setCursor(ibeamCursor)
  def setResizeEw(): Unit     = setCursor(resizeEwCursor)
  def setResizeNs(): Unit     = setCursor(resizeNsCursor)
  def setResizeAll(): Unit    = setCursor(resizeAllCursor)
  def setPointingHand(): Unit = setCursor(pointingHandCursor)

  @SubscribeEvent
  def onRender(event: RenderTickEvent): Unit =
    if !activeCursorRefreshed && activeCursor != 0 && event.phase == TickEvent.Phase.END then
      activeCursor = 0
      glfwSetCursor(window, 0)
      ()
    else
      activeCursorRefreshed = false


  def destroy(): Unit =
    //TODO: Execute this earlier
    glfwDestroyCursor(arrowCursor)
    glfwDestroyCursor(ibeamCursor)
    glfwDestroyCursor(resizeEwCursor)
    glfwDestroyCursor(resizeNsCursor)
    glfwDestroyCursor(resizeAllCursor)
    glfwDestroyCursor(pointingHandCursor)
