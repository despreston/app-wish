package controllers
import akka.actor._

/**
  * Created by despreston on 11/7/16.
  */
object MyWebSocketActor {
  def props(out: ActorRef) = Props(new MyWebSocketActor(out))
}

class MyWebSocketActor(out: ActorRef) extends Actor {
  def receive = {
    case msg: String => {
      out ! ("I received message: " + msg)
    }
  }
}
