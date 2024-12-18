package org.shiloh;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient;
import org.junit.After;
import org.junit.Before;
import org.shiloh.constant.ElasticConstants;

import java.io.IOException;

/**
 * Basic TestCase
 *
 * @author shiloh
 * @date 2024/12/18 15:22
 */
public abstract class BaseTests {
    protected ElasticsearchClient client;

    /**
     * init
     *
     * @author shiloh
     * @date 2024/12/18 15:26
     */
    @Before
    public void setup() {
        final RestClient restClient = RestClient.builder(HttpHost.create(ElasticConstants.SERVER_URL))
            .setDefaultHeaders(new Header[]{
                new BasicHeader(HttpHeaders.AUTHORIZATION, "ApiKey " + ElasticConstants.API_KEY_DEV)
            })
            .build();
        final RestClientTransport transport = new RestClientTransport(
            restClient, new JacksonJsonpMapper()
        );
        this.client = new ElasticsearchClient(transport);
    }

    /**
     * destroy
     *
     * @author shiloh
     * @date 2024/12/18 15:29
     */
    @After
    public void destroy() {
        try {
            this.client.close();
        } catch (IOException ignored) {
            // do nothing
        }
    }
}
