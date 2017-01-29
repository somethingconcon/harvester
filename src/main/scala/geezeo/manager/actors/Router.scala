package harvester.manager.actors

import akka.actor.{
  ActorSystem,
  ActorRef,
  Actor,
  ActorLogging,
  Props
}// ,
// akka.persistence.PersistentActor

class Router(implicit as: ActorSystem) extends Actor with ActorLogging with Routing {
  
  // event classes need to be centralized 
  import
    harvester.{
      fmtDateTime,
      getActorRef,
      nowDateTime,
      HMonitor },
    HMonitor._,
    harvester.manager.actors.CommandRouter.HCommand,
    harvester.manager.actors.EventRouter.HEvent,
    scala.collection.mutable.{ Map => MMap },
    Router._
  
  val children = MMap[RoutingType, ActorRef]()

  def getRouter(t: RoutingType) = children.get(t)
  
  override def preStart(): Unit = {
    children += (EventRouting()  -> getActorRef(EventRouter.props,   s"EventRouter-${fmtDateTime(nowDateTime)}"  ))
    children += (CommandRouting()-> getActorRef(CommandRouter.props, s"CommandRouter-${fmtDateTime(nowDateTime)}"))
  }

  /** 
    TODO
    supervision for the individual routers.
    actor lifecycle events and killing when bad things happen.
  */
  import 
    as._ // import actor system methods

  def receive = {
    case command: HCommand => {
      getRouter(CommandRouting()).map { commander => commander ! command }
    }
    case event: HEvent => {
      getRouter(EventRouting()).map { eventer => eventer ! event }
    }
    case message: String => {
      log.info(systemMemory.toString)
      log.info(message)
      log.info("not doing anything")
    }
  }
}

object Router {
  
  def props(implicit as: ActorSystem) = {
    import akka.actor.Props

    Props(new Router)
  }

  trait RoutingType
  case class CommandRouting() extends RoutingType
  case class EventRouting()   extends RoutingType
  case class MessageRouting() extends RoutingType

}

// fake bullshit for persistence stuff
trait Routing {
  def dao = new Dao { def events = new Client() }
}
trait Dao {
  def events: Client
}
class Client { def write(d: Any) = println("write"); def delete(d: Any) = println("delete") }