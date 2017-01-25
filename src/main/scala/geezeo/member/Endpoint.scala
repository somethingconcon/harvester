package members 


case class Endpoint(core: Core, downtime: Boolean, maxOpenConnections: ConnectionLimiter, url: String)

trait Core
case class Geezeo() extends Core
case class FiServ() extends Core
case class Q2()     extends Core
case class Ofx()    extends Core

trait ConnectionLimiter