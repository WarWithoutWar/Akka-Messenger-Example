import akka.actor.{Actor, ActorLogging}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
  * OfflineMessage Actor acts as a server to forward offline messages to client
  */
class OfflineMessageActor extends Actor with ActorLogging {

  var messageMap: mutable.Map[String, ListBuffer[Message]] = mutable.Map()

  override def receive = {
    case message: Message => addMessage(message)
    case OfflineMessage(to) => {
      println("OfflineMessage Request: " + to + " " + getMessages(to).size)
      getMessages(to).foreach(message => {
        context.actorSelection("/user/" + to) ! message
      })
    }
    case _ => println("Invalid request")
  }

  // Gets the list of message from the map (key: the user name)
  def getMessages(to: String): ListBuffer[Message] = {
    messageMap.get(to).getOrElse(ListBuffer())
  }

  // adds message to the map based on user name
  def addMessage(theMessage: Message) = {
    println("Adding message to Offline storage for: " + theMessage.to)
    val newMessageList = getMessages(theMessage.message) += theMessage
    messageMap.update(theMessage.to, newMessageList)
  }
}