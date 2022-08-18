package net.softel.ai.classify.util;

import ai.djl.training.dataset.Dataset;

import java.util.List;
import java.util.Random;
import ai.djl.training.dataset.RandomAccessDataset;
import ai.djl.basicdataset.cv.classification.ImageFolder;
import net.softel.ai.classify.dto.TrainingSuite;

import ai.djl.modality.cv.transform.Resize;
import ai.djl.modality.cv.transform.Crop;
import ai.djl.modality.cv.transform.CenterCrop;
import ai.djl.modality.cv.transform.RandomFlipTopBottom;
import ai.djl.modality.cv.transform.RandomFlipLeftRight;
import ai.djl.modality.cv.transform.RandomResizedCrop;
import ai.djl.modality.cv.transform.RandomBrightness;
import ai.djl.modality.cv.transform.RandomColorJitter;
import ai.djl.modality.cv.transform.RandomHue;
import ai.djl.modality.cv.transform.Normalize;
import ai.djl.modality.cv.transform.ToTensor;
import ai.djl.modality.cv.transform.Resize;
import ai.djl.modality.cv.transform.Crop;
import ai.djl.modality.cv.transform.CenterCrop;
import ai.djl.modality.cv.transform.RandomFlipTopBottom;
import ai.djl.modality.cv.transform.RandomFlipLeftRight;
import ai.djl.modality.cv.transform.RandomResizedCrop;
import ai.djl.modality.cv.transform.RandomBrightness;
import ai.djl.modality.cv.transform.RandomColorJitter;
import ai.djl.modality.cv.transform.RandomHue;
import ai.djl.modality.cv.transform.Normalize;
import ai.djl.modality.cv.transform.ToTensor;

import ai.djl.translate.TranslateException;
import java.io.IOException;
import ai.djl.translate.Pipeline;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.FileInputStream;
import org.apache.commons.codec.binary.Base64;
import java.io.File;

import com.github.kokorin.jaffree.ffprobe.FFprobe;
import com.github.kokorin.jaffree.ffprobe.FFprobeResult;
import com.github.kokorin.jaffree.ffprobe.Stream;
import java.math.BigDecimal;

public class Common {

    private static Logger log = LoggerFactory.getLogger(Common.class);

    public static String encodeFileToBase64Binary(File file) {
        try{
            FileInputStream fileInputStreamReader = new FileInputStream(file);
            byte[] bytes = new byte[(int)file.length()];
            fileInputStreamReader.read(bytes);
            return new String(Base64.encodeBase64(bytes), "UTF-8");
            }
        catch(Exception e){
            return "";
            }
            
        }


    public static Float getVideoDuration(String pathToVideo) {

        Float duration = new Float("0");

        FFprobeResult result = FFprobe.atPath()
                .setShowStreams(true)
                .setInput(pathToVideo)
                .execute();

        //ffprobe -i vid.mp4 -v quiet -show_entries format=duration -hide_banner -of default=noprint_wrappers=1:nokey=1

        for (Stream stream : result.getStreams()) {
            System.out.println(
                    "Stream #" + stream.getIndex()
                    + " type: " + stream.getCodecType()
                    + " duration: " + stream.getDuration() + " seconds"
                    );
                duration = stream.getDuration();
                }

        return duration;
        }
}