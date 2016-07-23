package mietsentimentanalyzer;
/**
 *
 * @author nilarnab
 */
public class Word {
    private String wordString;
    private double likelihoodPositive;
    private double likelihoodNegative;

    public String getWordString() {
        return wordString;
    }

    public void setWordString(String word) {
        this.wordString = word;
    }

    public double getLikelihoodPositive() {
        return likelihoodPositive;
    }

    public void setLikelihoodPositive(double priorPositve) {
        this.likelihoodPositive = priorPositve;
    }

    public double getLikelihoodNegative() {
        return likelihoodNegative;
    }

    public void setLikelihoodNegative(double priorNegative) {
        this.likelihoodNegative = priorNegative;
    }
    
}
