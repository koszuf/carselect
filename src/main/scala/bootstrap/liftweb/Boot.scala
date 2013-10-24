package bootstrap.liftweb

import net.liftweb._
import util._
import Helpers._

import common._
import http._
import sitemap._
import Loc._
import net.liftmodules.JQueryModule
import net.liftweb.http.js.jquery._
import net.liftweb.mapper.{Schemifier, StandardDBVendor, DB}
import net.liftweb.db.DefaultConnectionIdentifier
import code.model._


/**
 * A class that's instantiated early and run.  It allows the application
 * to modify lift's environment
 */
class Boot {
  def boot {
    // where to search snippet
    LiftRules.addToPackages("code")
    if (!DB.jndiJdbcConnAvailable_?) {
      val vendor =
        new StandardDBVendor(Props.get("db.driver") openOr "org.h2.Driver",
          Props.get("db.url") openOr
            "jdbc:h2:lift_proto.db;AUTO_SERVER=TRUE",
          Props.get("db.user"), Props.get("db.password"))

      LiftRules.unloadHooks.append(vendor.closeAllConnections_! _)

      DB.defineConnectionManager(DefaultConnectionIdentifier, vendor)
    }

    Schemifier.schemify(true, Schemifier.infoF _, Car, CarModel, CarMaker)

    val models = CarModel.findAll()
    if (models.length < 10)
      createCarMarkersAndModels

    // Build SiteMap
    val entries = List(
      Menu.i("Home") / "index",
      Menu.i("Show cars") / "show",
      Menu.i("Add cars") / "add"
    )

    // set the sitemap.  Note if you don't want access control for
    // each page, just comment this line out.
    LiftRules.setSiteMap(SiteMap(entries: _*))

    //Show the spinny image when an Ajax call starts
    LiftRules.ajaxStart =
      Full(() => LiftRules.jsArtifacts.show("ajax-loader").cmd)

    // Make the spinny image go away when it ends
    LiftRules.ajaxEnd =
      Full(() => LiftRules.jsArtifacts.hide("ajax-loader").cmd)

    // Force the request to be UTF-8
    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))

    // Use HTML5 for rendering
    LiftRules.htmlProperties.default.set((r: Req) =>
      new Html5Properties(r.userAgent))

    //Init the jQuery module, see http://liftweb.net/jquery for more information.
    LiftRules.jsArtifacts = JQueryArtifacts
    JQueryModule.InitParam.JQuery = JQueryModule.JQuery172
    JQueryModule.init()

  }


  def createCarMakerAndModels(Maker: String, Models: List[String]) {

    val maker = CarMaker.create
    maker.carMaker.set(Maker)
    maker.save()
    val makerId = maker.id.get

    Models.map(model => {
      val carModel = CarModel.create
      carModel.model.set(model)
      carModel.carMaker.set(makerId)
      carModel.save()
    }
    )
  }


  def createCarMarkersAndModels {
    val a1 = ("Alfa Romeo", List("GT", "ALFA BRERA", "ALFA MITO", "147", "148", "159", " 159 SPORTWAGON", " GT", "ALFA SPIDER"))
    val a2 = ("Audi", List(" A3", " A3 CABRIOLET", " A4", " A4 ALLROAD QUATTRO", " A5", " A5 CABRIOLET", " A6", " A6 ALLROAD QUATTRO", " A8", " A8L", " Q5", " Q7", " S3", " S4", " S5", " S6", " TT", " TT RS", " TTS"))
    val a3 = ("BMW", List("116i", "118d", "130i", "316i", "318d", "325i", "330d", "330i", "520d", "523i", "530d", "530i", "530xd", "530xi", "540i", "550i", "630i", "650i", "730d", "730i", "740i", "745d", "750i", "760i", "X3 2.0d", "X3 2.5i", "X3 3.0d", "X3 3.0i", "X5 3.0d", "X5 xDrive 30i", "X5 xDrive 35d", "X5 xDrive 48i", "X6 xDrive 30d", "X6 xDrive 35i", "X6 xDrive 50i", "Z4 2.2i", "Z4 2.5i", "Z4 3.0i"))
    val a4 = ("Cadillac", List("CTS", "Escalade", "SRX"))
    val a5 = ("Chevrolet", List("AVEO", "Captiva", "Corvette Z06", "Epica", "Lacetti", "REZZO", "SPARK"))

    val cars = List(a1, a2, a3, a4, a5)
    cars.map(p => createCarMakerAndModels(p._1, p._2))

  }
}