package object harvester {
  
  import 
    akka.actor.{
      ActorSystem,
      Props
    },
    akka.event.{
      Logging,
      LoggingAdapter
    }

  // where should this go?
  def getActorRef(props: Props, name: String)(implicit ac: ActorSystem) = {
    ac.actorOf(props, s"${name} - ${nowDateTime}")
  }
  
  def nowInstant = {
    import org.joda.time.Instant
    new Instant()
  }

  def nowDateTime = {
    import org.joda.time.DateTime
    new DateTime()
  }

  // def server = Http()
  //   .bindAndHandle(handler = routes, interface = httpServerConfig.hostname, port = httpServerConfig.port)
  //   .map { binding =>
  //     logger.info(s"HTTP server started at ${binding.localAddress}")
  //   }
  //   .recover { case ex => logger.error(ex, "Could not start HTTP server") }
  
  class HLogger(logAdapter: LoggingAdapter) {
    import Logging._
    
    def debug(d: AnyRef) = {
      logAdapter.debug(d.toString)
    }

    def error(d: AnyRef) = {
      logAdapter.error(d.toString)
    }

    def info(d: AnyRef) = {
      import Logging.LogLevel
      
      logAdapter.info(d.toString)
    }

    def log(d: AnyRef) = {
      logAdapter.log(LogLevel(1), d.toString)
    }

    def warn(d: AnyRef) = {
      logAdapter.warning(d.toString)
    }

  }

  object HLogger {

    import akka.event.{
      Logging,
      LoggingAdapter
    }

    var logger: HLogger = _

    def apply(implicit as: ActorSystem) = {
     logger = new HLogger(as.log)
    }
    
    def log = logger.info _

  }

  object HMonitor {

    case class SystemData(freeMemory: Long, maxMemory: Long, totalMemory: Long, usedMemory: Long)

    def now = {
      import org.joda.time.Instant
      new Instant()
    }

    def systemData = { // memory info
      
      val mb = 1024*1024
      val runtime = Runtime.getRuntime
      val free  = runtime.freeMemory / mb
      val used  = (runtime.totalMemory - runtime.freeMemory) / mb
      val max   = runtime.maxMemory / mb
      val total = runtime.totalMemory / mb

      HLogger.log("** Used Memory:  " + used)
      HLogger.log("** Free Memory:  " + free)
      HLogger.log("** Total Memory: " + total)
      HLogger.log("** Max Memory:   " + max)

      SystemData(free, used, max, total)
    }
  }
}