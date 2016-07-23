/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mietsentimentanalyzer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 *
 * @author nilarnab
 */
public class SentimentCalculator {
    
    //filenames
    private String trainingFile;
    private String testFile;

    //lists for training
    private ArrayList<TrainingRow> mTrainingRows; //total number of rows/lines in training set
    private ArrayList<Word> mUniqueWordsList;
    private ArrayList<WordWithClass> mWordsWithClassList;
    //list for testing
    private ArrayList<TrainingRow> mTestRows;
    private ArrayList<Word> mTestRowWordList;

    //priors
    private int mPositiveRows;  // total number of positive rows
    private int mNegativeRows;  // total number of negative rows
    private double pPositiveRows; // probability of positive rows
    private double pNegativeRows; // probability of negative rows
    
    //result
    TrainingRow.SentimentClass mResult;

    public TrainingRow.SentimentClass getResult() {
        return mResult;
    }
    
    

    private SentimentCalculator() {

    }

    public static SentimentCalculator getNewCalculatorInstacne(String training, String test) {
        SentimentCalculator calculator = new SentimentCalculator();
        calculator.setTestFile(test);
        calculator.setTrainingFile(training);
        
        return calculator;
    }

    public String getTrainingFile() {
        return trainingFile;
    }

    public void setTrainingFile(String trainingFile) {
        this.trainingFile = trainingFile;
    }

    public String getTestFile() {
        return testFile;
    }

    public void setTestFile(String testFile) {
        this.testFile = testFile;
    }
    
    

    public void start() {
        initializer();
        calculatePriors();
        likelihood();
        mResult = probabilityRowGivenClass();
    }

    private void initializer() {
        mTrainingRows = extractRowsFromDocument(getTrainingFile());
        mUniqueWordsList = new ArrayList();
        mWordsWithClassList = new ArrayList();
        extractWords(mTrainingRows, true); //true because it is for training
        mTestRows = extractRowsFromDocument(getTestFile());
        mTestRowWordList = new ArrayList();
        extractWords(mTestRows, false);
    }

    /**
     *
     * @param filename
     * @return
     */
    private ArrayList<TrainingRow> extractRowsFromDocument(String filename) {
        String line;
        ArrayList<TrainingRow> rows = new ArrayList();
        try {
            InputStream inputStream = new FileInputStream(filename);
            InputStreamReader isr = new InputStreamReader(inputStream, "UTF-8");
            BufferedReader fileLineReader = new BufferedReader(isr);
            while ((line = fileLineReader.readLine()) != null) {
                line.trim();
                line.toLowerCase();
                TrainingRow row = new TrainingRow();
                row.setSentence(line.substring(1).trim()); //assuming the first index is for  class
                char classNumber = line.charAt(0);
                switch (classNumber) {
                    case '0':
                        row.setSentimentClass(TrainingRow.SentimentClass.negative);
                        break;
                    case '1':
                        row.setSentimentClass(TrainingRow.SentimentClass.positive);
                        break;
                    case '2':
                        row.setSentimentClass(TrainingRow.SentimentClass.unassigned);
                        break;
                    default:
                        System.out.println("Invalid class number " + classNumber);
                        break;
                }
                rows.add(row);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return rows;
    }

    private void setWordListsTraining(String[] words, TrainingRow.SentimentClass sc) {
        for (int i = 0; i < words.length; i++) {
            Word newWord = new Word();
            newWord.setWordString(words[i]);
            newWord.setLikelihoodNegative(0);
            newWord.setLikelihoodPositive(0);
            WordWithClass wordWithClass = new WordWithClass();
            wordWithClass.setWord(newWord);
            wordWithClass.setSentimentClass(sc);
            mWordsWithClassList.add(wordWithClass);
            if (isUniqueWord(words[i])) {
                mUniqueWordsList.add(newWord);
            }
        }
    }

    private void setWordListTest(String[] words) {
        for (int i = 0; i < words.length; i++) {
            Word newWord = new Word();
            newWord.setWordString(words[i]);
        }
    }

    private void extractWords(ArrayList<TrainingRow> rows, boolean isTraining) {
        for (TrainingRow row : rows) {
            String sentence = row.getSentence();
            String words[] = sentence.split("\\s+"); //regex for spaces
            if (isTraining) {
                setWordListsTraining(words, row.getSentimentClass());
            } else {
                setWordListTest(words);
            }
        }
    }

    private boolean isUniqueWord(String word) {
        for (Word w : mUniqueWordsList) {
            if (word.equals(w.getWordString())) {
                return false;
            }
        }
        return true;
    }

    private void calculatePriors() {
        mPositiveRows = count(TrainingRow.SentimentClass.positive);
        mNegativeRows = count(TrainingRow.SentimentClass.negative);
        pPositiveRows = probability(mPositiveRows, mTrainingRows.size());
        pNegativeRows = probability(mNegativeRows, mTrainingRows.size());
        System.out.println("p positive : " + pPositiveRows + " p negative " + pNegativeRows);
    }

    private double probability(double event, double total) {
        double d = (double) (event / total);
        return d;
    }

    private int count(TrainingRow.SentimentClass sentimentClass) {
        int i = 0;
        for (TrainingRow row : mTrainingRows) {
            if (row.getSentimentClass() == sentimentClass) {
                i++;
            }
        }
        return i;
    }

    private void likelihood() {
        for (Word w : mUniqueWordsList) {
            w.setLikelihoodPositive(
                    likelihood(w, TrainingRow.SentimentClass.positive));
            w.setLikelihoodNegative(
                    likelihood(w, TrainingRow.SentimentClass.negative));
        }
    }

    private double likelihood(Word word, TrainingRow.SentimentClass sentimentClass) {
        double count = 0;
        double countTotal = 0;
        double uniqueWordsCount = mUniqueWordsList.size();
        //find count of word in that given sentimentClass + 1(laplace smoothing)
        for (WordWithClass w : mWordsWithClassList) {
            if (w.getWord().getWordString().equals(word.getWordString())
                    && w.getSentimentClass() == sentimentClass) {
                //number of given  "word" in give sentimentClass
                count++;
            }
            //for total words in the give sentimentClass
            if (w.getSentimentClass() == sentimentClass) {
                countTotal++;
            }
        }

        count = count + 1; //for laplace smoothing
        return (count / (countTotal + uniqueWordsCount));
    }

    private TrainingRow.SentimentClass probabilityRowGivenClass() {
        double probabilityPositive = 0;
        double probabilityNegative = 0;
        TrainingRow.SentimentClass resultingClass
                = TrainingRow.SentimentClass.unassigned;
        probabilityPositive = pPositiveRows;
        probabilityNegative = pNegativeRows;

        for (Word word : mTestRowWordList) {
            for (Word w : mUniqueWordsList) {
                if (w.getWordString().equals(word.getWordString())) {
                    probabilityPositive = probabilityPositive * w.getLikelihoodPositive();
                    probabilityNegative = probabilityNegative * w.getLikelihoodNegative();
                }
            }
        }

        return (probabilityPositive > probabilityNegative)
                ? TrainingRow.SentimentClass.positive : TrainingRow.SentimentClass.negative;
    }

}
