package net.katsstuff.danmakucore.client.gui

import scala.collection.mutable
import scala.compiletime.uninitialized
import scala.jdk.CollectionConverters.*

import com.mojang.blaze3d.vertex.Tesselator
import net.katsstuff.danmakucore.client.gui.widgets.NodeContainer
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.tabs.{GridLayoutTab, TabManager, TabNavigationBar}
import net.minecraft.client.gui.components.{AbstractWidget, Button, ObjectSelectionList}
import net.minecraft.client.gui.layouts.{FrameLayout, GridLayout}
import net.minecraft.client.gui.narration.NarratableEntry.NarrationPriority
import net.minecraft.client.gui.narration.{NarratableEntry, NarrationElementOutput}
import net.minecraft.client.gui.navigation.{FocusNavigationEvent, ScreenRectangle}
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen
import net.minecraft.client.gui.{ComponentPath, GuiGraphics}
import net.minecraft.locale.Language
import net.minecraft.network.chat.{CommonComponents, Component, Style}
import net.minecraft.util.{FormattedCharSequence, Mth}
import net.minecraftforge.client.gui.widget.ScrollPanel

class DanmakuEditorScreen extends Screen(Component.translatable("danmakucore.gui.danmakuEditor")) { screen =>

  // TODO: Grid layout
  // TODO: Toasts
  // TODO: Make node editor as a widget, and have the tabs be seperate node editor instances
  // TODO: Investigate other layouts

  private val tabManager = new TabManager(addRenderableWidget(_), removeWidget(_))

  private var tabNavigationBar: TabNavigationBar = uninitialized
  private var bottomButtons: GridLayout          = uninitialized

  private val tabs: mutable.Buffer[RepositionTab] = mutable.Buffer()

  private def onTabsChange(reposition: Boolean): Unit = {
    if tabNavigationBar != null then removeWidget(tabNavigationBar)

    val newCurrentTab = Option(tabManager.getCurrentTab).fold(0)(tabs.indexOf)

    tabNavigationBar = TabNavigationBar
      .builder(tabManager, this.width)
      .addTabs(tabs.toSeq*)
      .build()
    addRenderableWidget(tabNavigationBar)
    tabNavigationBar.selectTab(newCurrentTab, false)
    if reposition then repositionElements()
  }

  override def init(): Unit = {
    tabs.clear()
    tabs += new MainOptionsTab

    onTabsChange(reposition = false)

    this.bottomButtons = new GridLayout().columnSpacing(10)
    val rows = this.bottomButtons.createRowHelper(2)
    rows.addChild(
      Button
        .builder(
          CommonComponents.GUI_CANCEL,
          _ => this.onClose()
        )
        .build
    )
    rows.addChild(
      Button
        .builder(
          Component.translatable("danmakucore.gui.danmakuEditor.save"),
          _ => {
            tabs += new NodeEditorTab
            onTabsChange(reposition = true)
          }
        )
        .build
    )
    this.bottomButtons.visitWidgets { (widget: AbstractWidget) =>
      widget.setTabOrderGroup(1)
      this.addRenderableWidget(widget)
    }
    repositionElements()
  }

  override def render(pGuiGraphics: GuiGraphics, pMouseX: Int, pMouseY: Int, pPartialTick: Float): Unit =
    // renderBackground(pGuiGraphics)
    pGuiGraphics.blit(
      CreateWorldScreen.FOOTER_SEPERATOR,
      0,
      Mth.roundToward(this.height - 36 - 2, 2),
      0.0F,
      0.0F,
      this.width,
      2,
      32,
      2
    )
    super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick)

  override def repositionElements(): Unit = {
    if (tabNavigationBar != null && bottomButtons != null) {
      tabs.foreach(_.reposition())
      tabNavigationBar.setWidth(width)
      tabNavigationBar.arrangeElements()
      bottomButtons.arrangeElements()
      FrameLayout.centerInRectangle(bottomButtons, 0, height - 36, width, 36)
      val i               = tabNavigationBar.getRectangle.bottom
      val screenrectangle = new ScreenRectangle(0, i, width, bottomButtons.getY - i)
      tabManager.setTabArea(screenrectangle)

    }
  }

  override def isPauseScreen: Boolean = false

  override def tick(): Unit =
    tabManager.tickCurrent()

  abstract class RepositionTab(component: Component) extends GridLayoutTab(component):
    def reposition(): Unit

  class MainOptionsTab extends RepositionTab(Component.translatable("danmakucore.gui.danmakuEditor.mainOptions")):

    enum EntryVariant {
      case DanmakuInstantiation
      case DanmakuSystem

      def pretty: Component = this match
        case DanmakuInstantiation =>
          Component.translatable("danmakucore.gui.danmakuEditor.variant.danmakuInstantiation")
        case DanmakuSystem => Component.translatable("danmakucore.gui.danmakuEditor.variant.danmakuSystem")
    }

    class SavedNodeSystem(
        val name: String,
        val variant: EntryVariant,
        val author: String,
        val description: String,
        all: SavedNodeSystemsEntries
    ) extends ObjectSelectionList.Entry[SavedNodeSystem]:

      override def getNarration: Component = Component
        .translatable("danmakucore.gui.danmakuEditor.entry.name", name)
        .append(".")
        .append(Component.translatable("danmakucore.gui.danmakuEditor.entry.variant", variant.pretty))
        .append(".")
        .append(Component.translatable("danmakucore.gui.danmakuEditor.entry.author", author))

      override def render(
          pGuiGraphics: GuiGraphics,
          pIndex: Int,
          pTop: Int,
          pLeft: Int,
          pWidth: Int,
          pHeight: Int,
          pMouseX: Int,
          pMouseY: Int,
          pHovering: Boolean,
          pPartialTick: Float
      ): Unit = {
        pGuiGraphics.drawString(font, name, pLeft, pTop, 0xFFFFFFFF)
        pGuiGraphics.drawString(font, variant.pretty, pLeft, pTop + font.lineHeight, 0xFFFFFFFF)
        pGuiGraphics.drawString(
          font,
          Component.translatable("danmakucore.gui.danmakuEditor.entry.author", author),
          pLeft,
          pTop + font.lineHeight * 2,
          0xFFFFFFFF
        )
      }

      override def mouseClicked(pMouseX: Double, pMouseY: Double, pButton: Int): Boolean = {
        all.setSelected(this)
        info.entry = Some(this)
        false
      }

    end SavedNodeSystem

    class SavedNodeSystemsEntries(_width: Int, _height: Int, top: Int, bottom: Int)
        extends ObjectSelectionList[SavedNodeSystem](
          Minecraft.getInstance,
          _width,
          _height,
          top,
          bottom,
          font.lineHeight * 3 + 4
        ):
      setRenderHeader(true, font.lineHeight * 3)
      setRenderTopAndBottom(false)

      override def addEntry(pEntry: SavedNodeSystem): Int = super.addEntry(pEntry)

      override def renderHeader(pGuiGraphics: GuiGraphics, pX: Int, pY: Int): Unit = {
        pGuiGraphics.drawString(
          font,
          s"x0=$x0, x1=$x1, y0=$y0, y1=$y1, width=$width, height=$height",
          pX,
          pY,
          0xFFFFFFFF
        )
        pGuiGraphics.drawString(
          font,
          s"screen.width=${screen.width}, screen.height=${screen.height}",
          pX,
          pY + font.lineHeight,
          0xFFFFFFFF
        )

        super.renderHeader(pGuiGraphics, pX, pY)
      }

      override def getRowWidth: Int = width - 40

      override def getScrollbarPosition: Int = width - 6

    end SavedNodeSystemsEntries

    class EntriesWrapper(val entries: SavedNodeSystemsEntries)
        extends AbstractWidget(
          0,
          0,
          entries.getWidth,
          entries.getHeight,
          Component.translatable("danmakucore.gui.danmakuEditor.entries")
        ):
      export entries.{render as renderWidget, updateNarration as updateWidgetNarration}

      override def nextFocusPath(pEvent: FocusNavigationEvent): ComponentPath = entries.nextFocusPath(pEvent)

      override def mouseClicked(pMouseX: Double, pMouseY: Double, pButton: Int): Boolean =
        entries.mouseClicked(pMouseX, pMouseY, pButton)

      override def mouseReleased(pMouseX: Double, pMouseY: Double, pButton: Int): Boolean =
        entries.mouseReleased(pMouseX, pMouseY, pButton)

      override def mouseDragged(
          pMouseX: Double,
          pMouseY: Double,
          pButton: Int,
          pDragX: Double,
          pDragY: Double
      ): Boolean = entries.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY)

      override def mouseScrolled(pMouseX: Double, pMouseY: Double, pDelta: Double): Boolean =
        entries.mouseScrolled(pMouseX, pMouseY, pDelta)

      override def isMouseOver(pMouseX: Double, pMouseY: Double): Boolean = entries.isMouseOver(pMouseX, pMouseY)

      override def narrationPriority(): NarrationPriority = entries.narrationPriority()

      override def getRectangle: ScreenRectangle = entries.getRectangle

      override def getWidth: Int = entries.getWidth

      override def getHeight: Int = entries.getHeight

      override def getX: Int = entries.getLeft

      override def getY: Int = entries.getTop

      override def setWidth(pWidth: Int): Unit = {
        val x0 = entries.getLeft
        entries.updateSize(pWidth, entries.getHeight, entries.getTop, entries.getBottom)
        entries.setLeftPos(x0)
      }

      override def setHeight(value: Int): Unit = {
        val diff = value - entries.getHeight
        val x0   = entries.getLeft
        entries.updateSize(entries.getWidth, value, entries.getTop, entries.getBottom + diff)
        entries.setLeftPos(x0)
      }

      override def setX(pX: Int): Unit =
        entries.setLeftPos(pX)

      override def setY(pY: Int): Unit = {
        val diff = pY - entries.getTop
        entries.updateSize(entries.getWidth, entries.getHeight, pY, entries.getBottom + diff)
      }
    end EntriesWrapper

    class EntryInfo(_width: Int, _height: Int, _top: Int, _left: Int, content: Seq[FormattedCharSequence])
        extends ScrollPanel(Minecraft.getInstance, _width, _height, _top, _left):
      override def getContentHeight: Int = Math.max(content.length * font.lineHeight, bottom - top - 8)

      override def drawPanel(
          guiGraphics: GuiGraphics,
          entryRight: Int,
          relativeY: Int,
          tess: Tesselator,
          mouseX: Int,
          mouseY: Int
      ): Unit =
        content.view.zipWithIndex.foreach: (str, i) =>
          guiGraphics.drawString(minecraft.font, str, left + 5, relativeY + i * font.lineHeight, 0xFFFFFFFF)

      override def narrationPriority(): NarratableEntry.NarrationPriority = NarrationPriority.NONE

      override def updateNarration(pNarrationElementOutput: NarrationElementOutput): Unit = ()
    end EntryInfo

    class EntryInfoWrapper(_width: Int, _height: Int)
        extends AbstractWidget(
          0,
          0,
          _width,
          _height,
          Component.translatable("danmakucore.gui.danmakuEditor.entryInfo")
        ):
      private var info: EntryInfo                 = new EntryInfo(width, height, getX, getY, Nil)
      private var _entry: Option[SavedNodeSystem] = None
      resetInfo()

      def entry: Option[SavedNodeSystem] = _entry
      def entry_=(value: Option[SavedNodeSystem]): Unit = {
        _entry = value
        resetInfo()
      }

      override def renderWidget(pGuiGraphics: GuiGraphics, pMouseX: Int, pMouseY: Int, pPartialTick: Float): Unit =
        info.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick)

      override def updateWidgetNarration(pNarrationElementOutput: NarrationElementOutput): Unit =
        info.updateNarration(pNarrationElementOutput)

      override def narrationPriority(): NarrationPriority = info.narrationPriority()

      override def mouseScrolled(pMouseX: Double, pMouseY: Double, pDelta: Double): Boolean =
        info.mouseScrolled(pMouseX, pMouseY, pDelta)

      override def isMouseOver(pMouseX: Double, pMouseY: Double): Boolean = info.isMouseOver(pMouseX, pMouseY)

      override def mouseClicked(pMouseX: Double, pMouseY: Double, pButton: Int): Boolean =
        info.mouseClicked(pMouseX, pMouseY, pButton)

      override def mouseReleased(pMouseX: Double, pMouseY: Double, pButton: Int): Boolean =
        info.mouseReleased(pMouseX, pMouseY, pButton)

      override def mouseDragged(
          pMouseX: Double,
          pMouseY: Double,
          pButton: Int,
          pDragX: Double,
          pDragY: Double
      ): Boolean = info.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY)

      private def resetInfo(): Unit = {
        val content = _entry.toSeq.flatMap: entry =>
          Language.getInstance
            .getVisualOrder(
              font.getSplitter.splitLines(
                Component
                  .translatable("danmakucore.gui.danmakuEditor.entry.name", entry.name)
                  .append("\n")
                  .append(Component.translatable("danmakucore.gui.danmakuEditor.entry.variant", entry.variant.pretty))
                  .append("\n")
                  .append(Component.translatable("danmakucore.gui.danmakuEditor.entry.author", entry.author))
                  .append("\n\n")
                  .append(Component.translatable("danmakucore.gui.danmakuEditor.entry.description", entry.description)),
                width - 15,
                Style.EMPTY
              )
            )
            .asScala

        info = new EntryInfo(width, height, getY, getX, content)
      }

      override def setX(pX: Int): Unit = {
        super.setX(pX)
        resetInfo()
      }

      override def setY(pY: Int): Unit = {
        super.setY(pY)
        resetInfo()
      }

      override def setWidth(pWidth: Int): Unit = {
        super.setWidth(pWidth)
        resetInfo()
      }

      override def setHeight(value: Int): Unit = {
        super.setHeight(value)
        resetInfo()
      }
    end EntryInfoWrapper

    private val row = layout.createRowHelper(2)
    private val entries =
      row
        .addChild(
          new EntriesWrapper(new SavedNodeSystemsEntries(Mth.floor(width / 3D), height - 36 - 22, 0, height - 36 - 26))
        )
        .entries
    // entries.setRenderBackground(false)
    private val info = row.addChild(new EntryInfoWrapper(Mth.ceil((width / 3D) * 2), height - 36 - 26))

    entries.addEntry(
      new SavedNodeSystem(
        "Hello world!",
        EntryVariant.DanmakuInstantiation,
        "Katrix",
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam tincidunt iaculis urna, vel tempus purus imperdiet scelerisque. Ut efficitur, ex non euismod rhoncus, augue sapien eleifend neque, et vehicula nisi libero non lacus. Proin pretium ex id tellus ultricies posuere. Aenean nec nibh dignissim, lacinia lectus vel, condimentum eros. Nullam aliquam feugiat rutrum. Quisque quis ultrices lectus, sed dapibus sapien. Nullam lacinia, magna sit amet vestibulum hendrerit, velit tellus ornare lorem, id molestie orci magna quis dui. Aliquam tincidunt viverra felis, quis lobortis felis gravida cursus. Sed eu accumsan velit. Mauris lorem lectus, tempus in sollicitudin eget, vestibulum ut lectus. Phasellus sagittis, eros eget placerat pretium, est augue venenatis sapien, ut molestie diam lorem a nibh.\n\nQuisque in ex turpis. Pellentesque et augue metus. Cras eget mollis augue, ac auctor est. Vivamus at efficitur dui. Proin auctor quam mi, et elementum erat lobortis quis. Donec laoreet quam ut turpis suscipit auctor. Nunc varius tincidunt aliquet. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Nunc ac dolor et magna tempus vestibulum. Integer ultrices ex id magna posuere, id malesuada turpis tristique. ",
        entries
      )
    )
    entries.addEntry(
      new SavedNodeSystem(
        "A system!",
        EntryVariant.DanmakuSystem,
        "Katrix",
        " Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec gravida erat dolor, sit amet sodales est congue a. Curabitur a odio facilisis, elementum lorem sollicitudin, pretium massa. Vivamus sed sem non arcu varius ornare. Cras et maximus lacus. Duis leo neque, efficitur ut tellus quis, accumsan molestie mi. Integer a erat leo. Nam iaculis magna leo, id tincidunt felis tincidunt sit amet. Phasellus mollis in ipsum eget dignissim. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia curae;\n\nMauris nulla velit, posuere vitae congue ac, pretium vitae eros. Suspendisse fringilla quam quis risus suscipit maximus. Morbi aliquam, nunc et lacinia blandit, purus nisi posuere est, sit amet varius ex urna sed turpis. Proin et velit varius, elementum ligula id, fermentum magna. Curabitur faucibus viverra nunc, vestibulum accumsan massa molestie quis. Cras vulputate imperdiet sapien lobortis faucibus. Nullam efficitur porta placerat. Etiam ut pharetra diam. ",
        entries
      )
    )
    entries.addEntry(
      new SavedNodeSystem(
        "Foo",
        EntryVariant.DanmakuInstantiation,
        "System",
        " Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed ornare commodo velit et auctor. Morbi ultrices eu tortor vel sollicitudin. Duis non porta mauris. In vulputate quam et augue aliquam congue. Morbi ut turpis ac orci venenatis mattis. Interdum et malesuada fames ac ante ipsum primis in faucibus. Pellentesque euismod laoreet posuere. Pellentesque nec ornare eros, in finibus lectus.\n\nMaecenas semper semper ex id pulvinar. Praesent ornare risus nunc, in semper tellus efficitur id. Quisque non egestas ipsum, vitae sollicitudin massa. Curabitur mattis dolor ac nibh porta, vitae lacinia risus rutrum. Sed quis semper justo. Ut ornare dapibus ligula, ut posuere tortor gravida at. Integer tempor nunc egestas mi aliquam finibus. Nunc sit amet euismod nisl, eu lacinia ipsum. In vel viverra elit. Vivamus vel nisi scelerisque dui consequat mattis sed nec diam. Morbi viverra lobortis elit et eleifend. Cras vitae consequat diam. ",
        entries
      )
    )
    entries.addEntry(
      new SavedNodeSystem(
        "Bar",
        EntryVariant.DanmakuInstantiation,
        "Steve",
        " Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam venenatis urna vel tellus venenatis rutrum. Vestibulum iaculis facilisis urna, vitae blandit eros porttitor a. Aenean auctor aliquet risus at efficitur. Praesent a rutrum dolor. Quisque nisi magna, pellentesque sit amet luctus quis, sollicitudin quis tortor. Ut vulputate mollis mi. Suspendisse venenatis, ipsum eget fringilla efficitur, magna sem ornare sapien, ac congue enim felis vel lectus. Etiam in interdum lacus, sed elementum urna. Integer ante sem, hendrerit et tincidunt scelerisque, tempus vel nisl. Vivamus sit amet hendrerit sapien. Vestibulum gravida ex at ultricies lacinia.\n\nUt finibus, purus non gravida dictum, turpis nisl gravida sem, id rhoncus purus quam eu est. Duis ut nisi eget nisl eleifend molestie. Ut eget eleifend magna, eget sodales augue. Vestibulum venenatis venenatis enim, id dapibus nisi suscipit lacinia. Mauris fermentum tortor a aliquam lacinia. Integer et est suscipit, aliquam orci ac, viverra risus. Integer neque sapien, molestie quis pellentesque ut, scelerisque eu velit. Sed diam nisi, blandit quis mi nec, bibendum eleifend ante. Integer gravida ullamcorper arcu. Donec scelerisque, nulla ac convallis scelerisque, mi risus ultrices elit, at ultrices est dolor nec elit. Ut congue magna a tellus laoreet feugiat. Maecenas venenatis efficitur purus, vitae consectetur ante pharetra sed. Donec vel mi lacus. ",
        entries
      )
    )
    entries.addEntry(
      new SavedNodeSystem(
        "Baz",
        EntryVariant.DanmakuInstantiation,
        "Alex",
        " Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam imperdiet dapibus dictum. In semper justo ut neque efficitur, id tincidunt eros tincidunt. Pellentesque fermentum lacus eu rhoncus ornare. Morbi nulla mauris, sodales scelerisque nibh in, rutrum maximus ante. Ut tincidunt, arcu sed sagittis porta, erat purus sollicitudin nisi, ut aliquam lacus velit ut odio. Integer in quam purus. Maecenas convallis velit eu est pulvinar, tincidunt ultrices sapien sodales. Morbi condimentum velit sed velit ultricies mattis. Fusce ligula massa, facilisis sit amet varius sed, pellentesque pretium ligula. Nunc eget interdum massa. Nam a lectus urna. Morbi elementum sapien elit, nec mollis sapien maximus quis. Fusce vitae lacinia nunc. Ut leo felis, suscipit at tincidunt sit amet, hendrerit in tellus. Cras in facilisis sem. Sed sagittis magna eu ante mollis, eget euismod mauris placerat.\n\nNam velit sem, consequat a blandit a, tristique nec leo. Fusce auctor urna non fermentum viverra. Suspendisse id vestibulum nisi. Vestibulum vitae neque nunc. Ut quis porttitor felis, et porta urna. Morbi tellus lorem, sodales et fermentum eu, facilisis ac nisi. Sed dignissim augue id gravida dignissim. ",
        entries
      )
    )
    entries.addEntry(
      new SavedNodeSystem(
        "Bin",
        EntryVariant.DanmakuInstantiation,
        "Remi",
        " Lorem ipsum dolor sit amet, consectetur adipiscing elit. Cras commodo nulla ac dui faucibus, non interdum diam dictum. Nunc scelerisque hendrerit elit. Sed fringilla aliquam euismod. Aenean velit turpis, malesuada ut ante vitae, hendrerit fringilla mauris. Integer rutrum sagittis ante at dictum. Pellentesque rutrum tristique tellus eu ullamcorper. Nam tincidunt luctus auctor. Donec convallis turpis ex, maximus vulputate libero suscipit at. Nulla pharetra orci at enim ultricies tempor aliquet at elit. Nullam quis dapibus orci. In hac habitasse platea dictumst. Vestibulum porttitor, dolor ut pulvinar egestas, tortor nisi suscipit purus, ut tempus lacus dolor sed lorem. Etiam nec fringilla ante. Curabitur aliquet ipsum vehicula, laoreet justo id, ullamcorper nisi. Mauris eu turpis ac diam mollis porttitor. Nullam sit amet scelerisque ligula.\n\nPellentesque vehicula risus ac neque tincidunt pharetra. In at volutpat massa. Duis et augue tortor. Suspendisse potenti. Integer posuere eleifend libero at laoreet. Praesent lacus nulla, gravida aliquam dui a, luctus accumsan tortor. Pellentesque congue, dolor eu rutrum gravida, lectus dui euismod libero, non hendrerit arcu mauris lobortis odio. Nam tempus est non vulputate varius. ",
        entries
      )
    )
    entries.addEntry(
      new SavedNodeSystem(
        "Quox",
        EntryVariant.DanmakuInstantiation,
        "Unknown",
        " Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas nec diam nec lorem tincidunt placerat. Maecenas viverra ipsum nec nisi varius feugiat. Quisque vitae risus ipsum. Donec eget lorem pulvinar, vestibulum elit vitae, venenatis purus. Sed sollicitudin ullamcorper lectus a rutrum. Praesent euismod dolor ut erat gravida elementum. Donec ultricies blandit magna eu consectetur. Morbi id molestie justo, a eleifend neque. Nunc eu ornare justo.\n\nInteger nulla lorem, pretium quis orci ac, rhoncus fringilla augue. Suspendisse vulputate neque tincidunt tristique cursus. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Nullam id leo orci. Morbi non tincidunt urna, at sodales magna. Sed at tortor at est vestibulum viverra. Praesent eu metus imperdiet, efficitur nunc at, dapibus leo. Sed varius, arcu at dictum elementum, lacus erat auctor massa, placerat pulvinar lectus lorem id ipsum. Sed pharetra, massa eu interdum lacinia, eros orci iaculis mi, eget molestie nunc tellus nec tortor. Phasellus mattis diam et pellentesque porta. Fusce rhoncus, nulla id ultricies eleifend, sem lorem cursus lectus, ut tempor magna elit ultricies justo. In hac habitasse platea dictumst. ",
        entries
      )
    )
    entries.addEntry(
      new SavedNodeSystem(
        "Last system",
        EntryVariant.DanmakuSystem,
        "Katrix",
        " Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vivamus faucibus nulla at felis tempor, id facilisis nisl accumsan. Sed massa tellus, volutpat sit amet porta eu, aliquam id leo. Nulla eleifend dignissim ligula, sit amet consectetur dui. Donec semper at massa sit amet aliquet. Nunc lacus eros, pulvinar id eros ac, vehicula pulvinar justo. Vestibulum tempor eleifend risus in ultrices. Nunc non vehicula augue. Nunc maximus arcu eget tortor luctus, eget volutpat justo tristique. Pellentesque ac massa aliquam, venenatis tortor et, blandit purus. Donec cursus semper mi, scelerisque iaculis felis fringilla at. Mauris vitae vulputate est. Etiam facilisis sem nec arcu porttitor efficitur. Sed tincidunt non leo non tincidunt. Sed magna augue, lobortis at metus vel, feugiat laoreet lectus. Sed egestas vel ligula vel mattis. Nullam mattis mi metus.\n\nDonec maximus, turpis a mollis sodales, elit odio semper diam, et consequat ligula nulla commodo augue. In enim massa, fringilla vel augue sit amet, viverra dignissim felis. Praesent pulvinar mattis eros non laoreet. Pellentesque lacus ipsum, facilisis eu eros in, consequat fermentum erat. Vivamus feugiat faucibus massa non commodo. Vestibulum ac quam a neque bibendum tincidunt. Cras sagittis lorem vitae massa fermentum tristique. Proin placerat porta consectetur. Sed porta pharetra nunc sit amet sollicitudin. Nullam euismod odio eget ullamcorper convallis. ",
        entries
      )
    )

    override def reposition(): Unit = {
      entries.updateSize((width / 3D).toInt, height - 36 - 22, 0, height - 36 - 26)
      info.setWidth(Mth.ceil((width / 3D) * 2))
      info.setHeight(height - 36 - 26)
    }

  end MainOptionsTab

  class NodeEditorTab extends RepositionTab(Component.translatable("danmakucore.gui.nodeEditor")):
    private val container: NodeContainer[DanmakuInstantiationNodeFactory.type] =
      new NodeContainer(DanmakuInstantiationNodeFactory, 50, 50, 300, 200)
    container.newNodeAt(50, 50, container.nodeFactory.NodeType.Input)
    container.newNodeAt(50, 100, container.nodeFactory.NodeType.Input)
    container.newNodeAt(150, 150, container.nodeFactory.NodeType.Output)
    container.newNodeAt(150, 200, container.nodeFactory.NodeType.Math)

    layout.addChild(container, 1, 1)

    override def reposition(): Unit = ()
  end NodeEditorTab
}
