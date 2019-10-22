package ocwalk.pages

import ocwalk.box.BoxClass._
import ocwalk.box._
import ocwalk.common._
import ocwalk.icon.MaterialDesign
import ocwalk.jqbox._
import ocwalk.mvc.{Controller, HomePage, LibraryPage}
import ocwalk.style._

/** Layout for the page header */
object HeaderLayout {
  private implicit val headerStyle: Styler = under(headerId).sub(
    isRegion |> (
      _.fillColor(primaryColor),
      _.fixedH(64.0)
    ),
    isHBox && navId |> (
      _.spacingX(0.0)
      ),
    under(navButtonClass).sub(
      isButton |> (
        _.fillColor(primaryColor),
        _.cursor(Cursors.Pointer),
        _.fillY
      ),
      isButton && Hover |> (
        _.fillColor(primaryColor.lighter)
        ),
      isHBox |> (
        _.pad(15.0 xy 0.0),
        _.spacingX(10.0)
      ),
      isText |> (
        _.textFont(robotoSlab),
        _.textSize(24.0),
        _.textColor(whiteColor)
      ),
      isIcon |> (
        _.iconSize(32.0),
        _.iconColor(whiteColor)
      ),
    ),
    under(userBoxId).sub(
      isText |> (
        _.textFont(robotoSlab),
        _.textSize(16.0),
        _.textColor(whiteColor),
      ),
      isIcon |> (
        _.iconColor(whiteColor),
        _.iconSize(32.0)
      )
    )
  )

  /** Converts button into navigation button */
  def nav(button: ContainerButtonBox): ContainerButtonBox = button.addClass(navButtonClass)

  /** Navigation that takes your to home page */
  val navHome: ContainerButtonBox = nav(imageTextButton(logoWhite32, "OCWALK"))
  /** Navigation that takes your to library page */
  val navLibrary: ContainerButtonBox = nav(iconTextButton(MaterialDesign.LibraryMusic, "Library"))
  /** Navigation that takes your to shop website */
  val navShop: ContainerButtonBox = nav(iconTextButton(MaterialDesign.ShoppingCart, "Buy Ocarina"))

  private val userText: TextBox = text.as("Guest")
  private val signInButton: TextButtonBox = textButton.as("Sign In")
  private val signOutButton: TextButtonBox = textButton.as("Sign Out")
  private val userBox: ContainerBox = container(userBoxId).sub(
    hbox.fillY.sub(
      vbox.fillY.sub(
        userText,
        signInButton
      ),
      icon.as(MaterialDesign.Person)
    )
  )

  /** Binds the listeners to header buttons */
  def bind(controller: Controller): Unit = {
    navHome.onClick(controller.showPage(HomePage()))
    navLibrary.onClick(controller.showPage(LibraryPage()))
    navShop.onClick(controller.showShop())
  }

  /** Creates a header box with navigation */
  def header(nav: List[ContainerButtonBox], user: Boolean = true): Box = {
    val userBoxList = if (user) userBox :: Nil else Nil
    val fillerList = List(container.fillX)
    region(headerId).fillX.sub(
      hbox(navId).fillX.subs(nav ++ fillerList ++ userBoxList)
    )
  }
}