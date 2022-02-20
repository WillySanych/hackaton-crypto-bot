package rht.hack
import akka.Done
import akka.actor.{ActorRef, ActorSystem}
import akka.stream.{CompletionStrategy, OverflowStrategy}
import akka.stream.scaladsl._
import rht.hack.Main.SourceActor
import rht.hack.Postgres._


object KafkaStream {

  def getInstanseofActorRef: SourceActor = {

    implicit val actorSystem: ActorSystem = ActorSystem()

    val source: Source[Any, ActorRef] = Source.actorRef(
      completionMatcher = {
        case Done =>
          CompletionStrategy.immediately
      },
      failureMatcher = PartialFunction.empty,
      bufferSize = 2000,
      overflowStrategy = OverflowStrategy.dropHead)

    val streamActor: ActorRef = source.to(toPostgres).run()

    streamActor
  }

}