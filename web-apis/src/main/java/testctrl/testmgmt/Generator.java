package testctrl.testmgmt;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Generator {
    private Path _pRoot;
    private Map<String, Question> _qMap;
    private List<Question> _qList;
    private WebDoc _webDoc;
    private static Pattern regex = Pattern.compile("(?:(ap|ds)(\\d+)[^.]*)?\\.[QPA](\\d+)");

    private List<Question> sort(List<Question> lq) {
        Map<String, Question> map = new TreeMap<>();
        for (int i = lq.size(); i > 0; i--) {
            Question q = lq.remove(0);
            Matcher m = regex.matcher(q.getName());
            if (m.find()) {
                // Extract the first and second numbers
                String group = m.group(1) != null ? m.group(1) : ""; // optional "ap" or "ds"
                String firstNumber = m.group(2) != null ? m.group(2) : "0"; // Optional first number
                String secondNumber = m.group(3); // Always present
                int unitNumber = Integer.parseInt(firstNumber);
                int questionNumber = Integer.parseInt(secondNumber);
                String key = String.format("%s_%03d_%03d", group, unitNumber, questionNumber);
                map.put(key, q);
            } else {
                lq.add(q);
            }
        }
        Collection<Question> c = map.values();
        c.addAll(lq);
        return new LinkedList<>(c);
    }

    /**
     * Loads the list of questions found in the ".template" sub-folder.
     * @throws IOException
     */
    private void loadQuestions(Path pTemplate) throws IOException {
        List<Question> mcq = new LinkedList<Question>();
        List<Question> frq = new LinkedList<Question>();
        List<Question> apx = new LinkedList<Question>();

        for (Path qDir : Files.walk(pTemplate, 1).toArray(Path[]::new)) {
            if (Files.isDirectory(qDir) && !qDir.getFileName().toString().startsWith(".")) {
                try {
                    // Load the question from the given directory
                    Question question = new Question(qDir);
                    // Dispatch question to its specific bucket
                    switch(question.getType().toLowerCase()) {
                        case Question._MCQ: // multiple-choice question or bundle
                        case Question._MCB:
                            mcq.add(question);
                            break;
                        case Question._FRQ: // free-response question
                            frq.add(question);
                            break;
                        case Question._APX: // appendix
                            apx.add(question);
                            break;
                        default:
                            throw new RuntimeException("Invalid question type");
                    }
                } catch(Exception e) {
                    System.out.println(qDir);
                    throw e;
                }
            }
        }

        _qList = new LinkedList<Question>();
        _qList.addAll(sort(mcq));
        _qList.addAll(sort(frq));
        _qList.addAll(sort(apx));
        _qMap = new TreeMap<String, Question>();
        for(Question q : _qList) {
            _qMap.put(q.getName(), q);
        }
    }

    /**
     * Constructs a Generator object targetting the given {root} folder. The root is
     * expected to contain a ".template" subfolder, containing ".index.html" template and Questions subfolder,
     * each with their own ".meta" and ".png" files.
     * @param root - working folder for this generator.
     * @throws IOException
     */
    public Generator(String root) throws IOException {
        _pRoot = Paths.get(root);
        Path pTemplate = Paths.get(root, ".template");
        Path phIndex = Paths.get(root,".template", ".index.html");

        if (!Files.isDirectory(_pRoot) || !Files.isDirectory(pTemplate)) {
            throw new IOException("Template folder is missing or invalid!");
        }

        loadQuestions(pTemplate);
        _webDoc = new WebDoc(Files.readAllLines(phIndex));
    }

    /**
     * Gets the absolute path to root.
     * @return absolute path to root.
     */
    public String getRoot() {
        return _pRoot.toAbsolutePath().toString();
    }

    public Collection<QHeader> getQRecs() {
        List<QHeader> qRecs = new LinkedList<QHeader>();
        for (Question q : _qList) {
            qRecs.add(q.getQHeader());
        }
        return qRecs;
    }
    /**
     * Generates .meta and index.html for the full set of questions loaded in this generator.
     * @throws IOException
     */
    public void genRoot(boolean regenMeta) throws IOException {
        GMeta mRoot;
        if (regenMeta) {
            mRoot = new GMeta(".", "", _qList);
            mRoot.adjustPath(".template/");
            mRoot.save(_pRoot);
        } else {
            mRoot = new GMeta(_pRoot);
        }
        _webDoc.genIndexHtml(mRoot, _pRoot);
    }

    /**
     * Generates .meta and index.html files for the given test
     * @throws IOException
     */
    public void genTest(String testName, String[] qIDs, boolean regenMeta) throws IOException {
        Path pTest = Paths.get(_pRoot.toString(), testName);
        GMeta mTest;
        if (regenMeta) {
            List<Question> qList;
            if (qIDs.length == 0) {
                qList = _qList;
            } else {
                HashSet<Question> qSet = new HashSet<Question>();
                qList = new LinkedList<Question>();
                // make sure the question exists and is not provided more than once in the list
                for(String qID : qIDs) {
                    if (!_qMap.containsKey(qID) || qSet.contains((Object)qID)) {
                        throw new IllegalArgumentException(String.format("Question %s is non-existent or duplicated!", qID));
                    }
                    qSet.add(_qMap.get(qID));
                    qList.add(_qMap.get(qID));
                }
            }
            mTest = new GMeta(testName, "", qList);
            mTest.adjustPath("../.template/");
            mTest.anonymize(false);
            mTest.save(pTest);
        } else {
            mTest = new GMeta(pTest);
        }
        _webDoc.genTestHtml(mTest, pTest);
    }

    /**
    * Generates .meta and index.html files for each variant of the given test
    * @throws IOException
    */
    public void genTestVariants(String testName, String[] vIDs, List<String> excFRQs, boolean regenMeta) throws IOException {
        Path pTest = Paths.get(_pRoot.toString(), testName);
        GMeta mTest = new GMeta(pTest);
        for(int i = 0; i < vIDs.length; i++) {
            Path pVariant = Paths.get(pTest.toString(), vIDs[i]);
            GMeta mVariant;
            if (regenMeta) {
                mVariant = new GMeta(mTest.getName(), vIDs[i], mTest.getQuestions(excFRQs));
                mVariant.adjustPath("../../.template/");
                mVariant.anonymize(true);
                mVariant.save(pVariant);
            } else {
                mVariant = new GMeta(pVariant);
            }
            _webDoc.genTestHtml(mVariant, pVariant);
        }
    }

    public Question getQuestion(String qID) {
        return _qMap.get(qID);
    }
}
