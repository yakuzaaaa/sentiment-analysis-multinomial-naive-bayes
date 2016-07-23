/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mietsentimentanalyzer;

/**
 *
 * @author nilarnab
 */
public class WordWithClass {
    private Word word;
    private TrainingRow.SentimentClass sentimentClass;

    public Word getWord() {
        return word;
    }

    public void setWord(Word word) {
        this.word = word;
    }

    public TrainingRow.SentimentClass getSentimentClass() {
        return sentimentClass;
    }

    public void setSentimentClass(TrainingRow.SentimentClass sentimentClass) {
        this.sentimentClass = sentimentClass;
    }
    
}
