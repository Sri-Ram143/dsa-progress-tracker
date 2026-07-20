package tracker.models;

public enum Difficulty {
    EASY("Easy"),
    MEDIUM("Medium"),
    HARD("Hard");

    private final String label;

    Difficulty(String label){
        this.label=label;
    }

    public String getLabel(){
        return label;
    }

    public static Difficulty fromString(String input) {
        if (input == null) {
            throw new IllegalArgumentException("Difficulty cannot be null");
        }
        String normalized = input.trim().toUpperCase();
        try {
            return Difficulty.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid difficulty. Allowed values: EASY, MEDIUM, HARD");
        }
    }
}

