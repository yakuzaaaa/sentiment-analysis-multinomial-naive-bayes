package mietsentimentanalyzer;

/**
 *
 * @author nilarnab
 */
public class MIETsentimentAnalyzer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SentimentCalculator calculator =
                SentimentCalculator.getNewCalculatorInstacne("multi-train.txt", "multi-test.txt");
        calculator.start();
        System.out.println("Result ::: " + calculator.getResult());
    }
}
