/*
 *  Copyright (C) 2024 Piotr Przybył
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package org.przybyl.ddj21.concurrency.virtThreads;


import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.transport.ElasticsearchTransport;
import static org.przybyl.ddj21.concurrency.virtThreads.ClientHelper.createHttpClient;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class BooksSearcher implements AutoCloseable {

    private final ElasticsearchClient client;
    private final ElasticsearchTransport transport;
    private final String uri;
    private final HttpClient httpClient;

    public BooksSearcher(String uri) {
        this.uri = uri;
        this.httpClient = createHttpClient();
        this.transport = ClientHelper.createTransport(uri);
        this.client = new ElasticsearchClient(transport);
    }

    public synchronized void searchBooksWithEsClient(String query, int attempt) {
        try {
            var started = System.nanoTime();
            var response = client.search(sr -> sr.index("books").query(q -> q.match(mq -> mq.field("title").query(query))), Book.class);
            var stopped = System.nanoTime();
            System.out.println("[Attempt: " + attempt + ", response: " + response + ", took " + Duration.ofNanos(stopped - started).toMillis() + "ms] ");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void searchBooksWithHttpClient(String query, int attempt) {
        try {
            var started = System.nanoTime();
            var request = HttpRequest.newBuilder(URI.create(uri + "/books/_search"))
                .header("Content-type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(STR."""
                {"query": {"match": {"title":"\{query}"}}}"""))
                .build();
            var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            var stopped = System.nanoTime();
            System.out.println("[Attempt: " + attempt + ", response: " + response.body() + ", took " + Duration.ofNanos(stopped - started).toMillis() + "ms] ");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws Exception {
        transport.close();
    }
}
