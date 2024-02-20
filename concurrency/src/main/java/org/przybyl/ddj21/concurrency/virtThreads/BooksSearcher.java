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
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
import java.time.Duration;

@SuppressWarnings("deprecation")
public class BooksSearcher {

    public final RestHighLevelClient restHighLevelClient;
    private final ElasticsearchClient client;

    public BooksSearcher(
        RestHighLevelClient highLevelClient,
        ElasticsearchTransport transport
    ) {
        this.client = new ElasticsearchClient(transport);
        this.restHighLevelClient = highLevelClient;
    }

    public synchronized void searchBooksWithHighLevelRestClient(String query, int attempt) {
        try {
            var started = System.nanoTime();
            SearchRequest searchRequest = new SearchRequest("books");
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            sourceBuilder.query(QueryBuilders.matchQuery("title", query));
            searchRequest.source(sourceBuilder);

            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            var stopped = System.nanoTime();

            System.out.println("[Attempt: " + attempt + ", response: " + searchResponse + ", took " + Duration.ofNanos(stopped - started).toMillis() + "ms] ");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
}
