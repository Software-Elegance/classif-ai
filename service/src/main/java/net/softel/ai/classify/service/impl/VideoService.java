package net.softel.ai.classify.service.impl;

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
import com.github.kokorin.jaffree.ffmpeg.Frame;
import com.github.kokorin.jaffree.ffmpeg.FrameConsumer;
import com.github.kokorin.jaffree.ffmpeg.FrameOutput;
import com.github.kokorin.jaffree.ffmpeg.Stream;
import com.github.kokorin.jaffree.ffmpeg.UrlInput;

import javax.imageio.ImageIO;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;



import java.util.concurrent.TimeUnit;
import org.springframework.http.MediaType;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.util.ArrayList;
import java.io.IOException;
import java.util.stream.Collectors;
import net.softel.ai.classify.dto.PredictSuite;
import net.softel.ai.classify.service.IPredict;
import net.softel.ai.classify.dto.SummarizeVideo;
import java.math.BigDecimal;
import net.softel.ai.classify.service.IVideo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service("videoService")
public class VideoService implements IVideo {

    private Logger log = LoggerFactory.getLogger(this.getClass().getName());

    private static final String FORMAT = "classpath:mp4/%s.mp4";

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    IPredict predictService;

    FrameConsumer frameConsumer =  new FrameConsumer() {
        private long num = 1;

        @Override
        public void consumeStreams(List streams) {
            // All stream type except video are disabled. just ignore
            }

        @Override
        public void consume(Frame frame) {
            // End of Stream
            if (frame == null) {
                return;
                }

            try {
                String dir = "frames/rtsp/";    //needs to be dynamic
                Path rtspFramesDir = Paths.get(dir);

                String filename = "frame_" + num++ + ".png";
                Path output = rtspFramesDir.resolve(filename);
                ImageIO.write(frame.getImage(), "png", output.toFile());
                } 
            catch (Exception e) {
                e.printStackTrace();
                }
            }
        };


    public Mono<Resource> getVideo(String title) {
        return Mono.fromSupplier(() -> this.resourceLoader.getResource( String.format(FORMAT, title)));
        }

    public ResponseEntity<StreamingResponseBody> liveStream(String rtspUrl){
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(os -> {
                    FFmpeg.atPath()
                            .addArgument("-re")         //Read input at native frame rate. This is equivalent to setting -readrate 1.
                            .addArguments("-acodec", "pcm_s16le")       //do we need the audio codec ? 
                            .addArguments("-rtsp_transport", "tcp")
                            .addArguments("-i", rtspUrl)
                            .addArguments("-vcodec", "copy")        //Set the video codec
                            /*
                            asetrate: Set the sample rate without altering the PCM data. This will result in a change of speed and pitch.
                            atempo: adjusts audio tempo.
                            aresample: Resample the input audio 
                            */
                            .addArguments("-af", "asetrate=22050")  
                            .addArguments("-acodec", "aac")             //another audio codec ?
                            .addArguments("-b:a", "96k" )               //audio bit rate. do we need this  ?

                            .addOutput(PipeOutput.pumpTo(os)
                                    .disableStream(StreamType.AUDIO)
                                    .disableStream(StreamType.SUBTITLE)
                                    .disableStream(StreamType.DATA)
                                    // No more then 100 frames
                                    .setFrameCount(StreamType.VIDEO, 100L)
                                    //1 frame every 10 seconds
                                    .setFrameRate(0.1)       
                                    //Exit after ffmpeg has been running for duration seconds in CPU user time. Doesnt sound good    
                                    .setDuration(1, TimeUnit.HOURS)
                                    .setFormat("ismv"))     //Smooth Streaming muxer
                            .addArgument("-nostdin")
                            .execute();
                    });
        }


    public ResponseEntity<VideoSummary> summarizeVideo(SummarizeVideo summary) {

        String title = summary.getTitle();

        int stepInSeconds = summary.getStepInSeconds(); //           every x seconds
        Float videoDurationInSeconds = new Float("0");    //Length of the video. TODO use ffprobe (via jeffery library)

        String framesDir = "frames/samplevideo/";    //needs to be dynamic
        log.info("framesDir=" + framesDir);

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


        videoDurationInSeconds = Common.getVideoDuration(summary.getFilePath());
        log.info("videoDurationInSeconds = {}", videoDurationInSeconds);

        //extract frames in intervals
        for(int i = 0; i < videoDurationInSeconds ; i += stepInSeconds) {

            long millis = (i * 1000) % 1000;
            long second = i % 60;
            long minute = (i / 60) % 60;
            long hour = (i / 3600) % 24;

            String time = String.format("%02d:%02d:%02d.%03d", hour, minute, second, millis);

            log.info("step:{}, time:{}", i, time);

             FFmpeg.atPath()
                    .addArguments("-i", summary.getFilePath())
                    .addArguments("-ss", time)
                    .addArguments("-frames:v", "1")
                    .addArgument(framesDir + "pic-" + time + ".jpeg")
                    .addArgument("-nostdin")

                .execute();

            }


        //The source directory
        List<FrameIntel> frameList = new ArrayList<FrameIntel>();

        // Reading only files in the directory

        try {
            List<File> files = Files.list(Paths.get(framesDir))
                .map(Path::toFile)
                .filter(File::isFile)
                .collect(Collectors.toList());


            files.forEach(x->{

                log.info("Predicting file :{}", x);

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

     

        return ResponseEntity.ok(

            VideoSummary.builder()
                .title(title)
                .fileName(title)
                .frames(frameList)
                .build()

            );


    }


    //To be tested
    public ResponseEntity<StreamingResponseBody> liveStreamFrameOutput(String rtspUrl){

        String dir = "frames/rtsp/";    //needs to be dynamic
        log.info("rtspFramesDir={}",dir);

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
                                    .setDuration(1, TimeUnit.HOURS)     //should we remove this ? we want to stream indefinitely
                                    .setFormat("ismv"))
                        
                            .addOutput(FrameOutput
                                    .withConsumer(frameConsumer)
                                    // No more then 100 frames
                                    .setFrameCount(StreamType.VIDEO, 100L)
                                    // 1 frame every 10 seconds
                                    .setFrameRate(0.1)
                                    // Disable all streams except video
                                    .disableStream(StreamType.AUDIO)
                                    .disableStream(StreamType.SUBTITLE)
                                    .disableStream(StreamType.DATA)
                            )
                        
                            .addArgument("-nostdin")
                            .execute();
                    });
        }
}