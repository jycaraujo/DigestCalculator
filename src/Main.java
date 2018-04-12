public class Main {

    public static void main(String[] args) throws Exception {

        DigestCalculator digestCalculator = new DigestCalculator();
        if (!digestCalculator.ValidateArgs(args))
            System.exit(1);
        digestCalculator.setDigestType(args[0]);
        digestCalculator.setDigestListFilePath(args[2]);
        digestCalculator.GetArchives(args);

        // calculate
        // load
        // compare




    }
}
