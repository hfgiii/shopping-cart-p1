package com.example

import com.example.ShoppingCart.ItemResponse
import sttp.client3._
import sttp.client3.testing.SttpBackendStub
import sttp.model.{StatusCode, Uri}

object ShoppingCartTest extends ShoppingCartLike {

  private val partialTestingStub =
    SttpBackendStub
      .synchronous
      .whenRequestMatchesPartial({
        case r if r.uri.path.endsWith(List("cheerios.json")) =>
          Response.ok(Right(Item("Cheerios", 8.43)))
        case r if r.uri.path.endsWith(List("cornflakes.json")) =>
          Response.ok(Right(Item("Corn Flakes", 2.52)))
        case r if r.uri.path.endsWith(List("frosties.json")) =>
          Response.ok(Right(Item("Frosties", 4.99)))
        case r if r.uri.path.endsWith(List("shreddies.json")) =>
          Response.ok(Right(Item("Shreddies", 4.68)))
        case r if r.uri.path.endsWith(List("weetabix.json")) =>
          Response.ok(Right(Item("Weetabix", 9.98)))
      //  case _ => Response(Item("Bogus",0d),StatusCode.NotFound)
      })

  def retrieveItem(uri: Uri): ItemResponse = {
      val resp = basicRequest.get(uri).send(partialTestingStub)
      resp.asInstanceOf[ItemResponse]
  }
}