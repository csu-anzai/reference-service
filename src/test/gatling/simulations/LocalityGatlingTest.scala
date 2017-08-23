import ch.qos.logback.classic.LoggerContext
import org.slf4j.LoggerFactory

import scala.concurrent.duration._
import scala.util.Random

/**
  * Performance test for the Locality entity.
  */
class LocalityGatlingTest extends Simulation {

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
    val localityFeed = Iterator.continually(
        // Random number will be accessible in session under variable "OrderRef"
        Map(
            "cityNames" -> Random.alphanumeric.take(Random.nextInt(20)).mkString,
            "latitudes" -> Random.nextInt(80),
            "longitudes" -> Random.nextInt(20)
        )
    )

    val scn = scenario("Test the Locality entity")
        .feed(localityFeed)
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
            exec(http("Get all localities")
                .get("/referenceservice/api/localities")
                .headers(headers_http_authenticated)
                .check(status.is(200)))
                .pause(PAUSE seconds, 5 seconds)
                .exec(http("Search for localities")
                    .get("/referenceservice/api/_search/localities")
                    .queryParam("prefix", "${cityNames}")
                    .queryParam("resultSize", "10")
                    .check(status.is(200)))
                .pause(PAUSE)
                .exec(http("Search for nearest locality")
                    .get("/referenceservice/api/_search/localities/nearest")
                    .queryParam("latitude", "${latitudes}")
                    .queryParam("longitude", "${longitudes}")
                    .check(status.is(200)))
        }

    val users = scenario("Users").exec(scn)

    setUp(
        users.inject(rampUsers(Integer.getInteger("users", 100)) over (Integer.getInteger("ramp", 1) minutes))
    ).protocols(httpConf)
}
