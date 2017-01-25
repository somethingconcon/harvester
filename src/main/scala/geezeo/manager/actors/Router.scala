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
  
  // define supervision for RoutingManager children
  /** 
    TODO
    supervision for the individual routers.
    actor lifecycle events and killing when bad things happen.
  */
  import 
    harvester.getActorRef,
    as._ // import actor system methods

  lazy val eventRouter   = getActorRef(EventRouter.props, "HEventRouter-${now}")
  lazy val commandRouter = getActorRef(CommandRouter.props, "HEventRouter-${now}")

  // event classes need to be centralized 
  import 
    harvester.manager.actors.CommandRouter.HCommand,
    harvester.manager.actors.EventRouter.HEvent
  def receive = {
    case hCommand: HCommand => {
      commandRouter ! hCommand
    }
    case hEvent: HEvent => {
      eventRouter ! hEvent
    }
  }
}

object Router {
  def props(implicit as: ActorSystem) = {
    import akka.actor.Props

    Props(new Router)
  }
}

trait Routing {
  def dao = new Dao { def events = new Client() }
}
trait Dao {
  def events: Client
}
class Client { def write(d: Any) = println("write"); def delete(d: Any) = println("delete") }