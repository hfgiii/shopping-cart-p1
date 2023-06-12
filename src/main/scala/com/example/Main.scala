package com.example

import sttp.client3._
import sttp.model._
import sttp.client3.circe._
import io.circe._
import io.circe.generic.auto._
import cats.effect._
import cats.effect.unsafe.IORuntime
import cats.syntax.all._
import com.example.ShoppingCart.{ItemResponse, URIs}

object Main extends IOApp.Simple {

  def run: IO[Unit] =
    IO {
      val sc = ShoppingCart.getShoppingCart("cheerios", "cheerios", "cornflakes", "bogus")(runtime)
      val invoice = ShoppingCart.genInvoice(sc)
      println(s"invoice: $invoice")
    }
}

case class Item(title: String, price: Double)

case class CartItem(quantity: Int, item: Item)

case class ShoppingCart(cartItems: List[CartItem]) {
  override def toString = {
    s"""
       | ${cartItems.map(ci => " Quantity " + ci.quantity + ", Item " + ci.item.title + ", Price " + ci.item.price).mkString("\n")}
       |""".stripMargin
  }
}

case class Invoice(shoppingCart: ShoppingCart, subTotal: Double, tax: Double, total: Double) {
  override def toString = {
    s"""
       | $shoppingCart
       | Sub-total = $subTotal
       | Tax       = $tax
       | Total     = $total
       |""".stripMargin
  }
}

trait ShoppingCartLike {

  type ItemResponse = Response[Either[ResponseException[String, Error], Item]]

  private def firstToUpper(str: String): String =
    if (str.nonEmpty)
      str.replaceFirst(str.substring(0, 1), (str.substring(0, 1).toUpperCase))
    else str

  def retrieveItem(uri: Uri): ItemResponse

  def getItemDirectLR(titleUriLR: Either[String, Uri]): IO[ItemResponse] =
    titleUriLR match {
      case Right(titleUri) => IO(retrieveItem(titleUri))

      case Left(key) => IO(Response(Right(Item(firstToUpper(key), 0d)), StatusCode.NotFound, s"URI for $key not found"))
    }

  def getItemByTitle(title: String): IO[ItemResponse] =
    for {
      titleURI <- IO(Either.fromOption(URIs.get(title), title))
      item <- getItemDirectLR(titleURI)
    } yield item

  def runItem(itemIO: IO[ItemResponse])(implicit runtime: IORuntime): Either[ResponseException[String, Error], Item] =
    itemIO.unsafeRunSync().body

  def getShoppingCart(itemTitles: String*)(implicit runtime: IORuntime) = {
    val cartItems =
      itemTitles
        .map(title => (getItemByTitle _).andThen(runItem)(title))
        .foldRight(List.empty[Item]) { (eItem, titles) =>
          eItem match {
            case Right(item) => item :: titles
            case Left(_) => titles
          }
        }
        .groupBy(_.title).values
        .foldRight(List.empty[CartItem]) { (items, cItems) =>
          CartItem(quantity = items.size, item = items.head) :: cItems
        }

    ShoppingCart(cartItems)
  }

  def genInvoice(shoppingCart: ShoppingCart): Invoice = {

    def twoDecimalPointRoundedUp(value: Double): Double =
      BigDecimal(value).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble

    val subTotal = shoppingCart.cartItems.foldLeft(0d) { (st, ci) =>
      st + ci.quantity * ci.item.price
    }

    val tax = 0.125 * subTotal

    Invoice(shoppingCart = shoppingCart,
      subTotal = subTotal,
      tax = twoDecimalPointRoundedUp(tax),
      total = twoDecimalPointRoundedUp(tax + subTotal)
    )
  }
}

object ShoppingCart extends ShoppingCartLike {

  val URIs = Map(
    "cheerios" -> uri"https://raw.githubusercontent.com/mattjanks16/shopping-cart-test-data/main/cheerios.json",
    "cornflakes" -> uri"https://raw.githubusercontent.com/mattjanks16/shopping-cart-test-data/main/cornflakes.json",
    "frosties" -> uri"https://raw.githubusercontent.com/mattjanks16/shopping-cart-test-data/main/frosties.json",
    "shreddies" -> uri"https://raw.githubusercontent.com/mattjanks16/shopping-cart-test-data/main/shreddies.json",
    "weetabix" -> uri"https://raw.githubusercontent.com/mattjanks16/shopping-cart-test-data/main/weetabix.json"
  )

  def retrieveItem(titleUri: Uri): ItemResponse =
    SimpleHttpClient()
        .send(basicRequest
        .get(titleUri)
        .response(asJson[Item]))
}


