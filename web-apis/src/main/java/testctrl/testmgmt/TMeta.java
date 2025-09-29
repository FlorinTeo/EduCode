package testctrl.testmgmt;

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
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class TMeta {
    private static final Gson _GSON = new GsonBuilder().setPrettyPrinting().create();

    private String _name;
    private String _version;

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
    /**
     * Maps the name/path of the files generated for this test.
     * I.e:
     *     "_files": {
     *         "test": "test_EC5E.html",
     *         "answer": "answers_C918.html",
     *     },
     */
    private Map<String, String> _files;
    /**
     * Maps the variant name to the TMeta object of that variant.
     * I.e:
     *     "_variants": {
     *         "v1": {
     *             "_name": "2025_09_19_APCSA_Unit1",
     *             "_version": "v1",
     *         "_display": {
     *             "1": "ap1.Q4 eabcd",
     *             "2": "ap1.Q5 decba",
     *             "3": "ap1.Q1 acbed",
     *             "4": "ap1.Q6 cdbae",
     *             "5": "ap1.Q3 ceabd",
     *             "6": "ap1.Q2 abecd"
     *         },
     *         ...
     */
    private Map<String, TMeta> _variants;
    private List<Question> _mcQuestions;
    private List<Question> _frQuestions;
    private List<Question> _appendix;
    private String _notes;
    private boolean _isAnonymized;

    public static String genUUIDName(String namePrefix) {
        String uuid = UUID.randomUUID().toString().toUpperCase();
        return namePrefix.startsWith(".")
            ? namePrefix + "_" + uuid.substring(9, 13)
            : namePrefix.replaceFirst("\\.", "_" + uuid.substring(9, 13) + ".");
    }

    private void reset() {
        _name = "";
        _version = "";
        _display = new TreeMap<String, String>(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2)
            {
                return (o1.matches("-?\\d+(\\.\\d+)?") && o2.matches("-?\\d+(\\.\\d+)?"))
                    ? Integer.compare(Integer.parseInt(o1), Integer.parseInt(o2))
                    : o1.compareTo(o2);
            }
        });
        _files = new TreeMap<String, String>();
        _variants = new TreeMap<String, TMeta>();
        _mcQuestions = new LinkedList<Question>();
        _frQuestions = new LinkedList<Question>();
        _appendix = new LinkedList<Question>();
        _notes = "";
        _isAnonymized = false;
    }

    public TMeta() {
        reset();
    }

    public TMeta(Path pMetaDir) throws IOException {
        //Path pMeta = Paths.get(pMetaDir.toString(), ".meta");
        Path pMeta = Files.list(pMetaDir)
            .filter(p -> p.getFileName().toString().startsWith(".meta"))
            .findFirst()
            .orElseThrow(() -> new IOException("No .meta file found"));
        String jsonMeta = String.join("\n", Files.readAllLines(pMeta));
        TMeta loaded  = _GSON.fromJson(jsonMeta,TMeta.class);
        _name = loaded._name;
        _version = loaded._version;
        _display = loaded._display;
        _files = loaded._files;
        _variants = loaded._variants;
        _mcQuestions = loaded._mcQuestions;
        _frQuestions = loaded._frQuestions;
        _appendix = loaded._appendix;
        _notes = loaded._notes;
        _isAnonymized = loaded._isAnonymized;
    }

    public TMeta(String name, String version, List<Question> qList) {
        reset();
        _name = name;
        _version = version;
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

    public String getVersion() {
        return _version;
    }

    public int getMCQCount() {
        return _display.keySet().size();
    }

    public String getMCQTime() {
        // 2.25 minutes per question rounded up to the next 10 minutes
        double minMCQ = getMCQCount() * 2.25;
        return (int)(Math.ceil(minMCQ / 10) * 10) + " minutes";
    }

    public String getMCQPct() {
        // 100% if no free response questions, 40% otherwise
        return getFRQCount() == 0 
            ? "100%" 
            : "40%";
    }

    public List<Question> getMCQuestions() {
        return _mcQuestions;
    }

    public int getFRQCount() {
        return _frQuestions.size();
    }

    public String getFRQTime() {
        // 22.5 minutes per question rounded up to the next 10 minutes
        double minFRQ = getFRQCount() * 22.5;
        return (int)(Math.ceil(minFRQ / 10) * 10) + " minutes";
    }

    public String getFRQPct() {
        // 100% if no multiple choice questions, 60% otherwise
        return getMCQCount() == 0 
            ? "100%" 
            : "60%";
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

    public List<Question> getQuestions(int frqIndex) {
        List<Question> allQuestions = new LinkedList<Question>();
        // add all multiple choice questions
        allQuestions.addAll(_mcQuestions);
        // add the free response question (if any) at the given index
        Question[] frQuestions = _frQuestions.toArray(new Question[0]);
        if (frQuestions.length > 0) {
            allQuestions.add(frQuestions[frqIndex % frQuestions.length]);
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
        Path pMeta = Files.list(pMetaDir)
            .filter(p -> p.getFileName().toString().startsWith(".meta"))
            .findFirst()
            .orElse(Paths.get(pMetaDir.toString(), genUUIDName(".meta")));
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
            String qID = _isAnonymized ? "" : q.getName();
            String hAppendix = q.editApxHtml(format, qID);
            bw.write(hAppendix);
            bw.newLine();
            nPages++;
        }
        return nPages;
    }

    public String getFile(String fileID) {
        return _files.get(fileID);
    }

    public void setFile(String fileID, String filePath) {
        _files.put(fileID, filePath);
    }

    public TMeta getVariant(String variantID) {
        return _variants.get(variantID);
    }

    public void setVariant(String variantID, TMeta variantMeta) {
        _variants.put(variantID, variantMeta);
    }
}
