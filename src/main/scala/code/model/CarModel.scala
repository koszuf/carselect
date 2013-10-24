package code.model

import net.liftweb.mapper._

class CarModel extends LongKeyedMapper[CarModel] with IdPK {

  def getSingleton = CarModel

  object model extends MappedString(this, 50) {
    override def dbColumnName = "model"
  }

  object carMaker extends MappedLongForeignKey(this, CarMaker) {
    override def dbColumnName = "carmaker_id"
  }

}

object CarModel extends CarModel with LongKeyedMetaMapper[CarModel] {
  override def dbTableName = "carmodels"
}
