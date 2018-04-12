public class Main {

    public static void main(String[] args) throws Exception {

        DigestCalculator digestCalculator = new DigestCalculator();
        if (!digestCalculator.ValidateArgs(args))
            System.exit(1);
        digestCalculator.setDigestType(args[0]);
        digestCalculator.setDigestListFilePath(args[1]);
        digestCalculator.GetArchives(args);
        digestCalculator.loadDigestListFile();
        digestCalculator.calculateDigests();
        digestCalculator.compareDigests();
        digestCalculator.updateDigestFile();

        // calculate
        // load
        // compare




    }
}
