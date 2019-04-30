package AnalyzeAd;
import java.awt.*;
import java.awt.image.*;
import java.lang.Math;
/*
 *  Input Y-channel of two different frames, calculate average motion vector between two frames.
 *  @author: Yue
 */
public class AvgMotionCalc {
    
    private final int WIDTH = 480;
    private final int HEIGHT = 270;
    private BufferedImage pastFrame;
    private BufferedImage currFrame;
    private double[][] pastY;
    private double[][] currY;
    private final int K = 15; // K is the search block parameter
    private final int M = 15; // M * M is macroblock size
    private final int NUMBLOCKS = WIDTH * HEIGHT / (M * M);
    
    
    /* Convert RGB to Y channel */
    public AvgMotionCalc(double[][] past, double[][] curr) {   
//        this.pastFrame = past;
//        this.currFrame = curr;
//        Color[][] color1 = new Color[WIDTH][HEIGHT];
//        Color[][] color2 = new Color[WIDTH][HEIGHT];
//        for(int y = 0; y < HEIGHT; y++)
//        {
//            for(int x = 0; x < WIDTH; x++)
//            {
//              color1[x][y] = new Color(pastFrame.getRGB(x, y));
//              color2[x][y] = new Color(currFrame.getRGB(x, y));
////              System.out.println(color1[x][y].getRed());
////              System.out.println(color1[x][y].getGreen());
////              System.out.println(color1[x][y].getBlue());
//              pastY[x][y] = 0.299 * color1[x][y].getRed() + 0.587 * color1[x][y].getGreen() + 0.114 * color1[x][y].getBlue();
//              currY[x][y] = 0.299 * color2[x][y].getRed() + 0.587 * color2[x][y].getGreen() + 0.114 * color2[x][y].getBlue();            
//            }
//        } 

        this.pastY = past;
        this.currY = curr;
    }
    
    
//    public void rgbToY() {
//      Color[][] color1 = new Color[WIDTH][HEIGHT];
//      Color[][] color2 = new Color[WIDTH][HEIGHT];
//      for(int y = 0; y < HEIGHT; y++)
//      {
//          for(int x = 0; x < WIDTH; x++)
//          {
//            color1[x][y] = new Color(pastFrame.getRGB(x, y));
//            color2[x][y] = new Color(currFrame.getRGB(x, y));
//            System.out.println(color1[x][y].getRed());
//            System.out.println(color1[x][y].getGreen());
//            System.out.println(color1[x][y].getBlue());
//            pastY[x][y] = 0.299 * color1[x][y].getRed() + 0.587 * color1[x][y].getGreen() + 0.114 * color1[x][y].getBlue();
//            currY[x][y] = 0.299 * color2[x][y].getRed() + 0.587 * color2[x][y].getGreen() + 0.114 * color2[x][y].getBlue();            
//          }
//      } 
//    }
    
    
    public Vector BruteForceSearch(){
        
        //rgbToY();
        // for each macroblock, search in the given area.
        double min = 0;
        double mse = 0;
        int matchX = 0;
        int matchY = 0;
        int vectorX = 0;
        int vectorY = 0;
        double averageX = 0;
        double averageY = 0;
        int matchedBlocks = 0;
        
        for(int i = 0; i < WIDTH/M; i++) {
            for(int j = 0; j < HEIGHT/M; j++) {
                
                
                // if macroblock is not on the boundary
                //if ( (i > 0) && i < (WIDTH/M - 1) && (j > 0) && j < (HEIGHT/M - 1)) {
                    
                    // for each candidate block, compare the mse
                // (p,q) is the upper-left corner of candidate block, must inside the frame
                    for (int p = i * M - K; p < i * M - K + M ; p++) {
                        for (int q = j * M - K; q < j * M - K + M ; q++) {
                            
                            // if candidate is outside the boundary, jump to the next iteration
                            if (p < 0 || q < 0 || p >= WIDTH - M || q >= HEIGHT - M) {
                                continue;
                            }
                            
                            mse = MeanSquareError(i*M,j*M,p,q);
                            //initialize parameters as the first candidate block 
                            if (p ==(i * M - K) && q == (j * M - K)) {
                                min = mse;
                                matchX = p;
                                matchY = q;
                            }
                            
                            if (mse < min) {
                                min = mse;
                                matchX = p;
                                matchY = q;
                            } 
                        }
                    }
                    
                    //System.out.println(min);
                    
                    if (min < 1000) {
                        matchedBlocks++;
                    }
                    
                    
                // calculate motion vector (curr - past)???
                vectorX = i * M - matchX;
                vectorY = j * M - matchY;
                
                averageX = averageX + vectorX;
                averageY = averageY + vectorY;           
            }
        }      
        
        //System.out.println(matchedBlocks);
        averageX = averageX / NUMBLOCKS;
        averageY = averageY / NUMBLOCKS; 
        
        Vector vec  = new Vector(averageX,averageY,matchedBlocks);
        return vec;
    }
    
    /* Calculate two M*M size macroblock MSE */
    public double MeanSquareError(int a, int b, int p, int q) {
        double mse = 0;
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < M; j++ ) {
                mse = mse + Math.pow(currY[a+i][b+i] - pastY[p+i][q+i], 2);
            }
        }
        mse = mse/(M*M);
        
        return mse;
    }
    
    
    
    
    
}
