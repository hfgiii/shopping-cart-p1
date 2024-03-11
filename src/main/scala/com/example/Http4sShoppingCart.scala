package com.example

import adt._
import cats.effect.IO
//import sttp.client3._
//import sttp.client3.circe._
import io.circe.generic.auto._
import org.http4s.client.Client
import org.http4s.client.JavaNetClientBuilder
import org.http4s._
import org.http4s.circe.jsonOf

object Http4sShoppingCart extends ShoppingCartLike { // with LiteralsSyntax {

  implicit val itemDecoder: EntityDecoder[IO, Item] = jsonOf[IO, Item]

  val httpClient: Client[IO] = JavaNetClientBuilder[IO].create

  import org.http4s.Uri
  import org.http4s.implicits.http4sLiteralsSyntax

  val Http4sUris: Map[String, org.http4s.Uri] = Map(
    "cheerios" -> uri"https://raw.githubusercontent.com/mattjanks16/shopping-cart-test-data/main/cheerios.json",
    "cornflakes" -> uri"https://raw.githubusercontent.com/mattjanks16/shopping-cart-test-data/main/cornflakes.json",
    "frosties" -> uri"https://raw.githubusercontent.com/mattjanks16/shopping-cart-test-data/main/frosties.json",
    "shreddies" -> uri"https://raw.githubusercontent.com/mattjanks16/shopping-cart-test-data/main/shreddies.json",
    "weetabix" -> uri"https://raw.githubusercontent.com/mattjanks16/shopping-cart-test-data/main/weetabix.json"
  )

  private def getUri(productName: String): IO[Uri] = IO(
    Http4sUris(productName)
  ).handleError { _ =>
    println(s"no valid uri for product: $productName")
    uri"https://raw.githubusercontent.com/mattjanks16/shopping-cart-test-data/main/unavailable.json"
  }

  private def retrieveProduct(titleUri: Uri): IO[Item] =
    httpClient.expect[Item](titleUri).handleError { exp =>
      println(exp.getMessage)
      Item("unavailable", 0d)
    }

  override def getItem(itemReq: ItemRequest): IO[CartItem] =
    for {
      uri <- getUri(itemReq.title)
      product <- retrieveProduct(uri)
    } yield CartItem(quantity = itemReq.quantity, item = product)
}
