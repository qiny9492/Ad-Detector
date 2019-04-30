package Math;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MathCal {
//    not include end
    public double getAvg(List<Integer> list, int start, int end) {
//        not include end
        int sum = 0;
        for( int i = start; i < end; i++ ) {
            sum += list.get(i);
        }

        return (sum * 1.0)/(end - start);
    }

    public int getAvgLong(List<Integer> list, int start, int end) {
//        not include end
        long sum = 0;
        for( int i = start; i < end; i++ ) {
            sum += list.get(i);
        }

        return (int)(sum)/(end - start);
    }

    public double getRMS(List<Integer> list, int start, int end) {
        long sum = 0;

        for( int i = start; i < end; i++ ) {
            sum += (long)list.get(i) * (long)list.get(i);
        }
        return Math.sqrt((sum * 1.0)/(end - start));
    }

    public double getVariance(List<Integer> list, int start, int end) {
        double avg = getAvg(list, start, end);

        int sum = 0;

        for( int i = start; i < end; i++ ) {
            sum += Math.pow((avg - list.get(i)), 2);
        }
        return (sum * 1.0)/(end - start);
    }

    public double getStandardDeviation(List<Integer> list, int start, int end) {
        return Math.sqrt(getVariance(list, start, end));
    }

    private double getLog(double value) {
        return Math.log(value)/Math.log(2);
    }

    public double getEntropy(List<Integer> list, int start, int end) {
        Map<Integer, Integer> map = new HashMap<>();

        for( int i = start; i < end; i++ ) {
            map.put(list.get(i), map.getOrDefault(list.get(i), 0) + 1);
        }

        double res = 0;

        for( int key : map.keySet() ) {
            double probaility = map.get(key) * 1.0 / (end - start);

            res += probaility * getLog(probaility) * -1;
        }
        return res;
    }
}
