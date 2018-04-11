import java.io.*;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

public class DigestCalculator {

    private String digestListFilePath;
    private String digestType;
    private String digest_hex;
    private List<Archive> files;

    public List<FileLine> digestsFileList;

    public DigestCalculator() {
    }

    public void CompareDigests() {
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

    public void LoadDigestListFile() {
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

    public void CalculateDigestFiles() {
    }

    public void GetArchives(String[] args) {
        files = new ArrayList<Archive>();

        for (int i = 1; i < args.length - 1; i++) {
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


    public boolean ValidateArgs(String[] args) {
        if (args.length < 1) {
            System.err.println("\nNao foram passados argumentos!");
            return false;
        }

        if (args.length < 3) {
            System.err.println("\nFormato: DigestCalculator <SP> Tipo_Digest <SP>Caminho_Arq1... <SP>Caminho_ArqN<SP>Caminho_ArqListaDigest  ");
            System.exit(1);
        } else if (!args[0].equals("MD5") && !args[0].equals("SHA1")) {
            System.err.println("\nPrimeiro argumento invalido!!");
            System.err
                    .println("\nO primeiro argumento deve ser o tipo do digest (MD5 ou SHA1)");
            return false;
        }
        return true;
    }

    public String getDigestListFilePath() {
        return digestListFilePath;
    }

    public void setDigestListFilePath(String digestListFilePath) {
        this.digestListFilePath = digestListFilePath;
    }

    public String getDigestType() {
        return digestType;
    }

    public void setDigestType(String digestType) {
        this.digestType = digestType;
    }

    public String getDigest_hex() {
        return digest_hex;
    }

    public void setDigest_hex(String digest_hex) {
        this.digest_hex = digest_hex;
    }
}
