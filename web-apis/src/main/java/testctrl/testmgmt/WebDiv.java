package testctrl.testmgmt;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public class WebDiv {

    private String _divMCQTemplate;
    // private String _divMCBTemplate;
    // private String _divFRQTemplate;
    // private String _divAPXTemplate;
    
    public WebDiv(String templatesRoot) throws IOException {
        _divMCQTemplate = Files.readString(Paths.get(templatesRoot +  "testctrl/div-mcqTemplate.jsp"), StandardCharsets.UTF_8);
        // _divMCBTemplate = Files.readString(Paths.get(templatesRoot +  "testctrl/div-mcbTemplate.jsp"), StandardCharsets.UTF_8);
        // _divFRQTemplate = Files.readString(Paths.get(templatesRoot +  "testctrl/div-frqTemplate.jsp"), StandardCharsets.UTF_8);
        // _divAPXTemplate = Files.readString(Paths.get(templatesRoot +  "testctrl/div-apxTemplate.jsp"), StandardCharsets.UTF_8);
    }

    public String getDiv(Question q, boolean isAnswer) {
        String qType = q.getType();
        switch(qType) {
            case Question._MCQ:
                return getDivMCQ(q, isAnswer);
            case Question._MCB:
                break;
            case Question._FRQ:
                break;
            case Question._APX:
                break;
            default:
                throw new IllegalArgumentException(String.format("Question type '%s' is not supported!", qType));
        }
        return String.format("Returning %s div for question %s of type %s",
            isAnswer ? "answer" : "test",
            q.getName(),
            q.getType());
    }

    private String getDivMCQ(Question q, boolean answerDiv) {
        String qDiv = _divMCQTemplate.replaceAll("#QUID#", q.getName());
        QMeta meta = q.getMeta();

        qDiv = qDiv.replace("#QANS#", meta.answer);
        for (Map.Entry<String, String> kvp : meta.choices.entrySet()) {
            qDiv = qDiv.replace("#QOPT" + kvp.getKey() + "#", kvp.getValue());
            if (meta.correct.equalsIgnoreCase(kvp.getKey())) {
                qDiv = qDiv.replace("#ANSSTL" + kvp.getKey() + "#", "class=\"actTestMgmt_tbl_mcqAnswer\"");
            } else {
                qDiv = qDiv.replace("#ANSSTL" + kvp.getKey() + "#", "");
            }
        }

        return qDiv;
    }
}
