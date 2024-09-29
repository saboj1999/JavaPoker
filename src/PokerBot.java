import java.util.ArrayList;

public class PokerBot
{
    Hand hand;
    PokerDealer simDealer;
    int numPlayers;

    Card[] flop;
    Card turn;
    Card river;

    public PokerBot(Hand hand, int numPlayers)
    {
        this.hand = hand;
        this.numPlayers = numPlayers;
        this.simDealer = new PokerDealer();

        flop = null;
        turn = null;
        river = null;
    }

    public Double getEquityPreFlop(int numGames)
    {
        int winCount = 0;
        Hand handClone = this.hand;
        for (int i = 0; i < numGames; i++) {

            simDealer.DealHands(numPlayers);

            simDealer.RigHand(0, handClone);

            ArrayList<Hand> winningHands = simDealer.SimGame();
            if(winningHands.size() == 1 && handClone.isSameHand(winningHands.getFirst()))
            {
                winCount++;
            }
            handClone.ResetHandAchievements();
        }
            return (winCount / (double) numGames) * 100;
    }

    public Double getEquityPostFlop(int numGames)
    {
        if(flop == null) return 0.0;

        int winCount = 0;
        Hand handClone = this.hand;
        for (int i = 0; i < numGames; i++) {

            simDealer.DealHands(numPlayers);

            simDealer.RigHand(0, handClone);
            simDealer.RigFlop(flop);

            ArrayList<Hand> winningHands = simDealer.SimGame();
            if(winningHands.size() == 1 && handClone.isSameHand(winningHands.getFirst()))
            {
                winCount++;
            }
            handClone.ResetHandAchievements();
        }
        return (winCount / (double) numGames) * 100;
    }

    public Double getEquityPostTurn(int numGames)
    {
        if(flop == null || turn == null) return 0.0;

        int winCount = 0;
        Hand handClone = this.hand;
        for (int i = 0; i < numGames; i++) {

            simDealer.DealHands(numPlayers);

            simDealer.RigHand(0, handClone);
            simDealer.RigFlop(flop);
            simDealer.RigTurn(turn);

            ArrayList<Hand> winningHands = simDealer.SimGame();
            if(winningHands.size() == 1 && handClone.isSameHand(winningHands.getFirst()))
            {
                winCount++;
            }
            handClone.ResetHandAchievements();
        }
        return (winCount / (double) numGames) * 100;
    }

    public Double getEquityPostRiver(int numGames)
    {
        if(flop == null || turn == null || river == null) return 0.0;

        int winCount = 0;
        Hand handClone = this.hand;
        for (int i = 0; i < numGames; i++) {

            simDealer.DealHands(numPlayers);

            simDealer.RigHand(0, handClone);
            simDealer.RigFlop(flop);
            simDealer.RigTurn(turn);
            simDealer.RigRiver(river);

            ArrayList<Hand> winningHands = simDealer.SimGame();
            if(winningHands.size() == 1 && handClone.isSameHand(winningHands.getFirst()))
            {
                winCount++;
            }
            handClone.ResetHandAchievements();
        }
        return (winCount / (double) numGames) * 100;
    }

    public void setFlop(Card[] flop) {
        this.flop = flop;
    }

    public void setTurn(Card turn) {
        this.turn = turn;
    }

    public void setRiver(Card river) {
        this.river = river;
    }

    public Hand getHand() {
        return hand;
    }

    public void setHand(Hand hand)
    {
        this.hand = hand;
    }
}
