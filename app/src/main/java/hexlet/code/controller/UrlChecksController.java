package hexlet.code.controller;

import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import hexlet.code.util.NamedRoutes;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import kong.unirest.core.Unirest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.sql.SQLException;

public class UrlChecksController {

    public static void create(Context ctx) throws SQLException {
        var urlId = ctx.pathParamAsClass("id", Long.class).get();
        var url = UrlRepository.find(urlId)
                .orElseThrow(() -> new NotFoundResponse("URL not found"));

        var response = Unirest.get(url.getName()).asString();
        String body = response.getBody();
        Document doc = Jsoup.parse(body);

        var urlCheck = UrlCheck.builder()
                .statusCode(response.getStatus())
                .title(doc.title())
                .h1(doc.select("h1").text())
                .description(doc.select("meta[name=description]").attr("content"))
                .urlId(urlId)
                .build();

        UrlCheckRepository.save(urlCheck);
        ctx.redirect(NamedRoutes.urlPath(urlId));
    }
}
