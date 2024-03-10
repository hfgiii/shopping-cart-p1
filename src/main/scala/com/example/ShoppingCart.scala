package com.example

import adt._
import cats.effect.IO
import sttp.client3._
import sttp.client3.circe._
import sttp.model._
import cats.syntax.all._
import io.circe.generic.auto._
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.client.Client
import org.http4s.client.JavaNetClientBuilder
import io.circe.generic.semiauto._
import io.circe.Decoder
import org.http4s._
import org.http4s.circe.jsonOf
//import org.http4s.syntax.LiteralsSyntax

object ShoppingCart extends ShoppingCartLike { //with LiteralsSyntax {

  implicit val itemDecoder:EntityDecoder[IO,Item] = jsonOf[IO,Item]

  val httpClient: Client[IO] = JavaNetClientBuilder[IO].create
  val prefix =  "https://raw.githubusercontent.com/mattjanks16/shopping-cart-test-data/main"

  val URIs:Map[String,sttp.model.Uri] = Map(
    "cheerios" -> uri"https://raw.githubusercontent.com/mattjanks16/shopping-cart-test-data/main/cheerios.json",
    "cornflakes" -> uri"https://raw.githubusercontent.com/mattjanks16/shopping-cart-test-data/main/cornflakes.json",
    "frosties" -> uri"https://raw.githubusercontent.com/mattjanks16/shopping-cart-test-data/main/frosties.json",
    "shreddies" -> uri"https://raw.githubusercontent.com/mattjanks16/shopping-cart-test-data/main/shreddies.json",
    "weetabix" -> uri"https://raw.githubusercontent.com/mattjanks16/shopping-cart-test-data/main/weetabix.json"
  )


  import org.http4s.Uri
  import org.http4s.implicits.http4sLiteralsSyntax
  val productRepo = Map(
  "cheerios" -> Item(title = "Cheerios", price = 8.43),
    "cornflakes" -> Item(title = "Corn Flakes", price = 2.52),
    "shreddies" ->  Item(title = "Shreddies", price = 4.68),
    "frosties" -> Item(title = "Frosties", price =  4.99),
    "weetabix" ->  Item(title = "Weetabix", price =  9.98)
  )



  val Http4sUris:Map[String,org.http4s.Uri] = Map(
    "cheerios" -> Uri.uri("https://raw.githubusercontent.com/mattjanks16/shopping-cart-test-data/main/cheerios.json"),
    "cornflakes" ->  Uri.uri("https://raw.githubusercontent.com/mattjanks16/shopping-cart-test-data/main/cornflakes.json"),
    "frosties" ->  Uri.uri("https://raw.githubusercontent.com/mattjanks16/shopping-cart-test-data/main/frosties.json"),
    "shreddies" ->  Uri.uri("https://raw.githubusercontent.com/mattjanks16/shopping-cart-test-data/main/shreddies.json"),
    "weetabix" ->  Uri.uri("https://raw.githubusercontent.com/mattjanks16/shopping-cart-test-data/main/weetabix.json")

  )

  override def getUri(productName:String):IO[org.http4s.Uri] = IO(Http4sUris(productName))

  override def retrieveItem(titleUri: sttp.model.Uri): ItemResponse =
    SimpleHttpClient()
      .send(basicRequest
        .get(titleUri)
        .response(asJson[Item]))

  def retrieveProduct(titleUri: org.http4s.Uri):IO[Item] =
    httpClient.expect[Item](titleUri)
}
