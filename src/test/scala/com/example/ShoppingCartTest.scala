package com.example

import sttp.client3._
import sttp.client3.testing.SttpBackendStub
import sttp.model._
import adt._

//object ShoppingCartTest extends ShoppingCartLike {
//
//  private val partialTestingStub =
//    SttpBackendStub
//      .synchronous
//      .whenRequestMatchesPartial({
//        case r if r.uri.path.endsWith(List("cheerios.json")) =>
//          Response.ok(Right(Product("Cheerios", 8.43)))
//        case r if r.uri.path.endsWith(List("cornflakes.json")) =>
//          Response.ok(Right(Product("Corn Flakes", 2.52)))
//        case r if r.uri.path.endsWith(List("frosties.json")) =>
//          Response.ok(Right(Product("Frosties", 4.99)))
//        case r if r.uri.path.endsWith(List("shreddies.json")) =>
//          Response.ok(Right(Product("Shreddies", 4.68)))
//        case r if r.uri.path.endsWith(List("weetabix.json")) =>
//          Response.ok(Right(Product("Weetabix", 9.98)))
//      //  case _ => Response(Item("Bogus",0d),StatusCode.NotFound)
//      })
//
//  def retrieveItem(uri: Uri): ItemResponse = {
//      val resp = basicRequest.get(uri).send(partialTestingStub)
//      resp.asInstanceOf[ItemResponse]
//  }
//}
