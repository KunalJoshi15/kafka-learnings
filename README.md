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