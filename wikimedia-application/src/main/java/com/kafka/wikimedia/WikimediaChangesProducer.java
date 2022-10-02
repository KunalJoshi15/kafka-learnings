package com.kafka.wikimedia;

import com.launchdarkly.eventsource.EventHandler;
import com.launchdarkly.eventsource.EventSource;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

import java.net.URI;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class WikimediaChangesProducer {
    // kafka is able to pass the configuration as per the need.
    public static void main(String[] args) throws InterruptedException {
        String bootstrapServers = "127.0.0.1:9092";
        // create producer properties
        // performance of the kafka producer increasing using the kafka things.
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,bootstrapServers);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // set high throughput prodcuer config.
        properties.setProperty(ProducerConfig.LINGER_MS_CONFIG,"20");
        properties.setProperty(ProducerConfig.BATCH_SIZE_CONFIG,Integer.toString(21*1024));
        properties.setProperty(ProducerConfig.COMPRESSION_TYPE_CONFIG,"snappy");


        // create a producer
        KafkaProducer<String,String> producer = new KafkaProducer<String, String>(properties);

        String topic = "wikimedia.recentchange";
        EventHandler eventHandler = new WikimediaChangeHandler(producer,topic);
        String url = "https://stream.wikimedia.org/v2/stream/recentchange";
        EventSource eventSource = new EventSource.Builder(eventHandler, URI.create(url))
                .build();
        // start the producer in another thread
        eventSource.start();
        // blocking needs to be done.
        TimeUnit.MINUTES.sleep(10);
        // in case of kafka key and other things are spciefied using the murmur 2 algorithm.
        // key hashing is the process of determining the mapping of a key to a partition.
        // in the default kafka partitioner the keys are hashed using the murmur2 algorithm.
        // targetPartition = Math.abs(Utils.murmur2(keyBytes)) % (numPartitions-1)
        // this means that same key will go to the same partition ()/
    }
}
