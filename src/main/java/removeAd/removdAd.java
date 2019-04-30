package removeAd;

import AnalyzeAd.DetectAd;

import DetectLogo.DetectLogo;
import DetectLogo.MarkedImage;
import com.sun.media.sound.AiffFileWriter;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;
import shot.Shot;

import javax.sound.sampled.*;
import javax.sound.sampled.spi.AudioFileWriter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.List;

public class removdAd {
    private static boolean ifDetectLogo = false;
    private static Queue<MarkedImage> marked;

    private static Queue<AudioInputStream> audioQueue = new LinkedList<>();
    private static Queue<InputStream> videoQueue = new LinkedList<>();

    private static  int WIDTH = 480;
    private static int HEIGHT = 270;

    private static boolean ifAddBox = false;
    private static int boxFrames = 0;

    private static int upperX;
    private static int upperY;
    private static int boxWidth;
    private static int boxHeight;


    public static void main(String[] args) {
//        /Users/yangtian/Downloads/dataset/Videos/data_test1.rgb /Users/yangtian/Downloads/dataset/Videos/data_test1.wav
//        /Users/yangtian/576final/removedVideo.rgb /Users/yangtian/576final/removedVideo.wav
//        /Users/yangtian/Downloads/dataset2/Videos/data_test2.rgb /Users/yangtian/Downloads/dataset2/Videos/data_test2.wav
//        arg[0]: fileName, arg[1]: audio name

        long startTime=System.currentTimeMillis();

        String vfilename = args[0];
        String afilename = args[1];

        Queue<Shot> adShots = null;

        try {
            adShots = DetectAd.detect(args[0], args[1]);

            long endTime1=System.currentTimeMillis();
            System.out.println("endTime1 "+ (endTime1-startTime)*1.0/1000);
        } catch (Exception e) {
            e.printStackTrace();
        }

//        adShots = new LinkedList<>();
//        adShots.offer(new Shot(2400, 2850, 10));
//        adShots.offer(new Shot(5550, 6000, 10));

        if( args[2].equals( "1" ) ) {
//            1 remove ad
            DetectLogo detect = new DetectLogo(args[3],vfilename, new LinkedList<Shot>(adShots));//args[3] logo path
            ifDetectLogo = true;
            try {
                detect.Detecting();
                marked = detect.getIndexAndImage();

                Queue<String> logoList = detect.getLogos();
                long endTime2=System.currentTimeMillis();
                System.out.println("endTime2 "+ (endTime2-startTime)*1.0/1000);

                getAddedAd(logoList,args[4]);//ad Path

                long endTime3=System.currentTimeMillis();
                System.out.println("endTime3 "+ (endTime3-startTime)*1.0/1000);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        writeAudio(afilename, adShots);

        long endTime4=System.currentTimeMillis();
        System.out.println("endTime4 "+ (endTime4-startTime)*1.0/1000);

        writeVideo(vfilename,adShots);

        long endTime5=System.currentTimeMillis();
        System.out.println("endTime5 "+ (endTime5-startTime)*1.0/1000);
    }


    public static void writeAudio(String afilename, Queue<Shot> adShots) {
        List<File> audioSegmentFiles = new ArrayList<>();

        double frameTosample = 1.0/30*48000;
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(afilename));
            long AudioSize = audioInputStream.getFrameLength();

            long readSize = 0;
            long audioSizwWithoutAd = 0;
            long addAudioAdSize = 0;

            int idx = 0;

            for( Shot shot : adShots ) {
                int start = shot.getStart();
                int end = shot.getEnd();

                // write audio before shot
                long length = (long)((start * frameTosample));
                File outputFile = new File(idx+".wav");

                AudioInputStream audioStream = new AudioInputStream(audioInputStream, audioInputStream.getFormat(), length - readSize);

                audioSizwWithoutAd += (length - readSize);
                readSize += (length - readSize);

//                System.out.println(shot.getStart()+"    "+audioInputStream.available()+"  jjo");
                AudioSystem.write(audioStream, AudioFileFormat.Type.WAVE, outputFile);

                // skip ad

                long m = audioInputStream.skip((long)((end - start) * frameTosample * 2));
                readSize += (m/2);
//                System.out.println("m   "+m);

                audioSegmentFiles.add(outputFile);

                idx++;

                if( ifDetectLogo && !audioQueue.isEmpty() ) {
                    // add ad audio to original audio position
                    AudioInputStream addedAd = audioQueue.poll();

                    outputFile = new File(idx+".wav");
                    AudioSystem.write(addedAd, AudioFileFormat.Type.WAVE, outputFile);

                    audioSizwWithoutAd += addedAd.getFrameLength();

                    audioSegmentFiles.add(outputFile);
                    idx++;
                }



            }

            File outputFile = new File(idx+".wav");
            AudioInputStream audioStream = new AudioInputStream(audioInputStream, audioInputStream.getFormat(), AudioSize - readSize);
            audioSizwWithoutAd += AudioSize - readSize;


            AudioSystem.write(audioStream, AudioFileFormat.Type.WAVE, outputFile);
            audioSegmentFiles.add(outputFile);

            Vector<AudioInputStream> outStreams = new Vector<>();
            for( File f : audioSegmentFiles) {
                outStreams.add(AudioSystem.getAudioInputStream(f));
            }

            Enumeration<AudioInputStream> audioInputStreamEnumeration = outStreams.elements();
            SequenceInputStream sequenceInputStream = new SequenceInputStream(audioInputStreamEnumeration);

            File combineFile = new File("removedVideo.wav");
//            System.out.println(audioSizwWithoutAd);
            AudioInputStream combinedStream = new AudioInputStream(sequenceInputStream, audioInputStream.getFormat(), audioSizwWithoutAd);
            AudioSystem.write(combinedStream, AudioFileFormat.Type.WAVE, combineFile);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeVideo(String vfilename, Queue<Shot> adShots) {
        long frameCount = 0;


        try {
            File file = new File(vfilename);
//            System.out.println(vfilename);
            InputStream is = new FileInputStream(file);

            File outFile = new File("./removedVideo.rgb");
            OutputStream out = new FileOutputStream(outFile);


            long len = WIDTH*HEIGHT*3;
            byte[] bytes = new byte[(int)len];

            for( Shot shot : adShots ) {
                System.out.println(shot.getStart()+" "+frameCount);

                while ( frameCount <= shot.getStart() ) {

                    int offset = 0;
                    int numRead = 0;

                    if( frameCount == shot.getStart()  ) {
                        int frames = shot.getEnd() - shot.getStart();
                        is.skip(bytes.length * frames);

                        if( ifDetectLogo && !videoQueue.isEmpty() ) {
                            InputStream inputStream = videoQueue.poll();

                            add(numRead, inputStream, bytes, out);
                        }

                        frameCount += frames;
                    } else {
                        if( ifDetectLogo && !marked.isEmpty() && frameCount == marked.peek().getIndex() ) {
                            MarkedImage markedImage = marked.poll();

                            while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
                                offset += numRead;
                            }

                            BufferedImage img = bytesToImage(bytes);
                            upperX = markedImage.getUpperX();
                            upperY = markedImage.getUpperY();
                            boxWidth = markedImage.getBoxWidth();
                            boxHeight = markedImage.getBoxHeight();

                            boxFrames = 10;
                            ifAddBox = true;

                            DrawBoundingBox(img, upperX, upperY, boxWidth, boxHeight);
                            out.write(BufferedImageToBytes(img));

                        } else {
                            while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
                                offset += numRead;
                            }

                            if( ifDetectLogo && ifAddBox ) {
                                BufferedImage img = bytesToImage(bytes);
                                DrawBoundingBox(img, upperX, upperY, boxWidth, boxHeight);
                                out.write(BufferedImageToBytes(img));
                                boxFrames--;

                                if( boxFrames == 0 ) ifAddBox = false;
                            } else {
                                out.write(bytes,0,  offset);
                            }
                        }

                        frameCount++;
                    }
                }
//
//                System.out.println(shot.getStart()+"    "+frameCount);
            }

        int numRead = 0;

//        System.out.println("2");
        while ( (numRead = is.read(bytes)) >= 0 ) {
            out.write(bytes,0,  numRead);
        }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


//    TODO: delete

    public static void add(int numRead, InputStream inputStream, byte[] bytes, OutputStream out) throws IOException {

        while ( (numRead = inputStream.read(bytes)) >= 0 ) {
            out.write(bytes,0,  numRead);
        }
    }


    public static void getAddedAd(Queue<String> logoList, String prePath) {

        try {
            for( String logo : logoList ) {
                logo = logo.trim();
                logo = logo.toLowerCase();

                String logoName = matchAdAudioAndVideoPath(prePath, logo);

                if( logoName == null ) continue;
//                    System.out.println(logo+logoName+prePath);
                File file = new File(prePath + logoName.replace(".wav", ".rgb"));
//                    System.out.println("jjj   "+prePath + logoName.replace(".wav", ".rgb"));

//                    System.out.println("kk   "+logo+logoName+prePath);
                videoQueue.offer(new FileInputStream(file));
                audioQueue.offer(AudioSystem.getAudioInputStream(new File(prePath+logoName)));
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static String matchAdAudioAndVideoPath(String preAudioPath, String logo) {
        File folder = new File(preAudioPath);

        for( File file : folder.listFiles() ) {
            String name = file.getName().toLowerCase();
//            System.out.println(name+"   "+name.contains(logo)+" "+name.endsWith(".wav"));
            if( file.isFile() && name.endsWith(".wav") && name.contains(logo) ) {
                return file.getName();
            }
        }
        return null;
    }


    public static byte[] BufferedImageToBytes(BufferedImage image) {

        byte[] imageBytes = new byte[image.getWidth() * image.getHeight() * 3];

        int idx = 0;
        for( int j = 0; j < image.getHeight(); j++ ) {
            for( int i = 0; i < image.getWidth(); i++ ) {
                int pixel = image.getRGB(i, j);


                byte r = Integer.valueOf(((pixel >> 16) & 0xff)).byteValue();
                byte g = Integer.valueOf((pixel >> 8) & 0xff).byteValue();
                byte b = Integer.valueOf((pixel) & 0xff).byteValue();

                int step = image.getHeight() * image.getWidth();
                imageBytes[idx] = r;
                imageBytes[idx + step] = g;
                imageBytes[idx + step * 2] = b;
                idx++;
            }
        }
        return imageBytes;
    }

    public static void DrawBoundingBox(BufferedImage img,int x, int y, int width, int height) {
        Graphics g = img.getGraphics();
        g.setColor(Color.RED);
        g.drawRect(x, y, width, height);// upper-left x, y, width, height

    }


    public static BufferedImage bytesToImage(byte[] bytes) {
        int ind = 0;

        BufferedImage img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);

        for(int y = 0; y < HEIGHT; y++){
            for(int x = 0; x < WIDTH; x++){
                byte r = bytes[ind];
                byte g = bytes[ind+HEIGHT*WIDTH];
                byte b = bytes[ind+HEIGHT*WIDTH*2];

                int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
                img.setRGB(x,y,pix);
                ind++;
            }
        }
        return img;
    }

}
