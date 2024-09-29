import java.util.*;

public class PokerDealer {

    public static final String RESET = "\u001B[0m";
    public static final String GREEN = "\033[1;92m";
    public static final String YELLOW = "\033[1;93m";
    public static final String MAGENTA = "\033[1;95m";
    public static final String BLUE = "\033[1;94m";
    public static final String MAGENTA_BACK = "\033[0;105m";

    private final Deck deck;
    public Hand[] handsInPlay;
    private Card[] communityCards;
    private Card[] flop;
    private Card turn;
    private Card river;

    private boolean isFlopRigged;
    private boolean isTurnRigged;
    private boolean isRiverRigged;

    public PokerDealer() {
        deck = new Deck();

        isFlopRigged = false;
        isTurnRigged = false;
        isRiverRigged = false;

        flop = null;
        turn = null;
        river = null;
    }

    public ArrayList<Hand> PlayGame()
    {
        Scanner scanner = new Scanner(System.in);

        System.out.println(Arrays.toString(handsInPlay));

        System.out.println("Press "+ GREEN +"Enter "+RESET+"to reveal the "+YELLOW+"Flop"+RESET+"...");
//        scanner.nextLine();
        RevealFlop();

        System.out.println("Press "+ GREEN +"Enter "+RESET+"to reveal the "+MAGENTA+"Turn"+RESET+"...");
//        scanner.nextLine();
        RevealTurn();

        System.out.println("Press "+ GREEN +"Enter "+RESET+"to reveal the "+BLUE+"River"+RESET+"...");
//        scanner.nextLine();
        RevealRiver();

        System.out.println("Press "+ GREEN +"Enter "+RESET+"to reveal the "+GREEN+"Winning Hand"+RESET+"...");
//        scanner.nextLine();

        ArrayList<Hand> winningHands = RevealWinningHand();

        System.out.println("========================================================\n");

        deck.ResetDeck();

        return winningHands;
    }

    public void RevealRiver() {
        Card[] cards = new Card[]{flop[0], flop[1], flop[2], turn, river};
        System.out.println(BLUE+"\nRiver"+RESET+": " + Arrays.toString(cards));
    }

    public void RevealTurn() {
        Card[] cards = new Card[]{flop[0], flop[1], flop[2], turn};
        System.out.println(MAGENTA+"\nTurn"+RESET+": " + Arrays.toString(cards));
    }

    public void RevealFlop() {
        System.out.println(YELLOW+"\nFlop"+RESET+": " + Arrays.toString(flop));
    }

    public ArrayList<Hand> SimGame()
    {
        Flop();
        Turn();
        River();
        ArrayList<Hand> winningHands = GetWinningHand();
        deck.ResetDeck();
        return winningHands;
    }

    public void DealHands(int numPlayers) {
        handsInPlay = new Hand[numPlayers];

        for (int i = 0; i < numPlayers; i++) {
            handsInPlay[i] = new Hand(deck.DealCard(), deck.DealCard());
        }
    }

    public Hand getHand(int i)
    {
        if(i > handsInPlay.length - 1) return null;
        return handsInPlay[i];
    }

    public void RevealHands()
    {
        System.out.println(Arrays.toString(handsInPlay));
    }

    public void ResetDeck()
    {
        this.deck.ResetDeck();
    }

    public void RigHand(int i, Hand hand)
    {
        for(Card card : hand.getHand())
        {
            deck.RemoveCard(card);
        }
        for(Hand handInPlay : handsInPlay)
        {
            for(Card card : handInPlay.getHand())
            {
                if(hand.hasSameCard(card))
                {
                    handInPlay.SwapCardInHand(deck.DealCard(), card);
                }
            }
        }
        Hand handToReturnToDeck = handsInPlay[i];
        handsInPlay[i] = hand;
        for(Card card : handToReturnToDeck.getHand())
        {
            deck.AddCard(card);
        }
    }

    public void RigFlop(Card[] cards)
    {
        if(!isFlopRigged)
        {
            isFlopRigged = true;
            communityCards = new Card[5];
            for (int i = 0; i < cards.length; i++) {
                communityCards[i] = cards[i];
                deck.RemoveCard(cards[i]);
            }
        }
    }

    public void RigTurn(Card card)
    {
        if(!isTurnRigged)
        {
            isTurnRigged = true;
            communityCards[3] = card;
            turn = card;
            deck.RemoveCard(card);
        }
    }

    public void RigRiver(Card card)
    {
        if(!isRiverRigged)
        {
            isRiverRigged = true;
            communityCards[4] = card;
            river = card;
            deck.RemoveCard(card);
        }
    }

    private void BurnCard() {
        deck.DealCard();
    }

    public Card[] Flop() {
        BurnCard();
        if(!isFlopRigged)
        {
            communityCards = new Card[5];
            communityCards[0] = deck.DealCard();
            communityCards[1] = deck.DealCard();
            communityCards[2] = deck.DealCard();
        }
        flop = Arrays.copyOf(communityCards, 3);
        return flop;
    }

    public Card Turn() {
        BurnCard();
        if(!isTurnRigged)
        {
            communityCards[3] = deck.DealCard();
        }
        turn = communityCards[3];
        return turn;
    }

    public Card River() {
        BurnCard();
        if(!isRiverRigged)
        {
            communityCards[4] = deck.DealCard();
        }
        river = communityCards[4];
        return river;
    }

    public ArrayList<Integer> GetWinningHandIndex()
    {
        ArrayList<Integer> winningIndex = new ArrayList<>();
        for(int i = 0; i < handsInPlay.length; i++)
        {
            for(Hand hand : GetWinningHand())
            {
                if(hand.isSameHand(handsInPlay[i]))
                {
                    winningIndex.add(i);
                }
            }
        }
        return winningIndex;
    }

    public ArrayList<Hand> GetWinningHand()
    {
        EvaluateHands(false);
        ArrayList<Hand> winningHands = new ArrayList<>();

        Hand bestHand = null;
        for (Hand hand : handsInPlay) {
            hand.setCommunityCards(communityCards);
            if (bestHand == null)
            {
                bestHand = hand;
                winningHands.add(bestHand);
            }
            else {
                int outcome = CompareHands(bestHand, hand); // -1 means contender has better hand, 0 means equal hands, 1 means best hand still winning
                if (outcome < 0) {
                    bestHand = hand;
                    winningHands.clear();
                    winningHands.add(bestHand);
                }
                else if (outcome == 0)
                {
                    winningHands.add(hand);
                }
            }
        }
        return winningHands;
    }

    public ArrayList<Hand> RevealWinningHand() {

        EvaluateHands(false);
        ArrayList<Hand> winningHands = new ArrayList<>();

        Hand bestHand = null;
        for (Hand hand : handsInPlay) {
            hand.setCommunityCards(communityCards);
            if (bestHand == null)
            {
                bestHand = hand;
                winningHands.add(bestHand);
            }
            else {
                int outcome = CompareHands(bestHand, hand); // -1 means contender has better hand, 0 means equal hands, 1 means best hand still winning
                if (outcome < 0) {
                    bestHand = hand;
                    winningHands.clear();
                    winningHands.add(bestHand);
                }
                else if (outcome == 0)
                {
                    winningHands.add(hand);
                }
            }
        }
        if(winningHands.size() > 1)
        {
            System.out.println("\n\n            "+MAGENTA_BACK+"SPLIT"+RESET+" "+MAGENTA_BACK+"SPLIT"+RESET+" "
                    +MAGENTA_BACK+"SPLIT"+RESET+" "+MAGENTA_BACK+"SPLIT"+RESET+" "
                    +MAGENTA_BACK+"SPLIT"+RESET+" "+MAGENTA_BACK+"SPLIT"+RESET+" "+GREEN+" \n\nWinning Hands: "+RESET+winningHands);
            for(Hand hand : winningHands)
            {
                hand.PrintBestHandType();
                System.out.println(hand+"'s Best Cards: "+hand.GetTopFiveCards());
            }
        }
        else if(winningHands.size() == 1)
        {
            System.out.println(GREEN+"\n\nWinning Hand: "+RESET+winningHands.getFirst());
            winningHands.getFirst().PrintBestHandType();
            System.out.println(winningHands.getFirst()+"'s Best Cards: "+winningHands.getFirst().GetTopFiveCards());
        }
        else {System.out.println("Something went wrong...");}
        return winningHands;
    }

    private void EvaluateHands(boolean showText)
    {
        for(Hand hand : handsInPlay)
        {
            hand.EvaluateHand(communityCards, showText);
        }
    }

    private int CompareHands(Hand hand1, Hand hand2) {

        // Check for Fold
        if (!hand1.isFolded() && hand2.isFolded()) return 1;
        else if (hand1.isFolded() && !hand2.isFolded()) return -1;
        else if (hand1.isFolded() && hand2.isFolded())
        {
            return 0;
        }

        // Compare RoyalFlush
        if (hand1.hasRoyalFlush() && !hand2.hasRoyalFlush()) return 1;
        else if (!hand1.hasRoyalFlush() && hand2.hasRoyalFlush()) return -1;
        else if (hand1.hasRoyalFlush() && hand2.hasRoyalFlush())
        {
            return 0;
        }

        // Compare StraightFlush
        if (hand1.hasStraightFlush() && !hand2.hasStraightFlush()) return 1;
        else if (!hand1.hasStraightFlush() && hand2.hasStraightFlush()) return -1;
        else if (hand1.hasStraightFlush() && hand2.hasStraightFlush())
        {
            return CompareStraightFlushes(hand1, hand2);
        }

        // Compare 4 of a Kind
        if (hand1.hasFourOfAKind() && !hand2.hasFourOfAKind()) return 1;
        else if (!hand1.hasFourOfAKind() && hand2.hasFourOfAKind()) return -1;
        else if (hand1.hasFourOfAKind() && hand2.hasFourOfAKind())
        {
            return CompareFourOfAKind(hand1, hand2);
        }

        // Compare Full House
        if (hand1.hasFullHouse() && !hand2.hasFullHouse()) return 1;
        else if (!hand1.hasFullHouse() && hand2.hasFullHouse()) return -1;
        else if (hand1.hasFullHouse() && hand2.hasFullHouse())
        {
            return CompareFullHouse(hand1, hand2);
        }

        // Check Flush
        if (hand1.hasFlush() && !hand2.hasFlush()) return 1;
        else if (!hand1.hasFlush() && hand2.hasFlush()) return -1;
        else if (hand1.hasFlush() && hand2.hasFlush()) {

            return CompareFlushes(hand1, hand2);
        }

        // Check Straight
        if (hand1.hasStraight() && !hand2.hasStraight()) return 1;
        else if (!hand1.hasStraight() && hand2.hasStraight()) return -1;
        else if (hand1.hasStraight() && hand2.hasStraight()) {

            return CompareStraights(hand1, hand2);
        }

        // Compare Three of a Kind
        if (hand1.hasThreeOfAKind() && !hand2.hasThreeOfAKind()) return 1;
        else if (!hand1.hasThreeOfAKind() && hand2.hasThreeOfAKind()) return -1;
        else if (hand1.hasThreeOfAKind() && hand2.hasThreeOfAKind()) {

            return CompareThreeOfAKind(hand1, hand2);
        }

        // Compare Two Pair
        if (hand1.hasTwoPair() && !hand2.hasTwoPair()) return 1;
        else if (!hand1.hasTwoPair() && hand2.hasTwoPair()) return -1;
        else if (hand1.hasTwoPair() && hand2.hasTwoPair()) {

            return CompareTwoPair(hand1, hand2);
        }

        // Compare One Pair
        if (hand1.hasOnePair() && !hand2.hasOnePair()) return 1;
        else if (!hand1.hasOnePair() && hand2.hasOnePair()) return -1;
        else if (hand1.hasOnePair() && hand2.hasOnePair()) {

            return CompareOnePair(hand1, hand2);
        }

        // Compare HighCard
        return CompareHighCard(hand1, hand2);
    }

    private int CompareHighCard(Hand hand1, Hand hand2)
    {
        List<Card> cards1 = Arrays.asList(combine(hand1.getHand(), communityCards));
        List<Card> cards2 = Arrays.asList(combine(hand2.getHand(), communityCards));
        if(GetHighestRank(cards1).getValue() == GetHighestRank(cards2).getValue())
        {
            if(GetSecondHighestRank(cards1).getValue() == GetSecondHighestRank(cards2).getValue())
            {
                if(GetThirdHighestRank(cards1).getValue() == GetThirdHighestRank(cards2).getValue())
                {
                    if(GetFourthHighestRank(cards1).getValue() == GetFourthHighestRank(cards2).getValue())
                    {
                        if(GetFifthHighestRank(cards1).getValue() == GetFifthHighestRank(cards2).getValue())
                        {
                            return 0;
                        }
                        return Integer.compare(GetFifthHighestRank(cards1).getValue(), GetFifthHighestRank(cards2).getValue());
                    }
                    return Integer.compare(GetFourthHighestRank(cards1).getValue(), GetFourthHighestRank(cards2).getValue());
                }
                return Integer.compare(GetThirdHighestRank(cards1).getValue(), GetThirdHighestRank(cards2).getValue());
            }
            return Integer.compare(GetSecondHighestRank(cards1).getValue(), GetSecondHighestRank(cards2).getValue());
        }
        return Integer.compare(GetHighestRank(cards1).getValue(), GetHighestRank(cards2).getValue());
    }

    private int CompareOnePair(Hand hand1, Hand hand2)
    {
        if(hand1.getHighPairCard().getValue() == hand2.getHighPairCard().getValue())
        {
            if(hand1.getHighCard().getValue() == hand2.getHighCard().getValue())
            {
                List<Card> cards1 = Arrays.asList(combine(hand1.getHand(), communityCards));
                List<Card> cards2 = Arrays.asList(combine(hand2.getHand(), communityCards));
                if(GetSecondHighestRank(cards1).getValue() == GetSecondHighestRank(cards2).getValue())
                {
                    if(GetThirdHighestRank(cards1).getValue() == GetThirdHighestRank(cards2).getValue())
                    {
                        if(GetFourthHighestRank(cards1).getValue() == GetFourthHighestRank(cards2).getValue())
                        {
                            if(GetFifthHighestRank(cards1).getValue() == GetFifthHighestRank(cards2).getValue())
                            {
                                return 0;
                            }
                            return Integer.compare(GetFifthHighestRank(cards1).getValue(), GetFifthHighestRank(cards2).getValue());
                        }
                        return Integer.compare(GetFourthHighestRank(cards1).getValue(), GetFourthHighestRank(cards2).getValue());
                    }
                    return Integer.compare(GetThirdHighestRank(cards1).getValue(), GetThirdHighestRank(cards2).getValue());
                }
                return Integer.compare(GetSecondHighestRank(cards1).getValue(), GetSecondHighestRank(cards2).getValue());
            }
            return Integer.compare(hand1.getHighCard().getValue(), hand2.getHighCard().getValue());
        }
        return Integer.compare(hand1.getHighPairCard().getValue(), hand2.getHighPairCard().getValue());
    }

    private int CompareTwoPair(Hand hand1, Hand hand2)
    {
        if(hand1.getHighTwoPairCard().getValue() == hand2.getHighTwoPairCard().getValue())
        {
            if(GetSecondHighestRank(hand1.getTwoPair()).getValue() == GetSecondHighestRank(hand2.getTwoPair()).getValue())
            {
                Card highCardOutSidePairs1 = getHighestCardOutsideTwoPair(hand1);
                Card highCardOutSidePairs2 = getHighestCardOutsideTwoPair(hand2);
                if(highCardOutSidePairs1.getValue() == highCardOutSidePairs2.getValue())
                {
                    // TODO: possibly do not need this inner junk here, can replace with return 0
                    Card secondHighCardOutSidePairs1 = getSecondHighestCardOutsideTwoPair(hand1);
                    Card secondHighCardOutSidePairs2 = getSecondHighestCardOutsideTwoPair(hand2);
                    if(secondHighCardOutSidePairs1.getValue() == secondHighCardOutSidePairs2.getValue())
                    {
                        return 0;
                    }
                    return Integer.compare(secondHighCardOutSidePairs1.getValue(), secondHighCardOutSidePairs2.getValue());
                }
                return Integer.compare(highCardOutSidePairs1.getValue(), highCardOutSidePairs2.getValue());
            }
            return Integer.compare(GetSecondHighestRank(hand1.getTwoPair()).getValue(), GetSecondHighestRank(hand2.getTwoPair()).getValue());
        }
        return Integer.compare(hand1.getHighTwoPairCard().getValue(), hand2.getHighTwoPairCard().getValue());
    }

    private Card getHighestCardOutsideTwoPair(Hand hand1) {
        Card highCardOutSidePairs1 = hand1.getHighCard();
        if(hand1.getTwoPair().contains(hand1.getHighCard()))
        {
            ArrayList<Card> cards1 = new ArrayList<>();
            cards1.addAll(Arrays.asList(communityCards));
            cards1.addAll(Arrays.asList(hand1.getHand()));
            highCardOutSidePairs1 = GetSecondHighestRank(cards1);

            if(hand1.getTwoPair().contains(highCardOutSidePairs1))
            {
                highCardOutSidePairs1 = GetThirdHighestRank(cards1);
            }
        }
        return highCardOutSidePairs1;
    }

    private Card getSecondHighestCardOutsideTwoPair(Hand hand1) {
        Card highCardOutSidePairs1 = hand1.getHighCard();
        if(hand1.getTwoPair().contains(hand1.getHighCard()))
        {
            ArrayList<Card> cards1 = new ArrayList<>();
            cards1.addAll(Arrays.asList(communityCards));
            cards1.addAll(Arrays.asList(hand1.getHand()));
            highCardOutSidePairs1 = GetSecondHighestRank(cards1);

            if(hand1.getTwoPair().contains(highCardOutSidePairs1))
            {
                highCardOutSidePairs1 = GetFourthHighestRank(cards1);
            }
        }
        return highCardOutSidePairs1;
    }

    private int CompareThreeOfAKind(Hand hand1, Hand hand2)
    {
        if(hand1.getHighThreeOfAKindCard().getValue() == hand2.getHighThreeOfAKindCard().getValue())
        {
            List<Card> cards1 = Arrays.asList(combine(hand1.getHand(), communityCards));
            List<Card> cards2 = Arrays.asList(combine(hand2.getHand(), communityCards));
            if(hand1.getHighCard().getValue() == hand2.getHighCard().getValue())
            {
                if(GetSecondHighestRank(cards1).getValue() == GetSecondHighestRank(cards2).getValue())
                {
                    if(GetThirdHighestRank(cards1).getValue() == GetThirdHighestRank(cards2).getValue())
                    {
                        // TODO: I think third highest is enough
                        if(GetFourthHighestRank(cards1).getValue() == GetFourthHighestRank(cards2).getValue())
                        {
                            return 0;
                        }
                        return Integer.compare(GetFourthHighestRank(cards1).getValue(), GetFourthHighestRank(cards2).getValue());
                    }
                    return Integer.compare(GetThirdHighestRank(cards1).getValue(), GetThirdHighestRank(cards2).getValue());
                }
                return Integer.compare(GetSecondHighestRank(cards1).getValue(), GetSecondHighestRank(cards2).getValue());
            }
            return Integer.compare(hand1.getHighCard().getValue(), hand2.getHighCard().getValue());
        }
        return Integer.compare(hand1.getHighThreeOfAKindCard().getValue(), hand2.getHighThreeOfAKindCard().getValue());
    }

    private int CompareFullHouse(Hand hand1, Hand hand2)
    {
        if(hand1.getHighFullHouseCard().getValue() == hand2.getHighFullHouseCard().getValue())
        {
            if(hand1.getHighPairCard().getValue() == hand2.getHighPairCard().getValue())
            {
                return 0;
            }
            return Integer.compare(hand1.getHighPairCard().getValue(), hand2.getHighPairCard().getValue());
        }
        return Integer.compare(hand1.getHighFullHouseCard().getValue(), hand2.getHighFullHouseCard().getValue());
    }

    private int CompareFourOfAKind(Hand hand1, Hand hand2)
    {
        if(hand1.getHighFourOfAKindCard().getValue() == hand2.getHighFourOfAKindCard().getValue())
        {
            if(hand1.getHighCard().getValue() == hand2.getHighCard().getValue())
            {
                if(hand1.getHighCardInHand().getValue() == hand2.getHighCardInHand().getValue())
                {
                    return 0;
                }
                return Integer.compare(hand1.getHighCardInHand().getValue(), hand2.getHighCardInHand().getValue());
            }
            return Integer.compare(hand1.getHighCard().getValue(), hand2.getHighCard().getValue());
        }
        return Integer.compare(hand1.getHighFourOfAKindCard().getValue(), hand2.getHighFourOfAKindCard().getValue());
    }

    private int CompareStraightFlushes(Hand hand1, Hand hand2)
    {
//        System.out.println("Current Best Hand's High Straight Flush Card: "+hand1.getHighStraightFlushCard());
//        System.out.println("Contending Hand's High Straight Flush Card: "+hand2.getHighStraightFlushCard());

        if(hand1.getHighStraightFlushCard().getValue() == hand2.getHighStraightFlushCard().getValue())
        {
            return 0;
        }
        return Integer.compare(hand1.getHighStraightFlushCard().getValue(), hand2.getHighStraightFlushCard().getValue());
    }

    private int CompareStraights(Hand hand1, Hand hand2) {
//        System.out.println("Current Best Hand's High Straight Card: "+hand1.getHighStraightCard());
//        System.out.println("Contending Hand's High Straight Card: "+hand2.getHighStraightCard());

        if(hand1.getHighStraightCard().getValue() == hand2.getHighStraightCard().getValue())
        {
            Card highestCardInStraightAndHand1 = GetHighestRank(IntersectTopFiveWithHandByRank(hand1.getStraight(), hand1));
            Card highestCardInStraightAndHand2 = GetHighestRank(IntersectTopFiveWithHandByRank(hand2.getStraight(), hand2));
//            System.out.println("Current Best Hand's High Straight Card in Hand: "+highestCardInStraightAndHand1);
//            System.out.println("Contending Hand's High Straight Card in Hand: "+highestCardInStraightAndHand2);

            if(highestCardInStraightAndHand1.getValue() == highestCardInStraightAndHand2.getValue())
            {
                return 0;
            }
            return Integer.compare(highestCardInStraightAndHand1.getValue(), highestCardInStraightAndHand2.getValue());
        }
        return Integer.compare(hand1.getHighStraightCard().getValue(), hand2.getHighStraightCard().getValue());
    }

    private int CompareFlushes(Hand hand1, Hand hand2) {
//        System.out.println("Current Best Hand's High Flush Card: "+hand1.getHighFlushCard());
//        System.out.println("Contending Hand's High Flush Card: "+hand2.getHighFlushCard());

        if(hand1.getHighFlushCard().getValue() == hand2.getHighFlushCard().getValue())
        {
            Card highestCardInFlushAndHand1 = GetHighestRankWithSuit(hand1.getHand(), hand1.getFlush().getFirst().getSuit());
            Card highestCardInFlushAndHand2 = GetHighestRankWithSuit(hand2.getHand(), hand2.getFlush().getFirst().getSuit());
//            System.out.println("Current Best Hand's High Flush Card in Hand: "+highestCardInFlushAndHand1);
//            System.out.println("Contending Hand's High Flush Card in Hand: "+highestCardInFlushAndHand2);

            if(highestCardInFlushAndHand1.getValue() == highestCardInFlushAndHand2.getValue())
            {
                return 0;
            }
            return Integer.compare(highestCardInFlushAndHand1.getValue(), highestCardInFlushAndHand2.getValue());
        }
        return Integer.compare(hand1.getHighFlushCard().getValue(), hand2.getHighFlushCard().getValue());
    }

    private Card GetHighestRankWithSuit(Card[] cards, String suit)
    {
        ArrayList<Card> cardsOfSuit = new ArrayList<>();
        for(Card card : cards)
        {
            if(card.getSuit().equals(suit)) cardsOfSuit.add(card);
        }
        return GetHighestRank(cardsOfSuit);
    }

    private Card GetHighestRank(Card[] cards) {
        return Arrays.stream(cards).filter(Objects::nonNull).max(Comparator.comparingInt(Card::getValue)).orElse(null);
    }
    private Card GetHighestRank(List<Card> cards)
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

    private Card GetFifthHighestRank(List<Card> cards) {

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


    private Card GetFourthHighestRank(List<Card> cards) {

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


    private Card GetThirdHighestRank(List<Card> cards) {

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

    private Card[] combine(Card[] hand, Card[] communityCards) {
        Card[] combined = new Card[hand.length + communityCards.length];
        System.arraycopy(hand, 0, combined, 0, hand.length);
        System.arraycopy(communityCards, 0, combined, hand.length, communityCards.length);
        return combined;
    }

    private String ConvertValueToRank(int value)
    {
        String rank = "None";
        switch (value)
        {
            case 13 -> rank = "Ace";
            case 12 -> rank = "King";
            case 11 -> rank = "Queen";
            case 10 -> rank = "Jack";
            case 9 -> rank = "10";
            case 8 -> rank = "9";
            case 7 -> rank = "8";
            case 6 -> rank = "7";
            case 5 -> rank = "6";
            case 4 -> rank = "5";
            case 3 -> rank = "4";
            case 2 -> rank = "3";
            case 1 -> rank = "2";
        }
        return rank;
    }

    private ArrayList<Card> IntersectTopFiveWithHand(ArrayList<Card> topFiveCards, Hand hand)
    {
        ArrayList<Card> intersection = new ArrayList<>();
        for(Card card : topFiveCards)
        {
            if(hand.getHand()[0].equals(card) || hand.getHand()[1].equals(card))
            {
                intersection.add(card);
            }
        }
        return intersection;
    }
    private ArrayList<Card> IntersectTopFiveWithHandByRank(ArrayList<Card> topFiveCards, Hand hand)
    {
        ArrayList<Card> intersection = new ArrayList<>();
        for(Card card : topFiveCards)
        {
            if(hand.getHand()[0].getValue() == card.getValue())
            {
                intersection.add(hand.getHand()[0]);
            }
            else if(hand.getHand()[1].getValue() == card.getValue())
            {
                intersection.add(hand.getHand()[1]);
            }
        }
        return intersection;
    }

    public Card[] getFlop() {
        return flop;
    }

    public Card getTurn() {
        return turn;
    }

    public Card getRiver() {
        return river;
    }

}
