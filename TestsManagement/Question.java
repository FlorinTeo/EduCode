package TestsManagement;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.imageio.ImageIO;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Question {
    private static final Gson _GSON = new GsonBuilder().setPrettyPrinting().create();

    public static final String _MCQ = "mcq";
    public static final String _MCB = "mcb";
    public static final String _FRQ = "frq";
    public static final String _APX = "apx";

    private QMeta _meta;
    private int _pxHeightQ;
    private int _pxHeightA;

    private List<Question> _bQuestions = null;

     /**
     * Utility method to shuffle a given list into another one.
     * Method does not modify its {list} parameter.
     * @param list - list to be shuffled.
     * @return shuffled list.
     */
    public static <T> List<T> shuffle(List<T> list) {
        ArrayList<T> aList = new ArrayList<T>();
        for (T item : list) {
            int i = (int)((aList.size() + 1) * Math.random());
            aList.add(i, item);
        }
        return aList;
    }

    public void prependBundle(LinkedList<Question> qList) {
        if (_bQuestions != null) {
            Stack<Question> bStack = new Stack<Question>();
            for(Question bQ : _bQuestions) {
                bStack.push(bQ);
            }
            while(!bStack.isEmpty()) {
                qList.add(0, bStack.pop());
            }
        }
    }
    
    /**
     * Deep cloning constructor.
     * @param q - Question object to be cloned.
     */
    public Question(Question q) {
        _meta = new QMeta(q._meta);
        _pxHeightQ = q._pxHeightQ;
        _pxHeightA = q._pxHeightA;
        if (q._bQuestions != null) {
            _bQuestions = new LinkedList<>();
            for(Question bQuestion : q._bQuestions) {
                _bQuestions.add(new Question(bQuestion));
            }
        }
    }

    public Question(Path pQuestion) throws IOException {
        Path pMeta = Paths.get(pQuestion.toString(), ".meta");
        String jsonMeta = String.join("\n", Files.readAllLines(pMeta));
        _meta = _GSON.fromJson(jsonMeta, QMeta.class);
        loadPxHeight(pQuestion.toString());
        // if this is a bundle, load all questions in this bundle
        if (_meta.type.equalsIgnoreCase(_MCB)) {
            _bQuestions = new LinkedList<Question>();
            for(String pBQuestion : _meta.questions) {
                Path pbQuestion = Paths.get(pQuestion.toString(), pBQuestion);
                _bQuestions.add(new Question(pbQuestion));
            }
        }
    }

    private int pngHeight(Path pPng) throws IOException {
        return ImageIO.read(pPng.toFile()).getHeight();
    }

    private void loadPxHeight(String relPath) throws IOException {
        switch (_meta.type.toLowerCase()) {
            case _MCQ:
                _pxHeightQ = pngHeight(Paths.get(relPath, _meta.question));
                _pxHeightA = pngHeight(Paths.get(relPath, _meta.answer));

                for(String choice : _meta.choices.values()) {
                    int pxHeightC = pngHeight(Paths.get(relPath, choice));
                    _pxHeightQ += pxHeightC;
                    _pxHeightA += pxHeightC;
                }
                break;
            case _MCB:
                _pxHeightQ = pngHeight(Paths.get(relPath, _meta.question));
                _pxHeightA = pngHeight(Paths.get(relPath, _meta.answer));
                break;
            case _FRQ:
                _pxHeightQ = 0;
                for(String textPage : _meta.textPages) {
                    _pxHeightQ += pngHeight(Paths.get(relPath, textPage));
                }
                _pxHeightA = 0;
                for(String solPage : _meta.solutionPages) {
                    _pxHeightA += pngHeight(Paths.get(relPath, solPage));
                }
                break;
            case _APX:
                _pxHeightQ = 0;
                for(String textPage : _meta.textPages) {
                    _pxHeightQ += pngHeight(Paths.get(relPath, textPage));
                }
                break;
        }
    }

    public String getName() {
        return _meta.name;
    }

    public String getDisplayName(boolean isAnonimized, int qIdx) {
        return isAnonimized
            ? _meta.type.equalsIgnoreCase(_MCB)
                ? String.format("%d-%d", qIdx, qIdx + _bQuestions.size()-1)
                : String.format("%d",qIdx)
            : _meta.name;
    }

    public String getType() {
        return _meta.type;
    }

    public String getMetaLine(boolean shuffle) {
        List<String> choices = new LinkedList<String>(_meta.choices.keySet());
        if (shuffle) {
            choices = shuffle(choices);
        }
        return _meta.name + " " + String.join("", choices);
    }

    public int getPxHeightQ() {
        return _pxHeightQ;
    }

    public int getPxHeightA() {
        return _pxHeightA;
    }

    public String getPathPrefix() {
        String pathPrefix = "";
        int iQFile = _meta.question.indexOf(_meta.name);
        if (iQFile != -1) {
            pathPrefix = _meta.question.substring(0, iQFile);
        }
        return pathPrefix;
    }

    public List<Question> getBQuestions() {
        return _bQuestions;
    }

    public void adjustPath(String pathPrefix) {
        switch(_meta.type.toLowerCase()) {
            case _MCQ:
                String qFile = Paths.get(_meta.question).toFile().getName();
                String aFile = Paths.get(_meta.answer).toFile().getName();
                _meta.question = String.format("%s%s/%s", pathPrefix, _meta.name, qFile);
                _meta.answer = String.format("%s%s/%s", pathPrefix, _meta.name, aFile);
                for(Map.Entry<String, String> kvp : _meta.choices.entrySet()) {
                    // choice file name
                    String cFile = Paths.get(kvp.getValue()).toFile().getName();
                    _meta.choices.put(kvp.getKey(), String.format("%s%s/%s", pathPrefix, _meta.name, cFile));
                }
                break;
            case _MCB:
                qFile = Paths.get(_meta.question).toFile().getName();
                aFile = Paths.get(_meta.answer).toFile().getName();
                _meta.question = String.format("%s%s/%s", pathPrefix, _meta.name, qFile);
                _meta.answer = String.format("%s%s/%s", pathPrefix, _meta.name, aFile);
                String bPathPrefix = String.format("%s%s/", pathPrefix, _meta.name);
                for(Question bQuestion : _bQuestions) {
                    bQuestion.adjustPath(bPathPrefix);
                }
                break;
            case _FRQ:
            case _APX:
                for(int i = 0; i < _meta.textPages.size(); i++) {
                    String tpFile = Paths.get(_meta.textPages.get(i)).toFile().getName();
                    _meta.textPages.set(i, String.format("%s%s/%s", pathPrefix, _meta.name, tpFile));
                }
                if (_meta.type.equalsIgnoreCase("apx")) {
                    break;
                }
                for(int i = 0; i < _meta.solutionPages.size(); i++) {
                    String spFile = Paths.get(_meta.solutionPages.get(i)).toFile().getName();
                    _meta.solutionPages.set(i, String.format("%s%s/%s", pathPrefix, _meta.name, spFile));
                }
                break;
        }
    }

    public String editMCBHtml(String hSection1Q, String qID, boolean isAnswer) {
        hSection1Q = hSection1Q
            .replace("#QID#", qID)
            .replace("#QPNG#", isAnswer ? _meta.answer : _meta.question);
        return hSection1Q;
    }

    public String editMCQHtml(String hSection1Q, String qID, String metaLine, boolean isAnswer) {
        String metaChoices = metaLine.split(" ")[1];
        hSection1Q = hSection1Q
            .replaceFirst("#QID#", qID)
            .replaceFirst("#QPNG#", isAnswer ? _meta.answer : _meta.question);
        for(char c : metaChoices.toCharArray()) {
            String cPng = _meta.choices.get(""+c);
            hSection1Q = isAnswer && _meta.correct.equals(""+c)
                ? hSection1Q.replaceFirst("#ANS#", "ansTd")
                : hSection1Q.replaceFirst("#ANS#", "refTd");
            hSection1Q = hSection1Q.replaceFirst("#CPNG#", cPng);
        }
        return hSection1Q;
    }

    public String editFRQHtml(String format, String qID, boolean solutions) {
        String hSection2 = "";
        List<String> pages = solutions ? _meta.solutionPages : _meta.textPages; 
        boolean firstPage = true;
        for(String page : pages) {
            hSection2 += format
                .replace("#PID#", firstPage ? qID : "")
                .replace("#PPNG#", page);
            firstPage = false;
        }
        return hSection2;
    }

    public String editApxHtml(String format, String qID) {
        String hAppendix = "";
        for(String page : _meta.textPages) {
            hAppendix += format
                .replace("#AID#", "")
                .replace("#APNG#", page);
        }
        return hAppendix;
    }

    @Override
    public String toString() {
        return String.format("%s:pxQ=%d,pxA=%d", _meta.name, _pxHeightQ, _pxHeightA);
    }
}
