package com.example

import cats.effect.IO
import cats.syntax.all._
import adt._

trait ShoppingCartLike {

  def getItem(itemReq: ItemRequest): IO[CartItem]

  protected def genInvoice(shoppingCart: ShoppingCartModel): IO[Invoice] = IO {

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

  protected def generateInvoiceForShoppingCartWithItems(
      itemRequests: ItemRequest*
  ): IO[Invoice] =
    for {

      cartItems <-
        itemRequests.toList
          .parTraverse(req => getItem(req))

      invoice <- genInvoice(ShoppingCartModel(cartItems))

    } yield invoice

  def displayInvoiceForShoppingCart(itemRequests: ItemRequest*): IO[Unit] =
    generateInvoiceForShoppingCartWithItems(itemRequests: _*).flatMap(invoice =>
      IO.println(invoice)
    )
}
