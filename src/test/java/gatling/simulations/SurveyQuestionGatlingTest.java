package gatling.simulations;

import static io.gatling.javaapi.core.CoreDsl.StringBody;
import static io.gatling.javaapi.core.CoreDsl.exec;
import static io.gatling.javaapi.core.CoreDsl.rampUsers;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.header;
import static io.gatling.javaapi.http.HttpDsl.headerRegex;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;

/**
 * Performance test for the SurveyQuestion entity.
 *
 * @see <a href="https://github.com/jhipster/generator-jhipster/tree/v8.11.0/generators/gatling#logging-tips">Logging tips</a>
 */
public class SurveyQuestionGatlingTest extends Simulation {

    String baseURL = Optional.ofNullable(System.getProperty("baseURL")).orElse("http://localhost:8080");

    HttpProtocolBuilder httpConf = http
        .baseUrl(baseURL)
        .inferHtmlResources()
        .acceptHeader("*/*")
        .acceptEncodingHeader("gzip, deflate")
        .acceptLanguageHeader("fr,fr-fr;q=0.8,en-us;q=0.5,en;q=0.3")
        .connectionHeader("keep-alive")
        .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:33.0) Gecko/20100101 Firefox/33.0")
        .silentResources(); // Silence all resources like css or css so they don't clutter the results

    Map<String, String> headersHttp = Map.of("Accept", "application/json");

    Map<String, String> headersHttpAuthentication = Map.of("Content-Type", "application/json", "Accept", "application/json");

    Map<String, String> headersHttpAuthenticated = Map.of("Accept", "application/json", "Authorization", "${access_token}");

    ChainBuilder scn = exec(http("First unauthenticated request").get("/api/account").headers(headersHttp).check(status().is(401)))
        .exitHereIfFailed()
        .pause(10)
        .exec(
            http("Authentication")
                .post("/api/authenticate")
                .headers(headersHttpAuthentication)
                .body(StringBody("{\"username\":\"admin\", \"password\":\"admin\"}"))
                .asJson()
                .check(header("Authorization").saveAs("access_token"))
        )
        .exitHereIfFailed()
        .pause(2)
        .exec(http("Authenticated request").get("/api/account").headers(headersHttpAuthenticated).check(status().is(200)))
        .pause(10)
        .repeat(2)
        .on(
            exec(http("Get all surveyQuestions").get("/api/survey-questions").headers(headersHttpAuthenticated).check(status().is(200)))
                .pause(Duration.ofSeconds(10), Duration.ofSeconds(20))
                .exec(
                    http("Create new surveyQuestion")
                        .post("/api/survey-questions")
                        .headers(headersHttpAuthenticated)
                        .body(
                            StringBody(
                                "{" +
                                "\"surveyId\": 0" +
                                ", \"text\": \"SAMPLE_TEXT\"" +
                                ", \"questionType\": \"SCALE\"" +
                                ", \"scaleMin\": 0" +
                                ", \"scaleMax\": 0" +
                                ", \"isNeed\": null" +
                                ", \"orderNo\": 0" +
                                "}"
                            )
                        )
                        .asJson()
                        .check(status().is(201))
                        .check(headerRegex("Location", "(.*)").saveAs("new_surveyQuestion_url"))
                )
                .exitHereIfFailed()
                .pause(10)
                .repeat(5)
                .on(exec(http("Get created surveyQuestion").get("${new_surveyQuestion_url}").headers(headersHttpAuthenticated)).pause(10))
                .exec(http("Delete created surveyQuestion").delete("${new_surveyQuestion_url}").headers(headersHttpAuthenticated))
                .pause(10)
        );

    ScenarioBuilder users = scenario("Test the SurveyQuestion entity").exec(scn);

    {
        setUp(
            users.injectOpen(rampUsers(Integer.getInteger("users", 100)).during(Duration.ofMinutes(Integer.getInteger("ramp", 1))))
        ).protocols(httpConf);
    }
}
