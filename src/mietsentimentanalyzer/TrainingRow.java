package mietsentimentanalyzer;
/**
 *
 * @author nilarnab
 */
public class TrainingRow {
    private SentimentClass sentimentClass;
    private String sentence;

    public SentimentClass getSentimentClass() {
        return sentimentClass;
    }

    public void setSentimentClass(SentimentClass sentimentClass) {
        this.sentimentClass = sentimentClass;
    }
    
    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }
    
    public enum SentimentClass {
        negative,
        positive,
        unassigned
    }
    
}
