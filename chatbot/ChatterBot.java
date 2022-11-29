import java.util.*;

/**
 * Base file for the ChatterBot exercise.
 * The bot's replyTo method receives a statement.
 * If it starts with the constant REQUEST_PREFIX, the bot returns
 * whatever is after this prefix. Otherwise, it returns one of
 * a few possible replies as supplied to it via its constructor.
 * In this case, it may also include the statement after
 * the selected reply (coin toss).
 * @author Dan Nirel
 */
class ChatterBot {

	/**
	 * The correct start of a legal request. It is a constant (static final).
	 */
	static final String REQUEST_PREFIX = "say ";
	/**
	 * The substring of a reply to a legal request. Will be substituted in the actual reply. It is a
	 * constant (static final).
	 */
	static final String REQUESTED_PHRASE_PLACEHOLDER = "<phrase>";
	/**
	 * The substring of a reply to an illegal request. Will be substituted in the actual reply. It is a
	 * constant (static final).
	 */
	static final String ILLEGAL_REQUEST_PLACEHOLDER = "<request>";

	Random rand = new Random();  // The random object. Is used to create random values.
	String name;  // The name of a bot.
	String[] repliesToLegalRequest;  // The list of possible reply patterns to a legal request.
	String[] repliesToIllegalRequest;  // The list of possible reply patterns to an illegal request.

	/**
	 * Constructor.
	 * @param name The name of a bot.
	 * @param repliesToLegalRequest The list of possible reply patterns to a legal request.
	 * @param repliesToIllegalRequest The list of possible reply patterns to an illegal request.
	 */
	ChatterBot(String name, String[] repliesToLegalRequest, String[] repliesToIllegalRequest) {
		this.name = name;
		this.repliesToLegalRequest = new String[repliesToLegalRequest.length];
		for(int i = 0 ; i < repliesToLegalRequest.length ; i++) {
			this.repliesToLegalRequest[i] = repliesToLegalRequest[i];
		}
		this.repliesToIllegalRequest = new String[repliesToIllegalRequest.length];
		for(int i = 0 ; i < repliesToIllegalRequest.length ; i++) {
			this.repliesToIllegalRequest[i] = repliesToIllegalRequest[i];
		}
	}

	/**
	 * Returns the name of a bot.
	 * @return the value in the name field.
	 */
	String getName() {
		return name;
	}

	/**
	 * Replies to a given request according to its correctness. A request is correct iff it starts with the
	 * requested prefix (which is a constant).
	 * @param statement The request to reply to.
	 * @return a reply obtained either from the method that deals with legal requests or from another
	 * method that deals with illegal requests.
	 */
	String replyTo(String statement) {
		if(statement.startsWith(REQUEST_PREFIX)) {
			return respondToLegalRequest(statement);
		}
		return respondToIllegalRequest(statement);
	}

	/**
	 * Randomly chooses a reply pattern from a given list of possible reply patterns, and then replaces all
	 * the placeholders in the chosen reply pattern with a given substitute.
	 * @param possibleReplies The list of all possible reply patterns to a request.
	 * @param placeholder The phrase to substitute in a reply pattern.
	 * @param substituteForThePlaceholder The phrase to substitute the placeholder with.
	 * @return a randomly chosen reply.
	 */
	String replacePlaceholderInARandomPattern(String[] possibleReplies, String placeholder,
											  String substituteForThePlaceholder) {
		int randomIndex = rand.nextInt(possibleReplies.length);
		String responsePattern = possibleReplies[randomIndex];
		return responsePattern.replaceAll(placeholder, substituteForThePlaceholder);
	}

	/**
	 * Selects a random reply to a given legal request. The answer contains the main part of the request
	 * (without the prefix) that is integrated in a randomly chosen reply pattern.
	 * @param statement The legal request to reply to.
	 * @return a randomly chosen reply to a legal request.
	 */
	String respondToLegalRequest(String statement) {
		//we don’t repeat the request prefix, so delete it from the reply
		String phrase = statement.replaceFirst(REQUEST_PREFIX, "");
		return replacePlaceholderInARandomPattern(repliesToLegalRequest, REQUESTED_PHRASE_PLACEHOLDER,
				phrase);
	}

	/**
	 * Selects a random reply to a given illegal request. The answer contains the whole request that is
	 * integrated in a randomly chosen reply pattern.
	 * @param statement The illegal request to reply to.
	 * @return a randomly chosen reply to an illegal request.
	 */
	String respondToIllegalRequest(String statement) {
		return replacePlaceholderInARandomPattern(repliesToIllegalRequest, ILLEGAL_REQUEST_PLACEHOLDER,
				statement);
	}
}
