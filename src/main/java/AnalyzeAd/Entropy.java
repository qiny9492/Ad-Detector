package AnalyzeAd;


import java.util.HashMap;
import java.util.Map;

public class Entropy {
    private Map<Integer, Integer> map = new HashMap<>();

    public double calculateEntropy(double[] values) {
        for( double value : values ) {
            map.put((int)value, map.getOrDefault((int)value, 0) + 1);
        }
        double res = 0;

        for( int key : map.keySet() ) {
            double probaility = map.get(key) * 1.0 / values.length;

            res += probaility * getLog(probaility) * -1;
        }
        return res;
    }

    private double getLog(double value) {
        return Math.log(value)/Math.log(2);
    }
}
