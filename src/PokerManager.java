import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class PokerManager
{

    private PokerDealer dealer;
    private BetManager betManager;
    private int numPlayers;
    private String color;
    public static final String RESET = "\u001B[0m";
    public static final String GREEN = "\033[1;92m";
    public static final String YELLOW = "\033[1;93m";
    public static final String RED = "\033[1;91m";
    public static final String WHITE = "\033[1;97m";    // WHITE
    public static final String MAGENTA = "\033[1;95m";
    public static final String BLUE = "\033[1;94m";
    public static final String MAGENTA_BACK = "\033[0;105m";
    private final int simGames = 500_000;
    private String[] playerColors = new String[]{BLUE, GREEN, MAGENTA, YELLOW};
    private ArrayList<Integer> botI;
    private int numBots;
    private int handToSpectate = 0;

    public PokerManager(int numPlayers, int numBots)
    {
        this.handToSpectate = new Random().nextInt(numPlayers);
        this.numPlayers = numPlayers;
        this.numBots = numBots;
        this.botI = new ArrayList<>();
        this.dealer = new PokerDealer();
        this.betManager = new BetManager(numPlayers);
        PlayGame();
    }

    private boolean isHandToSpectateFoldedOrOut()
    {
        return betManager.getLosers().contains(handToSpectate) || betManager.getFolds().contains(handToSpectate);
    }

    private void SwitchHandToSpectate()
    {
        if(!betManager.getFolds().contains(handToSpectate)) return;
        do {
            this.handToSpectate = new Random().nextInt(numPlayers);
        } while (isHandToSpectateFoldedOrOut());
    }

    public void PlayGame()
    {
        Scanner scanner = new Scanner(System.in);
        while(true)
        {
//            SwitchHandToSpectate();
            if(numPlayers - betManager.getNumLosers() == 1)
            {
                return;
            }
            dealer.DealHands(numPlayers);

            ArrayList<PokerBot> bots = new ArrayList<>();
            ArrayList<Double> botEquities = new ArrayList<>();

            PreFlopEquity(bots, botEquities);

            betManager.SetBots(botI);
            betManager.InformBots(botI, botEquities);
            betManager.PreFlopBettingRound();
            botEquities.clear();
//            SwitchHandToSpectate();
//            scanner.nextLine();

            dealer.Flop();
            dealer.RevealFlop();

            PostFlopEquity(bots, botEquities);

            betManager.InformBots(botI, botEquities);
            betManager.PostFlopBettingRound();
            botEquities.clear();
//            SwitchHandToSpectate();
//            scanner.nextLine();

            dealer.Turn();
            dealer.RevealTurn();

            PostTurnEquity(bots, botEquities);

            betManager.InformBots(botI, botEquities);
            betManager.PostFlopBettingRound();
            botEquities.clear();
//            SwitchHandToSpectate();
//            scanner.nextLine();

            dealer.River();
            dealer.RevealRiver();

            PostRiverEquity(bots, botEquities);

            betManager.InformBots(botI, botEquities);
            betManager.PostFlopBettingRound();
            botEquities.clear();
//            SwitchHandToSpectate();
//            scanner.nextLine();

            SetFolds();
            if(dealer.RevealWinningHand().size() > 1)
            {
                betManager.SplitWinners(dealer.GetWinningHandIndex());
            }
            else {
                betManager.RewardWinner(dealer.GetWinningHandIndex().getFirst());
            }

            dealer.RevealHands();

            dealer.ResetDeck();
            botI.clear();
            System.out.println("\n========================================================\n");

        }
    }

    private void PostRiverEquity(ArrayList<PokerBot> bots, ArrayList<Double> botEquities) {
        for(int i = 0; i < numPlayers; i++)
        {
            bots.get(i).setRiver(dealer.getRiver());
            Double botWin = bots.get(i).getEquityPostRiver(simGames);
            SetColor(botWin);
            if(!betManager.getFolds().contains(i)  && !botI.contains(i))
            {
                System.out.println(WHITE+"\nPlayer "+RESET+playerColors[i%playerColors.length]+(i+1)+RESET+WHITE+" : " + bots.get(i).getHand() +WHITE+ " Equity: " + RESET + color + String.format("%.1f", botWin)+" %" + RESET);
            }
            if(botI.contains(i))
            {
                botEquities.add(botWin);
            }
//            if(i == handToSpectate)
//            {
//                System.out.println(WHITE+"\nPlayer "+RESET+playerColors[i%playerColors.length]+(i+1)+RESET+WHITE+" : " + bots.get(i).getHand() +WHITE+ " Equity: " + RESET + color + String.format("%.1f", botWin)+" %" + RESET);
//            }
        }
    }

    private void PostTurnEquity(ArrayList<PokerBot> bots, ArrayList<Double> botEquities) {
        for(int i = 0; i < numPlayers; i++)
        {
            bots.get(i).setTurn(dealer.getTurn());
            Double botWin = bots.get(i).getEquityPostTurn(simGames);
            SetColor(botWin);
            if(!betManager.getFolds().contains(i)  && !botI.contains(i))
            {
                System.out.println(WHITE+"\nPlayer "+RESET+playerColors[i%playerColors.length]+(i+1)+RESET+WHITE+" : " + bots.get(i).getHand() +WHITE+ " Equity: " + RESET + color + String.format("%.1f", botWin)+" %" + RESET);
            }
            if(botI.contains(i))
            {
                botEquities.add(botWin);
            }
//            if(i == handToSpectate)
//            {
//                System.out.println(WHITE+"\nPlayer "+RESET+playerColors[i%playerColors.length]+(i+1)+RESET+WHITE+" : " + bots.get(i).getHand() +WHITE+ " Equity: " + RESET + color + String.format("%.1f", botWin)+" %" + RESET);
//            }
        }
    }

    private void PostFlopEquity(ArrayList<PokerBot> bots, ArrayList<Double> botEquities) {
        for(int i = 0; i < numPlayers; i++)
        {
            bots.get(i).setFlop(dealer.getFlop());
            Double botWin = bots.get(i).getEquityPostFlop(simGames);
            SetColor(botWin);
            if(!betManager.getFolds().contains(i)  && !botI.contains(i))
            {
                System.out.println(WHITE+"\nPlayer "+RESET+playerColors[i%playerColors.length]+(i+1)+RESET+WHITE+" : " + bots.get(i).getHand() +WHITE+ " Equity: " + RESET + color + String.format("%.1f", botWin)+" %" + RESET);
            }
            if(botI.contains(i))
            {
                botEquities.add(botWin);
            }
//            if(i == handToSpectate)
//            {
//                System.out.println(WHITE+"\nPlayer "+RESET+playerColors[i%playerColors.length]+(i+1)+RESET+WHITE+" : " + bots.get(i).getHand() +WHITE+ " Equity: " + RESET + color + String.format("%.1f", botWin)+" %" + RESET);
//            }
        }
    }

    private void PreFlopEquity(ArrayList<PokerBot> bots, ArrayList<Double> botEquities) {
        for(int i = 0; i < numPlayers; i++)
        {
            if((i > numPlayers - numBots - 1) && !botI.contains(i) && !betManager.getLosers().contains(i)) botI.add(i);
            bots.add(new PokerBot(dealer.getHand(i), numPlayers - betManager.getNumLosers()));

            Double botWin = bots.get(i).getEquityPreFlop(simGames);
            SetColor(botWin);
            if(!betManager.getLosers().contains(i) && !botI.contains(i))
            {
                System.out.println(WHITE+"\nPlayer "+RESET+playerColors[i%playerColors.length]+(i+1)+RESET+WHITE+" : " + bots.get(i).getHand() +WHITE+ " Equity: " + RESET + color + String.format("%.1f", botWin)+" %" + RESET);
            }
            else if (botI.contains(i) && !betManager.getLosers().contains(i))
            {
                botEquities.add(botWin);
            }
            else {
                betManager.AutoFoldLoser(i);
            }
//            if(i == handToSpectate)
//            {
//                System.out.println(WHITE+"\nPlayer "+RESET+playerColors[i%playerColors.length]+(i+1)+RESET+WHITE+" : " + bots.get(i).getHand() +WHITE+ " Equity: " + RESET + color + String.format("%.1f", botWin)+" %" + RESET);
//            }

        }
    }

    private void SetColor(Double winPercent)
    {
        if(winPercent > 60)
        {
            color = GREEN;
        }
        else if(winPercent > 40)
        {
            color = YELLOW;
        }
        else
        {
            color = RED;
        }
    }

    private void SetFolds()
    {
        for(Integer i : betManager.getFolds())
        {
            dealer.handsInPlay[i].setFolded(true);
        }
    }

    private void ResetFolds()
    {
        for(int i = 0; i < dealer.handsInPlay.length; i++)
        {
            dealer.handsInPlay[i].setFolded(false);
        }
    }


}
