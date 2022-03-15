package tk.antoine.roux;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class HTTPCaller {
    private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    private final HttpClient client;

    public HTTPCaller() {
        client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NEVER)
                .build();
    }

    public String call(String website) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(website))
                .GET()
                .build();
        try {
            HttpResponse<String> send = client.send(request, getStringBodyHandler());
            return send.body();
        } catch (IOException | InterruptedException e) {
            LOGGER.severe(e.getMessage());
            throw e;
        }
    }

    public CompletableFuture<String> callAsync(String website) {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(website)).GET().build();

        return client.sendAsync(request, getStringBodyHandler())
                .thenApply(HttpResponse::body);
    }

    private HttpResponse.BodyHandler<String> getStringBodyHandler() {
        return response -> {
            LOGGER.config(String.format("http version : %s", response.version().name()));

            LOGGER.config("headers : ");
            response.headers().map().forEach((key, values) -> {
                String headerValue = String.join(", ", values);
                LOGGER.config(String.format("%s -> %s", key, headerValue));
            });
            return HttpResponse.BodySubscribers.ofString(StandardCharsets.ISO_8859_1);
        };
    }
}
