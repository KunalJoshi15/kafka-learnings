package com.example.kafkaconsumer;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.opensearch.action.index.IndexRequest;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestClient;
import org.opensearch.client.RestClientBuilder;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.client.indices.CreateIndexRequest;
import org.opensearch.client.indices.GetIndexRequest;
import org.opensearch.common.xcontent.XContentType;
import org.opensearch.index.Index;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;

public class OpenSearchCustomer {

    public static RestHighLevelClient createOpenSearchClient() {
        // get the connection string which will be used for accessing the data.
        String connString = "http://localhost:9200";
        RestHighLevelClient restHighLevelClient;
        URI connUri = URI.create(connString);

        String userInfo = connUri.getUserInfo();

        if(userInfo==null) {
            // REST clinet without security
            restHighLevelClient = new RestHighLevelClient(RestClient.builder(new HttpHost(connUri.getHost(),connUri.getPort(),"http")));
        } else {
            String[] auth = userInfo.split(":");

            CredentialsProvider c = new BasicCredentialsProvider();
            c.setCredentials(AuthScope.ANY,new UsernamePasswordCredentials(auth[0],auth[1]));

            restHighLevelClient = new RestHighLevelClient(
                    RestClient.builder(new HttpHost(connUri.getHost(),connUri.getPort(),connUri.getScheme()))
                            .setHttpClientConfigCallback(httpAsyncClientBuilder -> httpAsyncClientBuilder.setDefaultCredentialsProvider(c).setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy()))
            );

        }
        return restHighLevelClient;
    }

    private static KafkaConsumer<String,String> createKafkaConsumer() {
        String bootstrapServers = "127.0.0.1:9092";
        String groupId = "my-second-application";
        String topic = "wikimedia";

        // Create consumer related properties
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,bootstrapServers);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG,groupId);
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"latest");

        // create kafka consumer;
        return new KafkaConsumer<String,String>(properties);
    }
    public static void main(String[] args) throws IOException {
        Logger log = LoggerFactory.getLogger(OpenSearchCustomer.class.getSimpleName());
        // first create an OpenSearchClient
        RestHighLevelClient openSearchClient = createOpenSearchClient();
        // Setting up kafka consumer
        KafkaConsumer<String,String> consumer = createKafkaConsumer();
        consumer.subscribe(Arrays.asList("wikimedia.recentchange"));
        // create our Kafka Client
        try(openSearchClient) {
            boolean indexExists = openSearchClient.indices().exists(new GetIndexRequest("wikimedia"),RequestOptions.DEFAULT);

            if(!indexExists) {
                CreateIndexRequest createIndexRequest = new CreateIndexRequest("wikimedia");
                openSearchClient.indices().create(createIndexRequest, RequestOptions.DEFAULT);
                log.info("The wikimedia index has been created");
            } else {
                log.info("The wikimedia index already exists");
            }

            while (true) {
                ConsumerRecords<String,String> records = consumer.poll(Duration.ofMillis(3000));
                int recordCount = records.count();

                log.info("Received "+recordCount+" record(s)");

                for(ConsumerRecord<String,String> record:records){
                    IndexRequest indexRequest = new IndexRequest("wikimedia")
                            .source(record.value(), XContentType.JSON);

                    openSearchClient.index(indexRequest,RequestOptions.DEFAULT);
                    log.info("Inserted 1 document into Opensearch");

                }
            }
        }

        // main code
    }
}
/*
* Opensearch porject for docker.
* we need to install a plugin in intellij idea. we can start the container using the intellij idea itself. We
* just have to start the docker desktop
* In case of the opensearch we have indexes where the document gets stored
* For creating a document inside we use this command. PUT /my-first-index/_doc/1
* This will be creating the wikimedia index and which we can use as per the need.*/