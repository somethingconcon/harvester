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
  * select a collection of users to harvest next
  * an association to the partner's endpoint for healthchecks and stream control

  Persist to a place where we can retrieve
*/
package members 

abstract class HPartner(partnerName: String) {
  
  import HPartner._
  
  // val endpoints: Set[Endpoint]
  // val strategy:  Strategy
  // this can be handled by the current cycle
  // val users:            Seq[HUser] // average harvest time, number of accounts for HarvestUser

}

object HPartner {

  // endpoint type
  // downtime in effect?
  // maxOpenConnections
  // url
  // Time Events
  def apply(partnerName: String) = new HPartner(partnerName) {

  }
}

