# Web Flux Rest API in action :rocket: 
Implementing a Rest API using Spring Web Flux

Spring Web Flux is a fully non-blocking web framework on top of Spring Reactor project
By default it runs on Netty. 

## Usage
Java 8 > is required 

* Run the CB server
```
 ./bin/cbRunLocally.sh start
```

* Run the Application

## Things to remember - Notes
* We still use Controller annotations for mapping request to responses 
* Controller returned Mono<ResponseEntity<?>>. 
Note: we have 2 options when we use Controllers - either Mono<ResponseEntity<>> or Mono<Domain>/Flux<Domain>. 
If you use Controllers together with ServerResponse u get error
* if you use any blocking operation in your nio thread => IllegalStateException
  e.g given a Mono<Book> feed you do feed.block()
  or given a Flux<Book> feed you do feed.toIterable/toStream  
* if you try to subscribe to any pipeline you build => error some things like reading from inputstream can't happen twice 
* For debugging you can use log/onNextElement
