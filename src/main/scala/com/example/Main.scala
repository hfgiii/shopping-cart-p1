package com.example

import cats.effect._
import com.example.ShoppingCart._
import com.example.adt._

object Main extends IOApp.Simple {

  val run = displayInvoiceForShoppingCart(ItemRequest(quantity = 64, "cornflakes"),
    ItemRequest(quantity = 300, "cheerios"),
    ItemRequest(quantity = 300, "cheerioz"))

}






