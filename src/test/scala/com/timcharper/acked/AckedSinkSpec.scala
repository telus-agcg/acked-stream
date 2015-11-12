package com.timcharper.acked

import akka.pattern.ask
import akka.stream.ActorMaterializer
import org.scalatest.{FunSpec, Matchers}
import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success, Try}

class AckedSinkSpec extends FunSpec with Matchers with ActorSystemTest {

  describe("fold") {
    it("acknowledges promises as they are folded in") {
      case class LeException(msg: String) extends Exception(msg)
      val input = (Stream.continually(Promise[Unit]) zip Range.inclusive(1, 5)).toList
      implicit val materializer = ActorMaterializer()
      Try(await(AckedSource(input).runWith(AckedSink.fold(0){ (reduce, e) =>
        if(e == 4) throw LeException("dies here")
        e + reduce
      })))
      input.map { case (p, _) =>
        p.tryFailure(LeException("didn't complete"))
        Try(await(p.future)) match {
          case Success(_) => None
          case Failure(LeException(msg)) => Some(msg)
        }
      } should be (Seq(None, None, None, Some("dies here"), Some("didn't complete")))
    }
  }
}
