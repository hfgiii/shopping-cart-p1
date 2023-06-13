package com.example

import adt._
import sttp.client3._
import sttp.client3.circe._
import sttp.model._
import io.circe.generic.auto._

object ShoppingCart extends ShoppingCartLike {

  val URIs = Map(
    "cheerios" -> uri"https://raw.githubusercontent.com/mattjanks16/shopping-cart-test-data/main/cheerios.json",
    "cornflakes" -> uri"https://raw.githubusercontent.com/mattjanks16/shopping-cart-test-data/main/cornflakes.json",
    "frosties" -> uri"https://raw.githubusercontent.com/mattjanks16/shopping-cart-test-data/main/frosties.json",
    "shreddies" -> uri"https://raw.githubusercontent.com/mattjanks16/shopping-cart-test-data/main/shreddies.json",
    "weetabix" -> uri"https://raw.githubusercontent.com/mattjanks16/shopping-cart-test-data/main/weetabix.json"
  )

  def retrieveItem(titleUri: Uri): ItemResponse =
    SimpleHttpClient()
      .send(basicRequest
        .get(titleUri)
        .response(asJson[Item]))
}
