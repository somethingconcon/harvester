package strategy

import 
  org.joda.time.{
    Interval
  }

sealed trait HStrategy {
  val cycle: Interval
  // val rate: Rate // huh where does this go?
}
case class DynamicIndex(cycle: Interval) extends HStrategy
case class MaxRequests (cycle: Interval) extends HStrategy
case class Fixed       (cycle: Interval) extends HStrategy

object HStrategy {

  class StrategyError(reason: String) extends Error

  def apply(cycle: Interval, strategy: String) = strategy match {
    case "dynamic" => DynamicIndex(cycle)
    case "max"     => MaxRequests(cycle)
    case "fixed"   => Fixed(cycle)
    case dunno     => throw new StrategyError(s"Cannot create strategy from ${dunno}")
  }

  def build(strat: String) = {
    apply(new Interval(1000, 2000), strat)
  }
}