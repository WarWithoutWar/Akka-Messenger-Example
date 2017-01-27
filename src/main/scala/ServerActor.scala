import akka.actor.{Actor, ActorLogging, ActorRef}

case class Connect(ref: ActorRef)
case class Disconnect(ref: ActorRef)
case class Message(message: String, to: String, from: String)
case class OfflineMessage(to: String)

object ServerActor{
  var clients: Map[String, String] = Map() // Map containing the client and client path
}

/**
  * Actor acts as a server to serve Client Actor
  */
class ServerActor extends Actor with ActorLogging{

  var clients = ServerActor.clients

  override def receive: Receive = {
    case Connect(ref) => addClient(ref)
    case Disconnect(ref) => removeClient(ref)
    case message :Message=> sendMessage(message)
    case _ => log.info("unknown request")
  }


  // add client  to the list
  def addClient(ref: ActorRef) = {
    if(!clients.contains(ref.path.name)) {
      clients = clients + (ref.path.name.toString -> ref.path.toString)
    }
  }

  // remove client from the list
  def removeClient(ref: ActorRef) = {
    if(clients.contains(ref.path.name))
      clients = clients - ref.path.name
  }

  /**
    * Method send the message to the client else offlineMessageActor
    * @param message the messgae to send
    */
  def sendMessage(message: Message) = {
    if(clients.contains(message.to))
      context.actorSelection("/user/" + message.to) ! message
    else
      context.actorSelection("/user/offlineMessage") ! message
  }

  /**
    * Method pushes the messages to the offlineMessageActor that will handle message for
    * offline users
    * @param message the message to pass to client
    */
  def handleOfflineMessage(message: Message): Unit ={
    context.actorSelection("user/offlineMessage").tell(message, self)
  }

}