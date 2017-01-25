package harvester.manager.actors

import 
  akka.actor.{
    ActorSystem,
    Props,
    ActorRef,
    Actor,
    ActorLogging
  },
  // akka.persistence.PersistentActor,
  org.joda.time.{
    Instant,
    DateTime
  }

class CommandRouter() extends Actor with ActorLogging {

  import 
    Actor.Receive,
    context._,
    CommandRouter._,
    harvester.{
      getActorRef,
      nowDateTime
    }
    
  def receive = {
    case hCommand: HCommand => { 
      println("command event:" + hCommand.toString) //ewww
    }
  }
}

object CommandRouter {

 def props = {
    import akka.actor.Props

    Props(new CommandRouter)
  }
  trait HCommand
  case class WorkerCommand()           extends HCommand
  case class WarnEndpointCommand()     extends HCommand
  case class DownEndpointCommand()     extends HCommand
  case class TimeoutEndpointCommand()  extends HCommand
  case class SlowEndpointRateCommand() extends HCommand
  case class FastEndpointRateCommand() extends HCommand

}