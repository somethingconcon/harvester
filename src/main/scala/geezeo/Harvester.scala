import 
  akka.actor.{
    ActorSystem,
    Props,
    ActorRef,
    Actor,
    ActorLogging },
  com.typesafe.config._,
  harvester.manager.actors.Router

object Havester extends App {
  
  import harvester._

  val config = ConfigFactory.load
  
  // setup Actor System
  implicit val system = ActorSystem("Scheduler", config)
  implicit val hlog   = HLogger.apply

  // HarvestManager is a router to delegate requests
  val routingManager = getActorRef(Router.props, "RoutingManager")
  
  while (true) {

  }
}