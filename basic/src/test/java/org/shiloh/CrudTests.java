package org.shiloh;

import co.elastic.clients.elasticsearch._types.Result;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import co.elastic.clients.elasticsearch.indices.DeleteIndexResponse;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.shiloh.pojo.Product;

import java.io.IOException;
import java.util.List;

/**
 * ElasticSearch Crud Unit Tests
 *
 * @author shiloh
 * @date 2024/12/18 15:28
 */
@Slf4j
public class CrudTests extends BaseTests {
    private static final String INDEX_NAME = "products";

    /**
     * Create an index
     *
     * @author shiloh
     * @date 2024/12/18 15:28
     */
    @Test
    public void testCreateIndex() throws IOException {
        final CreateIndexResponse response = this.client.indices()
            .create(builder -> builder.index(INDEX_NAME));
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.acknowledged()).isTrue();
    }

    // region document crud

    /**
     * Indexing document - Create
     *
     * @author shiloh
     * @date 2024/12/18 15:43
     */
    @Test
    public void testIndexDocument() throws IOException {
        final Product product = new Product("City bike", 150.0);
        final IndexResponse response = this.client.index(builder -> builder
            .index(INDEX_NAME)
            .id(product.getId())
            .document(product)
        );
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.id()).isNotBlank()
            .isEqualTo(product.getId());
        log.info("Indexed with version {}", response.version());
    }

    /**
     * Getting document by id - Search
     *
     * @author shiloh
     * @date 2024/12/18 15:56
     */
    @Test
    public void testGetDocument() throws IOException {
        final String id = "560d1c97-dd08-4f12-92fc-b985238f9761";
        final GetResponse<Product> response = this.client.get(builder -> builder
                // index name of where the document is stored
                .index(INDEX_NAME)
                // document identifier
                .id(id),
            Product.class
        );
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.found()).isTrue();
        Assertions.assertThat(response.id()).isNotBlank()
            .isEqualTo(id);
        final Product product = response.source();
        Assertions.assertThat(product).isNotNull();
        Assertions.assertThat(product.getId()).isNotBlank()
            .isEqualTo(id);
        log.info("Product info = {}", product);
    }

    /**
     * Searching document with query
     *
     * @author shiloh
     * @date 2024/12/18 16:10
     */
    @Test
    public void testSearchDocument() throws IOException {
        final String searchText = "bike";
        final SearchResponse<Product> response = this.client.search(request -> request
                .index(INDEX_NAME)
                .query(query -> query
                    .match(match -> match
                        .field("name")
                        .query(searchText))),
            Product.class
        );
        Assertions.assertThat(response).isNotNull();
        final HitsMetadata<Product> hitsMetadata = response.hits();
        Assertions.assertThat(hitsMetadata).isNotNull();
        final List<Hit<Product>> hits = hitsMetadata.hits();
        Assertions.assertThat(hits).isNotEmpty();
        hits.forEach(hit -> {
            final Product product = hit.source();
            Assertions.assertThat(product).isNotNull();
            Assertions.assertThat(product.getId()).isNotBlank();
            log.info("Product: {}", product);
        });
    }

    /**
     * Updating document by id - Update
     *
     * @author shiloh
     * @date 2024/12/18 16:30
     */
    @Test
    public void testUpdateDocument() throws IOException {
        final String id = "560d1c97-dd08-4f12-92fc-b985238f9761";
        final Product product = new Product(id, "City bike(updated)", 233.33);
        final UpdateResponse<Product> response = this.client.update(builder -> builder
                .index(INDEX_NAME)
                .id(id)
                .doc(product)
                .docAsUpsert(true),
            Product.class
        );
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.id()).isNotBlank()
            .isEqualTo(id);
    }

    /**
     * Deleting document by id - Delete
     *
     * @author shiloh
     * @date 2024/12/18 16:43
     */
    @Test
    public void testDeleteDocument() throws IOException {
        final String id = "560d1c97-dd08-4f12-92fc-b985238f9761";
        final DeleteResponse response = this.client.delete(builder -> builder.index(INDEX_NAME).id(id));
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.result()).isEqualTo(Result.Deleted);
    }

    // endregion

    /**
     * Deleting index
     *
     * @author shiloh
     * @date 2024/12/18 16:45
     */
    @Test
    public void testDeleteIndex() throws IOException {
        final DeleteIndexResponse response = this.client.indices()
            .delete(builder -> builder.index(INDEX_NAME));
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.acknowledged()).isTrue();
    }
}
