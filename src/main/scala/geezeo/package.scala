package object harvester {
  
  import 
    akka.actor.{
      ActorContext,
      ActorRef,
      ActorSystem,
      Props
    },
    akka.event.{
      Logging,
      LoggingAdapter
    },
    com.typesafe.config._,
    org.joda.time.format.DateTimeFormat,
    org.joda.time.{
      DateTime,
      Instant
    },
    scala.concurrent.duration.DurationInt,
    scala.language.postfixOps

  val dateTimeFmt = DateTimeFormat.forPattern("dd-M-yyyy:H:m:s")
  val timeout     = 1000 millis

  def fmtDateTime(dt: DateTime) = {
    dt.toString(dateTimeFmt)
  }
  // where should this go?
  def getActorRef(props: Props, name: String)(implicit context: ActorContext, dao: Dao) = {
    context.actorOf(props, name)
  }
  
  def nowInstant = {
    new Instant()
  }

  def nowDateTime = {
    new DateTime()
  }

  // def server = Http()
  //   .bindAndHandle(handler = routes, interface = httpServerConfig.hostname, port = httpServerConfig.port)
  //   .map { binding =>
  //     logger.info(s"HTTP server started at ${binding.localAddress}")
  //   }
  //   .recover { case ex => logger.error(ex, "Could not start HTTP server") }
  
  /** 
    this method is only meant for gathering partners as the app starts up
  */
  def start(config: Config, hScheduler: ActorRef)(implicit as: ActorSystem) = {
    
    import
      as._,
      scala.collection.JavaConverters._,
      members.Endpoint,
      strategy.HStrategy.{ 
        build => buildStrat }

    val partners        = config.getObject("partners").entrySet.asScala.map { partner =>

      val partnerName   = partner.getKey
      val partnerConfig = partner.getValue.atKey("partner")
      val strategyVal   = partnerConfig.getString("partner.strategy")
      val endpoints_    = partnerConfig.getList("partner.endpoints")
                                       .listIterator.asScala
                                       .toSet
                                       .map { endpoint: ConfigValue => 
        
        Endpoint.build(endpoint.atPath("endpoint"))
      }

      new members.HPartner(partnerName) {
        val endpoints   = endpoints_
        val strategy    = buildStrat(strategyVal)
      }
    }
    
    import 
      harvester.manager.actors.Partner // access to the Partner's Protocol 
    
    partners.foreach { hPartner => 
      val partnerActor = actorOf(Partner.props(hPartner, hScheduler), name = s"Partner-${hPartner.name}")
      partnerActor ! Partner.Start()
    }
  }

  trait Dao {
    def primary: Dal
  }
  class Dal(client: Client)
  class Client { def write(d: Any) = println("write"); def delete(d: Any) = println("delete") }

  object HMonitor {

    case class SystemMemory(free: Long, 
                             max: Long, 
                           total: Long, 
                            used: Long,
                            time: Instant) { 
      import SystemMemory.fmt
      override def toString = fmt(this) }

    object SystemMemory {
      def fmt(memory: SystemMemory) = {
        s"Total:${memory.total} Free:${memory.free} Max:${memory.max} Used:${memory.used}"
      }
    }

    def systemMemory = {
      
      val mb      = 1024*1024
      val runtime = Runtime.getRuntime
      val free    = runtime.freeMemory / mb
      val used    = (runtime.totalMemory - runtime.freeMemory) / mb
      val max     = runtime.maxMemory / mb
      val total   = runtime.totalMemory / mb

      new SystemMemory(free, used, max, total, nowInstant)
    }
  }
}