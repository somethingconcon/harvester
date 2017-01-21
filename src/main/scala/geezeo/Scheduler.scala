import 
  akka.actor.{
    ActorSystem,
    Props,
    ActorRef,
    Actor,
    ActorLogging,
    PersistentActor }

object Scheduler extends App {
  
  // get config
  val config = 
  // setup Actor System
  implicit val system = ActorSystem("Scheduler-ActorSystem")

  // HarvestManager is a router to delegate requests
  val routingManager = system.actorOf(Props[RoutingManager], "HarvestScheduler")
  
  // open http port
  // get data for scheduler 
  // start streams
}

class RoutingManager(implicit system: ActorSystem) extends Actor with ActorLogging with PersistentActor {
  
  // define supervisor strat for Routers

  val routers: Set[ActorRef] = Set()

  lazy val eventRouter = system.actorOf(Props[RouteManager], "EventRouter")

  def recieve = {
    case e: HEvent => eventRouter ! e
  }
}

// Cluster Management
// import akka.actor.Terminated
// system.actorOf(Props(classOf[Terminator], a), "terminator")