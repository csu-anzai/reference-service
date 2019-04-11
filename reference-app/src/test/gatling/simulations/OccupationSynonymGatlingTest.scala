import _root_.io.gatling.core.scenario.Simulation
import ch.qos.logback.classic.LoggerContext
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import org.slf4j.LoggerFactory

import scala.concurrent.duration._
import scala.util.Random

/**
  * Performance test for the OccupationSynonym entity.
  */
class OccupationSynonymGatlingTest extends Simulation {

    val PAUSE = 2
    val context: LoggerContext = LoggerFactory.getILoggerFactory.asInstanceOf[LoggerContext]
    // Log all HTTP requests
    //context.getLogger("io.gatling.http").setLevel(Level.valueOf("TRACE"))
    // Log failed HTTP requests
    //context.getLogger("io.gatling.http").setLevel(Level.valueOf("DEBUG"))

    val baseURL = Option(System.getProperty("baseURL")) orElse Option(System.getenv("baseURL")) getOrElse """http://127.0.0.1:8080"""

    val httpConf = http
        .baseURL(baseURL)
        .inferHtmlResources()
        .acceptHeader("*/*")
        .acceptEncodingHeader("gzip, deflate")
        .acceptLanguageHeader("fr,fr-fr;q=0.8,en-us;q=0.5,en;q=0.3")
        .connectionHeader("keep-alive")
        .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:33.0) Gecko/20100101 Firefox/33.0")

    val headers_http = Map(
        "Accept" -> """application/json"""
    )

    val headers_http_authentication = Map(
        "Content-Type" -> """application/json""",
        "Accept" -> """application/json"""
    )

    val headers_http_authenticated = Map(
        "Accept" -> """application/json""",
        "Authorization" -> "${access_token}"
    )

    // Define an infinite feeder which calculates random numbers
    val occupationSynonymFeed = Iterator.continually(
        // Random number will be accessible in session under variable "OrderRef"
        Map(
            "occupationCodes" -> (90000000 + Random.nextInt(100000)).toString,
            "occupationNames" -> Random.alphanumeric.take(Random.nextInt(20)).mkString
        )
    )


    val scn = scenario("Test the OccupationSynonym entity")
        .feed(occupationSynonymFeed) // attaching feeder to session
        .exec(http("First unauthenticated request")
        .get("/api/account")
        .headers(headers_http)
        .check(status.is(401))).exitHereIfFailed
        .pause(PAUSE)
        .exec(http("Authentication")
            .post("/api/authenticate")
            .headers(headers_http_authentication)
            .body(StringBody("""{"username":"admin", "password":"admin"}""")).asJSON
            .check(header.get("Authorization").saveAs("access_token"))).exitHereIfFailed
        .pause(PAUSE)
        .exec(http("Authenticated request")
            .get("/api/account")
            .headers(headers_http_authenticated)
            .check(status.is(200)))
        .pause(PAUSE)
        .repeat(2) {
            exec(http("Get all occupations")
                .get("/referenceservice/api/occupations/synonym")
                .headers(headers_http_authenticated)
                .check(status.is(200)))
                .pause(PAUSE seconds, 5 seconds)
                .exec(http("Create new occupation")
                    .post("/referenceservice/api/occupations/synonym")
                    .headers(headers_http_authenticated)
                    .body(StringBody("""{"code":${occupationCodes}, "language":"de", "name":"${occupationNames}"}""")).asJSON
                    .check(status.is(201))
                    .check(headerRegex("Location", "(.*)").saveAs("new_occupation_url"))).exitHereIfFailed
                .pause(PAUSE)
                .repeat(5) {
                    exec(http("Get created occupation")
                        .get("/referenceservice${new_occupation_url}")
                        .headers(headers_http_authenticated))
                        .pause(PAUSE)
                }
                .exec(http("Delete created occupation")
                    .delete("/referenceservice${new_occupation_url}")
                    .headers(headers_http_authenticated))
                .pause(PAUSE)
        }

    val users = scenario("Users").exec(scn)

    setUp(
        users.inject(rampUsers(100) over (1 minutes))
    ).protocols(httpConf)
}
