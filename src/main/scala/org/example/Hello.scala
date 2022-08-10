package org.example

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.{Duration, DurationInt}
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success}



object Hello extends App {

  println("HelloWorld!")

//  创建一个actor
  val system = ActorSystem()
//  使用actorof工厂方法来创建
  val Kevin = system.actorOf(Props(classOf[PongActor]))
  implicit val timeout = Timeout(3.seconds)

//  val future = Kevin ? "Ping"
//  val res = Await.result(future.mapTo[String], 1.seconds)
//  println(res)
//  Kevin ! "hello"
//  Kevin ! "Ping"

  def askPong(message: String): Future[String] =
    (Kevin ? message).mapTo[String]

//  (Kevin ? "Ping").onComplete{
//    case Success(value) => println(s"rep:$value")
//    case Failure(exception) => println(s"failed:$exception")
//  }
  (Kevin ? "Ping").onComplete{
    case Success(value) => println(s"rep:$value")
    case Failure(exception) => println(s"failed:$exception")
  }


  val f: Future[String] = askPong("Ping").map(x => x)
  println("f:" + f.value)

  val f1: Future[String] = askPong("Ping")
    .flatMap(x => askPong("Ping" + x))
//    .recover({
//      case t:Exception => println("error") + "error" + t})


//  val f2: Future[String] = askPong("A")
//    .flatMap(x => askPong("Pong" + x))
//    .recover({case t:Exception => println("error") + "error"})

  Thread.sleep(2000)

  println("f1:" + f1.value)
//  println("f2:" + f2.value)

  val terminate = system.terminate()
  Await.ready(terminate, Duration.Inf)
}
class PongActor extends Actor{
  override def receive: Receive = {
    case "Ping" => sender() ! "pong"
    case _ => sender() ! Status.Failure(new Exception("unknown"))
  }

}
