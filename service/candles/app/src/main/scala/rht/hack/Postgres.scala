package rht.hack

import akka.Done
import akka.stream.scaladsl.Sink
import cats.effect.{ContextShift, IO}
import com.typesafe.config.ConfigFactory
import rht.common.domain.candles.Candle
import doobie._
import doobie.implicits._

import scala.concurrent.{ExecutionContext, Future}

object Postgres {

  val url = ConfigFactory.load().getString("app.postgres.url")
  val user = ConfigFactory.load().getString("app.postgres.user")
  val password = ConfigFactory.load().getString("app.postgres.password")

  private implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

  private val xa = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",
    url,
    user,
    password
  )

  def putCandle(candle: Candle): Unit =
    sql"""
        INSERT INTO candle (figi, interval, low, high, open, close, open_time)
        VALUES (${candle.figi.value},
        ${candle.interval.toString},
          ${candle.details.low},
          ${candle.details.high},
          ${candle.details.open},
          ${candle.details.close},
          ${candle.details.openTime.getEpochSecond})
        ON CONFLICT (figi)
        DO UPDATE SET
        interval = ${candle.interval.toString},
          low = ${candle.details.low},
          high = ${candle.details.high},
          open = ${candle.details.open},
          close = ${candle.details.close},
          open_time = ${candle.details.openTime.getEpochSecond};
      """.update.run.transact(xa).unsafeRunSync

  def toPostgres: Sink[Any, Future[Done]] = {
    Sink.foreach(createCandle)
  }

  def createCandle: Any => Unit = { item =>
    val candle = item.asInstanceOf[Candle]
    putCandle(candle)
  }

}
