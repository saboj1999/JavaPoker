import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class Main
{
    public static final String RESET = "\u001B[0m";
    public static final String GREEN = "\033[1;92m";
    public static final String YELLOW = "\033[1;93m";
    public static final String RED = "\033[1;91m";
    public static final String WHITE = "\033[1;97m";    // WHITE
    public static String color = "";

    static int numPlayers = 2;
    static int maxPlayers = 10;
    static int rigIndex = 0;
    static double totalGames = 10000;
    static double gamesWon = 0;
    static double gamesLost = 0;
    static double gamesSplit = 0;

    public static void main(String[] args)
    {

//        RunAllHands();
//        RunHand(new Hand(new Card("Ace", "Spades"), new Card("Ace", "Hearts")));

//        WinPercentGames();
        PokerManager pm = new PokerManager(numPlayers, numPlayers - 1);
        pm.PlayGame();
        main(args);
    }

    private static void WinPercentGames() {
        Scanner scanner = new Scanner(System.in);
        while(true)
        {
            PokerDealer dealer = new PokerDealer();
            dealer.DealHands(numPlayers);

            PokerBot bot = new PokerBot(dealer.getHand(0), numPlayers);

            Double botWin = bot.getEquityPreFlop(500);
            SetColor(botWin);
            System.out.println("\n" + bot.getHand() +WHITE+ " Equity: " + RESET + color + String.format("%.1f", botWin)+" %" + RESET);
            scanner.nextLine();

            dealer.Flop();
            dealer.RevealFlop();
            bot.setFlop(dealer.getFlop());
            botWin = bot.getEquityPostFlop(500);
            SetColor(botWin);
            System.out.println("\n" + bot.getHand() +WHITE+ " Equity: " + RESET + color + String.format("%.1f", botWin)+" %" + RESET);
            scanner.nextLine();

            dealer.Turn();
            dealer.RevealFlop();
            dealer.RevealTurn();
            bot.setTurn(dealer.getTurn());
            botWin = bot.getEquityPostTurn(500);
            SetColor(botWin);
            System.out.println("\n" + bot.getHand() +WHITE+ " Equity: " + RESET + color + String.format("%.1f", botWin)+" %" + RESET);
            scanner.nextLine();

            dealer.River();
            dealer.RevealFlop();
            dealer.RevealTurn();
            dealer.RevealRiver();
            bot.setRiver(dealer.getRiver());
            botWin = bot.getEquityPostRiver(500);
            SetColor(botWin);
            System.out.println("\n" + bot.getHand() +WHITE+ " Equity: " + RESET + color + String.format("%.1f", botWin)+" %" + RESET);
            scanner.nextLine();

            dealer.RevealHands();
            dealer.RevealWinningHand();

            System.out.println("\n========================================================\n");

        }
    }

    private static void SetColor(Double winPercent)
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

    private static void RunAllHands() {
        Deck deck = new Deck();
        ArrayList<Hand> possibleHands = new ArrayList<>();

        for(Card card1 : deck.__getDeck__())
        {
            for(Card card2 : deck.__getDeck__())
            {
                if(!card1.isSameCard(card2))
                {
                    possibleHands.add(new Hand(card1, card2));
                }
            }
        }

        for(Hand riggedHand : possibleHands) {

            RunHand(riggedHand);
        }
    }

    private static void RunHand(Hand riggedHand) {
        
        PokerDealer dealer = new PokerDealer();
        String currentHandFileName = riggedHand.getHand()[0].getRank() + "_" + riggedHand.getHand()[0].getSuit() +
                "__" + riggedHand.getHand()[1].getRank() + "_" + riggedHand.getHand()[1].getSuit() + ".txt";

        String currentFolder = riggedHand.getHand()[0].getRank() + "_" + riggedHand.getHand()[0].getSuit();
        File folder = new File(currentFolder);
        if (!folder.exists()) {
            folder.mkdir();
        }

        for (int j = 2; j < maxPlayers + 1; j++) {
            numPlayers = j;
            gamesWon = 0;
            gamesLost = 0;
            gamesSplit = 0;
            for (int i = 0; i < totalGames; i++) {

                System.out.println("Dealing hands...");
                dealer.DealHands(numPlayers);

                dealer.RigHand(rigIndex, riggedHand);
                System.out.println(dealer.getHand(0));

                ArrayList<Hand> winningHands = dealer.SimGame();
                UpdateWinLossCount(winningHands, riggedHand);
                riggedHand.ResetHandAchievements();
            }

            try {
                PrintWriter writer = new PrintWriter(new FileWriter(folder.getAbsolutePath() + "/" + currentHandFileName, true));
                writer.print(currentHandFileName.split("\\.")[0] + " ");
                writer.print("Player_Count: " + numPlayers);
                writer.print(" Win_Percentage: " + (gamesWon / totalGames) * 100);
                writer.print(" Loss_Percentage: " + (gamesLost / totalGames) * 100);
                writer.println(" Split_Percentage: " + (gamesSplit / totalGames) * 100); // Ensure everything is written to the file
                writer.flush();
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Win Percentage: " + (gamesWon / totalGames) * 100);
            System.out.println("Loss Percentage: " + (gamesLost / totalGames) * 100);
            System.out.println("Split Percentage: " + (gamesSplit / totalGames) * 100);

        }
    }

    public static void UpdateWinLossCount(ArrayList<Hand> winningHands, Hand riggedHand)
    {
        if(winningHands.size() > 1)
        {
            for(Hand hand : winningHands)
            {
                if(riggedHand.isSameHand(hand))
                {
                    gamesSplit++;
                    System.out.println("Rigged hand split! Total: "+gamesSplit);
                }
            }
        }
        else if(riggedHand.isSameHand(winningHands.getFirst()))
        {
            gamesWon++;
            System.out.println("Rigged hand won! Total: "+gamesWon);
        }
        else
        {
            gamesLost++;
            System.out.println("Rigged hand lost! Total: "+gamesLost);
        }
    }
}
