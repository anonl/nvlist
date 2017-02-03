package nl.weeaboo.vn.core;

public interface IChoiceSeenLog {

    /**
     * Registers a new choice, allowing the selected options to be marked.
     *
     * @param uniqueChoiceId Unique choice identifier.
     * @param numOptions Number of options for this choice.
     */
    void registerChoice(String uniqueChoiceId, int numOptions);

    /**
     * @param uniqueChoiceId Unique identifier of the choice, matching the call to {@link #registerChoice(String, int)}.
     * @param optionIndex Starts at 1
     */
    boolean hasSelectedChoice(String uniqueChoiceId, int optionIndex);

    /**
     * @param uniqueChoiceId Unique identifier of the choice, matching the call to {@link #registerChoice(String, int)}.
     * @param optionIndex Starts at 1
     */
    void markChoiceSelected(String uniqueChoiceId, int optionIndex);

}
