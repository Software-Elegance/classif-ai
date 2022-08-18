package net.softel.ai.classify.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import net.softel.ai.classify.util.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ai.djl.util.Utils;
import net.softel.ai.classify.dto.response.VideoSummary;
import net.softel.ai.classify.dto.response.FrameIntel;
import com.github.kokorin.jaffree.StreamType;
import com.github.kokorin.jaffree.ffmpeg.FFmpeg;
import com.github.kokorin.jaffree.ffmpeg.PipeOutput;
import java.util.concurrent.TimeUnit;
import org.springframework.http.MediaType;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.util.stream.Collectors;
import net.softel.ai.classify.dto.PredictSuite;
import net.softel.ai.classify.service.IPredict;
import net.softel.ai.classify.dto.SummarizeVideo;


@Service("videoService")
public class VideoService implements IVideo {

    private static final String FORMAT = "classpath:mp4/%s.mp4";

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    IPredict predictService;

    public Mono<Resource> getVideo(String title) {
        return Mono.fromSupplier(() -> this.resourceLoader.getResource( String.format(FORMAT, title)));
    }

    public ResponseEntity<StreamingResponseBody> livestream(String rtspUrl){
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(os -> {
                    FFmpeg.atPath()
                            .addArgument("-re")
                            .addArguments("-acodec", "pcm_s16le")
                            .addArguments("-rtsp_transport", "tcp")
                            .addArguments("-i", rtspUrl)
                            .addArguments("-vcodec", "copy")
                            .addArguments("-af", "asetrate=22050")
                            .addArguments("-acodec", "aac")
                            .addArguments("-b:a", "96k" )
                            .addOutput(PipeOutput.pumpTo(os)
                                    .disableStream(StreamType.AUDIO)
                                    .disableStream(StreamType.SUBTITLE)
                                    .disableStream(StreamType.DATA)
                                    .setFrameCount(StreamType.VIDEO, 100L)
                                    .setFrameRate(0.1)              //1 frame every 10 seconds
                                    .setDuration(1, TimeUnit.HOURS)
                                    .setFormat("ismv"))
                            .addArgument("-nostdin")
                            .execute();
                    });
        }



    public ResponseEntity<VideoSummary> summarizeVideo(SummarizeVideo summary) {

        String title = summary.getTitle();

        int stepInSeconds = summary.getStepInSeconds(); //           every x seconds
        int videoDurationInSeconds = 13;    //Length of the video. TODO use ffprobe (via jeffery library)

        String framesDir = "frames/samplevideo/";    //needs to be dynamic
        System.out.println("framesDir=" + framesDir);


        VideoSummary ex = VideoSummary.builder()
                .title(title)
                .fileName(title)
                .build();

        try{
            Path dir = Paths.get(framesDir);
            Utils.deleteQuietly(dir);
            Files.createDirectories(dir);
            }
        catch (IOException e){
            return ResponseEntity.ok(ex);
            }

        for(int i = 0; i < videoDurationInSeconds ; i += stepInSeconds) {

            long millis = (i * 1000) % 1000;
            long second = i % 60;
            long minute = (i / 60) % 60;
            long hour = (i / 3600) % 24;

            String time = String.format("%02d:%02d:%02d.%03d", hour, minute, second, millis);

            System.out.println("step:" + i + ", time:" + time);

             FFmpeg.atPath()
                            // .addArguments("-i", "/Users/zeguru/Projects/Softel/ML/classif-ai/service/src/main/resources/mp4/vid.mp4")
                            // .addArguments("-ss", "00:00:5.000")
                            .addArguments("-i", summary.getFilePath())
                            .addArguments("-ss", time)
                            .addArguments("-frames:v", "1")
                            .addArgument(framesDir + "pic-" + time + ".jpeg")
                            .addArgument("-nostdin")
                        .execute();

            }

        // return ResponseEntity.ok()
        //         .contentType(MediaType.TEXT_PLAIN)
        //         .body(os -> {
       
                    // });


                //The source directory
                List<FrameIntel> frameList = new ArrayList<FrameIntel>();

                // Reading only files in the directory
                try {
                    List<File> files = Files.list(Paths.get(framesDir))
                        .map(Path::toFile)
                        .filter(File::isFile)
                        .collect(Collectors.toList());


                    files.forEach(x->{

                        System.out.println("Predicting file :" + x);


                        PredictSuite suite = PredictSuite.builder()
                            .imagePath(x.getPath())
                            .imageSource("LOCAL")
                            .title("Testting at ...." + System.currentTimeMillis())
                            .neuralNetwork("RESNET_50")
                            .classes("buildings,forest,glacier,mountain,sea,street")
                            .modelName("intelModel")
                            .modelDirectory("models/intelModelDir")
                            .batchSize(32)
                            .imageHeight(150)
                            .imageWidth(150)
                        .build();

                        String prediction = predictService.predictBest(suite);
                        
                        String encodstring = "";

                        if(summary.getShowImage()){
                            encodstring = Common.encodeFileToBase64Binary(x);
                            }

                        frameList.add(FrameIntel.builder()
                                .imageName(x.getName())
                                .imageBase64(encodstring)
                                .timestamp(x.getName().substring(4,16))
                                .classPredictions(prediction)
                                .build()
                                );

                        });
                    } 
                catch (Exception e) {
                    return ResponseEntity.ok(ex);
                    }

     
        VideoSummary sum = VideoSummary.builder()
                .title(title)
                .fileName(title)
                .frames(frameList)
                .build();

        //return ResponseEntity.ok("{}");
        return ResponseEntity.ok(sum);


    }

}