public class Solution {

    // implement UntrustworthyMailWorker, Spy, Inspector, Thief, StolenPackageException, IllegalPackageException as public static classes here
    public static class UntrustworthyMailWorker implements MailService {
        private RealMailService rms;
        private MailService[] mailserv;

        public UntrustworthyMailWorker (MailService[] mailserv) {
            this.mailserv = mailserv;
            this.rms = new RealMailService ();
        }

        public Sendable processMail (Sendable mail) {

            for (int i = 0; i < mailserv.length; i++) {
                mail = mailserv[i].processMail (mail);
            }
            return rms.processMail (mail);
        }

        public RealMailService getRealMailService () {
            return this.rms;
        }
    }

    public static class Spy implements MailService {
        private Logger logger;

        public Spy () {
            logger = Logger.getLogger (Spy.class.getName ());
        }

        public Spy (Logger logger) {
            this.logger = logger;
        }


        public Sendable processMail (Sendable mail) {

            if (mail instanceof MailMessage) {
                MailMessage mail2 = (MailMessage) mail;


                if (mail2.getFrom ().equals (AUSTIN_POWERS) || mail2.getTo ().equals (AUSTIN_POWERS)) {
                    this.logger.log (Level.WARNING, "Detected target mail correspondence: from {0} to {1} \"{2}\"",
                            new Object[]{mail2.getFrom (), mail2.getTo (), mail2.getMessage ()});
                } else {
                    logger.log (Level.INFO, "Usual correspondence: from {0} to {1}",
                            new Object[]{mail2.getFrom (), mail2.getTo ()});
                }
            }
            return mail;
        }
    }

    public static class Thief implements MailService {
        private int min_stoimost;
        private int StolenValue;

        public Thief (int min_stoimost) {
            this.min_stoimost = min_stoimost;
            this.StolenValue = 0;
        }

        public int getStolenValue () {
            return StolenValue;
        }

        public Sendable processMail (Sendable mail) {
            if (mail instanceof MailPackage) {
                MailPackage mail2 = (MailPackage) mail;
                if (mail2.getContent ().getPrice () >= this.min_stoimost) {
                    this.StolenValue = this.StolenValue+mail2.getContent ().getPrice ();
                    return new MailPackage (mail2.getFrom (), mail2.getTo (),
                            new Package (
                                    "stones instead of " + mail2.getContent ().getContent (), 0));
                }
            }
            return mail;
        }
    }

    public static class Inspector implements MailService {
        public Sendable processMail (Sendable mail) {

            if (mail instanceof MailPackage) {
                MailPackage mail2 = (MailPackage) mail;
                if (mail2.getContent ().getContent ().contains (WEAPONS) ||
                        mail2.getContent ().getContent ().contains (BANNED_SUBSTANCE)) {
                    throw new IllegalPackageException ();
                }
                if (mail2.getContent ().getContent ().contains ("stones"))
                    throw new StolenPackageException ();
                return mail2;
            }
            return mail;
        }
    }

    public static class StolenPackageException extends RuntimeException {
        public StolenPackageException () {
            super ("Discovered the theft from the parcel!");
        }
    }

    public static class IllegalPackageException extends RuntimeException {
        public IllegalPackageException () {
            super ("IllegalPackageException!");
        }
    }


}
