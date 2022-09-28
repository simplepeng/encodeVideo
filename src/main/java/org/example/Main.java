package org.example;

import org.bytedeco.javacv.FFmpegFrameRecorder;

import java.io.File;
import java.util.List;

public class Main {
    public static void main(String[] args) throws FFmpegFrameRecorder.Exception {
        File root = new File("");
        System.out.println("root = " + root.getAbsolutePath());
        File imgDir = new File(root.getAbsolutePath(), "imgs");
        System.out.println("imgDir = " + imgDir.getAbsolutePath());

        File[] files = imgDir.listFiles();
        if (files == null || files.length < 1) return;
    }

    public static void encodeToVideo(File root) {

    }

    public static void encodeToVideo(List<File> files, String outPath) throws Throwable {
        int width = 0;
        int height = 0;
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outPath, width, height);
        recorder.release();
    }
}