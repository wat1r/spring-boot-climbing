

### 如何保证消息发布的可靠性
消息的不丢失对于消息队列来说至关重要。但要实现这一点也是非常困难，极端考虑甚至是不可能的，因为机器一定可能会挂，磁盘一定可能会坏，只是看能够承受多大的规模故障罢了。消息不丢失主要指:
- 如果发送失败，发送方要能够知道这个消息，方便它进行重试或者相应处理 。
- 如果发送成功，要确保发送成功后，即便一部分数量的 `Kafka` 机器全部被物理销毁，这个消息依旧能够被持久化保存下来。
 `Kafka` 的 `Partition` 有一个 `ISR` 机制，当一个 `message` 被写入到 `Leader Partition` 中后，并被所有 `ISR` 给同步到本地，此时只要`ISR`的机器有一台还存活着且磁盘完好，这个消息就能够正常存在。如果在`Leader`刚写入完，但此时 `Leader` 立马挂了，会导致这个消息永久丢失。如果要实现绝对意义的不丢失，就需要客户端当且仅当获知到这个状态时，才认为消息发送是成功的。但这种等待的性能损耗会随着 `Replication` 的数量增多而线形增多。
有时候我们要求可能并没有如此之精确，可以只要求 `Leader` 写入完了就告诉我们成功了。但这里会存在一个消息重发的情况，例如，`Leader` 写入完成后告诉我们，但路上丢包了，导致我们以为发送失败了，此时又继续发送了一份消息，这个时候可能会存两份 。 `Kafka` 是不会去管理这种复杂情况的，客户端需要在使用的时候明确知道这件事情并在程序设计上为此负责，比如可以在每条消息里加一个全局唯一ID去标识一个消息，在消费的时候去判断是否消费过这个消息。
如果我们要严格要求不重发，且能够接受消息丢失的情况，只要不去理睬 `Leader` 的写入成功信息即可，每个消息仅发送一次，不在乎发送是否成功。
在 `Kafka` 客户端中，我们可以有以下三个参数来处理上述情况:
- `acks=0`: `producer` 不等待 `broker` 的 `acks`而写入`leader`分区。发送的消息可能丢失，但永远不会重发。
- `acks=1`: `leader` 不等待其他 `follower` 同步，`leader` 直接写 `log` 然后发送 `acks` 给 `producer`。
- `acks=all`: `leader` 等待所有 `follower` 同步完成才返回`acks`。
![image-20210331195646692](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\kafka\深入理解Kafka(二)之消息可靠性.assets\image-20210331195646692.png)
![image-20210331200127609](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\kafka\深入理解Kafka(二)之消息可靠性.assets\image-20210331200127609.png)
![image-20210331200348061](D:\Dev\SrcCode\spring-boot-climbing\data-climbing-manuscripts\src\main\data\kafka\深入理解Kafka(二)之消息可靠性.assets\image-20210331200348061.png)
### 如何保证消息消费的可靠性
正常情况下，我们一般希望消息队列里的消息仅被消费一次，且一定会被消费一次，并且处理结果一定是成功的。但要实现这点非常困难，且这一点的可靠性大部分取决于用户编写代码本身的质量。
`Kafka` 的 `Consumer` 机制只是提供了一个保存 `offset` 的接口，由于在没有过期的情况下，`Kafka` 并不会主动去删除消息，所以我们的问题仅仅在于如何去确保保存 `offset`和处理消息成功这两个操作是一个原子操作。
#### 有且仅有一次 「exactly once」
一般性我们认为计算操作是无状态的，IO操作是有状态的，如果消费者仅仅只是做无状态的一些操作，我们其实完全不需要考虑它是否多次消费的问题。大部分时候让我们头痛的都是数据库的保存操作。有一种取巧的方案是，把每次消费的 `offset` 作为一个字段和正常保存操作一起存入数据库中，如果保存失败，则说明处理失败，此时可以重新保存。
#### 至少一次 「at least once」
但我们也可以用更好的程序设计来让这件事情做的更加优雅，如果我们的消费者函数是一个幂等函数，相同的输入执行多次也不会影响到最终结果。那么我们就能够接受重复处理消息的情况。而此时只要确保所有的消息都能够被至少消费一次就行了。这种场景我们可以选择先处理消息，再保存 `offset` 。
#### 至多一次 「at most once」
也有的时候我们希望最多处理消息一次，可以接受个别消息没有被处理的情况，我们也可以选择先保存 `offset` , 再处理消息。

### 如何保证消息的顺序

``Kafka`` 每个 ``Partition`` 都是相互独立的，``Kafka`` 只能保证单个 ``Partition`` 下的有序。如果你的应用程序需要严格按照消息发送的顺序进行消费，可以考虑在程序设计上去做文章。
举个例子是，我有一个游戏系统，每个人会顺序做一些不同操作，对应不同事件，发送到`Kafka`。我的消费者显然需要考虑到每个用户操作的上下文关系，但这个时候我们所需要的有序其实是针对单个用户的有序，而不要求全局有序。我们可以以用户的`ID`作为 `key` , 确保单个用户一定会被分配到某个固定的 `partition` 上，这样我们就能够实现单个用户维度的有序了。
如果你一定要全局的有序序列，还有一种取巧的做法是，所有消息都使用同一个 `key` , 这样他们一定会被分配到同一个 `partition` 上，这种做法适用于临时性且数据量不大的小需求，消息量大了会有性能压力。

### 高度实时的场景下能够有非常高的吞吐

在 `Linux` 操作系统中，当上层有写操作时，操作系统只是将数据写入 `Page` `Cache`，同时标记 `Page` 属性为 `Dirty`。当读操作发生时，先从`Page` `Cache`中查找，如果发生缺页才进行磁盘调度，最终返回需要的数据。
当我们的 `Producer` 处于一个高度实时的状态时，读和写的文件位置会非常接近，甚至完全一样，此时就能最大限度的利用该 `Page` `Cache` 机制，也就是这种情况下`Kafka` 甚至都没有直接去读磁盘的文件。

### `Kafka` `Producer` `Key` 选择

假设一个场景，我们需要将每个用户的 `Page` `View` 信息给存入 `Kafka` ，此时我们会很自然地想到以 `userId` 来作为 `key` 。理想情况下这种选择可能是不会错的，但如果假设有一个用户是一个爬虫用户，他个人的访问量可能是正常用户的百倍甚至千倍，这个时候你会发现，虽然 `userId` 作为 `key` 而言，它是均匀分布的，但其背后的数据量却并不一定是均匀分布的，久而久之，就可能产生`数据倾斜`的情况，导致各个`partition`数据量分布不均匀。当然对于 `Kafka` 自身而言，一个`Partition`里有再多的数据，也不会去影响到它的正常性能。但没有特殊需求时，在选择 `key` 的时候，还是要考虑到这种情况的发生。

### 如何选择 `Partiton` 的数量

在创建 `topic` 的时候可以指定 `partiton` 数量，也可以在常见完后手动修改。但`partiton` 数量只能增加不能减少。中途增加`partiton`会导致各个`partition`之间数据量的不平等。
`Partition` 的数量直接决定了该 `Topic` 的并发处理能力。但也并不是越多越好。`Partition` 的数量对消息延迟性会产生影响。
一般建议选择 `broker` `num` * `consumer` `num` ，这样平均每个 `consumer` 会同时读取`broker`数目个 `partition` , 这些 `partiton` 压力可以平摊到每台 ``broker`` 上。



## Reference
https://www.cnblogs.com/sujing/p/10960832.html
https://blog.csdn.net/suifeng3051/article/details/48053965
https://segmentfault.com/a/1190000003922549
https://kafka.apachecn.org/
http://kafka.apache.org/documentation/#introduction
https://segmentfault.com/a/1190000015886371
https://lotabout.me/2018/kafka-introduction/
https://guobinhit.blog.csdn.net/article/details/106745689
https://jifei-yang.blog.csdn.net/article/details/111927749
https://blog.csdn.net/a3125504x/article/details/108181309

https://blog.csdn.net/u013256816/article/details/71091774/

