public class FileLine {
    public int lineNumber;
    public String Name;
    public String DigestType1;
    public String DigestHEX1;
    public String DigestType2;
    public String DigestHEX2;

    public FileLine()
    {
    	lineNumber = -1;
    	Name = "";
    	DigestType1 = "";
    	DigestType2 = "";
    	DigestHEX1 = "";
    	DigestHEX2 = "";
    }
    
    public String toString(){
        String text = Name + " " + DigestType1 + " " + DigestHEX1;
        if (!DigestType2.equals("")) {
            text = text +  " " + DigestType2 + " " + DigestHEX2;
        }
        return text;
    }
}