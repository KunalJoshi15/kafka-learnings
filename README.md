# kafka-learnings
If key=null the producer has a default partitioner.
The algorithms which are used in that particualr case are 
Round Robin: for kafka 2.3 and below
Sticky Partitioner: For kafka 2.4 and above

Sticky Partitioner improves the performance of the producer especially when high throughput when the 
key is null.
In case of the round robin partitioner everything is done in the round robin manner the sending up of messages. IN case of the sticky paritioner algorithm.
If there are continuous message sending the partitioner is smart enough to buffer those all in single location.

Stick to a partition until the batch is full or linger.ms has elapsed.
After sending the batch the partition that is sticky changes.
Larger batches are reduced latency larger requests, and batch.size more likely to be reached. <Messages are spread in all the partitions.>

max.block.ms -> If producer produces faster than the broker can take, the records will be buffered in memory.
buffer.memory = 32mb the size of the send buffer.

the max.block.ms = 60000ms this means that when data is send and broker is not accepting any data then send it blocking.
if 60s has elapsed then exception is thrown in the blocking request.
Open search is the open form of the elastic search we will be setting up a project in which we will be using open search for data sending.
Open search is the open form of elastic search. GET / gives us the information regarding the page as per our requirement.
Using PUT method in case of the opensearch help us in creating a document inside the index. PUT /my-first-index (This will create the index).
GET /my-first-index/_doc/1 to get the information from a index and a specific document. DELETE method is used for deleting the index.
In the delivery semantics - At most once means that the offsets are committed when message is received. There could be data loss in this case.
As consumer will be reading from the locations later. At least once offsets are commited after the message is processed. If the processing goes wrong the message 
will be read again. This can result in duplicate processing of messages. Make sure your processing is idempotent.
In the java consumer API offsets are regularly committed. Enable at least once reading scenario by default.
Offsets are committed when you .poll().
In case of the autocommit behaviour the offsets will be committed automatically for you at regular interval. auto.commit.interval.ms=5000 by default.
every time you call poll method. 
IF the autocommit functionality is not allowed then we use the certain proprety as per our requirement for committing the offsets.
while(true) {
    batch += comsumer.poll(Duration.ofMillis(100));
    if isReady(batch) {
        doSomethingSynchronous(batch)
        consumer.commitAsync();
    }
}
There is even a third strategy which is sometimes used. But it is quite advanced. We need to assign partitions to our consumers at launch manually using .seek() API
You need to model and store your offsets in a database table for example. You need to handle the cases where rebalances happen(ConsumerRebalanceListner interface)
Each time we receive some data it should not always be sent to the consumer we need to send the bulk request in that particular case. As there might be some performance issues otherwise.