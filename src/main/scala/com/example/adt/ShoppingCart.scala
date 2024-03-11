package com.example.adt

final case class ShoppingCart(cartItems: List[CartItem]) {
  override def toString = {
    s"""
       | ${cartItems
        .map(ci =>
          " Quantity " + ci.quantity + ", Item " + ci.item.title + ", Price " + ci.item.price
        )
        .mkString("\n")}
       |""".stripMargin
  }
}

final case class Item(title: String, price: Double)

final case class ItemRequest(quantity: Int, title: String)

final case class CartItem(quantity: Int, item: Item)

final case class Invoice(
    shoppingCart: ShoppingCart,
    subTotal: Double,
    tax: Double,
    total: Double
) {
  override def toString = {
    s"""
         | $shoppingCart
         | Sub-total = $subTotal
         | Tax       = $tax
         | Total     = $total
         |""".stripMargin
  }
}
