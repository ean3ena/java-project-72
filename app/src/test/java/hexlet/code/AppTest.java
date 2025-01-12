package hexlet.code;

import hexlet.code.model.Url;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import hexlet.code.util.NamedRoutes;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AppTest {

    private Javalin app;
    private static MockWebServer mockServer;

    @BeforeAll
    public static void startMockServer() throws IOException {
        MockResponse mockResponse = new MockResponse()
                .setResponseCode(200)
                .setBody(Files.readString(Paths.get("src/test/resources/fixtures/test.html")
                        .toAbsolutePath().normalize()));

        mockServer = new MockWebServer();
        mockServer.enqueue(mockResponse);
        mockServer.start(7095);
    }

    @AfterAll
    public static void shutdownMockServer() throws IOException {
        mockServer.shutdown();
    }

    @BeforeEach
    public final void setUp() throws Exception {
        app = App.getApp();
    }

    @Test
    public void testMainPage() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get(NamedRoutes.rootPath());
            assertEquals(200, response.code());
            assertTrue(response.body().string().contains("Анализатор страниц"));
        });
    }

    @Test
    public void testAddUrl() {
        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=http://example.com";
            var response = client.post(NamedRoutes.urlsPath(), requestBody);
            assertEquals(200, response.code());
            assertTrue(response.body().string().contains("http://example.com"));

            var url = UrlRepository.getEntities().stream()
                    .filter(el -> el.getName().equals("http://example.com"))
                    .toList();
            assertEquals(1, url.size());
        });
    }

    @Test
    public void testAddWrongUrl() {
        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=hhpp://example.com";
            var response = client.post(NamedRoutes.urlsPath(), requestBody);
            assertEquals(200, response.code());
            assertFalse(response.body().string().contains("hhpp://example.com"));

            var url = UrlRepository.getEntities();
            assertEquals(0, url.size());
        });
    }

    @Test
    public void testAddUrlWithPort() {
        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=http://example.com:7890";
            var response = client.post(NamedRoutes.urlsPath(), requestBody);
            assertEquals(200, response.code());
            assertTrue(response.body().string().contains("http://example.com:7890"));

            var url = UrlRepository.getEntities().stream()
                    .filter(el -> el.getName().equals("http://example.com:7890"))
                    .toList();
            assertEquals(1, url.size());
        });
    }

    @Test
    public void testAddUrlAgain() {
        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=http://example.com";
            var response = client.post(NamedRoutes.urlsPath(), requestBody);
            assertEquals(200, response.code());
            var response2 = client.post(NamedRoutes.urlsPath(), requestBody);
            assertEquals(200, response2.code());

            var url = UrlRepository.getEntities().stream()
                    .filter(el -> el.getName().equals("http://example.com"))
                    .toList();
            assertEquals(1, url.size());
        });
    }

    @Test
    public void testAddFewUrl() throws SQLException {
        JavalinTest.test(app, (server, client) -> {
            var response = client.post("/urls", "url=http://example.com");
            assertEquals(200, response.code());
            var response2 = client.post("/urls", "url=http://example2.com");
            assertEquals(200, response2.code());
            var response3 = client.post("/urls", "url=http://example3.com");
            assertEquals(200, response3.code());

            var urlList = UrlRepository.getEntities();
            assertEquals(3, urlList.size());
        });
    }

    @Test
    public void testUrlsList() throws SQLException {
        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=http://example.com";
            var response = client.post(NamedRoutes.urlsPath(), requestBody);
            assertEquals(200, response.code());
            var response2 = client.get(NamedRoutes.urlsPath());
            assertTrue(response2.body().string().contains("http://example.com"));
        });
    }

    @Test
    public void testUrlPage() throws SQLException {
        var url = new Url("http://example.com", LocalDateTime.now());
        UrlRepository.save(url);
        JavalinTest.test(app, (server, client) -> {
            var response = client.get(NamedRoutes.urlPath(url.getId()));
            assertEquals(200, response.code());
            assertTrue(response.body().string().contains("http://example.com"));
        });
    }

    @Test
    public void testUrlNotFound() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get(NamedRoutes.urlPath(999999L));
            assertEquals(404, response.code());
            assertTrue(response.body().string().contains("not found"));

            assertEquals(Optional.empty(), UrlRepository.find(99999L));
        });
    }

    @Test
    public void testUrlChecks() throws SQLException {

        var baseUrl = mockServer.url("/").toString();
        var url = new Url(baseUrl, LocalDateTime.now());
        UrlRepository.save(url);

        JavalinTest.test(app, (server, client) -> {

            var response = client.post(NamedRoutes.urlChecksPath(url.getId()));
            assertEquals(200, response.code());

            var body = response.body().string();
            assertTrue(body.contains("Тестовый заголовок"));
            assertTrue(body.contains("Тестовое описание страницы"));

            var checks = UrlCheckRepository.getByUrlId(url.getId());
            assertEquals(1, checks.size());

            var checks2 = UrlCheckRepository.getLastChecks();
            assertEquals(1, checks2.size());
        });
    }
}
