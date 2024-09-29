import javax.print.DocFlavor;

public class Card {

    public static final String RESET = "\u001B[0m";
    public static final String BOLD_BLACK = "\033[1;90m";
    public static final String BOLD_RED = "\u001B[1;31m";
    public static final String BOLD_WHITE = "\033[1;97m";

    private int value;
    private final String suit;
    private final String rank;
    private final String spade = "♠";
    private final String heart = "♥";
    private final String diamond = "♦";
    private final String club = "♣";

    public Card(String rank, String suit)
    {
        this.suit = suit;
        this.rank = rank;
        setValue();
    }

    private void setValue()
    {
        switch (rank)
        {
            case "Ace" -> value = 13;
            case "King" -> value = 12;
            case "Queen" -> value = 11;
            case "Jack" -> value = 10;
            case "10" -> value = 9;
            case "9" -> value = 8;
            case "8" -> value = 7;
            case "7" -> value = 6;
            case "6" -> value = 5;
            case "5" -> value = 4;
            case "4" -> value = 3;
            case "3" -> value = 2;
            case "2" -> value = 1;
            case "" -> value = 0;
        }
    }

    public int getValue() {
        return value;
    }

    public String getSuit() {
        return suit;
    }

    public String getRank() {
        return rank;
    }

    public boolean isSameCard(Card card)
    {
        return card.getSuit().equals(this.suit) && card.getValue() == this.value;
    }

    public String toString()
    {
        String suitString;
        String rankString;
        switch(suit)
        {
            case "Spades" -> suitString = spade;
            case "Clubs" -> suitString = club;
            case "Diamonds" -> suitString = diamond;
            case "Hearts" -> suitString = heart;
            default -> suitString = "";
        }
        if(Character.isLetter(rank.charAt(0)))
        {
            rankString = String.valueOf(rank.charAt(0));
        }
        else {
            rankString = rank;
        }
        if(suit.equals("Clubs") || suit.equals("Spades"))
        {
            return BOLD_WHITE + rankString + RESET + " " + BOLD_BLACK + suitString + RESET;
        }
        else
        {
            return BOLD_WHITE + rankString + RESET + " " + BOLD_RED + suitString + RESET;
        }

    }

}
