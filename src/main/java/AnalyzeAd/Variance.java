package AnalyzeAd;

public class Variance {
    public float getVariance(double[] audio) {
        double avg = getAvg(audio);
        float sum = 0;

        for( int i = 0; i < audio.length; i++ ) {
            sum += Math.pow((avg - audio[i]), 2);
        }

        return sum/audio.length;
    }

    private double getAvg(double[] audio) {
        double sum = 0;

        for( double value : audio ) {
            sum += value;
        }

        return sum/audio.length;
    }
}
