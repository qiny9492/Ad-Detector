package AnalyzeAd;

public class EntropyCalc {

    private int[][][] hist;
    private final int WIDTH = 480;
    private final int HEIGHT = 270;
    
    public EntropyCalc(int[][][] hist) {
        this.hist = hist;
    }
    
    public double Calculate() {
        double entropy = 0;
        double prob = 0;
        //double sum = 0;
        
        for (int i = 0; i < 4 ; i++) {
            for (int j = 0; j < 4 ; j++) {
                for (int k = 0; k < 4 ; k++) {
                    prob = ((double)hist[i][j][k])/(WIDTH * HEIGHT);
                    if (prob > 0) {
                        entropy = entropy - prob * (Math.log(prob)/Math.log(2));
                    }
                    
                    //sum = sum + prob;
                    
                }
            }
        }
        
        //System.out.println(sum);
        return entropy;
    }
}
