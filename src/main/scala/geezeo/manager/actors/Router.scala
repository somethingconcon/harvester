package harvester.manager.actors

import akka.actor.{
  ActorSystem,
  ActorRef,
  Actor,
  ActorLogging,
  Props
},
akka.persistence.PersistentActor

class Router(implicit as: ActorSystem) extends Actor with ActorLogging with Routing {
  
  // event classes need to be centralized 
  import
    harvester.{
      fmtDateTime,
      getActorRef,
      nowDateTime },
    harvester.manager.actors.CommandRouter.HCommand,
    harvester.manager.actors.EventRouter.HEvent,
    scala.collection.mutable,
    Router._
  
  val children = mutable.Map[RoutingType, ActorRef]()

  def getChild(t: RoutingType) = children.get(t)
  
  override def preStart(): Unit = {
    children += (EventRouting()  -> getActorRef(EventRouter.props,   s"HEventRouter-${fmtDateTime(nowDateTime)}"  ))
    children += (CommandRouting()-> getActorRef(CommandRouter.props, s"HCommandRouter-${fmtDateTime(nowDateTime)}"))
  }

  // define supervision for RoutingManager children
  /** 
    TODO
    supervision for the individual routers.
    actor lifecycle events and killing when bad things happen.
  */
  import 
    as._ // import actor system methods

  def receive = {
    case command: HCommand => {
      getChild(CommandRouting()).map {
        case child: ActorRef => child ! command
      }
    }
    case event: HEvent => {
      getChild(EventRouting()).map {
        case child: ActorRef => child ! event
      }
    }
  }
}

object Router {

  import 
    harvester.nowDateTime
  
  def props(implicit as: ActorSystem) = {
    import akka.actor.Props

    Props(new Router)
  }

  trait RoutingType
  case class CommandRouting() extends RoutingType
  case class EventRouting() extends RoutingType


}

trait Routing {
  def dao = new Dao { def events = new Client() }
}
trait Dao {
  def events: Client
}
class Client { def write(d: Any) = println("write"); def delete(d: Any) = println("delete") }