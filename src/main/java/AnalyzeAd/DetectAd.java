package AnalyzeAd;


import shot.Shot;

import java.util.*;
import java.awt.*;
//import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.io.*;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*;


public class DetectAd {

    public static Queue<Shot> detect(String vFileName, String aFileName) throws Exception {
        File file = new File(vFileName);
        String audioFileName = aFileName;


        RandomAccessFile raf = new RandomAccessFile(file, "r");

        int[][][] pastHist = new int[4][4][4];
        int[][][] currHist = new int[4][4][4];
        int[][][] tempHist = new int[4][4][4];
        int height = 270;
        int width = 480;

        List<Integer> matchedCount = new ArrayList<>();

        long frameLength = width*height*3;
        byte[] bytes = new byte[(int) frameLength];
        BufferedImage img1 = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        BufferedImage img2 = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        //System.out.println(img1.getRGB(100, 100));

        int numFrames = (int)((double)raf.length()/frameLength) + 1;
        Vector[] motionMatrix = new Vector[numFrames];
//        TODO: breaks shot boundary
        ArrayList<Integer> breaks = new ArrayList<Integer>(); // breaks store the shot boundaries


        double[][] pastY = new double[width][height];
        double[][] currY = new double[width][height];

        double colorDiff = 0;



        long offset = 0;
        int ind = 0;


        double motionVectorX = 0;
        double motionVectorY = 0;
        int matchedBlocks = 0;
        double euclidean = 0;
        double entropyCurr = 0;
        double entropyPast = 0;
        double entropyDiff = 0;

        int index = 0;

        //offset = offset + frameLength;
        offset = frameLength * 0;
        index = 0;
        int startIdx = index;
        while (offset <= raf.length()) {

            ind = 0;
            raf.seek(offset);
            raf.read(bytes);

            // for the second run.
            if (index >= (startIdx + 1)) {
                for(int i = 0; i < 4; i++)
                    for(int j = 0; j < 4; j++)
                        for(int p = 0; p < 4; p++)
                            pastHist[i][j][p] = currHist[i][j][p];

                for(int i = 0; i < width; i++) {
                    for(int j = 0; j < height; j++) {
                        pastY[i][j] = currY[i][j];
                    }
                }

                //entropyPast = entropyCurr;
            }


            for (int i = 0 ; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    Arrays.fill(currHist[i][j], 0);
                }
            }

            //printHist(currHist);

            for(int y = 0; y < height; y++)
            {
                for(int x = 0; x < width; x++)
                {
                    byte a = 0;
                    byte r = bytes[ind];
                    byte g = bytes[ind+height*width];
                    byte b = bytes[ind+height*width*2];

                    int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
                    //int pix = ((a << 24) + (r << 16) + (g << 8) + b);


                    img2.setRGB(x,y,pix);




                    int ri = (int)( (r & 0xff) &0xC0);
                    int gi = (int)( (g & 0xff) &0xC0);
                    int bi = (int)( (b & 0xff) &0xC0);

                    currHist[ri/64][gi/64][bi/64]++;

                    currY[x][y] = 0.299 * ri + 0.587 * gi + 0.114 * bi;

                    ind++;
                }
            }

//            EntropyCalc entro = new EntropyCalc(currHist);
//            entropyCurr = entro.Calculate();

            //display(img2,index);





            // calculate color histogram

            if ( index >= (startIdx + 1)) {
                AvgMotionCalc motion = new AvgMotionCalc(pastY,currY);
                Vector vector = motion.BruteForceSearch();
                motionVectorX = vector.getX();
                motionVectorY = vector.getY();
                //System.out.println(index);
                motionMatrix[index] = vector;
                matchedBlocks = vector.getMatch();
//               euclidean = Math.sqrt(motionVectorX * motionVectorX + motionVectorY * motionVectorY);


                //EntropyCalc entro = new EntropyCalc(currHist - pastHist);
//               double entropy = entro.Calculate();

//               entropyDiff = entropyCurr - entropyPast;
//               if (entropyDiff > 0.1) {
//                   System.out.println(index);
//
//               }

//           TODO: color diff
                colorDiff = ColorDiff(pastHist,currHist);

                //System.out.println(colorDiff);
                if (colorDiff > 8.0E6 && matchedBlocks < 300) {
                    //System.out.println(index + "  " );
                    breaks.add(index);
                }



            }

//           System.out.println(index +"  "+motionMatrix[index]);
            if( index > 0)
                matchedCount.add(motionMatrix[index].getMatch());

            offset = offset + frameLength ;
            index++;

        }


        //System.out.println(index + "  " );


//        TODO: motionMatrix average motion vector for each frame
        VoteShots vote = new VoteShots(breaks,motionMatrix,numFrames, matchedCount, audioFileName);
        return  vote.vote();


    }

    public static void main(String[] args) throws Exception {
//        dataset1
//        /Users/yangtian/Downloads/dataset/Videos/data_test1.rgb
//        dataset2
//        /Users/yangtian/Downloads/dataset2/Videos/data_test2.rgb
        File file = new File(args[0]);
        String audioFileName = args[1];


        RandomAccessFile raf = new RandomAccessFile(file, "r");

        int[][][] pastHist = new int[4][4][4];
        int[][][] currHist = new int[4][4][4];
        int[][][] tempHist = new int[4][4][4];
        int height = 270;
        int width = 480;

        List<Integer> matchedCount = new ArrayList<>();

        long frameLength = width*height*3;
        byte[] bytes = new byte[(int) frameLength];
        BufferedImage img1 = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        BufferedImage img2 = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        //System.out.println(img1.getRGB(100, 100));

        int numFrames = (int)((double)raf.length()/frameLength) + 1;
        Vector[] motionMatrix = new Vector[numFrames];
//        TODO: breaks shot boundary
        ArrayList<Integer> breaks = new ArrayList<Integer>(); // breaks store the shot boundaries


        double[][] pastY = new double[width][height];
        double[][] currY = new double[width][height];

        double colorDiff = 0;



        long offset = 0;
        int ind = 0;


        double motionVectorX = 0;
        double motionVectorY = 0;
        int matchedBlocks = 0;
        double euclidean = 0;
        double entropyCurr = 0;
        double entropyPast = 0;
        double entropyDiff = 0;

        int index = 0;

        //offset = offset + frameLength;
        offset = frameLength * 0;
        index = 0;
        int startIdx = index;
        while (offset <= raf.length()) {

            ind = 0;
            raf.seek(offset);
            raf.read(bytes);

            // for the second run.
            if (index >= (startIdx + 1)) {
                for(int i = 0; i < 4; i++)
                    for(int j = 0; j < 4; j++)
                        for(int p = 0; p < 4; p++)
                            pastHist[i][j][p] = currHist[i][j][p];

                for(int i = 0; i < width; i++) {
                    for(int j = 0; j < height; j++) {
                        pastY[i][j] = currY[i][j];
                    }
                }

                //entropyPast = entropyCurr;
            }


            for (int i = 0 ; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    Arrays.fill(currHist[i][j], 0);
                }
            }

            //printHist(currHist);

            for(int y = 0; y < height; y++)
            {
                for(int x = 0; x < width; x++)
                {
                    byte a = 0;
                    byte r = bytes[ind];
                    byte g = bytes[ind+height*width];
                    byte b = bytes[ind+height*width*2];

                    int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
                    //int pix = ((a << 24) + (r << 16) + (g << 8) + b);


                        img2.setRGB(x,y,pix);




                    int ri = (int)( (r & 0xff) &0xC0);
                    int gi = (int)( (g & 0xff) &0xC0);
                    int bi = (int)( (b & 0xff) &0xC0);

                    currHist[ri/64][gi/64][bi/64]++;

                    currY[x][y] = 0.299 * ri + 0.587 * gi + 0.114 * bi;

                    ind++;
                }
            }

//            EntropyCalc entro = new EntropyCalc(currHist);
//            entropyCurr = entro.Calculate();

            //display(img2,index);





            // calculate color histogram

           if ( index >= (startIdx + 1)) {
               AvgMotionCalc motion = new AvgMotionCalc(pastY,currY);
               Vector vector = motion.BruteForceSearch();
               motionVectorX = vector.getX();
               motionVectorY = vector.getY();
               //System.out.println(index);
               motionMatrix[index] = vector;
               matchedBlocks = vector.getMatch();
//               euclidean = Math.sqrt(motionVectorX * motionVectorX + motionVectorY * motionVectorY);


               //EntropyCalc entro = new EntropyCalc(currHist - pastHist);
//               double entropy = entro.Calculate();

//               entropyDiff = entropyCurr - entropyPast;
//               if (entropyDiff > 0.1) {
//                   System.out.println(index);
//
//               }

//           TODO: color diff
               colorDiff = ColorDiff(pastHist,currHist);

               //System.out.println(colorDiff);
               if (colorDiff > 8.0E6 && matchedBlocks < 300) {
                   //System.out.println(index + "  " );
                   breaks.add(index);
               }



           }

//           System.out.println(index +"  "+motionMatrix[index]);
            if( index > 0)
                matchedCount.add(motionMatrix[index].getMatch());

            offset = offset + frameLength ;
            index++;

        }


        //System.out.println(index + "  " );


//        TODO: motionMatrix average motion vector for each frame
        VoteShots vote = new VoteShots(breaks,motionMatrix,numFrames, matchedCount, audioFileName);
        vote.vote();

    }

    // MSE of two color histograms
    private static double ColorDiff(int[][][] pastHist, int[][][] currHist) {
        double diff = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                for (int k = 0; k < 4; k++) {

                    diff = diff + Math.pow(currHist[i][j][k] - pastHist[i][j][k],2);
                    //System.out.println(diff + "  " );
                }
            }
        }
        diff = diff/64;

        return diff;
    }


    private static void printY(double[][] ychannel) {
        for (int i = 0; i < 4 ; i++) {
            for (int j = 0; j < 4 ; j++) {
                System.out.print(ychannel[i][j] + "  ");
            }
        }

        System.out.println();
    }


    private static void printHist(int[][][] histogram) {
        int sum = 0 ;
      for(int i = 0; i < histogram.length; i++) {
          for(int j = 0; j < histogram[i].length; j++) {
              for(int p = 0; p < histogram[i][j].length; p++) {
                 System.out.println("t[" + i + "][" + j + "][" + p + "] = " + histogram[i][j][p]);
                  sum = sum + histogram[i][j][p];
              }

          }

      }


      //System.out.println(sum);
    }

    private static void display(BufferedImage img, int index) {
        JFrame frame = new JFrame();
        JPanel panel = new JPanel();

        //frame.getContentPane().setLayout(BorderLayout.CENTER);

        JLabel label1 = new JLabel(String.valueOf(index),new ImageIcon(img),SwingConstants.CENTER);

        panel.add(label1);
        frame.getContentPane().add(panel,BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
    }


}
