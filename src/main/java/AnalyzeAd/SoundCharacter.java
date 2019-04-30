package AnalyzeAd;

import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import readFiles.PlaySound;

import javax.sound.sampled.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import Math.MathCal;

///Users/yangtian/Downloads/dataset/Videos/data_test1.wav
///Users/yangtian/Downloads/dataset/Ads/Starbucks_Ad_15s.wav


///Users/yangtian/Downloads/dataset2/Videos/data_test2.wav
public class SoundCharacter {
    private AudioInputStream audioInputStream;
    private final int EXTERNAL_BUFFER_SIZE = 3200;
    private byte[] audioBuffer;
    private List<Integer> audioFrames =  new ArrayList<>();
    private MathCal mathCal = new MathCal();


    private int getAmp(byte b0, byte b1) {
        return(((b1 & 0xff) << 8) | (b0 & 0xff));
    }


    public boolean isAd(int start, int end) {
        end = Math.min(audioFrames.size(), end);
        double entropy = mathCal.getEntropy(audioFrames, start, end);
//        System.out.println("sound entropy   "+entropy+"     indx  "+idx);

        if( entropy > 7 ) {
            return false;
        } else {
            return true;
        }
//        SoundAmplitude soundAmplitude = new SoundAmplitude();

//        int[][] shots = {{0, 606}, {607, 1179}, {1180, 2240}, {2241, 2435}, {2436, 2515},
//                        {2516, 2596}, {2597, 2659}, {2660, 2695}, {2696, 2748}, {2749, 2803}, {2804, 2838},
//                {2839, 2850}, {2851, 3630}, {3631, 4350}, {4351, 5550}, {5551, 5612}, {5613, 5699}, {5700, 5846}, {5847, 5925},
//                {5926, 6000}, {6001, 6450}, {6451, audioFrames.size()}};


//        int[][] shots = {{0, 13}, {14, 34}, {35, 117}, {118, 267}, {268, 450}, {451, 1849}, {1850, 2013},
//                {2014, 3018}, {3019, 4008}, {4009, 5548}, {5549, 6000}, {6001, 6056}, {6057, 6082}, {6083, 6118},
//                {6119, 6155}, {6156, 6197}, {6198, 6232}, {6233, 6274}, {6275, 6304}, {6305, 6336}, {6337, 6387},{6388, 6411},{6412, 6450},
//                {6451, 6588}, {6589, 7939}, {7940, audioFrames.size()}};


//        int i = 0;
//        double prev = 0;

//        for( int[] shot : shots ) {
////            List<Double> rmses = soundAmplitude.getAllRMSES(audioFrames, shot[0], shot[1]);
////            double avgRMS = soundAmplitude.getAvgRMS(rmses);
////            double variance = soundAmplitude.getVariance(rmses, shot[0], shot[1], avgRMS);
////
////            double deviation = Math.sqrt(variance);
////            double max = avgRMS + deviation;
////            double min = avgRMS - deviation;
////
////            int count = 0;
////
////            for( double rms : rmses ) {
//////                System.out.println(avgRMS+" "+variance+"    "+rms);
////                if( rms > max || rms < min ) count++;
////            }
////
////            double cur = count*1.0/rmses.size();
////            double diff = cur - prev;
////            prev = cur;
//
//            System.out.println("  index "+i +" entropy "+mathCal.getEntropy(audioFrames, shot[0], shot[1]));
//
////            if( diff > 0.1 ) {
////                System.out.println(diff+"   "+i);
////            }
//
//            i++;
//        }
    }

    private List<Integer> Amplitudes(byte[] bytes) {
        List<Integer> list = new ArrayList<>();

        for( int i = 0; i < bytes.length; i += 2 ) {
            list.add(getAmp(bytes[i], bytes[i+1]));
        }
        return list;
    }

    public SoundCharacter(String fileName) {
        try {
            audioBuffer = new byte[EXTERNAL_BUFFER_SIZE];

            FileInputStream inputStream = new FileInputStream(fileName);
            InputStream bufferedIn = new BufferedInputStream(inputStream);

            audioInputStream = AudioSystem.getAudioInputStream(bufferedIn);

            int readBytes = 0;
            while ( readBytes != -1 ) {
                readBytes = audioInputStream.read(audioBuffer, 0, audioBuffer.length);
                List<Integer> data = Amplitudes(audioBuffer);
                int amplitude = mathCal.getAvgLong(data, 0, data.size());

                audioFrames.add(amplitude);
            }

//            System.out.println("hhh" + audioFrames.size());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        SoundCharacter soundCharacter = new SoundCharacter(args[0]);
//
//        soundCharacter.Amplitude();


//        System.out.println(soundCharacter.audioInputStream.getFormat().getFrameRate()+"     "+soundCharacter.audioInputStream.getFormat().getFrameSize()+"  "+soundCharacter.audioInputStream.getFormat().getSampleRate());

//        try {
//            soundCharacter.FastFFT();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }
}
