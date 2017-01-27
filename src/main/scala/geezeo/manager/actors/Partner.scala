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

// I honestly don't really know what this is for yet.
class Partner(internalName: String) extends Actor with ActorLogging {
  
}

object Partner {

  def props(name: String)(implicit as: ActorSystem) = {
    Props(new Partner(name))
  }
}