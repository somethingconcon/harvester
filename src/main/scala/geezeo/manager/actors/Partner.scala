package harvester.manager.actors

import 
  akka.actor.{
    ActorSystem,
    Props,
    ActorRef,
    Actor,
    ActorLogging
  },
  akka.util.Timeout,
  harvester.scheduler.Scheduler,
  members.{
    HPartner
  },
  org.joda.time.{
    DateTime,
    Duration,
    Instant
  },
  scala.concurrent.{
    Await,
    duration
  }

// I honestly don't really know what this is for yet.
// FSM Please
class Partner(harvestPartner: HPartner, harvestScheduler: ActorRef) extends Actor with ActorLogging {
  
  import
    context._,
    Partner._

  implicit val timeout: Timeout = Timeout(duration.Duration(1000, "seconds"))
  /**
    Looking for a better solution for this
      - mutable state within the partner actor to adapt during a harvest cycle
  */
  var activeCycle: Cycle = NoCycle()
  var activeBatch: Batch = NoBatch()
  var eventStream: HarvestEventStream = NoStream() // it truns out i might not need this
  var registered = false
  var pauseUntil: DateTime = _

  override def preStart() = {
    import scala.util.{
      Failure,
      Success
    }
    // make this a blocking call solution
    // so it will fail if a partner cannot
    // be attached to the scheduler
    // case Registered() => { registered = true; println("registered") }
    regsiter.onComplete {
      case Success(registerAskResponse) => { 
        setRegistered(true)
      }
      case Failure(ex)                  => { 
        setRegistered(false)
        // retry?
        throw new Exception(s"Cannot start Partner Actor ${harvestPartner.name}")
      }
    }
  }

  def receive = {
    case Next    (numberOfUsers) => next(numberOfUsers)
    case Throttle(limit)         => println("throttle")
    case Pause   (delay)         => println("pause")
    case Start()                 => cycle
  }

  private def cycle = {
    harvestPartner.cycle
  }

  private def next(numberOfUsers: Option[Int]): Unit = {
    println("next")
    val notPaused = true //FIXME

    if(registered && notPaused) {
      schedule(numberOfUsers)
    } else {
      error("called next but cannot deliver new batch of users.")
    }
  }

  private def pause(delay: Duration) = {
    // nowInstant + delay
    // scheduler ! UnpauseAt()
  }

  private def setRegistered(r: Boolean): Unit = {
    if(r) {

    } else {

    }
  }

  private def schedule(numberOfUsers: Option[Int]): Unit = {
    val userBatchNext = activeCycle.next
    
    if(userBatchNext.size > 0) {
      harvestScheduler ! Request(userBatchNext)
    } else {
      complete
    }
  }
  
  private def complete = {
    // reset
    activeCycle = NoCycle()
    activeBatch = NoBatch()
    eventStream = NoStream()
    
    harvestScheduler ! CycleComplete()
  }

  private def start = {
    activeCycle = ActiveCycle()
    harvestScheduler ! Request(activeCycle.next)
  }

  private def regsiter = {
    import 
      akka.pattern.ask
    
    harvestScheduler ? Register(self)
  }

}

object Partner {
  
  import
    org.joda.time.Duration,
    members.HUser

  /*
    It has been recommended that communication protols are centralized so that
    they can be easily identified.

    If that's the case I need to move these into an object that contains all 
    messages that the system's actors can send to each other.
  */
  case class CycleComplete()
  // the size attribute should be something
  // we can do more with
  case class Next(size: Option[Int])
  case class Pause(duration: Duration)
  case class Register(partner: ActorRef)
  case class Registered()
  case class Throttle(downsize: Int)
  case class Unthrottle()
  case class SendMore()
  case class Start()

  case class Request(users: Set[HUser])

  // harvest instance case classes
  trait HarvestEventStream
  case class NoStream() extends HarvestEventStream
  
  trait Batch 
  case class NoBatch() extends Batch
  
  trait Cycle {
    def next: Set[HUser]
  }
  case class NoCycle() extends Cycle {
    def next = Set[HUser]()
  }
  case class ActiveCycle() extends Cycle {
    def next = Set[HUser]()
  }

  def error(message: String) = {
    // router ! message
  }

  def props(harvestPartner: HPartner, harvestScheduler: ActorRef)(implicit as: ActorSystem) = {
    // where do i send schedule events?
    Props(new Partner(harvestPartner, harvestScheduler))
  }
}