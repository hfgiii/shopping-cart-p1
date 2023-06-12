package com.example

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ShoppingCartSpec extends AnyFlatSpec with Matchers {

  import cats.effect.unsafe.IORuntime

  implicit val runtime: IORuntime = IORuntime.global

  """ShoppingCart("cheerios")""" should """ return a shopping cart with Item("Cheerios",8.43)""" in {
    ShoppingCartTest.getShoppingCart("cheerios") should be(ShoppingCart(List(CartItem(1, Item("Cheerios", 8.43)))))
  }

  """ShoppingCart("cheerios","cornflakes")""" should
  """ return a shopping cart with CartItem(1,"Cheerios",8.43) and CartItem(1,"Corn Flakes",2.52) """ in {
      ShoppingCartTest.getShoppingCart("cheerios","cornflakes") should be(ShoppingCart(List(CartItem(1,Item("Cheerios",8.43)),CartItem(1,Item("Corn Flakes",2.52)))))
  }

  """ShoppingCart("cheerios","cornflakes","bogus) with non-existent "bogus" item """ should
    """ return a shopping cart with CartItem(1,"Cheerios",8.43) and CartItem(1,"Corn Flakes",2.52) and CartItem(1,"Bogus",0.0) """ in {
    ShoppingCartTest.getShoppingCart("cornflakes","cheerios","bogus") should be(ShoppingCart(List(CartItem(1,Item("Corn Flakes",2.52)),
                                                                                                            CartItem(1,Item("Cheerios",8.43)),
                                                                                                            CartItem(1,Item("Bogus",0d)))))
  }

  """Invoice(ShoppingCart("cheerios","cheerios","cornflakes","bogus"))""" should
    """ return an invoice for shopping cart with CartItem(1,"Cheerios",8.43),CartItem(1,"Corn Flakes",2.52), CartItem(1,"Bogus",0.0). Note: the non-existent item "Bogus" """ in {
    ShoppingCart.genInvoice(ShoppingCartTest.getShoppingCart("cheerios","cheerios","cornflakes","bogus")) should
      be(Invoice(ShoppingCart(List(CartItem(1,Item("Corn Flakes",2.52)),CartItem(2,Item("Cheerios",8.43)),CartItem(1,Item("Bogus",0d)))),subTotal = 19.38, tax = 2.42, total = 21.80))
  }

}
