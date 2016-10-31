package walidus.cshop

object Shop {
  val costs: Map[Item, Bill] = Map(
    Orange -> 25,
    Apple -> 60,
    Banana -> 20
  )
  def promotionCounting(order: Order): Order = {
    order ++ orangePromotion(order) ++ bananaAndApplePromotion(order)
  }
  def makeBill(order: Order): Bill =
    promotionCounting(order)
      .map(pair => costs(pair._1)*pair._2) // count prices with quantities
      .sum

  def orangePromotion(order: Order): Order = {
    val amount = order(Orange)
    Map(Orange -> ((amount/3)*2 + amount%3))
  }
  def bananaAndApplePromotion(order: Order): Order = {
    val applicatives = Seq(
      Banana -> order(Banana),
      Apple -> order(Apple)
    ).sortBy(_._2).reverse

    val totalAmount = applicatives.foldLeft(0)(_+_._2)
    val freeFruits = totalAmount/2
    val moreExpensive = applicatives.head
    val lessExpensive = applicatives.tail.head

    val remainFree: Bill = freeFruits - lessExpensive._2

    val more = moreExpensive._1 -> (moreExpensive._2 - Math.max(remainFree,0))
    val less = lessExpensive._1 -> Math.max(-remainFree,0)

    Map(more, less)
  }
}
