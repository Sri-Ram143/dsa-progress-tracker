package tracker.analytics;

import java.util.*;
import tracker.analytics.AnalyticsEngine;

public class Recommendation {
    private String topic;
    private List<String> reasons;
    public Recommendation(String topic){
        this.topic=topic;
        this.reasons=new ArrayList<>();
    }

    public String getTopic(){
        return topic;
    }
    public List<String> getReasons(){
        return reasons;
    }

    public void addReason(String reason){
        reasons.add(reason);
    }
}
