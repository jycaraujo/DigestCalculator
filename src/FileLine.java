public class FileLine {
    public int lineNumber;
    public String Name;
    public String DigestType1;
    public String DigestHEX1;
    public String DigestType2;
    public String DigestHEX2;

    public FileLine()
    {
    }
    
    public String toString(){
        String text = Name + " " + DigestType1 + " " + DigestHEX1;
        if (DigestType2 != null) {
            text = text +  " " + DigestType2 + " " + DigestHEX2;
        }
        return text;
    }
}