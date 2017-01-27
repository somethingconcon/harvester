package harvester.manager.actors

import 
  akka.actor.{
    ActorSystem,
    Props,
    ActorRef,
    Actor,
    ActorLogging
  },
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
      println("harvest command event:" + hCommand.toString) //ewww
    }
    case sCommand: SCommand => {
      println("scheduler command event:" + sCommand.toString) //ewww
    }
  }
}

object CommandRouter {

  def props(implicit as: ActorSystem) = {
    import akka.actor.Props

    Props(new CommandRouter)
  }

  trait HCommand // HarvestCommand
  case class WorkerCommand()               extends HCommand
  case class WarnEndpointCommand()         extends HCommand
  case class DownEndpointCommand()         extends HCommand
  case class TimeoutEndpointCommand()      extends HCommand
  case class SlowdownEndpointRateCommand() extends HCommand
  case class SpeedUpEndpointRateCommand()  extends HCommand

  trait SCommand // SchedulerCommand
  case class RateCommand()                 extends SCommand
}