import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Deck
{

    private final Random rand = new Random();
    private final List<Card> deck = new ArrayList<>();
    private final String[] suits = {"Spades", "Hearts", "Diamonds", "Clubs"};
    private final String[] ranks = {"Ace", "2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King"};

    public Deck()
    {
        ResetDeck();
    }

    public void ResetDeck()
    {
        deck.clear();

        for (String suit : suits) {
            for (String rank : ranks) {
                deck.add(new Card(rank, suit));
            }
        }
    }

    public Card DealCard()
    {
        if (deck.isEmpty()) { ResetDeck(); }

        int drawNumber = rand.nextInt(deck.size());
        return deck.remove(drawNumber);
    }

    public void RemoveCard(Card cardToRemove)
    {
        for(int i = 0; i < deck.size(); i++)
        {
            if(deck.get(i).getValue() == cardToRemove.getValue() && deck.get(i).getSuit().equals(cardToRemove.getSuit()))
            {
                deck.remove(i);
                return;
            }
        }
    }

    public void AddCard(Card cardToAdd)
    {
        deck.add(cardToAdd);
    }

    public List<Card> __getDeck__()
    {
        return this.deck;
    }






}
