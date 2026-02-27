package testctrl.testmgmt;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Generator {
    public static final String[] VARIANTS = { "v1", "v2", "v3", "v4" };

    private Path _pRoot;
    private Map<String, Question> _qMap;
    private List<Question> _qList;
    private WebDoc _webDoc;
    private static Pattern _regex = Pattern.compile("^(ap|ds)(\\d+)([^.]*)(\\.)([QPA])(\\d+)(.*)$");

    private List<Question> sort(List<Question> lq) {
        Map<String, Question> map = new TreeMap<>();
        for (int i = lq.size(); i > 0; i--) {
            Question q = lq.remove(0);
            Matcher m = _regex.matcher(q.getName());
            String key = null;
            // if this question name matches the expected pattern...
            if (m.find()) {
                // ... extract components from the name and build a key
                String group = m.group(1); // "ap" or "ds"
                String unitNumStr = m.group(2); // unit number, i.e. "4"
                String unitSuffixStr = m.group(3); // an optional suffix, i.e "b" or "-7"
                String questionNumStr = m.group(6); // the question number, i.e. "11"
                String questionSuffixStr = m.group(7); // the suffix following question number.
                int unitNumber = Integer.parseInt(unitNumStr);
                int questionNumber = Integer.parseInt(questionNumStr);
                key = String.format("%s_%03d_%s_%03d_%s", group, unitNumber, unitSuffixStr, questionNumber, questionSuffixStr);
            }
            // if we managed to build a key for the question, and there's no collision in the map..
            if (key != null && !map.containsKey(key)) {
                // .. add the question to the map
                map.put(key, q);
            } else {
                // .. otherwise put question back in the queue such that it doesn't get lost
                lq.add(q);
            }
        }
        // Final list of questions is what's been sorted in the map, followed by all non-matching questions.
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

    public Collection<QHeader> getQRecs() {
        List<QHeader> qRecs = new LinkedList<QHeader>();
        for (Question q : _qList) {
            qRecs.add(q.getQHeader());
        }
        return qRecs;
    }

    /**
     * Deletes the entire test folder, with all its content.
     * @param pTest - Path to be deleted
     * @return true if successful, false otherwise.
     * @throws IOException 
     */
    public void delTest(String testName, boolean silent) throws IOException {
        Path pTest = Paths.get(_pRoot.toString(), testName);
        if (Files.exists(pTest)) {
            Files.walk(pTest)
                .sorted(Comparator.reverseOrder())
                .forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (IOException e) {
                        throw new RuntimeException(String.format("Failed to delete test '%s': %s!", testName, e.getMessage()));
                    }
                });
        } else if (!silent) {
            throw new RuntimeException(String.format("Missing test '%s'. Nothing to delete!", testName));
        }
    }

    /**
     * Generates .meta and index.html files for the given test
     * @throws IOException
     */
    public TMeta genTest(String testName, String[] qIDs, boolean genVariants) throws IOException {
        Path pTest = Paths.get(_pRoot.toString(), testName);
        TMeta tMeta;
        if (!Files.exists(pTest)) {
            List<Question> qList;
            if (qIDs.length == 0) {
                throw new IllegalArgumentException("Empty questions set are illegal!");
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
            tMeta = new TMeta(testName, "", qList);
            tMeta.adjustPath("../.template/");
            tMeta.anonymize(false);
        } else {
            tMeta = new TMeta(pTest);
        }
        tMeta = _webDoc.genTestHtml(tMeta, pTest);

        // generate variants, if requested
        if (genVariants) {
            int frqIndex = 0;
            for(String variant : VARIANTS) {
                TMeta vMeta = genTestVariant(tMeta, variant, frqIndex++);
                tMeta.setVariant(variant, vMeta);
            }
        }

        tMeta.save(pTest);
        return tMeta;
    }

    /**
    * Generates .meta and index.html files for one variant of the given test
    * @throws IOException
    */
    private TMeta genTestVariant(TMeta tMeta, String variantName, int frqIndex) throws IOException {
        Path pVariant = Paths.get(_pRoot.toString(), tMeta.getName(), variantName);
        TMeta vMeta;
        if (!Files.exists(pVariant)) {
            vMeta = new TMeta(tMeta.getName(), variantName, tMeta.getQuestions(frqIndex));
            vMeta.adjustPath("../../.template/");
            vMeta.anonymize(true);
        } else {
            vMeta = new TMeta(pVariant);
        }
        vMeta = _webDoc.genTestHtml(vMeta, pVariant);
        vMeta.save(pVariant);
        return vMeta;
    }

    /**
     * Gets a specific question from the database.
     * @param qID - question ID (i.e. "ap4.Q6")
     * @return the Question instance for the targeted question ID.
     */
    public Question getQuestion(String qID) {
        return _qMap.get(qID);
    }
}
