package com.example

import adt._
import munit.CatsEffectSuite

class ShoppingCartTest extends CatsEffectSuite with TestShoppingCart {
  test ("Get a CartItem") {
    assertIO(getItem(ItemRequest(20,"cheerios")), CartItem(20,Item(title = "Cheerios", price = 8.43)))
  }

  test ("Get a Unavailable CartItem") {
    assertIO(getItem(ItemRequest(20,"darkmatter")), CartItem(0,Item(title = "unavailable", price = 0d)))
  }

  test ("Generate ShoppingCartModel with multiple items") {
    val shoppingCartModel =
    for {
      item1 <- getItem(ItemRequest(20,"cheerios"))
      item2 <- getItem(ItemRequest(30,"frosties"))
    } yield ShoppingCartModel(List(item1,item2))

    assertIO(shoppingCartModel,
             ShoppingCartModel(List(CartItem(20,Item(title = "Cheerios", price = 8.43)),CartItem(30,Item(title = "Frosties", price = 4.99)))))
    }

  test ("Generate ShoppingCartModel with multiple items and unavailable item") {
    val shoppingCartModel =
      for {
        item1 <- getItem(ItemRequest(20,"cheerios"))
        item2 <- getItem(ItemRequest(30,"frosties"))
        item3 <- getItem(ItemRequest(20,"darkmatter"))
      } yield ShoppingCartModel(List(item1,item2, item3))

    assertIO(shoppingCartModel,
      ShoppingCartModel(List(CartItem(20,Item(title = "Cheerios", price = 8.43)),
                             CartItem(30,Item(title = "Frosties", price = 4.99)),
                             CartItem(0,Item(title = "unavailable", price = 0d)))))
  }

  test ("Generate Invoice from ShoppingCart Model") {
    val shoppingCartModel = ShoppingCartModel(List(CartItem(20,Item(title = "Cheerios", price = 8.43)),CartItem(30,Item(title = "Frosties", price = 4.99))))
    assertIO(genInvoice(shoppingCartModel),Invoice(shoppingCartModel,318.30, 39.79, 358.09))
  }

  test ("Generate Invoice from ShoppingCart Model with unavailable item") {
    val shoppingCartModel = ShoppingCartModel(List(CartItem(20,Item(title = "Cheerios", price = 8.43)),
                                                   CartItem(30,Item(title = "Frosties", price = 4.99)),
                                                   CartItem(0,Item(title = "unavailable", price = 0d))))
    assertIO(genInvoice(shoppingCartModel),Invoice(shoppingCartModel,318.30, 39.79, 358.09))
  }

  test ("Generate Invoice from list of item requests") {
    val shoppingCartModel = ShoppingCartModel(List(CartItem(20,Item(title = "Cheerios", price = 8.43)),CartItem(30,Item(title = "Frosties", price = 4.99))))
    assertIO(generateInvoiceForShoppingCartWithItems(ItemRequest(20,"cheerios"),ItemRequest(30,"frosties")),Invoice(shoppingCartModel,318.30, 39.79, 358.09))
  }

  test ("Generate Invoice from list of item requests and unavailable items") {
    val shoppingCartModel = ShoppingCartModel(List(CartItem(20,Item(title = "Cheerios", price = 8.43)),CartItem(30,Item(title = "Frosties", price = 4.99)),CartItem(0,Item(title = "unavailable", price = 0d))))
    assertIO(generateInvoiceForShoppingCartWithItems(ItemRequest(20,"cheerios"),
                                                     ItemRequest(30,"frosties"),
                                                     ItemRequest(20,"darkmatter")),Invoice(shoppingCartModel,318.30, 39.79, 358.09))
  }

}