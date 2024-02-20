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

import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

public interface ClientHelper {

    static ElasticsearchTransport createTransport(String uri) {
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
            new UsernamePasswordCredentials("elastic", System.getenv().getOrDefault("ESPSWD", "changeme")));

        final RestClient restClient = RestClient.builder(HttpHost.create(uri))
            .setHttpClientConfigCallback(hcb -> hcb.setDefaultCredentialsProvider(credentialsProvider))
            .build();
        return new RestClientTransport(restClient, new JacksonJsonpMapper());
    }

    static RestHighLevelClient createHighLevelRestClient(String uri) {
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
            new UsernamePasswordCredentials("elastic", System.getenv().getOrDefault("ESPSWD", "changeme")));

        return new RestHighLevelClient(RestClient.builder(HttpHost.create(uri))
            .setHttpClientConfigCallback(hcb -> hcb.setDefaultCredentialsProvider(credentialsProvider)));
    }

}
