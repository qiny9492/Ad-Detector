package shot;

public class Shot {
    private int start;
    private int end;
    private double time;

    public Shot(int start, int end, double time) {
        this.start = start;
        this.end = end;
        this.time = time;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public double getTime() {
        return time;
    }
}
