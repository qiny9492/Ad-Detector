package AnalyzeAd;

import java.util.ArrayList;
import java.util.List;

public class SoundAmplitude {
    private double getAvg(List<Double> data) {
        double sum = 0;

        for( double value : data ) {
            sum += value;
        }

        return sum/data.size();
    }

//    private double getAvg(double[] audio) {
//        for( int a :)
//    }

//    private  double getAvg(List<int[]> audioFrames, int start, int end) {
//        double sum = 0;
//
//
//        for( int i = start; i <= end; i++ ) {
//            double[] audio = audioFrames.get(i);
//            double s = 0;
//
//            for( double v : audio ) {
//                s += v;
//            }
//            sum += s/audio.length;
//        }
//        return sum/audioFrames.size();
//    }


//    public double getAllVariance(List<int[]> audioFrames, int start, int end) {
//        int count = 0;
//        double sum = 0;
//        double avg = getAvg(audioFrames, start, end);
////        System.out.println("avg "+avg);
//        for( int i = start; i <= end; i++ ) {
//            double[] audio = audioFrames.get(i);
//
//            for( double v : audio ) {
//                System.out.println("hhh "+(avg - v));
//                sum += Math.pow((avg - v), 2);
//            }
//        }
//        return sum/count;
//    }

    public float getVariance(List<Double> RMS, int start, int end, double avg) {
        float sum = 0;
        for( int i = 0; i < RMS.size(); i++ ) {
            sum += Math.pow((avg - RMS.get(i)), 2);
        }
//        System.out.println(sum+"    "+RMS.size()+"  "+avg);
        return sum/RMS.size();
    }

    public double getAvgRMS(List<Double> RMS) {
       return getAvg(RMS);
    }

    public List<Double> getAllRMSES(List<long[]> audioFrames, int start, int end) {
        return getRMS(audioFrames, start, end);
    }

    public List<Double> getRMS(List<long[]> audioFrames, int start, int end) {
        List<Double> res = new ArrayList<>();

        for(int i = start; i < end; i++ ) {
            long[] audio = audioFrames.get(i);
            res.add(getRMSHelper(audio));
        }
        return res;
    }


    private double getRMSHelper(long[] audio) {
        long sum_square = 0;

        for( long v :audio ) {
            sum_square += v*v;
        }

        return Math.sqrt(sum_square/audio.length);
    }
}
