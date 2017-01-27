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

class EventRouter extends Actor with ActorLogging {

  import 
    Actor.Receive,
    context._,
    EventRouter._,
    harvester.{
      getActorRef,
      nowDateTime
    }

  // routing is for managing events that happen outside 
  // of the application
  // internal messages go directly to the actors necessary
  def receive = {
    case hLifeCycleEvent: HLifeCycleEvent => { 
      become(lifecycle(hLifeCycleEvent))
    }
    case hScheduleEvent: HSchedulerEvent => {
      become(scheduler(hScheduleEvent))
    }
    case hRequest: HRequest => {
      become(request(hRequest))
    }
  }
  
  // TODO
  // replace state strings with case classes
  def lifecycle(event: HLifeCycleEvent): Receive = {
    case enqueue: HEnqueue => {
      val state   = "enqueue"
      val message = stateMessage("Harvest Enqueue:")
      
      eventDetail(event, message, state)
    }
    case fail: HFailed => {
      val state   = "failed"
      val message = stateMessage("Harvest Failed:")
      
      eventDetail(event, message, state)
    }
    case finish: HFinished => {
      val state   = "finished"
      val message = stateMessage("Harvest Finished:")
      
      eventDetail(event, message, state)
    }
    case start: HStarted => {
      val state   = "started"
      val message = stateMessage("Harvest Started:")
      
      eventDetail(event, message, state)
    }
  }
  
  def request(event: HRequest): Receive = {
    case HSchedule(endpoint, harvestAt, userId) => {
      val uuid      = UUID.get
      val timeStamp = nowDateTime.toString

      import members.HUser
      val harvest = new Harvest(1, s"${uuid} ${timeStamp}", HUser(userId))
      
      // Do something with the Harvest
      log.info(s"Scheduling a harvest at: ${timeStamp}")
    }
  }

  // FSM please
  def scheduler(event: HSchedulerEvent): Receive = {
    case _ => println("scheduler event.")
  }
}

object UUID {
  def get = {
    "zszsdzsdo"
  }
}

object EventRouter extends Routing {
  
  import
    harvester.{ fmtDateTime, nowDateTime },
    members.{
      Endpoint,
      HUser
    }
  
  def stateMessage(m: String) = {
    s"${m} ${fmtDateTime(nowDateTime)}"
  }

  def props(implicit as: ActorSystem) = {
    import akka.actor.Props

    Props(new EventRouter)
  }
  
  trait HEvent {
    val at  : DateTime
    val uuid: String
  }

  // HLifeCycleEvent
  // Once a Harvest has been kicked off (the scheduler submits the job)
  // Enqueue -> Finished/Failed persist events track timeouts
  trait HLifeCycleEvent extends HEvent {
    val harvest: Harvest
    lazy val uuid = harvest.id
  }
  case class HFailed  (at: DateTime, harvest: Harvest) extends HLifeCycleEvent
  case class HFinished(at: DateTime, harvest: Harvest) extends HLifeCycleEvent
  case class HStarted (at: DateTime, harvest: Harvest) extends HLifeCycleEvent
  case class HEnqueue (at: DateTime, harvest: Harvest) extends HLifeCycleEvent
  
  // HSchedulerEvent
  // 
  trait HSchedulerEvent extends HEvent {
    val uuid: String
    val at: DateTime = nowDateTime
  }
  case class HEnqueueAck    (uuid: String) extends HSchedulerEvent
  case class HSchedulerError(uuid: String, error: Error) extends HSchedulerEvent
  case class HTimeout       (uuid: String) extends HSchedulerEvent
  
  // HRequests
  //
  trait HRequest
  case class HSchedule(endpointId: Int, at: DateTime, userId: Int) extends HRequest

  // wrapper case for Events
  case class EventDetail(event: HEvent, message: String, state: String, time: DateTime)
  
  // Case for Harvest Data
  // endpoint: location for harvest
  // id:       uuid + time
  // user:     HUser
  case class Harvest(endpointId: Int, id: String, user: HUser)

  // this is just a pathetic spike on this FIXME
  // this needs to be an actor, duh
  case class HMetrics(event: HLifeCycleEvent)
  import
    scala.util.{
      Failure,
      Success
    }
  
  def create(eventDetail: EventDetail) = {

    // dao.create(eventDetail.event, eventDetail.state).onComplete {
    //   case Success(event) => event
    //   case Failure(ex)    => throw new Error(s"Trying to write to db store. ${ex.getMessage}")
    // }.andThen { event: HEvent =>
    //   HMetrics(event)
    // }
    println("create event status")
  }

  // move into its own builder
  def eventDetail(event: HEvent, message: String, state: String) = {
    EventDetail(event, message, state, nowDateTime)
  }
}