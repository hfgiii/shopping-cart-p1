package com.example.adt

case class ShoppingCart(cartItems: List[CartItem]) {
  override def toString = {
    s"""
       | ${cartItems.map(ci => " Quantity " + ci.quantity + ", Item " + ci.item.title + ", Price " + ci.item.price).mkString("\n")}
       |""".stripMargin
  }
}
