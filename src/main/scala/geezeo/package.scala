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
  def getActorRef(props: Props, name: String)(implicit context: ActorContext) = {
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
  
  // this method is only meant for gathering partners as the app starts up
  def startPartners(config: Config, hScheduler: ActorRef)(implicit as: ActorSystem) = {
    
    import
      as._,
      scala.collection.JavaConversions._,
      members.Endpoint

    // getConfigList
    val partners = config.getObject("partners").map { case (key, partnerHarvestConf) =>
      val partnerName = key
      // check this shit
      val endpointsConf = partnerHarvestConf.atPath("endpoints").atKey("endpoints")
      val harvestEndpoints = endpointsConf.getObjectList("endpoints").map { endpoint => 
        Endpoint.build(endpoint)
      }

      // val harvestStrat = strategy.HStrategy.build(partnerHarvestConf.getString("strategy"))
      val harvestStrat = strategy.HStrategy.build("fixed")
      new members.HPartner(partnerName) {
        val endpoints = harvestEndpoints
        val strategy  = harvestStrat
      }
    }
    
    import harvester.manager.actors.Partner
    
    partners.foreach { hPartner => 
      val partnerActor = actorOf(Partner.props(hPartner, hScheduler))
      partnerActor ! harvester.manager.actors.Partner.Start()
    }
  }

  object HMonitor {

    case class SystemData(freeMemory: Long, 
                           maxMemory: Long, 
                         totalMemory: Long, 
                          usedMemory: Long,
                                time: Instant)

    def systemData = { // memory info
      
      val mb      = 1024*1024
      val runtime = Runtime.getRuntime
      val free    = runtime.freeMemory / mb
      val used    = (runtime.totalMemory - runtime.freeMemory) / mb
      val max     = runtime.maxMemory / mb
      val total   = runtime.totalMemory / mb

      SystemData(free, used, max, total, nowInstant)
    }
  }
}