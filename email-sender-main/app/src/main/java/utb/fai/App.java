package utb.fai;

public class App {

    public static void main(String[] args) {
        // Pro testování lze nastavit výchozí hodnoty argumentů, pokud nejsou zadány
        if (args.length != 6) {
            // Výchozí testovací hodnoty
            args = new String[] {
                "smtp.utb.cz", // host
                "25",          // port
                "v_tomecek@utb.cz",  // from
                "v_tomecek@utb.cz",  // to
                "Testovací email",  // subject
                "Toto je tělo emailu." // message
            };
            System.out.println("Hodnoty byli použity");
        }

        try {
            // Odeslání emailu s výchozími nebo zadanými hodnotami
            EmailSender sender = new EmailSender(args[0], Integer.parseInt(args[1]));
            sender.send(args[2], args[3], args[4], args[5]);
            sender.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
