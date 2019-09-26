# instant-kill
### **秒杀系统**

springboot + redis + mysql + rabbitMq

### **秒杀接口优化：目的减少数据库访问**
（1）系统初始化时把库存数加载到redis中；  
（2）收到请求redis预减库存，redis库存0时直接返回秒杀失败，大于0时进入（3）；  
（3）MQ入队，立即返回排队中；  
（4）MQ出队，生成订单、减少库存；  
（5）客户端轮询，是否秒杀成功（异步了：请求时不知道成功失败，另起查询才知道）。  


### **MQ交换机四种模式：direct、topic、fanout、headers**

### **GET/POST区别：GET是幂等的，获取服务端数据，执行多次结果不变，POST向服务端提交数据**