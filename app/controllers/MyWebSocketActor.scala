package controllers
import akka.actor._

/**
  * Created by despreston on 11/7/16.
  */
object MyWebSocketActor {
  def props(out: ActorRef) = Props(new MyWebSocketActor(out))
}

class MyWebSocketActor(out: ActorRef) extends Actor {
//  val mediator = DistributedPubSub(context.system).mediator

//  mediator ! Subscribe("TwitterStream", self)

  def receive = {
    case msg: String => {
      out ! ("I received message: " + msg)
    }
//    case SubscribeAck(Subscribe("TwitterStream", None, `self`)) => {
//      println("Subscribed to TwitterStream")
//    }
  }

}
