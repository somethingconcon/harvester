package harvester.manager

import
  akka.actor.{
    Actor,
    ActorRef,
    ActorSystem,
    Props },
  akka.testkit.{ 
    TestActors,
    TestActorRef,
    TestProbe,
    DefaultTimeout, 
    ImplicitSender, 
    TestKit },
  com.typesafe.config.ConfigFactory,
  org.joda.time.DateTime,
  org.scalamock.scalatest.MockFactory,
  org.scalatest.{
    BeforeAndAfterAll,
    Matchers,
    WordSpecLike },
  scala.concurrent.{
    ExecutionContext,
    Future
  },
  concurrent.duration._

class ManagerSpec() extends TestKit(ActorSystem("ManagerSpec")) 
                    with WordSpecLike
                    // with MockFactory
                    with ImplicitSender
                    with Matchers 
                    with BeforeAndAfterAll {
  
  import
    harvester.manager.actors.Router,
    ManagerSpec._

  val router      = TestActorRef[Router]
  val routerActor = router.underlyingActor
  
  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  // test hEvents go to the EventsRouter
  "Router" should {
    
    import harvester.manager.actors.Router._

    "forward HarvestEvent messages to the EventRouter" in {
      val probe = TestProbe()
      (routerActor.getChild _).expects(EventRouting(), Option(probe))  
      
      within(timeout) {
        router ! hEvent
        routerActor.getChild(EventRouting()).map { 
          _.expectMsg(hEvent)
        }
      }
    }
  } 
}

trait ActorSpec {
  val config: String
}

object ManagerSpec extends ActorSpec {
  
  // Define your test specific configuration here
  
  import
    harvester.manager.actors.EventRouter._,
    members.HUser,
    scala.language.postfixOps
  
  override val config = """
    akka {
      loglevel = "WARNING"
    }
    """
  val hEvent =  new HLifeCycleEvent {
    val harvest = new Harvest(1, "id", HUser(1))
    val at      = new DateTime()
  }

  val timeout = 500.millis
  /**
   * An Actor that forwards every message to a next Actor
   */
  // class ForwardingActor(next: ActorRef) extends Actor {
  //   def receive = {
  //     case msg => next ! msg
  //   }
  // }
 
  /**
   * An Actor that only forwards certain messages to a next Actor
   */
  // class FilteringActor(next: ActorRef) extends Actor {
  //   def receive = {
  //     case msg: String => next ! msg
  //     case _           => None
  //   }
  // }
 
  /**
   * An actor that sends a sequence of messages with a random head list, an
   * interesting value and a random tail list. The idea is that you would
   * like to test that the interesting value is received and that you cant
   * be bothered with the rest
   */
  // class SequencingActor(next: ActorRef, head: Seq[String],
  //                       tail: Seq[String]) extends Actor {
  //   def receive = {
  //     case msg => {
  //       head foreach { next ! _ }
  //       next ! msg
  //       tail foreach { next ! _ }
  //     }
  //   }
  // }
}