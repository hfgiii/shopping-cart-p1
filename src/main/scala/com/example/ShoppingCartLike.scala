package com.example

import cats.effect.IO
import cats.effect.unsafe.IORuntime
import com.example.ShoppingCart.URIs
import io.circe.Error
import sttp.client3.{Response, ResponseException}
import sttp.model.{StatusCode, Uri}
import cats.syntax.all._
import adt._

trait ShoppingCartLike {

  type ItemResponse = Response[Either[ResponseException[String, Error], Item]]
  type CartItemResponse = Response[Either[ResponseException[String, Error], CartItem]]


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

  def getItemByRequest(itemReq: ItemRequest): IO[CartItemResponse] =
    for {
      titleURI <- IO(Either.fromOption(URIs.get(itemReq.title), itemReq.title))
      itemResponse <- getItemDirectLR(titleURI)
      citemLR <- IO(itemResponse.body.map(cc => CartItem(itemReq.quantity, cc)))
    } yield Response.ok(citemLR)


  def genInvoice(shoppingCart: ShoppingCart): IO[Invoice] = {

    def twoDecimalPointRoundedUp(value: Double): Double =
      BigDecimal(value).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble

    val subTotal = shoppingCart.cartItems.foldLeft(0d) { (st, ci) =>
      st + ci.quantity * ci.item.price
    }

    val tax = 0.125 * subTotal

    IO(
      Invoice(shoppingCart = shoppingCart,
        subTotal = subTotal,
        tax = twoDecimalPointRoundedUp(tax),
        total = twoDecimalPointRoundedUp(tax + subTotal)
      )
    )
  }

  def displayInvoiceForShoppingCart(itemRequests:ItemRequest*):IO[Unit] =
    for {

      cartItems <-
        itemRequests
          .toList
          .traverse(req => getItemByRequest(req))

      invoice <-
        genInvoice(ShoppingCart(cartItems.map(_.body.getOrElse(CartItem(0, Item("Bogus", 0d))))))

      _ <- IO.println(invoice)

    } yield ()

///These following methods are only used in unit tests
  def getItemByTitle(title: String): IO[ItemResponse] =
    for {
      titleURI <- IO(Either.fromOption(URIs.get(title), title))
      item <- getItemDirectLR(titleURI)
    } yield item

  def runItem(itemIO: IO[ItemResponse])(implicit runtime: IORuntime): Either[ResponseException[String, Error], Item] =
    itemIO.unsafeRunSync().body

  def getShoppingCart(itemRequests: ItemRequest*)(implicit runtime: IORuntime) = {
    val items =
      itemRequests
        .map(req => (req.quantity, (getItemByTitle _).andThen(runItem)(req.title)))
        .foldRight(List.empty[CartItem]) { (respItem, titles) =>
          val (quantity, eItem) = respItem
          eItem match {
            case Right(item) =>
              CartItem(quantity, item) :: titles
            case Left(_) => titles
          }
        }


    ShoppingCart(items)
  }
///////////////////////////////////////////////
}