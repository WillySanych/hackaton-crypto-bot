package rht.hack
import akka.Done
import akka.actor.{ActorRef, ActorSystem}
import akka.stream.{CompletionStrategy, OverflowStrategy}
import akka.stream.scaladsl._
import rht.hack.Main.SourceActor


object KafkaStream {

  def getInstanseofActorRef: SourceActor = {
    implicit val actorSystem: ActorSystem = ActorSystem()

    val source: Source[Any, ActorRef] = Source.actorRef(
      completionMatcher = {
        case Done =>
          CompletionStrategy.immediately
      },
      failureMatcher = PartialFunction.empty,
      bufferSize = 5000,
      overflowStrategy = OverflowStrategy.dropHead)

    val streamActor: ActorRef = source.to(Sink.foreach(println)).run()

    streamActor
  }
  }