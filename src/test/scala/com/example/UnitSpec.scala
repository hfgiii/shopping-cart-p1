package com.example


import org.scalatest._
import flatspec._
import matchers._

trait UnitSpec
  extends AnyFlatSpec
    with should.Matchers
    with OptionValues
    with Inside
    with Inspectors
