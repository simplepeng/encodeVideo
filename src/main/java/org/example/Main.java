package org.example;

import org.bytedeco.javacv.FFmpegFrameRecorder;

public class Main {
    public static void main(String[] args) {
        String path = "";
        int width = 0;
        int height = 0;
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(path, width, height);
    }
}