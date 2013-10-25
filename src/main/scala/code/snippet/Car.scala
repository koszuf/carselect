package code.snippet

import code.model._
import net.liftweb.mapper._
import net.liftweb.common._
import net.liftweb.http.{S, SHtml}

import net.liftweb.http.js.JsCmds._
import net.liftweb.http.SHtml._
import net.liftweb.http.js.JsCmd
import net.liftweb.http.js.JsCmds.ReplaceOptions
import net.liftweb.common.Full
import net.liftweb.util.Helpers._


class Car {
  var carMaker = ""
  var carModel = ""


  private def modelChoice(maker: String): Seq[(String, String)] = {
    val models = CarMaker.find(By(CarMaker.carMaker, maker)) match {
      case Full(m: CarMaker) => m.models.map(_.model.toString).toList
      case Empty => List(" _ ") //There's no model for that brand
    }
    models.map(m => (m, m)).toSeq
  }

  private def replace(marker: String): JsCmd = {
    val models = modelsFor(marker)
    val first = models.head
    ReplaceOptions("carmodel", models.map(m => (m, m)), Full(first))
  }

  private def modelsFor(makerName: String): List[String] = {
    CarMaker.find(By(CarMaker.carMaker, makerName)) match {
      case Full(m: CarMaker) => m.models.map(_.model.get).toList
      case Empty => List(" _ ") //There's no model
    }
  }


  def carAdd = {
    def process() = {
      val car = Car.create
      car.carBrand.set(carMaker)
      car.carModel.set(carModel)
      car.save()
      S.notice("Saved.")
      S.redirectTo("/show")
    }

    "#carbrand" #> SHtml.ajaxUntrustedSelect(CarMaker.findAll.map(_.carMaker.toString).map(m => (m, m)), Full(carMaker), {
      m => carMaker = m; After(200, replace(carMaker))
    }) &
      "#carmodel" #> SHtml.select(modelChoice(carMaker), Empty, carModel = _) &
      "name=submit" #> SHtml.submit("Save", process)
  }

  def carList =
    "#row *" #> Car.findAll.map(car =>
      "#brand *" #> car.carBrand &
        "model *" #> car.carModel)

}



