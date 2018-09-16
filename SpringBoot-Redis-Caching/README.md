## Spring Boot Caching With Redis
                                                                            By Jason/GeW

This is a comprehensive basic usage for Spring Boot and Redis...


* Provide RedisTemplate config for serialize/deserialize object in JSON format; 
entities also implemented JDK Serializable interface for basic Object serialization/deserialization (Binary) with Redis.

* Provide REST API for basic Redis CRUD operations with String key and value (String and SET), 
provide GET operation with type (and index range for LIST and ZSET) config for retrieving different type of Redis Data.

* Provide Spring Caching with Redis type, 
direct caching (@Catchable, @CachePut and @CacheEvict) within Spring Persistence service.

* Provide sample "User Information Management" with MySQL + Redis as use case.

* Provide Swagger API Web UI as REST API doc.



`->` This project will be under continuous development in order to add more functionality.

