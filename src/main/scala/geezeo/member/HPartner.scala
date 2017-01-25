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
package members 

abstract class HPartner(downtime: Boolean, partnerId: Int) {
  
  import HPartner._
  
  val harvestEndpoints: Set[Endpoint]
  val harvestStrategy:  Strategy
  val users:            Seq[HUser] // average harvest time, number of accounts for HarvestUser

}

object HPartner {

  // endpoint type
  // downtime in effect?
  // maxOpenConnections
  // url
  // Time Events

}

import 
  org.joda.time.{
    LocalDateTime,
    Period }
    
// time it will take for a full harvest
// quiet hours
// ErrorStrategy might belong here
case class Strategy(cycle: Period, quietHoursStart: LocalDateTime, quierHoursStop: LocalDateTime)

// HUser and HAccouts are representations of what has been harvested
case class HUser(accountIds: Traversable[Int], hEndpointResponse: String, userId: Int)
object HUser {
  def apply(userId: Int) = {
    new HUser(Set(), "test.com", userId)
  }
}

