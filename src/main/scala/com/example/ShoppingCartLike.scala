package com.example

import cats.effect.IO
import cats.syntax.all._
import org.http4s.Uri
import adt._

trait ShoppingCartLike {

  def getUri(productName: String): IO[Uri]

  def retrieveProduct(titleUri: Uri): IO[Item]

  protected def getItem(itemReq: ItemRequest): IO[CartItem] =
    for {
      uri <- getUri(itemReq.title)
      product <- retrieveProduct(uri)
    } yield CartItem(quantity = itemReq.quantity, item = product)

  def genInvoice(shoppingCart: ShoppingCart): IO[Invoice] = IO {

    def twoDecimalPointRoundedUp(value: Double): Double =
      BigDecimal(value).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble

    val subTotal = shoppingCart.cartItems.foldLeft(0d) { (st, ci) =>
      st + ci.quantity * ci.item.price
    }

    val tax = 0.125 * subTotal

    Invoice(
      shoppingCart = shoppingCart,
      subTotal = subTotal,
      tax = twoDecimalPointRoundedUp(tax),
      total = twoDecimalPointRoundedUp(tax + subTotal)
    )
  }

  def generateInvoiceForShoppingCart(itemRequests: ItemRequest*): IO[Invoice] =
    for {

      cartItems <-
        itemRequests.toList
          .parTraverse(req => getItem(req))

      invoice <- genInvoice(ShoppingCart(cartItems))

    } yield invoice

  def displayInvoiceForShoppingCart(itemRequests: ItemRequest*): IO[Unit] =
    generateInvoiceForShoppingCart(itemRequests: _*).flatMap(invoice =>
      IO.println(invoice)
    )
}
