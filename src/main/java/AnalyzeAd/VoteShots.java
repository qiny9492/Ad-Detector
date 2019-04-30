package AnalyzeAd;

import java.util.*;
import Math.MathCal;
import shot.Shot;

public class VoteShots {

    private ArrayList<Integer> breaks;
    private Vector[] motionMtx;
    private int lastFrameIndex; // index start from 0 to 9000, numFrames = 9001
    private List<Integer> matchedCount;
    SoundCharacter soundCharacter;

    private List<Shot> adShots = new LinkedList<>();


    public VoteShots(ArrayList<Integer> breaks, Vector[] motionMatrix, int numFrames, List<Integer> matchedCount, String audioFileName) {
        this.breaks = breaks;
        this.motionMtx = motionMatrix;
        this.lastFrameIndex = numFrames - 1;
        this.matchedCount = matchedCount;
        this.soundCharacter = new SoundCharacter(audioFileName);

    }
    
    
    public Queue<Shot> vote() {
        int score = 0;
        
        int first = breaks.get(0);
//        System.out.println("first   " + first);
        int last = breaks.get(breaks.size() - 1);
        
        if (breaks.get(0) > 0) {
            breaks.add(0,0);
        }
        
        if (breaks.get(breaks.size() - 1) < this.lastFrameIndex) {
            breaks.add(this.lastFrameIndex);
        }
        
        int[] voteMtx = new int[breaks.size() - 1]; // number of intervals
        Arrays.fill(voteMtx, 0);

        Shot prevShot = null;
        for (int i = 1; i < breaks.size(); i++) {
            int start = breaks.get(i-1);
            int end = breaks.get(i);

            MathCal mathCal = new MathCal();
            int count = 0;

            double entropy = mathCal.getEntropy(matchedCount, start,end);

            if (end - start <= 600) {
                count++;
            }


            if( entropy < 6 ) {
                count++;
            }

            if( soundCharacter.isAd(start, end) ) {
                count++;
            }

            double time = (end - start)/30;
            Shot shot = new Shot(start, end, time);

            if( count >= 2 ) {
                Shot prevAd = null;


                if( adShots.size() > 0 )
                    prevAd = adShots.get(adShots.size() - 1);

//                if( i >=16 && i <= 20 ){
//                    System.out.println(prevShot.getStart()+"    "+prevShot.getEnd()+" "+prevShot.getTime());
//                    System.out.println(prevAd.getEnd()+"    "+prevAd.getStart());
////                    System.out.println(prevAd+" "+prevShot+"  "+ (prevAd.getEnd() == prevShot.getStart()) + prevShot.getTime()+"    "+(i-1));
//                }

                if ( prevShot != null && prevAd != null && prevAd.getEnd() == prevShot.getStart() && prevShot.getTime() < 5 ) {
                    adShots.add(prevShot);
                }

                adShots.add(shot);

            }

            prevShot = shot;
//            System.out.println(voteMtx[i-1]);
//            System.out.println(breaks.get(i)+" "+(i-1)+" "+"avg     "+avg+"   RMS  "+RMS+"  variance "+variance+"   entropy " + entropy);

        }

        
        return mergetIntervel();
    }

    public Queue<Shot> mergetIntervel() {
        if( adShots.isEmpty() ) return null;

//        for( Shot shott : adShots ) {
//            System.out.println(shott.getStart()+" "+shott.getEnd());
//        }

        Queue<Shot> queue = new LinkedList<>();

        Shot shot = adShots.get(0);
        int start = shot.getStart();
        int end = shot.getEnd();
        double time = shot.getTime();

        for( int i = 1; i < adShots.size(); i++ ) {
            shot = adShots.get(i);

            if( end == shot.getStart() ) {
                end = shot.getEnd();
                time += shot.getTime();
            } else {
                queue.offer(new Shot(start, end, time));
                start = shot.getStart();
                end = shot.getEnd();
            }
        }
        queue.offer(new Shot(start, end, time));

//        for( Shot shot1 : queue )
//            System.out.println(shot1.getStart()+"   "+shot1.getEnd());
        return queue;
    }
    

}
