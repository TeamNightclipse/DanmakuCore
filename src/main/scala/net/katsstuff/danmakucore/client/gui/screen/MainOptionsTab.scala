package net.katsstuff.danmakucore.client.gui.screen

import java.util

import scala.beans.BooleanBeanProperty
import scala.jdk.CollectionConverters.*

import com.mojang.blaze3d.vertex.Tesselator
import net.katsstuff.danmakucore.client.gui.widgets.{ObjectSelectionListDelegatingWidget, ScrollPanelDelegatingWidget}
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.events.{ContainerEventHandler, GuiEventListener}
import net.minecraft.client.gui.components.{AbstractWidget, Button, ObjectSelectionList}
import net.minecraft.client.gui.layouts.{GridLayout, LayoutElement}
import net.minecraft.client.gui.narration.NarratableEntry.NarrationPriority
import net.minecraft.client.gui.narration.{NarratableEntry, NarrationElementOutput}
import net.minecraft.client.gui.navigation.{FocusNavigationEvent, ScreenRectangle}
import net.minecraft.client.gui.{ComponentPath, GuiGraphics}
import net.minecraft.locale.Language
import net.minecraft.network.chat.{Component, Style}
import net.minecraft.util.{FormattedCharSequence, Mth}
import net.minecraftforge.client.gui.widget.ScrollPanel

class MainOptionsTab(screen: DanmakuEditorScreen, addWidget: AbstractWidget => Unit, removeWidget: AbstractWidget => Unit)
    extends RepositionTab(Component.translatable("danmakucore.gui.danmakuEditor.mainOptions")):

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
  ) extends ObjectSelectionList.Entry[SavedNodeSystem],
        ContainerEventHandler:

    @BooleanBeanProperty var dragging: Boolean    = false
    private var focused: Option[GuiEventListener] = None

    override def getFocused: GuiEventListener = focused.orNull

    override def setFocused(pFocused: GuiEventListener): Unit = {
      focused.foreach(_.setFocused(false))
      focused = Option(pFocused)
      focused.foreach(_.setFocused(true))
    }

    override def isFocused: Boolean                  = super[Entry].isFocused
    override def setFocused(pFocused: Boolean): Unit = super[Entry].setFocused(pFocused)

    override def nextFocusPath(pEvent: FocusNavigationEvent): ComponentPath =
      val containerPath = super[ContainerEventHandler].nextFocusPath(pEvent)
      if containerPath == null then super[Entry].nextFocusPath(pEvent)
      else containerPath
    end nextFocusPath

    private val editBox = Button
      .builder(
        Component.translatable("danmakucore.gui.danmakuEditor.edit"),
        _ => {
          println("Not implemented")
        }
      )
      .size(30, 20)
      .build()
    private val deleteBox = Button
      .builder(
        Component.translatable("danmakucore.gui.danmakuEditor.delete"),
        _ => {
          println("Not implemented")
        }
      )
      .size(30, 20)
      .build()

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
      pGuiGraphics.drawString(screen.fontInstance, name, pLeft, pTop, 0xFFFFFFFF)
      pGuiGraphics.drawString(
        screen.fontInstance,
        variant.pretty,
        pLeft,
        pTop + screen.fontInstance.lineHeight,
        0xFFFFFFFF
      )
      pGuiGraphics.drawString(
        screen.fontInstance,
        Component.translatable("danmakucore.gui.danmakuEditor.entry.author", author),
        pLeft,
        pTop + screen.fontInstance.lineHeight * 2,
        0xFFFFFFFF
      )

      deleteBox.setX(pLeft + pWidth - deleteBox.getWidth - 5)
      editBox.setX(pLeft + pWidth - deleteBox.getWidth - editBox.getWidth - 5)

      editBox.setY(pTop)
      deleteBox.setY(pTop)

      editBox.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick)
      deleteBox.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick)
    }

    override def children(): util.List[_ <: GuiEventListener] = Seq(editBox, deleteBox).asJava

    override def mouseClicked(pMouseX: Double, pMouseY: Double, pButton: Int): Boolean = {
      val res = super[ContainerEventHandler].mouseClicked(pMouseX, pMouseY, pButton)
      setFocused(null)

      if !res then
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
        screen.fontInstance.lineHeight * 3 + 4
      ):
    setRenderTopAndBottom(false)

    override def addEntry(pEntry: SavedNodeSystem): Int = super.addEntry(pEntry)

    override def getRowWidth: Int = width - 40

    override def getScrollbarPosition: Int = width - 6
  end SavedNodeSystemsEntries

  class EntriesWrapper(val selectionList: SavedNodeSystemsEntries)
      extends ObjectSelectionListDelegatingWidget[SavedNodeSystem](
        0,
        0,
        selectionList.getWidth,
        selectionList.getHeight,
        Component.translatable("danmakucore.gui.danmakuEditor.entries")
      ):
  end EntriesWrapper

  class EntryInfo(_width: Int, _height: Int, _top: Int, _left: Int, content: Seq[FormattedCharSequence])
      extends ScrollPanel(Minecraft.getInstance, _width, _height, _top, _left):
    override def getContentHeight: Int = Math.max(content.length * screen.fontInstance.lineHeight, bottom - top - 8)

    override def drawPanel(
        guiGraphics: GuiGraphics,
        entryRight: Int,
        relativeY: Int,
        tess: Tesselator,
        mouseX: Int,
        mouseY: Int
    ): Unit =
      content.view.zipWithIndex.foreach: (str, i) =>
        guiGraphics.drawString(
          screen.fontInstance,
          str,
          left + 5,
          relativeY + i * screen.fontInstance.lineHeight,
          0xFFFFFFFF
        )

    override def narrationPriority(): NarratableEntry.NarrationPriority = NarrationPriority.NONE

    override def updateNarration(pNarrationElementOutput: NarrationElementOutput): Unit = ()
  end EntryInfo

  class EntryInfoWrapper(_width: Int, _height: Int)
      extends ScrollPanelDelegatingWidget(
        0,
        0,
        _width,
        _height,
        Component.translatable("danmakucore.gui.danmakuEditor.entryInfo")
      ):
    private var _entry: Option[SavedNodeSystem] = None

    protected var scrollPanel: EntryInfo = new EntryInfo(width, height, getX, getY, Nil)

    override protected def resetPanel(): Unit = {
      // Wrap in option for first call of resetPanel
      val content = Option(_entry).flatten.toSeq.flatMap: entry =>
        Language.getInstance
          .getVisualOrder(
            screen.fontInstance.getSplitter.splitLines(
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

      scrollPanel = new EntryInfo(width, height, getY, getX, content)
    }

    def entry: Option[SavedNodeSystem] = _entry
    def entry_=(value: Option[SavedNodeSystem]): Unit = {
      _entry = value
      resetPanel()
    }
  end EntryInfoWrapper

  // entries.setRenderBackground(false)
  private val rows = layout.createRowHelper(2)
  private val side = rows.addChild(new GridLayout())
  private val info = rows.addChild(new EntryInfoWrapper(Mth.ceil((screen.width / 3D) * 2), screen.height - 26 + 1))

  private val addSystemButton = side.addChild(
    Button
      .builder(
        Component.translatable("danmakucore.gui.danmakuEditor.addSystem"),
        _ => {
          println("Not implemented")
        }
      )
      .size(Mth.floor(screen.width / 3D), Button.DEFAULT_HEIGHT)
      .build(),
    1,
    0,
    side.newCellSettings().alignHorizontallyCenter()
  )
  private val addInstantiationButton = side.addChild(
    Button
      .builder(
        Component.translatable("danmakucore.gui.danmakuEditor.addInstantiation"),
        _ => {
          val newTab = new NodeEditorTab(screen, addWidget, removeWidget)
          screen.tabs += newTab
          screen.onTabsChange(reposition = true)
          screen.tabManager.setCurrentTab(newTab, false)
        }
      )
      .size(Mth.floor(screen.width / 3D), Button.DEFAULT_HEIGHT)
      .build(),
    2,
    0,
    side.newCellSettings().alignHorizontallyCenter()
  )
  private val entries =
    side
      .addChild(
        new EntriesWrapper(
          new SavedNodeSystemsEntries(
            Mth.floor(screen.width / 3D),
            screen.height - 26 + 1 - addSystemButton.getHeight - addInstantiationButton.getHeight,
            0,
            screen.height - 26 + 1 - addSystemButton.getHeight - addInstantiationButton.getHeight
          )
        ),
        0,
        0
      )
      .selectionList

  override def reposition(): Unit = {
    entries.updateSize(
      (screen.width / 3D).toInt,
      screen.height - 26 + 1 - addSystemButton.getHeight - addInstantiationButton.getHeight,
      0,
      screen.height - 26 + 1 - addSystemButton.getHeight - addInstantiationButton.getHeight
    )
    info.setWidth(Mth.ceil((screen.width / 3D) * 2))
    info.setHeight(screen.height - 26 + 1)
    addSystemButton.setWidth(Mth.floor(screen.width / 3D))
    addInstantiationButton.setWidth(Mth.floor(screen.width / 3D))
  }

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
