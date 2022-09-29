package org.example;

import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.List;

//https://github.com/bytedeco/javacv
public class Main {
    public static void main(String[] args) throws FFmpegFrameRecorder.Exception {
        File root = new File("");
        System.out.println("root = " + root.getAbsolutePath());

        File imgDir = new File(root.getAbsolutePath(), "files" + File.separator + "imgs");
        System.out.println("imgDir = " + imgDir.getAbsolutePath());

        File[] files = imgDir.listFiles();
        if (files == null || files.length < 1) return;

        //临时的适配文件
        File tmpFile = new File(root.getAbsoluteFile(), "files" + File.separator + "tmp.mp4");
        if (tmpFile.exists()) {
            tmpFile.delete();
        }
        //音频文件
        File audioFile = new File(root.getAbsoluteFile(), "files" + File.separator + "audio.mp3");
        //合成完成的视频文件
        File outFile = new File(root.getAbsoluteFile(), "out.mp4");
        if (outFile.exists()) {
            outFile.delete();
        }

        //视频的宽高
        int width = 540;
        int height = 960;

        try {
            encodeToVideo(Arrays.stream(files).toList(), tmpFile, width, height, audioFile);
//            mixer(tmpFile, audioFile, outFile);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void encodeToVideo(List<File> files, File outFile, int width, int height,
                                     File audioFile) throws Throwable {
        FFmpegFrameGrabber audioGrabber = new FFmpegFrameGrabber(audioFile);
        audioGrabber.start();

        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outFile.getAbsolutePath(), width, height);
        recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
        recorder.setFrameRate(25);
        recorder.setPixelFormat(avutil.AV_PIX_FMT_YUV420P);
        recorder.setSampleRate(audioGrabber.getSampleRate());
        recorder.setAudioChannels(audioGrabber.getAudioChannels());
        recorder.setFormat("mp4");

        recorder.start();
        Java2DFrameConverter converter = new Java2DFrameConverter();

        Frame audioFrame = null;

        int frameDuration = 2;//每张图片停留的秒速
        for (File file : files) {
            BufferedImage read = ImageIO.read(file);
            for (int i = 0; i < 25 * frameDuration; i++) {
                recorder.record(converter.getFrame(read));
                audioFrame = audioGrabber.grabFrame();
                if (audioFrame != null) {
                    recorder.record(audioFrame);
                }else {
                    recorder.record(audioFrame);
                }
            }
        }

        audioGrabber.stop();
        audioGrabber.release();
        recorder.stop();
        recorder.release();
    }

    public static void mixer(File videoFile, File audioFile, File outVideoFile) throws Throwable {
        if (!videoFile.exists() || !audioFile.exists()) {
            return;
        }

        FFmpegFrameGrabber videoGrabber = new FFmpegFrameGrabber(videoFile);
        FFmpegFrameGrabber audioGrabber = new FFmpegFrameGrabber(audioFile);

        videoGrabber.start();
        audioGrabber.start();

        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outVideoFile,
                videoGrabber.getImageWidth(), videoGrabber.getImageHeight(),
                audioGrabber.getAudioChannels());

        recorder.setFrameRate(videoGrabber.getFrameRate());
        recorder.setSampleRate(audioGrabber.getSampleRate());
        recorder.setFormat("mp4");
        recorder.start();

        //开始合并
        Frame frame = null;
        while ((frame = videoGrabber.grabFrame()) != null) {
            recorder.record(frame);
        }
        while ((frame = audioGrabber.grabFrame()) != null) {
            recorder.record(frame);
        }

        videoGrabber.stop();
        audioGrabber.stop();
        recorder.stop();

        videoGrabber.release();
        audioGrabber.release();
        recorder.release();
    }
}