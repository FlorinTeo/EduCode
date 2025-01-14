package TestsManagement;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class WebDoc {
    public static final String _PRINT_BREAK = "<div style=\"break-after:page\"></div><br>";
    public static final int _MAX_PX_PER_PAGE = 800;
    public static final int _FRQ_ANSWER_PAGES = 4;

    private static final String _TAG_STYLE = "<!--======== STYLE ========-->";
    private String _style;

    private static final String _TAG_ANSWERS = "<!--======== ANSWERS BOOKLET ========-->";
    private String _answers;
    private static final String _TAG_ANSWERS_MCQ = "<!--~~~~~~~~ ANSWER MCQ ~~~~~~~~-->";
    private String _answersMCQ;
    private static final String _TAG_ANSWERS_FRQ = "<!--~~~~~~~~ ANSWER FRQ ~~~~~~~~-->";
    private String _answersFRQ;

    private static final String _TAG_SECTION1 = "<!--======== SECTION 1 (MCQ) ========-->";
    private String _section1;
    private static final String _TAG_SECTION1_MCQ = "<!--~~~~~~~~ MULTIPLE CHOICE QUESTION (MCQ) ~~~~~~~~-->";
    private String _section1MCQ;
    private static final String _TAG_SECTION1_MCB = "<!--~~~~~~~~ MULTIPLE CHOICE BUNDLE (MCB) ~~~~~~~~-->";
    private String _section1MCB;


    private static final String _TAG_SECTION2 = "<!--======== SECTION 2 (FRQ) ========-->";
    private String _section2;
    private static final String _TAG_SECTION2_PAGE = "<!--~~~~~~~~ SECTION 2 PAGE ~~~~~~~~-->";
    private String _section2Page;

    private static final String _TAG_APPENDIX = "<!--======== APPENDIX ========-->";
    private String _appendix;
    private static final String _TAG_APPENDIX_PAGE = "<!--~~~~~~~~ APPENDIX PAGE ~~~~~~~~-->";
    private String _appendixPage;

    // #region: [private methods] Loading HTML web document template
    private String loadEmptyLines(Queue<String> qLines) {
        String emptyLines = "";
        while(!qLines.isEmpty() && qLines.peek().isEmpty()) {
            emptyLines += "\n";
            qLines.remove();
        }
        if (qLines.isEmpty()) {
            throw new RuntimeException("Invalid .index[eof] format.");
        }
        return emptyLines;
    }

    private String loadBlock(String tag, Queue<String> qLines) {
        loadEmptyLines(qLines);
        if (qLines.peek().contains(tag)) {
            String block = qLines.remove() + "\n";
            while(!qLines.isEmpty()) {
                String line = qLines.remove();
                block += line + "\n";
                if (line.contains(tag)) {
                    return block;
                }
            }
        }
        throw new RuntimeException("Invalid .index[style] format.");
    }

    private void loadAnswers(Queue<String> qLines) {
        _answers = loadEmptyLines(qLines);
        if (qLines.peek().contains(_TAG_ANSWERS)) {
            _answers += qLines.remove() + "\n";
            while(!qLines.isEmpty()) {
                String line = qLines.peek();
                if (line.contains(_TAG_ANSWERS_MCQ)) {
                    _answersMCQ = loadBlock(_TAG_ANSWERS_MCQ, qLines);
                    _answers += _TAG_ANSWERS_MCQ + "\n";
                } else if (line.contains(_TAG_ANSWERS_FRQ)) {
                    _answersFRQ = loadBlock(_TAG_ANSWERS_FRQ, qLines);
                    _answers += _TAG_ANSWERS_FRQ + "\n";
                } else {
                    _answers += qLines.remove() + "\n";
                }
                if (line.contains(_TAG_ANSWERS)) {
                    return;
                }    
            }
        }
        throw new RuntimeException("Invalid .index[answers] format.");
    }

    private void loadSection1(Queue<String> qLines) {
        _section1 = loadEmptyLines(qLines);
        if (qLines.peek().contains(_TAG_SECTION1)) {
            _section1 += qLines.remove() + "\n";
            while(!qLines.isEmpty()) {
                String line = qLines.peek();
                if (line.contains(_TAG_SECTION1_MCQ)) {
                    _section1MCQ = loadBlock(_TAG_SECTION1_MCQ, qLines);
                    _section1 += _TAG_SECTION1_MCQ + "\n";
                } else if (line.contains(_TAG_SECTION1_MCB)) {
                    _section1MCB = loadBlock(_TAG_SECTION1_MCB, qLines);
                    _section1 += _TAG_SECTION1_MCB + "\n";
                } else {
                    _section1 += qLines.remove() + "\n";
                }
                if (line.contains(_TAG_SECTION1)) {
                    return;
                }    
            }
        }
        throw new RuntimeException("Invalid .index[section1] format");
    }

    private void loadSection2(Queue<String> qLines) {
        _section2 = loadEmptyLines(qLines);
        if (qLines.peek().contains(_TAG_SECTION2)) {
            _section2 += qLines.remove() + "\n";
            while(!qLines.isEmpty()) {
                String line = qLines.peek();
                if (line.contains(_TAG_SECTION2_PAGE)) {
                    _section2Page = loadBlock(_TAG_SECTION2_PAGE, qLines);
                    _section2 += _TAG_SECTION2_PAGE + "\n";
                } else {
                    _section2 += qLines.remove() + "\n";
                }
                if (line.contains(_TAG_SECTION2)) {
                    return;
                }    
            }
        }
        throw new RuntimeException("Invalid .index[section2] format");
    }

    private void loadAppendix(Queue<String> qLines) {
        _appendix = loadEmptyLines(qLines);
        if (qLines.peek().contains(_TAG_APPENDIX)) {
            _appendix += qLines.remove() + "\n";
            while(!qLines.isEmpty()) {
                String line = qLines.peek();
                if (line.contains(_TAG_APPENDIX_PAGE)) {
                    _appendixPage = loadBlock(_TAG_APPENDIX_PAGE, qLines);
                    _appendix += _TAG_APPENDIX_PAGE + "\n";
                } else {
                    _appendix += qLines.remove() + "\n";
                }
                if (line.contains(_TAG_APPENDIX)) {
                    return;
                }    
            }
        }
        throw new RuntimeException("Invalid .index[section2] format");
    }
    // #endregion: [private methods] Loading web document template

    // #region: [private methods] Writing HTML web document parts
    private int genBookletHtml(BufferedWriter bw, GMeta gMeta) throws IOException {
        String bkHtml = _answers
            .replaceAll("#TNAME#", gMeta.getName())
            .replace("#QNUM#", "" + gMeta.getMCQCount());

        int iMCQ = bkHtml.indexOf(_TAG_ANSWERS_MCQ);
        int iFRQ = bkHtml.indexOf(_TAG_ANSWERS_FRQ);
        bw.write(bkHtml.substring(0, iMCQ));
        bw.newLine();       
        //Write mcq answer lines
        for (int i = 0; i < gMeta.getMCQCount(); i++) {
            String bkMCQLine = _answersMCQ.replace("#N#", "" + (i + 1));
            bw.write(bkMCQLine);
        }

        bw.write(bkHtml.substring(iMCQ + _TAG_ANSWERS_MCQ.length(), iFRQ));
        bw.newLine();

        //Write frq answer pages if there are any such questions
        if (gMeta.getFRQCount() > 0) {
            for (int i = 0; i < _FRQ_ANSWER_PAGES; i++) {
                String bkFRQPage = _answersFRQ.replace("#GRIDPATH#", gMeta.getPathPrefix());
                bw.write(bkFRQPage);
            }

            bw.write(bkHtml.substring(iFRQ + _TAG_ANSWERS_FRQ.length()));
        }

        return 2 + (gMeta.getFRQCount() > 0 ? _FRQ_ANSWER_PAGES : 0);
    }

    private int genSection1Html(BufferedWriter bw, GMeta gMeta, boolean answers) throws IOException {
        if (gMeta.getMCQCount() == 0) {
            return 0;
        }

        String s1Html = _section1
            .replaceAll("#TNAME#", gMeta.getName())
            .replace("#QNUM#", "" + gMeta.getMCQCount());

        int iMCQ = s1Html.indexOf(_TAG_SECTION1_MCQ);
        bw.write(s1Html.substring(0, iMCQ));
        bw.newLine();       
        int nPages = gMeta.genMCQHtml(bw, _section1MCB, _section1MCQ, answers);
        bw.write(s1Html.substring(iMCQ + _TAG_SECTION1_MCQ.length()));
        return 1 + nPages;
    }

    private int genSection2Html(BufferedWriter bw, GMeta gMeta, boolean solutions) throws IOException {
        if (gMeta.getFRQuestions().size() == 0) {
            return 0;
        }

        String s2Html = _section2
            .replaceAll("#TNAME#", gMeta.getName())
            .replace("#PNUM#", "" + gMeta.getFRQuestions().size());

        int iFRQ = s2Html.indexOf(_TAG_SECTION2_PAGE);
        bw.write(s2Html.substring(0, iFRQ));
        int nPages = gMeta.genFRQHtml(bw, _section2Page, solutions);
        bw.write(s2Html.substring(iFRQ + _TAG_SECTION2_PAGE.length()));
        return 1 + nPages;
    }

    private int genAppendix(BufferedWriter bw, GMeta gMeta) throws IOException {
        if (gMeta.getAppendix().size() == 0) {
            return 0;
        }
        
        String apxHtml = _appendix
            .replaceAll("#TNAME#", gMeta.getName());
        int iAPX = apxHtml.indexOf(_TAG_APPENDIX_PAGE);
        bw.write(apxHtml.substring(0, iAPX));
        int nPages = gMeta.genApxHtml(bw, _appendixPage);
        bw.write(apxHtml.substring(iAPX + _TAG_APPENDIX_PAGE.length()));
        return nPages;
    }
    // #endregion: [private methods] Writing HTML web document parts

    public WebDoc(List<String> lines) {
        Queue<String> qLines = new LinkedList<String>(lines);
        _style = loadBlock(_TAG_STYLE, qLines);
        loadAnswers(qLines);
        loadSection1(qLines);
        loadSection2(qLines);
        loadAppendix(qLines);
    }

    public void genIndexHtml(GMeta gMeta, Path pRoot) throws IOException {
        Path pIndexHtml = Paths.get(pRoot.toString(), "index.html");
        BufferedWriter bw = Files.newBufferedWriter(pIndexHtml);
        // fill in the styling portion
        bw.write(_style);
        // fill in the section 1 answers
        genSection1Html(bw, gMeta, true);
        // fill in the section 2 pages
        genSection2Html(bw, gMeta, true);
        // fill in the appendix pages
        genAppendix(bw, gMeta);
        bw.close();
    }

    public void genTestHtml(GMeta gMeta, Path pTest) throws IOException {
        Path pTestHtml = Paths.get(pTest.toString(), "test.html");
        BufferedWriter bw = Files.newBufferedWriter(pTestHtml);
        // fill in the styling portion
        bw.write(_style);
        // fill in the booklet
        int nPages = genBookletHtml(bw, gMeta);
        if (nPages % 2 != 0) {
            bw.write(_PRINT_BREAK);
            nPages++;
        }
        // fill in the appendix pages
        if (gMeta.getAppendix().size() != 0) {
            nPages += genAppendix(bw, gMeta);
            if (nPages % 2 != 0) {
                bw.write(_PRINT_BREAK);
                nPages++;
            }
        }
        // fill in the section 1 questions
        nPages += genSection1Html(bw, gMeta, false);

        // fill in the section 2 questions, if there are any
        if (gMeta.getFRQCount() > 0) {
            if (nPages % 2 != 0) {
                bw.write(_PRINT_BREAK);
                nPages++;
            }
            // fill in the section 2 pages
            nPages += genSection2Html(bw, gMeta, false);
        }
        bw.close();

        nPages = 0;
        Path pAnswersHtml = Paths.get(pTest.toString(), "answers.html");
        bw = Files.newBufferedWriter(pAnswersHtml);
        // fill in the styling portion
        bw.write(_style);
        // fill in the section 1 answers
        genSection1Html(bw, gMeta, true);
        // fill in the section 2 answers
        genSection2Html(bw, gMeta, true);
        // fill in the appendix pages
        genAppendix(bw, gMeta);
        bw.close();
    }
}
