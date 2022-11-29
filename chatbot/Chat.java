import java.util.Scanner;

class Chat {
    public static void main(String[] args) {
        String[] LegalReplyPatterns = new String[] {
                "well, if you insist I will say "+ChatterBot.REQUESTED_PHRASE_PLACEHOLDER+". "+
                ChatterBot.REQUESTED_PHRASE_PLACEHOLDER,
                "I heard that people usually like "+ChatterBot.REQUESTED_PHRASE_PLACEHOLDER+".",
                "wow, people are so smart. They even invented "+ChatterBot.REQUESTED_PHRASE_PLACEHOLDER,
                "what is so special about "+ChatterBot.REQUESTED_PHRASE_PLACEHOLDER+"?"};
        String[] IllegalReplyPatterns = new String[] {
                "sorry, I can't say "+ChatterBot.ILLEGAL_REQUEST_PLACEHOLDER+".",
                "speak louder, please. can't hear you.",
                "can you repeat what you've just said, please?",
                "I heard that it's not true.",
                "who said that?",
                "say ''"+ChatterBot.ILLEGAL_REQUEST_PLACEHOLDER+"'' yourself.",
                "completely agree with you.",
                "wow, you are smarter than I thought.",
                "oh, that was nice.",
                "say something, please.",
                "I have a friend now. it's you, silly!",
                "can you hear me?",
                "yeah, totally.",
                "no.",
                "never been interested in that.",
                "what do you want?"
        };
        ChatterBot[] bots = {
                new ChatterBot("Alice", LegalReplyPatterns, IllegalReplyPatterns),
                new ChatterBot("Bob", LegalReplyPatterns, IllegalReplyPatterns)};
        Scanner scanner = new Scanner(System.in);
        String statement = scanner.nextLine();
        while(true) {
            for(ChatterBot bot : bots) {
                statement = bot.replyTo(statement);
                System.out.print(bot.getName()+": "+statement);
                scanner.nextLine(); //wait for “enter” before continuing
            }
        }
    }
}
