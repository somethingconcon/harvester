/**
  
  HarvestPartner is a representation of a partner in the HarvestScheduler.


  Right now this is mostly a representation of an entity that has Users.

  I want to represent the collection of harvest events that need to happen
  per harvest cycle.  Most Partners in the current nightly harvest operate on 
  a nightly reconciliation strategy.  While this is effective and easy to
  do it is almost impossible for us to improve on this in OG.

  HarvestPartner needs 
  
  * to get, and manage a collection of users to harvest (get and also update data without
    restarting the application).
  * select a user and collection of users to harvest next
  * an association to the partner's endpoint for healthchecks and stream control

  Persist to a place where we can retrieve
*/

abstract class HarvestPartner(downtime: Boolean, partnerId: Int) {
  
  import HarvestPartner._
  
  val harvestEndpoints: Set[Endpoint]
  val harvestStrategy: Strategy
  val harvestLogger: HLogger
  val users: Seq[HUser] // average harvest time, number of accounts for HarvestUser

  def harvested(success: HSuccess) = {
    //
  }

  def errored(fail: HFailed) = {
    
  }

  def log(event: HEvent) {
    // set time to life
   harvestLogger.writeToLog(event.toString)
  }

}

object HarvestPartner {

  import org.joda.time.Period

  // endpoint type
  // downtime in effect?
  // maxOpenConnections
  // url
  case class Endpoint(core: Core, downtime: Boolean, maxOpenConnections: ConnectionLimiter, url: String)
  
  // time it will take for a full harvest
  // quiet hours 
  case class Strategy(cycle: Period, quietHoursStart: LocalDateTime, quierHoursStop: LocalDateTime)

  trait HState
  case class HFailed(error: String, user: HUser) extends HState // choosing String here to interpret endpoint responses
  case class HSuccess(user: HUser)               extends HState

  // HUser and HAccouts are representations of what has been harvested
  case class HAccounts(accounts: Accounts) // too much?
  case class HUser(hAccounts: HAccounts, hEndpointResponse: String)

  class HLogger() {
    def writeToLog(message: String) = {
      println(message)
    }
  }


  // Time Events

}