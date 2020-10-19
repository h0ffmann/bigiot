package it

import org.scalatest.flatspec.AnyFlatSpec

/**
  * Requires "docker-compose up -d zk kafka emqx"
  */
class FakeItSpec extends AnyFlatSpec {

  behavior of "An empty Set"
  it should "have size 0" in {
    assert(Set.empty.size === 0)
  }
}
