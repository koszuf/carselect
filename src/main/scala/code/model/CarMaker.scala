package code.model

import net.liftweb.mapper._

class CarMaker extends LongKeyedMapper[CarMaker] with IdPK with OneToMany[Long, CarMaker] {

  def getSingleton = CarMaker

  // producent samochodu
  object carMaker extends MappedString(this, 50) {
    override def dbColumnName = "name"
  }

  //wszystkie modele danego producenta
  object models extends MappedOneToMany(CarModel, CarModel.carMaker)

}

object CarMaker extends CarMaker with LongKeyedMetaMapper[CarMaker] {
  override def dbTableName = "carmakers"
}
