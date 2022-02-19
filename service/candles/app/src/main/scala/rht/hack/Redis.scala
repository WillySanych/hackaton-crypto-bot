package rht.hack

import akka.Done
import akka.stream.scaladsl.Sink
import com.redis.RedisClient
import rht.common.domain.candles.Candle

import scala.concurrent.Future

object Redis {

  private val redisClient = new RedisClient("localhost", 6379)

  def candleToRedis(candle: Candle): Boolean = {

    val figi = candle.figi.toString

    val candleDetailsMap = Map(
      "interval" -> candle.interval.toString,
      "low" -> candle.details.low.toString,
      "high" -> candle.details.high.toString,
      "open" -> candle.details.open.toString,
      "close" -> candle.details.close.toString,
      "openTime" -> candle.details.openTime.toString,
    )

    redisClient.hmset(figi, candleDetailsMap)
  }

  def toRedis: Sink[Any, Future[Done]] = {
    Sink.foreach(createCandle)
  }

  def createCandle: Any => Unit = { item =>
    val candle = item.asInstanceOf[Candle]
    sendToRedis(candle)
  }

  def sendToRedis(candle: Candle): Candle => Unit = {
    Redis.candleToRedis
  }

}
