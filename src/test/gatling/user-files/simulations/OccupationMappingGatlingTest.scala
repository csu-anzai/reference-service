import _root_.io.gatling.core.scenario.Simulation
import ch.qos.logback.classic.LoggerContext
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import org.slf4j.LoggerFactory

import scala.concurrent.duration._

/**
  * Performance test for the OccupationMapping entity.
  */
class OccupationMappingGatlingTest extends Simulation {

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

    val scn = scenario("Test the OccupationMapping entity")
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
            exec(http("Get occupationlabelMappings for avam:73311")
                .get("/referenceservice/api/occupations/label/mapping/avam/73311?language=it)
                .headers(headers_http_authenticated)
                .check(status.is(200)))
                .pause(10 seconds, 20 seconds)
        }

    val users = scenario("Users").exec(scn)

    setUp(
        users.inject(rampUsers(Integer.getInteger("users", 100)) over (Integer.getInteger("ramp", 1) minutes))
    ).protocols(httpConf)
}
