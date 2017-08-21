package io.sqooba.traildb;


public class TrailDBTimeRange {

    private long beginTS;
    private long endTS;
    
    public TrailDBTimeRange(long beginTimestamp, long endTimestamp) {
        this.beginTS = beginTimestamp;
        this.endTS = endTimestamp;
    }

    
    public long getBeginTimestamp() {
        return beginTS;
    }

    
    public long getEndTimestamp() {
        return endTS;
    }
    
}
