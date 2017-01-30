import 
  akka.actor.{
    ActorSystem,
    Props,
    ActorRef,
    Actor,
    ActorLogging },
  com.typesafe.config._,
  harvester.manager.actors.Router,
  harvester.scheduler.Scheduler,
  monix.execution.schedulers.AsyncScheduler

object Havester extends App {
  
  import harvester._
  
  val systemSettings = ConfigFactory.systemProperties
  val config         = getConfig
  
  def getConfig = {
    if (args.size == 0) {
      ConfigFactory.load
    } else {
      throw new Error("Cannot start application with params.")
    }
  }
  
  implicit val system = ActorSystem("Harvester", config)
  implicit val dao    = new Dao { 
    def primary = new Dal(new Client)
  }

  // HarvestManager is a router to delegate requests
  val router    = system.actorOf(Router.props, "RoutingManager")
  val scheduler = system.actorOf(Scheduler.props, "Scheduler")
  // This is the object we're going to attach events to
  // val scheduler = new AsyncScheduler()
  
  start(config, scheduler)
}