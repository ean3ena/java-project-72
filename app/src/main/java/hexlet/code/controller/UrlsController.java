package hexlet.code.controller;

import hexlet.code.dto.urls.UrlPage;
import hexlet.code.dto.urls.UrlsPage;
import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import hexlet.code.util.NamedRoutes;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Map;

import static io.javalin.rendering.template.TemplateUtil.model;

public class UrlsController {

    public static void index(Context ctx) throws SQLException {
        var urls = UrlRepository.getEntities();
        Map<Long, UrlCheck> lastChecks = UrlCheckRepository.getLastChecks();
        var page = new UrlsPage(urls, lastChecks);
        page.setFlash(ctx.consumeSessionAttribute("flash"));
        page.setFlashType(ctx.consumeSessionAttribute("flashType"));
        ctx.render("urls/index.jte", model("page", page));
    }

    public static void show(Context ctx) throws SQLException {
        var id = ctx.pathParamAsClass("id", Long.class).get();
        var url = UrlRepository.find(id)
                .orElseThrow(() -> new NotFoundResponse("URL not found"));
        var checks = UrlCheckRepository.getByUrlId(url.getId());
        var page = new UrlPage(url, checks);
        ctx.render("urls/show.jte", model("page", page));
    }

    public static void create(Context ctx) throws SQLException {
        var urlFormParam = ctx.formParam("url");

        try {
            URL checkableUrl = new URI(urlFormParam).toURL();

            var protocol = checkableUrl.getProtocol();
            var host = checkableUrl.getHost();
            var port = checkableUrl.getPort();
            String urlString = String.format("%s://%s%s", protocol, host, port != -1 ? ":" + port : "");

            Boolean urlExists = UrlRepository.getEntities().stream()
                    .anyMatch(el -> el.getName().equals(urlString));

            if (urlExists) {
                ctx.sessionAttribute("flash", "Страница уже существует");
                ctx.sessionAttribute("flashType", "warning");
            } else {
                var urlObj = new Url(urlString);
                UrlRepository.save(urlObj);
                ctx.sessionAttribute("flash", "Страница успешно добавлена");
                ctx.sessionAttribute("flashType", "success");
            }
            ctx.redirect(NamedRoutes.urlsPath());
        } catch (URISyntaxException | MalformedURLException | IllegalArgumentException e) {
            ctx.sessionAttribute("flash", "Некорректный URL");
            ctx.sessionAttribute("flashType", "danger");
            ctx.redirect(NamedRoutes.rootPath());
        }
    }
}
