package com.example.adt

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
