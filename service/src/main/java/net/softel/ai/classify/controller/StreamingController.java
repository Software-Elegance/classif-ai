// package net.softel.ai.classify.controller;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;
// import org.springframework.http.MediaType;

// // import io.swagger.v3.oas.annotations.Operation;
// // import io.swagger.v3.oas.annotations.tags.Tag;

// import lombok.extern.slf4j.Slf4j;

// import net.softel.ai.classify.service.IPredict;
// import net.softel.ai.classify.dto.PredictSuite;

// import javax.validation.Valid;
// import reactor.core.publisher.Flux;
// import reactor.core.publisher.Sinks;

// import java.time.Duration;
// import java.util.concurrent.atomic.AtomicInteger;
// import java.util.concurrent.atomic.AtomicLong;

// @Slf4j
// @RestController
// @RequestMapping("/stream")
// // @Tag(name = "Train", description = "Slack request and callbacks")
// public class StreamingController {


//     final Sinks.Many sink;
//     final AtomicLong counter;

//     String[] str = new String[]{"A", "B", "C"};
//     Flux<String> fluxString = Flux.fromArray(str);


//     @GetMapping("/add")
//     public void add() {
//         Sinks.EmitResult result = sink.tryEmitNext("One More #" + counter.getAndIncrement());
//         if (result.isFailure()) {
//             System.out.println("Failed...");
//             }
//         }

//     @RequestMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//     public Flux<String> sse() {
//         return sink.asFlux().map(e -> new String(e + "..."));
//         }

//     // @RequestMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//     // public Flux<ServerSentEvent> sse() {
//     //     return sink.asFlux().map(e -> ServerSentEvent.builder(e).build());
//     //     }


//     @GetMapping(value = "/flux", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
//     public Flux<String> streamJsonObjects() {
//         // return Flux.interval(Duration.ofSeconds(5))
//         //     .map(i -> new String("New line..."));
//         return fluxString;
        
//         }
    
//     }