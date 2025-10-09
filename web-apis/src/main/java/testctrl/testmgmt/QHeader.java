package testctrl.testmgmt;

public class QHeader {
    public String _qName;
    public String _qType;
    public int _qCount;

    public QHeader(String qName, String qType) {
        _qName = qName;
        _qType = qType;
        _qCount = 1;
    }
}
