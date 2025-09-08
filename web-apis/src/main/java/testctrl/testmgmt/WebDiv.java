package testctrl.testmgmt;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class WebDiv {

    private String _divMCQTemplate;
    private String _divMCBTemplate;
    private String _divFRQTemplate;
    private String _divAPXTemplate;
    
    public WebDiv(String templatesRoot) throws IOException {
        _divMCQTemplate = Files.readString(Paths.get(templatesRoot +  "testctrl/div-mcqTemplate.jsp"), StandardCharsets.UTF_8);
        _divMCBTemplate = Files.readString(Paths.get(templatesRoot +  "testctrl/div-mcbTemplate.jsp"), StandardCharsets.UTF_8);
        _divFRQTemplate = Files.readString(Paths.get(templatesRoot +  "testctrl/div-frqTemplate.jsp"), StandardCharsets.UTF_8);
        _divAPXTemplate = Files.readString(Paths.get(templatesRoot +  "testctrl/div-apxTemplate.jsp"), StandardCharsets.UTF_8);
    }

    public String getDiv(Question q, boolean isAnswer) {
        String qType = q.getType();
        switch(qType) {
            case Question._MCQ:
                return getDivMCQ(q, isAnswer, null);
            case Question._MCB:
                return getDivMCB(q, isAnswer);
            case Question._FRQ:
                return getDivFRQ(q, isAnswer);
            case Question._APX:
                return getDivAPX(q);
            default:
                throw new IllegalArgumentException(String.format("Question type '%s' is not supported!", qType));
        }
    }

    private String getDivMCQ(Question q, boolean isAnswer, Question parentQ) {
        // replace #QUID# identifying the question
        String qDiv = _divMCQTemplate.replaceAll("#QUID#", q.getName());
        // replace #QDIR# locating the question directory
        qDiv = qDiv.replaceAll("#QDIR#", parentQ != null ? parentQ.getName() + "/" + q.getName() : q.getName());
        // replace #QTXT# providing the question's text (content)
        QMeta meta = q.getMeta();
        qDiv = qDiv.replace("#QTXT#", isAnswer ? meta.answer : meta.question);
        // replace #QOPT# pointing to each option for this question
        for (Map.Entry<String, String> kvp : meta.choices.entrySet()) {
            qDiv = qDiv.replace("#QOPT" + kvp.getKey() + "#", kvp.getValue());
            // replace #ANSSTL# with style to apply to this option (either highlighing or hiding the answer)
            if (isAnswer & meta.correct.equalsIgnoreCase(kvp.getKey())) {
                qDiv = qDiv.replace("#ANSSTL" + kvp.getKey() + "#", "class=\"actTestMgmt_tbl_mcqAnswer\"");
            } else {
                qDiv = qDiv.replace("#ANSSTL" + kvp.getKey() + "#", "");
            }
        }

        return qDiv;
    }

    private String getDivMCB(Question q, boolean isAnswer) {
        String qDiv = _divMCBTemplate.replaceAll("#QUID#", q.getName());
        qDiv = qDiv.replaceAll("#QDIR#", q.getName());
        QMeta meta = q.getMeta();
        qDiv = qDiv.replace("#QTXT#", isAnswer ? meta.answer : meta.question);
        for (Question bq : q.getBQuestions()) {
            qDiv += "<br>" + getDivMCQ(bq, isAnswer, q);
        }
        return qDiv;
    }

    private String getDivFRQ(Question q, boolean isAnswer) {
        final String qPage = "##QPAGE##";
        int iStart = _divFRQTemplate.indexOf(qPage) + qPage.length();
        int iEnd = _divFRQTemplate.indexOf(qPage, iStart);
        
        String pDivTemplate = _divFRQTemplate.substring(iStart, iEnd).trim();
        QMeta meta = q.getMeta();
        List<String> pages = isAnswer ? meta.solutionPages : meta.textPages;
        String qPageBlocks = "";
        for (String page  : pages) {
            qPageBlocks += pDivTemplate
                .replaceAll("#QDIR#", q.getName())
                .replaceAll("#QTXT#", page);
            qPageBlocks += "<br>";
        }

        String qDiv = _divFRQTemplate.substring(_divFRQTemplate.indexOf("-->", iEnd) + 3).trim();
        qDiv = qDiv.replaceAll("#QUID#", q.getName());
        qDiv = qDiv.replaceAll(qPage, qPageBlocks);
        return qDiv;
    }

    private String getDivAPX(Question q) {
        final String qPage = "##QPAGE##";
        int iStart = _divAPXTemplate.indexOf(qPage) + qPage.length();
        int iEnd = _divAPXTemplate.indexOf(qPage, iStart);
        
        String pDivTemplate = _divAPXTemplate.substring(iStart, iEnd).trim();
        QMeta meta = q.getMeta();
        String qPageBlocks = "";
        for (String page  : meta.textPages) {
            qPageBlocks += pDivTemplate
                .replaceAll("#QDIR#", q.getName())
                .replaceAll("#QTXT#", page);
            qPageBlocks += "<br>";
        }

        String qDiv = _divAPXTemplate.substring(_divAPXTemplate.indexOf("-->", iEnd) + 3).trim();
        qDiv = qDiv.replaceAll("#QUID#", q.getName());
        qDiv = qDiv.replaceAll(qPage, qPageBlocks);
        return qDiv;
    }
}
