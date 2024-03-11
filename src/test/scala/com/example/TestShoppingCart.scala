package com.example

import cats.effect.IO
import com.example.adt.{CartItem, Item, ItemRequest}

trait TestShoppingCart extends ShoppingCartLike {

  val productRepo = Map(
    "cheerios" -> Item(title = "Cheerios", price = 8.43),
    "cornflakes" -> Item(title = "Corn Flakes", price = 2.52),
    "shreddies" -> Item(title = "Shreddies", price = 4.68),
    "frosties" -> Item(title = "Frosties", price = 4.99),
    "weetabix" -> Item(title = "Weetabix", price = 9.98)
  )

  override def getItem(itemReq: ItemRequest): IO[CartItem] =
    IO(productRepo(itemReq.title)).map(item => CartItem(quantity = itemReq.quantity, item = item)).handleError {
      _ => println(s"Error unavailable product: ${itemReq.title}")
        CartItem(quantity = 0, item = Item("unavailable", 0d))
    }
}
