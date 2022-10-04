package com.kafka.wikimedia;

import com.launchdarkly.eventsource.EventHandler;
import com.launchdarkly.eventsource.MessageEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@AllArgsConstructor
//@Slf4j
public class WikimediaChangeHandler implements EventHandler {
    private static final Logger log = LoggerFactory.getLogger(WikimediaChangeHandler.class.getName());
    KafkaProducer<String,String> kafkaProducer;
    String topic;

    @Override
    public void onOpen() throws Exception {

    }

    @Override
    public void onClosed() throws Exception {
        //
        kafkaProducer.close();
    }
    /*Increasing the batch size in kafka helps us in increasing the amount of data tha is being sent to
    * the consumer. when using the property linger.ms it is used to specify the time duration for which the producer will wait before sending
    * the chunk of data to kafka.*/
    @Override
    public void onMessage(String s, MessageEvent messageEvent) throws Exception {
        log.info("Sending data to kafka topic "+messageEvent.getData());
        // asynchronous when the message is produced then the message is sent to kafka.
        kafkaProducer.send(new ProducerRecord<>(topic,messageEvent.getData()));
    }

    @Override
    public void onComment(String s) throws Exception {
    }
    // in order to incease the throughput in case of kafka the producer need to increase the linger.ms property and the producer
    // will wait a few milliseconds for the batches to fill up before sending them.
    // if you are sending full batches and have memory to spare, you can increase the batch.size and send larger batches.
    // the code for it looks like this.
    // properties.setProperty(ProducerConfig.COMPRESSION_TYPE_CONFIG,"snappy") -> This is the algorithm which is usually used.
    // properties.setProperty(ProducerConfig.LINGER_MS_CONFIG,"20")'.
    // properties.setProperty(ProducerConfig.BATCH_SIZE_CONFIG,Integer.toString(32*1024))
    // well add snappy message compression in our producer.
    // snappy is very helpful if you messages are text based, for example log lines or JSON documents.
    // snappy has a good balance of CPU/compression ratio.
    @Override
    public void onError(Throwable throwable) {
        log.error("Error in Stream Reading",throwable.getMessage());
    }
}
