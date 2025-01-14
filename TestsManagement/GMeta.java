package TestsManagement;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GMeta {
    private static final Gson _GSON = new GsonBuilder().setPrettyPrinting().create();

    private String _name;

    /**
     * Maps the name/index of a question with the specific question and its choices in the chosen (same/shuffled) order.
     * I.e:  
     *     "_display": {
     *         "1": "Q3 dbace",
     *         "2": "Q2 bacde",
     *         "3": "Q1 dbace",
     *         "4": "Q4 dcabe"
     *     },
     */
    private Map<String, String> _display;
    private List<Question> _mcQuestions;
    private List<Question> _frQuestions;
    private List<Question> _appendix;
    private String _notes;
    private boolean _isAnonymized;

    private void reset() {
        _name = "";
        _display = new TreeMap<String, String>(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2)
            {
                return (o1.matches("-?\\d+(\\.\\d+)?") && o2.matches("-?\\d+(\\.\\d+)?"))
                    ? Integer.compare(Integer.parseInt(o1), Integer.parseInt(o2))
                    : o1.compareTo(o2);
            }
        });
        _mcQuestions = new LinkedList<Question>();
        _frQuestions = new LinkedList<Question>();
        _appendix = new LinkedList<Question>();
        _notes = "";
        _isAnonymized = false;
    }

    public GMeta() {
        reset();
    }

    public GMeta(Path pMetaDir) throws IOException {
        Path pMeta = Paths.get(pMetaDir.toString(), ".meta");
        String jsonMeta = String.join("\n", Files.readAllLines(pMeta));
        GMeta loaded  = _GSON.fromJson(jsonMeta,GMeta.class);
        _name = loaded._name;
        _display = loaded._display;
        _mcQuestions = loaded._mcQuestions;
        _frQuestions = loaded._frQuestions;
        _appendix = loaded._appendix;
        _notes = loaded._notes;
        _isAnonymized = loaded._isAnonymized;
    }

    public GMeta(String name, List<Question> qList) {
        reset();
        _name = name;
        for(int i = 0; i < qList.size(); i++) {
            Question q = new Question(qList.get(i));
            switch(q.getType().toLowerCase()) {
                case Question._MCQ:
                    _display.put(q.getName(), q.getMetaLine(false));
                    _mcQuestions.add(q);
                    break;
                case Question._MCB:
                    for(Question bQ : q.getBQuestions()) {
                        _display.put(bQ.getName(), bQ.getMetaLine(false));
                    }
                    _mcQuestions.add(q);
                    break;
                case Question._FRQ:
                    _frQuestions.add(q);
                    break;
                case Question._APX:
                    _appendix.add(q);
                    break;
            }
        }
        _notes = "";
        _isAnonymized = false;
    }

    public String getName() {
        return _name;
    }

    public int getMCQCount() {
        return _display.keySet().size();
    }

    public List<Question> getMCQuestions() {
        return _mcQuestions;
    }

    public int getFRQCount() {
        return _frQuestions.size();
    }
    
    public List<Question> getFRQuestions() {
        return _frQuestions;
    }

    public List<Question> getAppendix() {
        return _appendix;
    }

    public List<Question> getQuestions(List<String> excFRQs) {
        List<Question> allQuestions = new LinkedList<Question>();
        // add all multiple choice questions
        allQuestions.addAll(_mcQuestions);
        // add all the non-filtered free response question
        for(int i = 0; i < _frQuestions.size(); i++) {
            if (excFRQs.contains(""+ (i + 1))) {
                continue;
            }
            allQuestions.add(_frQuestions.get(i));
        }
        // add all appendix pages
        allQuestions.addAll(_appendix);
        return allQuestions;
    }

    public void adjustPath(String pathPrefix) {
        for(Question q : _mcQuestions) {
            q.adjustPath(pathPrefix);
        }
        for(Question q : _frQuestions) {
            q.adjustPath(pathPrefix);
        }
        for(Question q : _appendix) {
            q.adjustPath(pathPrefix);
        }
    }

    public String getPathPrefix() {
        return _mcQuestions.get(0).getPathPrefix();
    }

    public void anonymize(boolean randomize) {
        if (randomize) {
            _mcQuestions = Question.shuffle(_mcQuestions);
        }
        _display.clear();
        int i = 0;
        LinkedList<Question> mcQuestions = new LinkedList<Question>(_mcQuestions);
        while(!mcQuestions.isEmpty()) {
            Question q = mcQuestions.remove(0);
            if (q.getType().equalsIgnoreCase(Question._MCB)) {
                q.prependBundle(mcQuestions);
            } else {
                _display.put("" + (i+1), q.getMetaLine(randomize));
                i++;
            }
        }
        _isAnonymized = true;
    }

    public void save(Path pMetaDir) throws IOException {
        if (!_name.equals(".") && !Files.exists(pMetaDir)) {
            Files.createDirectories(pMetaDir);
        }

        Path pMeta = Paths.get(pMetaDir.toString(), ".meta");
        BufferedWriter bw = Files.newBufferedWriter(pMeta);
        bw.write(_GSON.toJson(this));
        bw.close();
    }

    public int genMCQHtml(BufferedWriter bw, String formatMCB, String formatMCQ, boolean answers) throws IOException {
        int pxSum = 0;
        int nPages = 1;
        int i = 0;
        LinkedList<Question> mcQuestions = new LinkedList<Question>(_mcQuestions);
        while(!mcQuestions.isEmpty()) {
            Question q = mcQuestions.remove(0);
            String qDisplayID = q.getDisplayName(_isAnonymized, i+1);
            String hSection1Q = "";
            if (q.getType().equalsIgnoreCase(Question._MCB)) {
                hSection1Q = q.editMCBHtml(formatMCB, qDisplayID, answers);
                q.prependBundle(mcQuestions);
            } else if (q.getType().equalsIgnoreCase(Question._MCQ)) {
                String qMetaLine = _display.get(qDisplayID);
                hSection1Q = q.editMCQHtml(formatMCQ, qDisplayID, qMetaLine, answers);
                i++;
            }
            int pxHeight = answers ? q.getPxHeightA() : q.getPxHeightQ();
            if (pxSum + pxHeight > WebDoc._MAX_PX_PER_PAGE) {
                nPages++;
                bw.write(WebDoc._PRINT_BREAK);
                bw.newLine();
                pxSum = pxHeight;
            } else {
                pxSum += pxHeight;
            }
            bw.write(hSection1Q);
            bw.newLine();
        }
        return nPages;
    }

    public int genFRQHtml(BufferedWriter bw, String format, boolean solutions) throws IOException {
        int nPages = 0;
        for (int i = 0; i < _frQuestions.size(); i++) {
            Question q = _frQuestions.get(i);
            String qID = _isAnonymized ? "" + (i+1) : q.getName();
            String hSection2P = q.editFRQHtml(format, qID + ".", solutions);
            bw.write(hSection2P);
            bw.newLine();
            int nPixels = solutions ? q.getPxHeightA() : q.getPxHeightQ();
            nPages += nPixels / WebDoc._MAX_PX_PER_PAGE + 1;
        }
        return nPages;
    }

    public int genApxHtml(BufferedWriter bw, String format) throws IOException {
        int nPages = 0;
        for (int i = 0; i < _appendix.size(); i++) {
            Question q = _appendix.get(i);
            String qID = _isAnonymized ? "" + (i+1) : q.getName();
            String hAppendix = q.editApxHtml(format, qID);
            bw.write(hAppendix);
            bw.newLine();
            nPages++;
        }
        return nPages;
    }
}
