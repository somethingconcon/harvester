package harvester.scheduler // i fucking hate package collisions

import 
  akka.actor.{
    ActorSystem,
    Props,
    ActorRef,
    Actor,
    ActorLogging
  },
  harvester.manager.actors.Partner

class Scheduler() extends Actor with ActorLogging {
  
  import Partner.{
    Register,
    Request
  }
  
  var partners = Set[ActorRef]()
  var max      = 0
  var inflight = 0

  def receive = {
    case Register(partner) => register(partner)
    case Request(users)    => { println("request")}
  }

  private def register(partner: ActorRef) = {
    partners += partner
  }
  private def getHarvestQueueSize = 0
}

object Scheduler {
  def props(implicit as: ActorSystem) = {
    Props(new Scheduler)
  }
}