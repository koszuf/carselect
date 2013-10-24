package code.model

import net.liftweb.mapper._

class Car extends LongKeyedMapper[Car] with IdPK{

  def getSingleton = Car

  object carBrand extends MappedString(this, 45) {
    override def dbColumnName = "carbrand"
  }

  object carModel extends MappedString(this, 45) {
    override def dbColumnName = "carmodel"
  }

}

object Car extends Car with LongKeyedMetaMapper[Car] {
  override def dbTableName = "cars"
}
