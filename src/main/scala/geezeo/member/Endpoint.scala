package members 

import
  com.typesafe.config.{
    Config,
    ConfigValue
  },
  org.joda.time.{
    Period,
    Seconds
  }

case class Endpoint(core: Core, 
                 limiter: Rate, 
                     url: String) {
  // isRateLimited
  // isDowntime
}

object Endpoint {
  import com.typesafe.config.Config

  def build(conf: Config) = {
    Builder(conf)
  }

  object Builder {
    def apply(conf: Config) = {
      val core = Core(conf.getString("endpoint.core"))
      val rate = Rate(conf.getString("endpoint.rate"))
      val url  = conf.getString("endpoint.url")

      new Endpoint(core, rate, url)
    }
  }
}
trait Limiter {
  val maxConnections: Int
}

/**
  Move these suckers 
*/
case class Rate(num: Int, limiter: Limiter, per: Period)

object Rate {
  def apply(conf: String) = {
    Builder(conf.split(" ", 2))
  }

  object Builder {
    def apply(conf: Array[String]) = {
      val rateParts  = conf.head.split("/")
      val limit       = conf.tail.head.split(" ").head.toInt
      val interval   = new Period(Seconds.seconds(rateParts.last.toInt))
      val limiter_   = new Limiter { val maxConnections = limit }
      
      new Rate(conf.head.toInt, limiter_, interval)
    }
  }
}

trait Core
case class Geezeo() extends Core
case class FiServ() extends Core
case class Q2()     extends Core
case class Ofx()    extends Core

object Core {
  def apply(coreType: String) = coreType match {
    case "geezeo" => Geezeo()
    case "fiserv" => FiServ()
    case "q2"     => Q2()
    case "ofx"    => Ofx()
    case _        => throw new Error("Cannot determine CORE.")
  }
}