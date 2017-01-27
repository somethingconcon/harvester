package members 

import
  com.typesafe.config.{
    Config,
    ConfigValue
  },
  org.joda.time.{
    Duration,
    Interval
  }

case class Endpoint(core: Core, 
                 limiter: Rate, 
                     url: String) {
  // isRateLimited
  // isDowntime
}

object Endpoint {
  import com.typesafe.config.Config
  
  def build(endpointConfig: Config) = {
    
    val core = Core(endpointConfig.getString("core"))
    val rate = Rate(endpointConfig.getString("rate")) // getDuration(java.lang.String path, java.util.concurrent.TimeUnit unit)
    val url  = endpointConfig.getString("url")

    new Endpoint(core, rate, url)
  }

  def build(endpointConfig: ConfigValue) = {
    new Endpoint(Geezeo(), Rate("poo"), "localhost:8089")
  }
}
// Not really sure if duration is correct here
case class Rate(x: Int, per: Duration) {
  val normalizeTo = {
    "poo"  //FIXME
  }
}

object Rate {
  def apply(rateConfig: String) = {
    new Rate(20, new Duration(1000))
  }
}

trait Core
object Core {
  def apply(coreName: String) = coreName match {
    case "geezeo" => Geezeo()
    case "fiserv" => FiServ()
    case "q2"     => Q2()
    case "ofx"    => Ofx()
    case _        => throw new Error("Cannot determine CORE.")
  }
}
case class Geezeo() extends Core
case class FiServ() extends Core
case class Q2()     extends Core
case class Ofx()    extends Core

trait Limiter