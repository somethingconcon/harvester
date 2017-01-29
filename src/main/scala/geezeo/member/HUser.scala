package members 

// HUser and HAccouts are representations of what has been harvested
case class HUser(accountIds: Traversable[Int], userId: Int)

object HUser {
  def apply(userId: Int) = {
    new HUser(Set[Int](), userId)
  }
}