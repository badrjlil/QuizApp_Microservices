package com.example.QuestionService.dto;

import java.util.List;
import com.example.QuestionService.model.AnswerChoice;

public class QuestionDTO {
    private Long id;
    private String questionText;
    private List<AnswerChoice> answerChoices;
    private CompetencyDTO competency; // Champ pour la compétence

    public QuestionDTO() {
    }

    public QuestionDTO(Long id, String questionText, List<AnswerChoice> answerChoices, CompetencyDTO competency) {
        this.id = id;
        this.questionText = questionText;
        this.answerChoices = answerChoices;
        this.competency = competency;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public List<AnswerChoice> getAnswerChoices() {
        return answerChoices;
    }

    public void setAnswerChoices(List<AnswerChoice> answerChoices) {
        this.answerChoices = answerChoices;
    }

    public CompetencyDTO getCompetency() {
        return competency;
    }

    public void setCompetency(CompetencyDTO competency) {
        this.competency = competency;
    }
}
