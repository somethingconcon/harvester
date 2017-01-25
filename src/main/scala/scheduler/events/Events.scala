package scheduler.events

trait Events {
  val events: Set[Event]
}

trait Event
// trait HarvestEvent extends Event

// case class HarvestRequested() extends HarvestEvent
// case class HarvestRequested() extends HarvestEvent