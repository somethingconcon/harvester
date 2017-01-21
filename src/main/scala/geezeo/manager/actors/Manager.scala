package harvester.manager.actors

class RoutingManager extends Actor with ActorLogging {

  // define supervision for RoutingManager children

  def recieve = {
    case HarvestStarted => 
    case HarvestEnded   => 
    case _ => new 
  }
}

object RoutingManager {

  //command notifications
  case object HarvestStarted
  case object HarvestEnded
  case class  HarvestErrored(throwable: Throwable, actor: ActorRef, partner: Partner, user: User, endpoint: String)
  case class  HarvestError(harvestError: HarvestErrored)

}