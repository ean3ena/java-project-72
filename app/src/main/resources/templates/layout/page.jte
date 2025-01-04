@import hexlet.code.dto.BasePage
@import gg.jte.Content
@import hexlet.code.util.NamedRoutes
@param Content content
@param BasePage page = null

<!DOCTYPE html>
<html lang="ru">
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Анализатор страниц</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>
    </head>
    <body class="d-flex flex-column min-vh-100">
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>
        <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
            <div class="container-fluid">
                <div id="navbarNav" class="collapse navbar-collapse">
                    <div class="navbar-nav">
                        <a class="nav-link" href="${NamedRoutes.rootPath()}">Главная</a>
                        <a class="nav-link" href="${NamedRoutes.urlsPath()}">Сайты</a>
                    </div>
                </div>
            </div>
        </nav>
        <main class="flex-grow-1">
            @if(page != null && page.getFlash() != null)
                <div class="rounded-0 m-0 alert alert-dismissible fade show alert-${page.getFlashType()}">
                    <p class="m-0">${page.getFlash()}</p>
                    <button class="btn-close" type="button" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
            @endif
            <section>
                ${content}
            </section>
        </main>
        <footer class="footer border-top py-3 mt-5 bg-light">
            <div class="container-xl">
                <div class="text-center">
                    created by
                    <a href="https://github.com/ean3ena">ean3ena</a>
                </div>
            </div>
        </footer>
    </body>
</html>