























## 2.常见错误

### 2.1.[Ask timed out](https://github.com/spark-jobserver/spark-jobserver/issues/1057)

```log
{
"status": "ERROR",
"result": {
"message": "Ask timed out on [Actor[akka.tcp://JobServer@127.0.0.1:56705/user/jobManager-f8-9b1c-3d2f747a609b#-2122314191]] after [30000 ms]. Sender[null] sent message of type "spark.jobserver.JobManagerActor$StartJob".",
"errorClass": "akka.pattern.AskTimeoutException",
"stack": "akka.pattern.AskTimeoutException: Ask timed out on [Actor[akka.tcp://JobServer@127.0.0.1:56705/user/jobManager-f8-9b1c-3d2f747a609b#-2122314191]] after [30000 ms]. Sender[null] sent message of type "spark.jobserver.JobManagerActor$StartJob".\n\tat akka.pattern.PromiseActorRef$$anonfun$1.apply$mcV$sp(AskSupport.scala:604)\n\tat akka.actor.Scheduler$$anon$4.run(Scheduler.scala:126)\n\tat scala.concurrent.Future$InternalCallbackExecutor$.unbatchedExecute(Future.scala:601)\n\tat scala.concurrent.BatchingExecutor$class.execute(BatchingExecutor.scala:109)\n\tat scala.concurrent.Future$InternalCallbackExecutor$.execute(Future.scala:599)\n\tat akka.actor.LightArrayRevolverScheduler$TaskHolder.executeTask(LightArrayRevolverScheduler.scala:331)\n\tat akka.actor.LightArrayRevolverScheduler$$anon$4.executeBucket$1(LightArrayRevolverScheduler.scala:282)\n\tat akka.actor.LightArrayRevolverScheduler$$anon$4.nextTick(LightArrayRevolverScheduler.scala:286)\n\tat akka.actor.LightArrayRevolverScheduler$$anon$4.run(LightArrayRevolverScheduler.scala:238)\n\tat java.lang.Thread.run(Thread.java:745)\n"
}
}
```

















### Reference

- https://www.cnblogs.com/yueminghai/p/10413171.html

- https://github.com/spark-jobserver/spark-jobserver

