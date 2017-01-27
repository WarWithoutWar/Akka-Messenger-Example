import akka.actor.{Actor, ActorLogging}

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import akka.util.Timeout

import scala.util.Random


/**
  * This actor represents a client/user that connects to the server
  */
class ClientActor extends Actor with ActorLogging{

  @scala.throws[Exception](classOf[Exception])
  override def preStart(): Unit = {
    signIn()
  }

  override def receive: Receive = {
    case Connect(ref) => println(ref)
    case Message(message, to, from) => println("[From: " +  from + " = " + message + "]")
    case _ => log.info("unknown request")
  }

  def signIn()= {
    println(self.path.name +" signing in....")
    implicit val resolveTimeout = Timeout(5 second)
    val systemRef = Await.result(context.system.actorSelection("user/server").resolveOne(), resolveTimeout.duration)
    val offlineRef = Await.result(context.system.actorSelection("user/offlineMessage").resolveOne(), resolveTimeout.duration)
    val r = Random
    context.system.scheduler.scheduleOnce(1000 millisecond, systemRef, Connect(self))
    context.system.scheduler.scheduleOnce(1000 millisecond, offlineRef, OfflineMessage(context.self.path.name))
  }

}

