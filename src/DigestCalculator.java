import java.io.*;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import java.util.HashMap;
import java.util.function.Consumer;


public class DigestCalculator {

    private String digestListFilePath;
    private String digestType;
    private List<Archive> files;

    public final List<FileLine> digestsFileList = new ArrayList<FileLine>();
    public final HashMap<String, FileLine> digestsFileHashMap = new HashMap<String,FileLine>();

    public static void main(String[] args) throws Exception {
        if (args.length < 3) {
        	System.err.println("\nNúmero insuficiente de argumentos:");
            System.err.println("\nFormato: DigestCalculator Tipo_Digest Caminho_ArqListaDigest Caminho_Arq1 ... Caminho_ArqN");
            System.exit(1);
        } else if (!args[0].equals("MD5") && !args[0].equals("SHA1")) {
            System.err.println("\nPrimeiro argumento invalido!!");
            System.err.println("\nO primeiro argumento deve ser o tipo do digest (MD5 ou SHA1)");
            System.exit(1);
        }

        DigestCalculator digestCalculator = new DigestCalculator(args[0], args[1]);
        
        digestCalculator.GetArchives(args);
        digestCalculator.run();
    }
    
    public DigestCalculator(String digestType, String digestListFilePath) {
    	this.digestListFilePath = digestListFilePath;
    	this.digestType = digestType;
    }
    
    public void run() throws Exception{
    	 loadDigestListFile();
         calculateDigests();
         compareDigests();
         updateDigestFile();
         printReport();
    }
    
    private void printReport(){
    	for (Archive file : files) {
    		System.out.println(file.FileName+" "+this.digestType+" "+file.CalculatedDigestHEX+" ("+file.Status+")");
    	}
    }

    private void compareDigests() {
        for (Archive file : files) {
            for (Archive aux : files) {
                /* verificando se o digest calculado colide com o digest de outro arquivo de
                nome diferente encontrado nos arquivos fornecidos na linha de comando */
                if (!aux.FileName.endsWith(file.FileName) && aux.CalculatedDigestHEX.equals(file.CalculatedDigestHEX)) {
                    file.Status = "COLISION";
                    break;
                }
            }

            // se houve colisao, pula pro proximo arquivo
            if (file.Status.equals("COLISION"))
                continue;

            /* verificando se o digest calculado colide com o digest de outro arquivo de
                nome diferente encontrado no arquivo ArqListaDigest */
            for (FileLine fileLine : digestsFileList) {
                if (!fileLine.Name.equals(file.FileName)) {
                    // digest 1
                    if (fileLine.DigestType1.equals(digestType)
                            && fileLine.DigestHEX1.equals(file.CalculatedDigestHEX)) {
                        file.Status = "COLISION";
                        break;
                    }
                    // digest 2
                    else if (fileLine.DigestType2.equals(digestType)
                            && fileLine.DigestHEX2.equals(file.CalculatedDigestHEX)) {
                        file.Status = "COLISION";
                        break;
                    }
                } else {
                    // digest 1
                    if (fileLine.DigestType1.equals(digestType)) {
                        if (fileLine.DigestHEX1.equals(file.CalculatedDigestHEX))
                            file.Status = "OK";
                        else
                            file.Status = "NOT OK";
                    }

                    // digest 2
                    else if (fileLine.DigestType2.equals(digestType)) {
                        if (fileLine.DigestHEX2
                                .equals(file.CalculatedDigestHEX))
                            file.Status = "OK";
                        else
                            file.Status = "NOT OK";
                    }
                }
            }
            if (file.Status.equals("")) {
                file.Status = "NOT FOUND";
            }
        }

    }

    private void loadDigestListFile() throws IOException{
        Path path=Paths.get(digestListFilePath);  
        Consumer<String> lineConsumer = new Consumer<String>(){
            public void accept(String line) {
                String[] parts = line.split(" ");
                FileLine fileLine = new FileLine();
                fileLine.lineNumber = digestsFileList.size();
                fileLine.Name = parts[0];
                fileLine.DigestType1 = parts[1];
                fileLine.DigestHEX1 = parts[2];
                if(parts.length > 3){
                    fileLine.DigestType2 = parts[3];    
                    fileLine.DigestHEX2 = parts[4];
                }
                digestsFileHashMap.put(parts[0], fileLine);
                digestsFileList.add(fileLine);
            }
        };
        
        try(Stream<String> lines = Files.lines(path)){
            lines.forEach(lineConsumer);
            lines.close();
        }
    }
    
    private boolean listFileHasFilename(String filename){
        return digestsFileHashMap.containsKey(filename);
    }
    
    private void addDigestToDigestListFile (String filename, String digestType, String digestHEX)throws IOException {
        if (listFileHasFilename(filename)){
            // add second hash
            FileLine line = digestsFileHashMap.get(filename);
            line.DigestType2 = digestType;
            line.DigestHEX2 = digestHEX;
        }
        else {
            // add new line
            FileLine newLine = new FileLine();
            newLine.Name = filename;
            newLine.DigestType1 = digestType;
            newLine.DigestHEX1 = digestHEX;
            digestsFileList.add(newLine);
        }
    }
    private void updateDigestList()throws IOException{
        for (Archive file : files) {
            if(file.Status.equals("NOT FOUND")){
                addDigestToDigestListFile(file.FileName, digestType, file.CalculatedDigestHEX);
            }
        }
    }
    
    private void updateDigestFile() throws IOException{
        updateDigestList();
        Path path = Paths.get(digestListFilePath);
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            for (FileLine fileLine : digestsFileList) {
                writer.write(fileLine.toString());
                writer.newLine();
            }
        }
    }

    private void calculateDigests() throws Exception{
    	for (Archive file : files) {
    		 MessageDigest digest = MessageDigest.getInstance(digestType);
    		 FileInputStream inputStream = new FileInputStream(digestListFilePath);
    		 byte[] bytes = new byte[2048];
			 int numBytes;
			 
		     while ((numBytes = inputStream.read(bytes)) != -1) {
		    	 digest.update(bytes, 0, numBytes);
    		 }
			 file.CalculatedDigest = digest.digest();
			 file.CalculatedDigestHEX = ByteToString(file.CalculatedDigest);
			 inputStream.close();
    	}
    }

    public void GetArchives(String[] args) {
        files = new ArrayList<Archive>();

        for (int i = 2; i < args.length - 1; i++) {
            Archive file = new Archive();
            file.Path = args[i];
            files.add(file);
        }

        for (Archive file : files) {
            System.out.println(file.Path);
            File f = new File(file.Path);
            if (!f.exists() || f.isDirectory()) {
                System.err.println("The file \"" + file.Path + "\" does not exist.");
                System.exit(1);
            }
        }

        File f = new File(digestListFilePath);
        if (!f.exists() || f.isDirectory()) {
            System.err.println("TheDigest List file \"" + digestListFilePath + "\" does not exist.");
            System.exit(1);
        }
        for (Archive file : files) {
            file.FileName = file.Path.substring(
                    file.Path.lastIndexOf("\\") + 1,
                    file.Path.length());
            System.out.println(file.FileName);
        }

    }
    
    public static String ByteToString(byte[] info) {
        // convert to hexadecimal
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < info.length; i++) {
            String hex = Integer.toHexString(0x0100 + (info[i] & 0x00FF))
                    .substring(1);
            buf.append((hex.length() < 2 ? "0" : "") + hex);
        }
        return buf.toString();
    }

    public String getDigestListFilePath() {
        return digestListFilePath;
    }

    public String getDigestType() {
        return digestType;
    }
}