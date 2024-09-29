import java.util.*;

public class Hand
{
    String RESET = "\u001B[0m";
    String WHITE_BOLD_BRIGHT = "\033[1;97m";    // WHITE
    String CYAN_BOLD_BRIGHT = "\033[1;96m";   // CYAN
    String MAGENTA_BOLD_BRIGHT = "\033[1;95m";  // MAGENTA
    String YELLOW_BOLD_BRIGHT = "\033[1;93m";   // YELLOW
    String GREEN_BOLD_BRIGHT = "\033[1;92m";    // GREEN
    String BLUE_BOLD_BRIGHT = "\033[1;94m";     // BLUE
    String RED_BOLD_BRIGHT = "\033[1;91m";      // RED
    String BLUE_BACKGROUND_BRIGHT = "\033[0;104m";
    String RED_BACKGROUND_BRIGHT = "\033[0;101m";      // RED


    private final Card[] hand;

    private Card[] communityCards;

    private boolean isFolded;

    private boolean hasRoyalFlush;
    private boolean hasStraightFlush;
    private boolean hasFourOfAKind;
    private boolean hasFullHouse;
    private boolean hasFlush;
    private boolean hasStraight;
    private boolean hasThreeOfAKind;
    private boolean hasTwoPair;
    private boolean hasOnePair;

    private ArrayList<Card> royalFlush;
    private ArrayList<Card> straightFlush;
    private ArrayList<Card> fourOfAKind;
    private final ArrayList<Card> fullHouse;
    private ArrayList<Card> flush;
    private ArrayList<Card> straight;
    private ArrayList<Card> threeOfAKind;
    private final ArrayList<Card> twoPair;
    private ArrayList<Card> onePair;

    private Card highRoyalFlushCard;
    private Card highStraightFlushCard;
    private Card highFourOfAKindCard;
    private Card highFullHouseCard;
    private Card highFlushCard;
    private Card highStraightCard;
    private Card highThreeOfAKindCard;
    private Card highTwoPairCard;
    private Card highPairCard;
    private Card highCard;
    private final Card highCardInHand;


    public Hand(Card card1, Card card2)
    {
        hand = new Card[]{card1, card2};

        hasRoyalFlush = false;
        hasStraightFlush = false;
        hasFourOfAKind = false;
        hasFullHouse = false;
        hasFlush = false;
        hasStraight = false;
        hasThreeOfAKind = false;
        hasTwoPair = false;
        hasOnePair = false;
        isFolded = false;

        royalFlush = new ArrayList<>();
        straightFlush = new ArrayList<>();
        fourOfAKind = new ArrayList<>();
        fullHouse = new ArrayList<>();
        flush = new ArrayList<>();
        straight = new ArrayList<>();
        threeOfAKind = new ArrayList<>();
        twoPair = new ArrayList<>();
        onePair = new ArrayList<>();

        highRoyalFlushCard = new Card("", "");
        highStraightFlushCard = new Card("", "");
        highFourOfAKindCard = new Card("", "");
        highFullHouseCard = new Card("", "");
        highFlushCard = new Card("", "");
        highStraightCard = new Card("", "");
        highThreeOfAKindCard = new Card("", "");
        highTwoPairCard = new Card("", "");
        highPairCard = new Card("", "");
        highCardInHand = GetHighestRank(hand);
        highCard = GetHighestRank(hand);

    }

    public void EvaluateHand(Card[] communityCards, boolean showText)
    {
        highCard = GetHighestRank(combine(hand, communityCards));

        CheckSimilarCards(communityCards, showText);

        CheckStraight(communityCards, showText);
        GetStraight(communityCards);

        CheckFlush(communityCards, showText);
        GetFlush(communityCards);

        CheckStraightFlush(communityCards, showText);
        GetStraightFlush(communityCards);

        CheckRoyalFlush(communityCards, showText);
        GetRoyalFlush(communityCards);

    }

    public ArrayList<Card> GetTopFiveCards()
    {
        if(hasRoyalFlush) return getRoyalFlush();
        if(hasStraightFlush) return getStraightFlush();
        if(hasFourOfAKind) return getFourOfAKind();
        if(hasFullHouse) return getFullHouse();
        if(hasFlush) return getFlush();
        if(hasStraight) return getStraight();
        if(hasThreeOfAKind) return getThreeOfAKind();
        if(hasTwoPair) return __getTwoPair();
        if(hasOnePair) return getOnePair();
        return __getTop5HighCard();
    }

    public void PrintBestHandType()
    {
        if(hasRoyalFlush) {System.out.println(RED_BACKGROUND_BRIGHT+"Royal Flush!"+RESET); return;}
        if(hasStraightFlush) {System.out.println(BLUE_BACKGROUND_BRIGHT+"Straight Flush!"+RESET); return;}
        if(hasFourOfAKind) {System.out.println(RED_BOLD_BRIGHT+"Four Of A Kind!"+RESET); return;}
        if(hasFullHouse) {System.out.println(BLUE_BOLD_BRIGHT+"Full House!"+RESET); return;}
        if(hasFlush) {System.out.println(GREEN_BOLD_BRIGHT+"Flush!"+RESET); return;}
        if(hasStraight) {System.out.println(MAGENTA_BOLD_BRIGHT+"Straight!"+RESET); return;}
        if(hasThreeOfAKind) {System.out.println(CYAN_BOLD_BRIGHT+"Three Of A Kind!"+RESET); return;}
        if(hasTwoPair) {System.out.println(YELLOW_BOLD_BRIGHT+"Two Pair!"+RESET); return;}
        if(hasOnePair) {System.out.println(WHITE_BOLD_BRIGHT+"One Pair!"+RESET); return;}
        System.out.println("High Card.");

    }

    public Card[] getHand() {
        return hand;
    }

    public void setCommunityCards(Card[] communityCards)
    {
        this.communityCards = communityCards;
    }

    private void resetCommunityCards()
    {
        this.communityCards = new Card[]{};
    }

    public boolean isSameHand(Hand hand)
    {
        return (hand.getHand()[0].isSameCard(this.hand[0]) || hand.getHand()[0].isSameCard(this.hand[1])) &&
                (hand.getHand()[1].isSameCard(this.hand[0]) || hand.getHand()[1].isSameCard(this.hand[1]));
    }

    public boolean hasSameCard(Card card)
    {
        return this.hand[0].isSameCard(card) || this.hand[1].isSameCard(card);
    }

    public void SwapCardInHand(Card newCard, Card oldCard)
    {
        for(int i = 0; i < hand.length; i++)
        {
            if(hand[i].isSameCard(oldCard))
            {
                hand[i] = newCard;
            }
        }
    }

    public void ResetHandAchievements()
    {
        hasRoyalFlush = false;
        hasStraightFlush = false;
        hasFourOfAKind = false;
        hasFullHouse = false;
        hasFlush = false;
        hasStraight = false;
        hasThreeOfAKind = false;
        hasTwoPair = false;
        hasOnePair = false;

        royalFlush.clear();
        straightFlush.clear();
        fourOfAKind.clear();
        fullHouse.clear();
        flush.clear();
        straight.clear();
        threeOfAKind.clear();
        twoPair.clear();
        onePair.clear();

        highRoyalFlushCard = new Card("", "");
        highStraightFlushCard = new Card("", "");
        highFourOfAKindCard = new Card("", "");
        highFullHouseCard = new Card("", "");
        highFlushCard = new Card("", "");
        highStraightCard = new Card("", "");
        highThreeOfAKindCard = new Card("", "");
        highTwoPairCard = new Card("", "");
        highPairCard = new Card("", "");
        highCard = GetHighestRank(hand);

        resetCommunityCards();
    }

    public boolean isFolded() {
        return isFolded;
    }

    public void setFolded(boolean folded) {
        isFolded = folded;
    }

    public Card getHighCardInHand() {
        return highCardInHand;
    }

    public boolean hasRoyalFlush() {
        return hasRoyalFlush;
    }

    public boolean hasStraightFlush() {
        return hasStraightFlush;
    }

    public boolean hasFourOfAKind() {
        return hasFourOfAKind;
    }

    public boolean hasFullHouse() {
        return hasFullHouse;
    }

    public boolean hasFlush() {
        return hasFlush;
    }

    public boolean hasStraight() {
        return hasStraight;
    }

    public boolean hasThreeOfAKind() {
        return hasThreeOfAKind;
    }

    public boolean hasTwoPair() {
        return hasTwoPair;
    }

    public boolean hasOnePair() {
        return hasOnePair;
    }

    public ArrayList<Card> getRoyalFlush() {
        SortCards(royalFlush);
        while(royalFlush.size() > 5)
        {
            royalFlush.removeFirst();
        }
        return royalFlush;
    }

    public ArrayList<Card> getStraightFlush() {
        SortCards(straightFlush);
        while(straightFlush.size() > 5)
        {
            straightFlush.removeFirst();
        }
        return straightFlush;
    }

    public ArrayList<Card> getFourOfAKind() {

        if(highCard.getValue() == highFourOfAKindCard.getValue())
        {
            Card[] cards = combine(this.hand, this.communityCards);
            fourOfAKind.add(GetSecondHighestRank(cards));
        }
        else {fourOfAKind.add(highCard);}
        SortCards(fourOfAKind);
        return fourOfAKind;
    }

    public ArrayList<Card> getFullHouse() {
        SortCards(fullHouse);
        return fullHouse;
    }

    public ArrayList<Card> getFlush() {
        SortCards(flush);
        while(flush.size() > 5)
        {
            flush.removeFirst();
        }
        return flush;
    }

    public ArrayList<Card> getStraight() {
        SortCards(straight);
        while(straight.size() > 5)
        {
            straight.removeFirst();
        }
        return straight;
    }

    // TODO: pocket aces got trip aces and there was a king on the board, output should have been [A,A,A,K,8] but was [A,A,A,K,K] -> kings same card
    public ArrayList<Card> getThreeOfAKind() {
        Card[] cards = combine(this.hand, this.communityCards);
        if(highCard.getValue() == highThreeOfAKindCard.getValue())
        {
            threeOfAKind.add(GetSecondHighestRank(cards));
        }
        else {
            threeOfAKind.add(highCard);
        }
        if(GetSecondHighestRank(cards).getValue() == highThreeOfAKindCard.getValue())
        {
            threeOfAKind.add(GetThirdHighestRank(cards));
        }
        else {
            threeOfAKind.add(GetSecondHighestRank(cards));
        }
        SortCards(threeOfAKind);
        return threeOfAKind;
    }

    public ArrayList<Card> getTwoPair()
    {
        return twoPair;
    }
    private ArrayList<Card> __getTwoPair() {
        SortCards(twoPair);
        while(twoPair.size() > 4)
        {
            twoPair.removeFirst();
        }
        if(highCard.getValue() == highTwoPairCard.getValue())
        {
            Card[] cards = combine(this.hand, this.communityCards);

            highCard = GetSecondHighestRank(cards);
            if(highCard.getValue() == GetSecondHighestRank(twoPair).getValue())
            {
                twoPair.add(GetThirdHighestRank(cards));
            }
            else {
                twoPair.add(highCard);
            }
        }
        else {
            twoPair.add(highCard);
        }
        SortCards(twoPair);
        return twoPair;
    }

    public ArrayList<Card> getOnePair() {

        Card[] cards = combine(this.hand, this.communityCards);

        if(highCard.getValue() == highPairCard.getValue())
        {
            onePair.add(GetSecondHighestRank(cards));
            onePair.add(GetThirdHighestRank(cards));
            onePair.add(GetFourthHighestRank(cards));
        }
        else if(GetSecondHighestRank(cards).getValue() == highPairCard.getValue())
        {
            onePair.add(highCard);
            onePair.add(GetThirdHighestRank(cards));
            onePair.add(GetFourthHighestRank(cards));
        }
        else if(GetThirdHighestRank(cards).getValue() == highPairCard.getValue())
        {
            onePair.add(highCard);
            onePair.add(GetSecondHighestRank(cards));
            onePair.add(GetFourthHighestRank(cards));
        }
        else {
            onePair.add(highCard);
            onePair.add(GetSecondHighestRank(cards));
            onePair.add(GetThirdHighestRank(cards));
        }
        SortCards(onePair);
        return onePair;
    }

    private ArrayList<Card> __getTop5HighCard()
    {
        ArrayList<Card> bestCards = new ArrayList<>();
        Card[] cards = combine(this.hand, this.communityCards);
        bestCards.add(GetHighestRank(cards));
        bestCards.add(GetSecondHighestRank(cards));
        bestCards.add(GetThirdHighestRank(cards));
        bestCards.add(GetFourthHighestRank(cards));
        bestCards.add(GetFifthHighestRank(cards));
        return bestCards;
    }

    public Card getHighRoyalFlushCard() {
        return highRoyalFlushCard;
    }

    public Card getHighFourOfAKindCard() {
        return highFourOfAKindCard;
    }

    public Card getHighFullHouseCard() {
        return highFullHouseCard;
    }

    public Card getHighThreeOfAKindCard() {
        return highThreeOfAKindCard;
    }

    public Card getHighTwoPairCard() {
        return highTwoPairCard;
    }

    public Card getHighPairCard() {
        return highPairCard;
    }

    public Card getHighCard() {
        return highCard;
    }

    public Card getHighStraightFlushCard() {
        return highStraightFlushCard;
    }

    public Card getHighFlushCard() {
        return highFlushCard;
    }

    public Card getHighStraightCard() {
        return highStraightCard;
    }

    private void CheckRoyalFlush(Card[] communityCards, boolean showText)
    {
        if(hasStraightFlush && !hasRoyalFlush)
        {
            if(GetHighestRank(straightFlush).getValue() == 13)
            {
                hasRoyalFlush = true;
                highRoyalFlushCard = GetHighestRank(straightFlush);
                if(showText) System.out.println("\n"+Arrays.toString(hand) +" has a Royal Flush of "+highRoyalFlushCard.getSuit()+"!");
            }
        }
    }

    private void GetRoyalFlush(Card[] communityCards)
    {
        if(hasRoyalFlush && royalFlush.isEmpty())
        {
            royalFlush = straightFlush;
        }
    }

    private void CheckStraightFlush(Card[] communityCards, boolean showText)
    {
        if(hasFlush && hasStraight && !hasStraightFlush)
        {
            GetFlush(communityCards);
            flush.sort(Comparator.comparingInt(Card::getValue));
            ArrayList<Card> straightCards = new ArrayList<>();

            int straightCount = 1;
            for (int i = 1; i < flush.size(); i++) {
                straightCards.add(flush.get(i - 1));
                if (flush.get(i).getValue() == flush.get(i - 1).getValue() + 1) {
                    straightCount++;
                    straightCards.add(flush.get(i));
                    if (straightCount >= 5)
                    {
                        highStraightFlushCard = GetHighestRank(straightCards);
                        if(showText) System.out.println("\n"+Arrays.toString(hand) +" has a Straight Flush of "+highStraightFlushCard.getSuit()+" with "+highStraightFlushCard.getRank()+" high!");
                        hasStraightFlush = true;
                    }
                } else if (flush.get(i).getValue() != flush.get(i - 1).getValue())
                {
                    straightCount = 1;
                    straightCards.clear();
                }
            }
        }
    }

    private void GetStraightFlush(Card[] communityCards)
    {
        if(hasStraightFlush && straightFlush.isEmpty())
        {
            GetFlush(communityCards);
            flush.sort(Comparator.comparingInt(Card::getValue));
            ArrayList<Card> straightCards = new ArrayList<>();

            int straightCount = 1;
            straightCards.add(flush.getFirst());
            for (int i = 1; i < flush.size(); i++) {

                if (flush.get(i).getValue() == flush.get(i - 1).getValue() + 1) {
                    straightCount++;
                    if(flush.get(i - 1).getValue() != flush.get(i).getValue())
                    {
                        straightCards.add(flush.get(i));
                    }
                    if (straightCount >= 5)
                    {
                        if(straightCards.size() > straightFlush.size())
                        {
                            straightFlush = straightCards;
                        }
                    }
                } else if (flush.get(i).getValue() != flush.get(i - 1).getValue())
                {
                    if(straightFlush.size() >= 5) return;
                    straightCount = 1;
                    straightCards.clear();
                    straightCards.add(flush.get(i));
                }
            }
        }
    }

    private void CheckSimilarCards(Card[] communityCards, boolean showText)
    {
        if(!hasFourOfAKind || !hasThreeOfAKind || !hasTwoPair || !hasOnePair)
        {
            ArrayList<ArrayList<Card>> cards = new ArrayList<>(14);
            for(int i = 0; i < 14; i++)
            {
                cards.add(new ArrayList<>());
            }
            int pairCount = 0;
            for (Card card : combine(hand, communityCards))
            {
                switch (card.getValue())
                {
                    case 0 -> cards.get(0).add(card);
                    case 1 -> cards.get(1).add(card);
                    case 2 -> cards.get(2).add(card);
                    case 3 -> cards.get(3).add(card);
                    case 4 -> cards.get(4).add(card);
                    case 5 -> cards.get(5).add(card);
                    case 6 -> cards.get(6).add(card);
                    case 7 -> cards.get(7).add(card);
                    case 8 -> cards.get(8).add(card);
                    case 9 -> cards.get(9).add(card);
                    case 10 -> cards.get(10).add(card);
                    case 11 -> cards.get(11).add(card);
                    case 12 -> cards.get(12).add(card);
                    case 13 -> cards.get(13).add(card);
                }
            }
            for (ArrayList<Card> card : cards)
            {
                if (card.size() == 4)
                {
                    highFourOfAKindCard = GetHighestRank(card);
                    hasFourOfAKind = true;
                    fourOfAKind = card;
                    if(showText) System.out.println("\n" + Arrays.toString(hand) + " has Four of a Kind: " + fourOfAKind);
                } else if (card.size() == 3)
                {
                    if(GetHighestRank(card).getValue() > highThreeOfAKindCard.getValue()) {
                        highThreeOfAKindCard = GetHighestRank(card);
                        threeOfAKind = card;
                    }
                    if(showText) System.out.println("\n" + Arrays.toString(hand) + " has Three of a Kind: " + threeOfAKind);
                    hasThreeOfAKind = true;

                } else if (card.size() == 2)
                {
                    if(GetHighestRank(card).getValue() > highPairCard.getValue()) {
                        highPairCard = GetHighestRank(card);
                        onePair = card;
                    }
                    if(showText) System.out.println("\n" + Arrays.toString(hand) + " has a Pair of " + highPairCard.getRank() + "'s!");
                    pairCount++;
                    hasOnePair = true;
                    twoPair.addAll(card);
                }
            }
            if(hasThreeOfAKind && (hasOnePair || hasTwoPair))
            {
                hasFullHouse = true;
                fullHouse.addAll(threeOfAKind);
                fullHouse.addAll(onePair);
                highFullHouseCard = GetHighestRank(fullHouse);
                if(showText) System.out.println("\n" + Arrays.toString(hand) + " has a Full House: "+fullHouse);
            }
            if(pairCount >= 2)
            {
                if(showText) System.out.println("\n" + Arrays.toString(hand) + " has Two Pair!");
                hasTwoPair = true;
                highTwoPairCard = GetHighestRank(twoPair);
            }
        }
    }

    private void GetFlush(Card[] communityCards)
    {
        if(hasFlush && flush.isEmpty())
        {
            int[] suits = new int[4]; // 0: Clubs, 1: Diamonds, 2: Hearts, 3: Spades
            ArrayList<Card> clubs = new ArrayList<>();
            ArrayList<Card> diamonds = new ArrayList<>();
            ArrayList<Card> hearts = new ArrayList<>();
            ArrayList<Card> spades = new ArrayList<>();
            for (Card card : combine(hand, communityCards)) {
                switch (card.getSuit()) {
                    case "Clubs" -> {
                        suits[0]++;
                        clubs.add(card);
                    }
                    case "Diamonds" -> {
                        suits[1]++;
                        diamonds.add(card);
                    }
                    case "Hearts" -> {
                        suits[2]++;
                        hearts.add(card);
                    }
                    case "Spades" -> {
                        suits[3]++;
                        spades.add(card);
                    }
                }
            }
            for (int i = 0; i < suits.length; i++) {
                if (suits[i] >= 5) {
                    switch (i) {
                        case 0 -> {
                            flush = clubs;
                        }
                        case 1 -> {
                            flush = diamonds;
                        }
                        case 2 -> {
                            flush = hearts;
                        }
                        case 3 -> {
                            flush = spades;
                        }
                    }
                }
            }
        }
    }

    private void CheckFlush(Card[] communityCards, boolean showText)
    {
        if(!hasFlush)
        {
            int[] suits = new int[4]; // 0: Clubs, 1: Diamonds, 2: Hearts, 3: Spades
            ArrayList<Card> clubs = new ArrayList<>();
            ArrayList<Card> diamonds = new ArrayList<>();
            ArrayList<Card> hearts = new ArrayList<>();
            ArrayList<Card> spades = new ArrayList<>();
            for (Card card : combine(hand, communityCards)) {
                switch (card.getSuit()) {
                    case "Clubs" -> {
                        suits[0]++;
                        clubs.add(card);
                    }
                    case "Diamonds" -> {
                        suits[1]++;
                        diamonds.add(card);
                    }
                    case "Hearts" -> {
                        suits[2]++;
                        hearts.add(card);
                    }
                    case "Spades" -> {
                        suits[3]++;
                        spades.add(card);
                    }
                }
            }
            for (int i = 0; i < suits.length; i++) {
                if (suits[i] >= 5) {
                    switch (i)
                    {
                        case 0 -> {
                            highFlushCard = GetHighestRank(clubs);
                            if(showText) System.out.println("\n" + Arrays.toString(hand) + " has a Flush of Clubs with " + highFlushCard.getRank() + " high!");
                        }
                        case 1 -> {
                            highFlushCard = GetHighestRank(diamonds);
                            if(showText) System.out.println("\n" + Arrays.toString(hand) + " has a Flush of Diamonds with " + highFlushCard.getRank() + " high!");
                        }
                        case 2 -> {
                            highFlushCard = GetHighestRank(hearts);
                            if(showText) System.out.println("\n" + Arrays.toString(hand) + " has a Flush of Hearts with " + highFlushCard.getRank() + " high!");
                        }
                        case 3 -> {
                            highFlushCard = GetHighestRank(spades);
                            if(showText) System.out.println("\n" + Arrays.toString(hand) + " has a Flush of Spades with " + highFlushCard.getRank() + " high!");
                        }
                    }
                    hasFlush = true;
                }
            }
        }
    }

    private void GetStraight(Card[] communityCards)
    {
        if(hasStraight && straight.isEmpty())
        {
            Card[] cards = combine(hand, communityCards);
            ArrayList<Card> straightCards = new ArrayList<>();
            Arrays.sort(cards, Comparator.comparingInt(Card::getValue));

            int straightCount = 1;
            straightCards.add(cards[0]);
            for (int i = 1; i < cards.length; i++) {

                if (cards[i].getValue() == cards[i - 1].getValue() + 1) {
                    straightCount++;

                    if(cards[i - 1].getValue() != cards[i].getValue())
                    {
                        straightCards.add(cards[i]);
                    }
                    if (straightCount >= 5) {

                        if(straightCards.size() > straight.size())
                        {
                            // sometimes calling GetTopFiveCards on a hand with a straight returns []
                            // TODO: sort and trim to top 5 first
                            straight = straightCards;
                        }
                    }
                } else if (cards[i].getValue() != cards[i - 1].getValue()) {
                    if(straight.size() >= 5) return;
                    straightCount = 1;
                    straightCards.clear();
                    straightCards.add(cards[i]);
                }
            }

            if (isLowStraight(cards) && !(GetHighestRank(straightCards).getValue() >= 5 /*because 6's value is 5*/)) {
                straightCards.clear();
                for (Card card : cards) {
                    if (card.getValue() == 13 || card.getValue() <= 5) { // Ace (value 13) is treated as 0 here
                        straightCards.add(card);
                    }
                }
                if (straightCards.size() >= 5) {
                    straight = new ArrayList<>(straightCards);
                }
            }
        }


    }

    private void CheckStraight(Card[] communityCards, boolean showText) {
        if (!hasStraight) {
            Card[] cards = combine(hand, communityCards);
            ArrayList<Card> straightCards = new ArrayList<>();

            // Sort cards based on value, treating Ace as both 1 and 13
            Arrays.sort(cards, Comparator.comparingInt(card -> (card.getValue() == 13 ? 0 : card.getValue())));

            int straightCount = 1;
            for (int i = 1; i < cards.length; i++) {
                straightCards.add(cards[i - 1]); // Add the previous card to straightCards
                if (cards[i].getValue() == cards[i - 1].getValue() + 1 ||
                        (cards[i].getValue() == 1 && cards[i - 1].getValue() == 0)) // Ace as low straight
                {
                    straightCount++;
                    straightCards.add(cards[i]);

                    if (straightCount >= 5) {
                        highStraightCard = GetHighestRank(straightCards);
                        if (showText) {
                            System.out.println("\n" + Arrays.toString(hand) + " has a Straight with " + highStraightCard.getRank() + " high!");
                        }
                        hasStraight = true;
                        break;
                    }
                } else if (cards[i].getValue() != cards[i - 1].getValue()) {
                    straightCount = 1;
                    straightCards.clear();
                }
            }

            // Special case for low straight (A, 2, 3, 4, 5)
            if (!hasStraight && isLowStraight(cards)) {
                if (showText) {
                    System.out.println("\n" + Arrays.toString(hand) + " has a Low Straight (A-2-3-4-5)!");
                }
                highStraightCard = GetHighestRank(straightCards);
                hasStraight = true;
            }
        }
    }

    private boolean isLowStraight(Card[] cards) {
        // Check for Ace (treated as 1), 2, 3, 4, 5
        boolean hasAce = false, hasTwo = false, hasThree = false, hasFour = false, hasFive = false;

        for (Card card : cards) {
            int value = card.getValue();
            if (value == 13) hasAce = true; // Ace treated as 1
            if (value == 2) hasTwo = true;
            if (value == 3) hasThree = true;
            if (value == 4) hasFour = true;
            if (value == 5) hasFive = true;
        }

        return hasAce && hasTwo && hasThree && hasFour && hasFive;
    }

    private Card[] combine(Card[] hand, Card[] communityCards) {
        Card[] combined = new Card[hand.length + communityCards.length];
        System.arraycopy(hand, 0, combined, 0, hand.length);
        System.arraycopy(communityCards, 0, combined, hand.length, communityCards.length);
        return combined;
    }

    private Card GetHighestRank(Card[] cards) {
        return Arrays.stream(cards).filter(Objects::nonNull).max(Comparator.comparingInt(Card::getValue)).orElse(null);
    }
    private Card GetHighestRank(ArrayList<Card> cards)
    {
        Card highestCard = new Card("", "");
        int highestRankValue = 0;
        for(int i = 0; i < cards.size(); i++)
        {
            if(!Objects.equals(cards.get(i), null))
            {
                int value = cards.get(i).getValue();

                if(i == 0) {
                    highestCard = cards.get(i);
                    highestRankValue = value;
                }
                else if (value > highestRankValue) {
                    highestCard = cards.get(i);
                    highestRankValue = value;
                }
            }
        }
        return highestCard;
    }

    private Card GetSecondHighestRank(Card[] cards) {

        Card highestCard = new Card("", "");
        Card secondHighestCard = new Card("", "");

        for (Card card : cards) {
            if (card.getValue() > highestCard.getValue()) {
                secondHighestCard = highestCard;
                highestCard = card;
            } else if (card.getValue() > secondHighestCard.getValue() && card.getValue() < highestCard.getValue()) {
                secondHighestCard = card;
            }
        }

        return secondHighestCard;
    }

    private Card GetSecondHighestRank(List<Card> cards)
    {
        Card highestCard = new Card("", "");
        Card secondHighestCard = new Card("", "");

        for (Card card : cards) {
            if (card.getValue() > highestCard.getValue()) {
                secondHighestCard = highestCard;
                highestCard = card;
            } else if (card.getValue() > secondHighestCard.getValue() && card.getValue() < highestCard.getValue()) {
                secondHighestCard = card;
            }
        }

        return secondHighestCard;

    }


    private Card GetThirdHighestRank(Card[] cards) {

        Card highestCard = new Card("", "");
        Card secondHighestCard = new Card("", "");
        Card thirdHighestCard = new Card("", "");

        for (Card card : cards) {
            if (card.getValue() > highestCard.getValue()) {
                thirdHighestCard = secondHighestCard;
                secondHighestCard = highestCard;
                highestCard = card;
            } else if (card.getValue() > secondHighestCard.getValue() && card.getValue() < highestCard.getValue()) {
                thirdHighestCard = secondHighestCard;
                secondHighestCard = card;
            } else if (card.getValue() > thirdHighestCard.getValue() && card.getValue() < secondHighestCard.getValue()) {
                thirdHighestCard = card;
            }
        }

        return thirdHighestCard;
    }

    private Card GetFourthHighestRank(Card[] cards) {
        if (cards == null || cards.length < 4) {
            throw new IllegalArgumentException("At least four cards are required to determine the fourth highest card.");
        }

        Card highestCard = new Card("", "");
        Card secondHighestCard = new Card("", "");
        Card thirdHighestCard = new Card("", "");
        Card fourthHighestCard = new Card("", "");

        for (Card card : cards) {
            if (card.getValue() > highestCard.getValue()) {
                fourthHighestCard = thirdHighestCard;
                thirdHighestCard = secondHighestCard;
                secondHighestCard = highestCard;
                highestCard = card;
            } else if (card.getValue() > secondHighestCard.getValue() && card.getValue() < highestCard.getValue()) {
                fourthHighestCard = thirdHighestCard;
                thirdHighestCard = secondHighestCard;
                secondHighestCard = card;
            } else if (card.getValue() > thirdHighestCard.getValue() && card.getValue() < secondHighestCard.getValue()) {
                fourthHighestCard = thirdHighestCard;
                thirdHighestCard = card;
            } else if (card.getValue() > fourthHighestCard.getValue() && card.getValue() < thirdHighestCard.getValue()) {
                fourthHighestCard = card;
            }
        }

        return fourthHighestCard;
    }

    private Card GetFifthHighestRank(Card[] cards) {
        if (cards == null || cards.length < 5) {
            throw new IllegalArgumentException("At least five cards are required to determine the fifth highest card.");
        }

        Card highestCard = new Card("", "");
        Card secondHighestCard = new Card("", "");
        Card thirdHighestCard = new Card("", "");
        Card fourthHighestCard = new Card("", "");
        Card fifthHighestCard = new Card("", "");

        for (Card card : cards) {
            if (card.getValue() > highestCard.getValue()) {
                fifthHighestCard = fourthHighestCard;
                fourthHighestCard = thirdHighestCard;
                thirdHighestCard = secondHighestCard;
                secondHighestCard = highestCard;
                highestCard = card;
            } else if (card.getValue() > secondHighestCard.getValue() && card.getValue() < highestCard.getValue()) {
                fifthHighestCard = fourthHighestCard;
                fourthHighestCard = thirdHighestCard;
                thirdHighestCard = secondHighestCard;
                secondHighestCard = card;
            } else if (card.getValue() > thirdHighestCard.getValue() && card.getValue() < secondHighestCard.getValue()) {
                fifthHighestCard = fourthHighestCard;
                fourthHighestCard = thirdHighestCard;
                thirdHighestCard = card;
            } else if (card.getValue() > fourthHighestCard.getValue() && card.getValue() < thirdHighestCard.getValue()) {
                fifthHighestCard = fourthHighestCard;
                fourthHighestCard = card;
            } else if (card.getValue() > fifthHighestCard.getValue() && card.getValue() < fourthHighestCard.getValue()) {
                fifthHighestCard = card;
            }
        }

        return fifthHighestCard;
    }


    public String toString()
    {
        return "[ "+hand[0]+", "+hand[1]+" ]";
    }

    private void SortCards(Card[] cards)
    {
        Arrays.sort(cards, Comparator.comparingInt(Card::getValue));
    }

    private void SortCards(ArrayList<Card> cards)
    {
        cards.sort(Comparator.comparingInt(Card::getValue));
    }
}
