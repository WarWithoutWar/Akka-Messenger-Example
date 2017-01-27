
import akka.actor.{ActorSystem, Props}
import scala.concurrent.duration.DurationInt
import scala.util.Random
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Class simulates user logging into a system and sending messages
  */
object MessengerApp extends App{

  var r = Random

  println("Starting Messenger App.....")
  val system = ActorSystem("MessengerSystem"); // creates the actor system

  val server = system.actorOf(Props[ServerActor], "server")
  val offlineMessage = system.actorOf(Props[OfflineMessageActor], "offlineMessage")

  // connecting user to the server
  val client1 = system.actorOf(Props[ClientActor], "user1")
  val client2 = system.actorOf(Props[ClientActor], "user2")
  val client3 = system.actorOf(Props[ClientActor], "user3")


  system.scheduler.scheduleOnce(5000 millisecond)(f = {
    println("Start sending message")
    server.tell(Message("Message for user2", "user2", "user1"), client1)
    server.tell(Message("Message for user1", "user1", "user2"), client2)
    server.tell(Message("Message for user4","user4", "user3"), client3)
  })

  // user 4 login and view offline message
    system.scheduler.scheduleOnce(10000 millisecond)(f = {
    val client4 = system.actorOf(Props[ClientActor], "user4")
  })


  // Just Terminate the akka system
  system.scheduler.scheduleOnce(17000 millisecond)(f = {
    system.terminate()
  })



}
