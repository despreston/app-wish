package services.Twitter

/**
 * Connects to the Twitter Stream API
 */

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpHeader.ParsingResult
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer
import javax.inject._

import play.api.inject.ApplicationLifecycle
import com.hunorkovacs.koauth.domain.KoauthRequest
import com.hunorkovacs.koauth.service.consumer.DefaultConsumerService
import com.typesafe.config.ConfigFactory
import org.json4s._
import org.json4s.native.JsonMethods._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

trait TwitterStreamer {
  def run(): Unit
}

@Singleton
class TwitterStreamerImpl @Inject() (appLifeCycle: ApplicationLifecycle) extends TwitterStreamer {
  val conf = ConfigFactory.load()

  private val consumerKey = "cYBOlr7fdiINJIfl2U2Hb0Nwu"
  private val consumerSecret = "0vfVQU5i92JtHyiu9vS9rNeq9OsTdu6ugbPpRTN3keOJGGw3mo"
  private val accessToken = conf.getString("twitter.accessToken")
  private val accessTokenSecret = conf.getString("twitter.accessTokenSecret")
  private val url = "https://stream.twitter.com/1.1/statuses/filter.json"

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val formats = DefaultFormats

  private val consumer = new DefaultConsumerService(system.dispatcher)

  val body: Map[String, String] = Map("track" -> "boston")
  val source = Uri(url)

  override def run() = {
    //Create Oauth 1a header
    val oauthHeader: Future[String] = consumer.createOauthenticatedRequest(
      KoauthRequest(
        method = "POST",
        url = url,
        authorizationHeader = None,
        body = Some(body.toList.map(x => x._1 + '=' + x._2).mkString)
      ),
      consumerKey,
      consumerSecret,
      accessToken,
      accessTokenSecret
    ) map (_.header)

    oauthHeader.onComplete {
      case Success(header) =>
        val httpHeaders: List[HttpHeader] = List(
          HttpHeader.parse("Authorization", header) match {
            case ParsingResult.Ok(h, _) => Some(h)
            case _ => None
          },
          HttpHeader.parse("Accept", "*/*") match {
            case ParsingResult.Ok(h, _) => Some(h)
            case _ => None
          }
        ).flatten

        val httpRequest: HttpRequest = HttpRequest(
          method = HttpMethods.POST,
          uri = source,
          headers = httpHeaders,
          entity = FormData(body).toEntity
        )

        val request = Http().singleRequest(httpRequest)
        request.flatMap { response =>
          if (response.status.intValue() != 200) {
            println(response.entity.dataBytes.runForeach(_.utf8String))
            Future(Unit)
          } else {
            response.entity.dataBytes
              .scan("")((acc, curr) => if (acc.contains("\r\n")) curr.utf8String else acc + curr.utf8String)
              .filter(_.contains("\r\n"))
              .map(json => Try(parse(json).extract[Tweet]))
              .runForeach {
                case Success(tweet) =>
                  println("-----")
                  println(tweet.text)
//                  publisher ! tweet.text
                case Failure(e) =>
                  println("-----")
                  println(e.getStackTrace)
              }
          }
        }
      case Failure(failure) => println(failure.getMessage)
    }
  }

  run()
}