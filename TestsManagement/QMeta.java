package TestsManagement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Schema for the JSON Deserialization of Question/.meta file 
 */
public class QMeta {
    String name;                    // *
    String type;                    // *
    String notes;                   // *
    String question;                // _MCQ, _MCB
    Map<String, String> choices;    // _MCQ
    String correct;                 // _MCQ
    String answer;                  // _MCQ, _MCB
    List<String> questions;         // _MCB
    List<String> textPages;         // _FRQ, _APX
    List<String> solutionPages;     // _FRQ

    QMeta(QMeta qm) {
        name = qm.name;
        type = qm.type;
        question = qm.question;
        // deep copy the map of choices
        choices = (qm.choices != null) ? new TreeMap<String, String>(qm.choices) : null;
        correct = qm.correct;
        answer = qm.answer;
        notes = qm.notes;
        textPages = (qm.textPages != null) ? new ArrayList<String>(qm.textPages) : null;
        solutionPages = (qm.solutionPages != null) ? new ArrayList<String>(qm.solutionPages) : null;
        // deep copy of the list of questions
        questions = (qm.questions != null) ? new ArrayList<String>(qm.questions) : null;

    }
}