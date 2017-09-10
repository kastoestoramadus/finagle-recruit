package walidus.stock

import org.scalatest.{FlatSpec, Matchers}
import walidus.stock.model._

class OrderBookTest extends FlatSpec with Matchers{
  val o1 = BuyLimitOrder(1, 14, 20)
  val o2 = BuyIcebergOrder(2, 15, 50, 20)
  val o3 = SellLimitOrder(3, 16, 15)
  val baseOrders = Seq(o1, o2, o3)

  "OrderBook" should "stack up the orders" in {
    val result = baseOrders.foldRight((OrderBook.empty, Seq[Transaction]())){
      (order, pairBookAndTransactions) => pairBookAndTransactions._1.placeOrder(order)}
    val resultOrders = result._1.remainingOrders

    resultOrders.buyOrders should contain(o1)
    resultOrders.buyOrders should contain(o2)
    resultOrders.sellOrders should contain(o3)
    result._2 shouldBe empty
  }
  "OrderBook" should "execute transactions for matched orders" in {
    val o4 = SellLimitOrder(4, 13, 60)
    val result = (baseOrders :+ o4).foldRight((OrderBook.empty, Seq[Transaction]())){
      (order, pairBookAndTransactions) => pairBookAndTransactions._1.placeOrder(order)}
    val resultOrders = result._1.remainingOrders

    resultOrders.buyOrders.head.quantity shouldBe 10
    resultOrders.sellOrders.head.quantity shouldBe 15
    result._2.size shouldBe 4
    result._2.last shouldBe Transaction(1, 4, 14, 10)
  }
}
