package com.example.demo.service;

import com.example.demo.repositories.TestRepository;

import com.example.demo.api.AdministrateurClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.api.CandidatClient;
import com.example.demo.api.CompetenceClient;
import com.example.demo.api.EmailingClient;
import com.example.demo.api.LevelClient;
import com.example.demo.api.QuestionClient;
import com.example.demo.api.RoleClient;
import com.example.demo.api.ThemeClient;
import com.example.demo.dto.AdministrateurDTO;
import com.example.demo.dto.CandidatDTO;
import com.example.demo.dto.CompetenceDTO;
import com.example.demo.dto.QuestionDTO;
import com.example.demo.dto.TestDTO;
import com.example.demo.model.*;

@Service
public class TestService {
    @Autowired
    private TestRepository testRepository;
    @Autowired
    private CandidatClient candidatClient;
    @Autowired
    private RoleClient roleClient;
    @Autowired
    private LevelClient levelClient;
    @Autowired
    private ThemeClient themeClient;
    @Autowired
    private CompetenceClient competenceClient;
    @Autowired
    private QuestionClient questionClient;
    @Autowired
    private EmailingClient emailingClient;
    @Autowired
    private AdministrateurClient adminClient;

    public String createTest(TestDTO testDTO) {
        Test test = new Test();
        test.setName(testDTO.getName());
        test.setRoleId(testDTO.getRole().getId());
        test.setLevelId(testDTO.getLevel().getId());
        test.setAdminId(testDTO.getAdmin().getId());
        test.setThemeId(testDTO.getTheme().getId());

        Test savedTest = testRepository.save(test);
        System.out.println(savedTest.getId());

        
        List<CandidatDTO> savedCandidates = testDTO.getCandidats().stream()
        .map(c -> candidatClient.addCandidate(c))
        .collect(Collectors.toList());
        /*for (CandidatDTO candidat : savedCandidates) {
            emailingClient.sendTestLink(candidat);
        }*/
        
        List<Long> candidateIds = savedCandidates.stream()
                .map(CandidatDTO::getId)
                .collect(Collectors.toList());
        test.setCandidateIds(candidateIds);

        List<Long> competenceIds = testDTO.getCompetences().stream()
                .map(CompetenceDTO::getId)
                .collect(Collectors.toList());
        test.setCompetenceIds(competenceIds);

        List<Long> questionIds = testDTO.getQuestions().stream()
                .map(QuestionDTO::getId)
                .collect(Collectors.toList());
        test.setQuestionIds(questionIds);

        testRepository.save(test);

        for (CandidatDTO candidat : savedCandidates) {
            candidat.setTestId(savedTest.getId());
            emailingClient.sendTestLink(candidat);
        }
        return "Test créé avec succès";
    }

    public List<TestDTO> getAllTests() {
        List<Test> tests = testRepository.findAll();

        return tests.stream().map(test -> {
            List<CandidatDTO> candidates = test.getCandidateIds().stream()
                    .map(candidatClient::getCandidateById)
                    .collect(Collectors.toList());

            List<CompetenceDTO> competences = test.getCompetenceIds().stream()
                    .map(competenceClient::getCompetenceById)
                    .collect(Collectors.toList());

            List<QuestionDTO> questions = test.getQuestionIds().stream()
                    .map(questionClient::getQuestionById)
                    .collect(Collectors.toList());

            AdministrateurDTO admin = adminClient.getAdministrateurById(test.getAdminId());

            return new TestDTO(
                    test.getId(),
                    test.getName(),
                    candidates,
                    roleClient.getRoleById(test.getRoleId()),
                    levelClient.getLevelById(test.getLevelId()),
                    themeClient.getThemeById(test.getThemeId()),
                    admin,
                    competences,
                    questions);
        }).collect(Collectors.toList());
    }

    public TestDTO getTestById(Long testId) {
        return testRepository.findById(testId).map(test -> {
            List<CandidatDTO> candidates = test.getCandidateIds().stream()
                    .map(candidatClient::getCandidateById)
                    .collect(Collectors.toList());

            List<CompetenceDTO> competences = test.getCompetenceIds().stream()
                    .map(competenceClient::getCompetenceById)
                    .collect(Collectors.toList());

            List<QuestionDTO> questions = test.getQuestionIds().stream()
                    .map(questionClient::getQuestionById)
                    .collect(Collectors.toList());

            AdministrateurDTO admin = adminClient.getAdministrateurById(test.getAdminId());

            return new TestDTO(
                    test.getId(),
                    test.getName(),
                    candidates,
                    roleClient.getRoleById(test.getRoleId()),
                    levelClient.getLevelById(test.getLevelId()),
                    themeClient.getThemeById(test.getThemeId()),
                    admin,
                    competences,
                    questions);
        }).orElse(null);
    }

    public List<QuestionDTO> getTestQuestions(Long testId) {
        Optional<Test> test = testRepository.findById(testId);
        return test.map(value -> value.getQuestionIds().stream()
                .map(questionClient::getQuestionById)
                .collect(Collectors.toList())).orElse(null);
    }

    public List<CandidatDTO> getTestCandidates(Long testId) {
        Optional<Test> test = testRepository.findById(testId);
        return test.map(value -> value.getCandidateIds().stream()
                .map(candidatClient::getCandidateById)
                .collect(Collectors.toList())).orElse(null);
    }

    public Long getTestIdByCandidatId(Long candidatId) {
        return testRepository.findTestIdByCandidatId(candidatId);
    }


   /* @Transactional
    public void addCandidateToTest(Long testId, Long candidateId) {
        if (testId == null || candidateId == null) {
            throw new IllegalArgumentException("L'ID du test et du candidat ne peuvent pas être nuls.");
        }
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test non trouvé avec ID : " + testId));

        List<Long> candidateIds = test.getCandidateIds();
        if (!candidateIds.contains(candidateId)) {
            testRepository.addCandidateToTest(testId, candidateId);
            candidateIds.add(candidateId);
            test.setCandidateIds(candidateIds);
        } else {
            throw new RuntimeException("Le candidat est déjà associé à ce test.");
        }
    }*/

    public void addCandidateToTest(Long testId, Long candidateId) {
        if (testId == null || candidateId == null) {
            throw new IllegalArgumentException("L'ID du test et du candidat ne peuvent pas être nuls.");
        }

        // 🔥 Vérifier si le test existe
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test non trouvé avec ID : " + testId));

        // 🔥 Récupérer la liste des candidats
        List<Long> candidateIds = test.getCandidateIds();
        if (candidateIds == null) {
            candidateIds = new ArrayList<>();  // ✅ Initialisation si la liste est vide
        }

        if (!candidateIds.contains(candidateId)) {
            candidateIds.add(candidateId);  // ✅ Ajout du candidat à la liste
            test.setCandidateIds(candidateIds);  // ✅ Mise à jour de l'entité
            testRepository.save(test);  // ✅ Hibernate met automatiquement à jour test_candidate_ids
        } else {
            throw new RuntimeException("Le candidat est déjà associé à ce test.");
        }
    }
    
}
