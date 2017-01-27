package strategy

trait HStrategy {
  
}
case class DynamicIndex() extends HStrategy
case class MaxRequests()  extends HStrategy
case class Fixed()        extends HStrategy