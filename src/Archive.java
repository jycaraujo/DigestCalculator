public class Archive {

    public String FileName;
    public String Path;
    public byte[] CalculatedDigest;
    public String CalculatedDigestHEX;
    public String Status;

    public Archive() {
        FileName = "";
        Path = "";
        Status = "";
        CalculatedDigestHEX = "";
    }
}