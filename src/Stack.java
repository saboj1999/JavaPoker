import java.util.Random;
import java.util.Scanner;

public class Stack
{
    int stackSize;
    private boolean isBot;

    public static final String RESET = "\u001B[0m";
    public static final String GREEN = "\033[1;92m";
    public static final String YELLOW = "\033[1;93m";
    public static final String RED = "\033[1;91m";
    public static final String WHITE = "\033[1;97m";    // WHITE
    public static final String MAGENTA = "\033[1;95m";
    public static final String BLUE = "\033[1;94m";
    public static final String MAGENTA_BACK = "\033[0;105m";
    String[] playerColors = new String[]{BLUE, GREEN, MAGENTA, YELLOW};
    private Double equity;
    private int currentPot;
    private int numPlayers;
    private int amountPaid;
    private Random rand;

    public Stack(int amount)
    {
        this.stackSize = amount;
        this.isBot = false;
        this.equity = 0.0;
        this.rand = new Random();
        this.amountPaid = 0;
    }

    public int getStackSize() {
        return stackSize;
    }

    public void setBot()
    {
        this.isBot = true;
    }

    public void setCurrentPot(int currentPot)
    {
        this.currentPot = currentPot;
    }

    public void setNumPlayers(int numPlayers)
    {
        this.numPlayers = numPlayers;
    }

    // should probably inform bot with things like pot size and other peoples stack size
    public void InformBot(Double equity)
    {
        this.equity = equity;
    }

    public void setStackSize(int stackSize) {
        this.stackSize = stackSize;
    }

    public void withdraw(int amount)
    {
        if(amount > stackSize)
        {
            stackSize = 0;
        }
        else {
            stackSize -= amount;
        }
    }

    public void deposit(int amount)
    {
        stackSize += amount;
    }

    private static void Sleep(int time) {
        try {
            Thread.sleep(time); // 1 second = 1000 milliseconds
        } catch (InterruptedException e) {
            // Handle the exception if the sleep is interrupted
            e.printStackTrace();
        }
    }

    public void setAmountPaid(int amountPaid)
    {
        this.amountPaid = amountPaid;
    }

    public int getAmountPaid()
    {
        return amountPaid;
    }


    // TODO: get access to current pot
    //  use pot size to determine bet amount
    //  use own stack size to evaluate worth (during raise?) based on equity and pot size
    //  maybe even use player count to determine what equity means
    //  get what best 5 cards can beat the bot currently
    private int BOTWhatWillItBe(int currentIndexBetting)
    {
        PrintStack(currentIndexBetting);
        Sleep(3000);

        if(rand.nextInt(100) < 10) return Bet((int) (currentPot*(rand.nextDouble() * rand.nextInt(2) + 2)));
        if(rand.nextInt(100) < 60 && equity > 50) {
            if(currentPot <= 150)
            {
                return Bet(currentPot/3);
            }
            else {
                return Bet(currentPot / 10);
            }
        }
        // equity / pot / stack
        if(Math.ceil(equity) < 18) return Check();
        if(equity < 30) return Bet(currentPot/3);
        if(equity < 45) return Bet(currentPot/2);
        if(equity < 60) return Bet(currentPot);
        if(equity >= 60) return Bet(currentPot*2);
        return 0;
    }

    private String __getPlayerColor(int i)
    {
        return playerColors[(i)%playerColors.length];
    }

    private void PrintAllIn(int i)
    {
        System.out.println(WHITE+"\nPlayer "+RESET+__getPlayerColor(i)+(i+1)+RESET+WHITE+" is "+RESET+__getPlayerColor(i)+"ALL IN"+RESET+WHITE+" for "+RESET+GREEN+stackSize+RESET+WHITE+"!"+RESET);
    }

    private int BOTSomeoneRaised(int totalOwed, int currentIndexBetting)
    {
        // NEED NumPlayers and Pot Size
        // add chance to shove if pot size gets too big
        System.out.println(WHITE+"Bet raised to "+RESET+GREEN+totalOwed+RESET+WHITE+".");
        System.out.println(GREEN+(totalOwed - amountPaid)+RESET+WHITE+" to Call."+RESET);
        PrintStack(currentIndexBetting);
        Sleep(3000);
        if(rand.nextInt(100) < 5 && stackSize < 0.10 * currentPot/*15 % of current pot*/) {
            PrintAllIn(currentIndexBetting);
            return Bet(stackSize);
        }
        if(rand.nextInt(100) < 5 && stackSize < 0.15 * 1000/*15 % of default stack size*/) {
            PrintAllIn(currentIndexBetting);
            return Bet(stackSize);
        }
        if(rand.nextInt(100) < 15) {
            Call(totalOwed - amountPaid);
            return Bet(currentPot/(rand.nextInt(2) + 2));
        }

        // equity * pot > totalOwed ?
        if(((equity/100.0) * currentPot) < (totalOwed - amountPaid))
        {
            if(rand.nextInt(100) < 25) {
                return Call(totalOwed - amountPaid);
            }
            if(equity < 35 || (rand.nextInt(100) < 40 && totalOwed > currentPot)) return Fold(); // Fold
            return Call(totalOwed); // Call
        }
        else {
            if(equity >= 60) {
                if(rand.nextInt(100) < 50 && stackSize < 0.40 * 1000/*40% of default stack size*/) {
                    PrintAllIn(currentIndexBetting);
                    return Bet(stackSize);
                } // all in
                Call(totalOwed - amountPaid);
                return Bet((int) ((equity/100) * currentPot));
            }
            else {
                return Call(totalOwed - amountPaid);
            }
        }
    }

    public int WhatWillItBe(int currentIndexBetting)
    {
        if(isBot) return BOTWhatWillItBe(currentIndexBetting);
        Scanner scanner = new Scanner(System.in);
        String input;

        PrintStack(currentIndexBetting);

        while (true) {
            System.out.println(WHITE+"\n'Check', 'Fold', or Bet (value): "+RESET);
            input = scanner.nextLine().trim();

            // Check if input is "Check" or "Fold"
            if(input.equalsIgnoreCase("Check"))
            {
                return Check();
            }
            else if(input.equalsIgnoreCase("Fold"))
            {
                return Fold();
            }

            // Check if input is a valid integer bet
            try {
                int bet = Integer.parseInt(input);
                if (bet > 0) {
                    if(bet >= stackSize) PrintAllIn(currentIndexBetting);
                    return Bet(bet);
                } else {
                    System.out.println(WHITE+"\nBet must be a positive integer."+RESET);
                }
            } catch (NumberFormatException e) {
                System.out.println(RED+"\nInvalid input."+RESET+WHITE+"\nPlease enter 'Check', 'Fold', or a positive integer value."+RESET);
            }

        }
    }

    public int SomeoneRaised(int raise, int raiseIndex, int currentIndexBetting)
    {
        if(isBot) return BOTSomeoneRaised(raise, currentIndexBetting);
        Scanner scanner = new Scanner(System.in);
        String input;

        PrintStack(currentIndexBetting);
        System.out.println(WHITE+"Bet raised to "+RESET+GREEN+raise+RESET+WHITE+".");

        System.out.println(WHITE+"\nPlayer "+RESET+playerColors[(currentIndexBetting)%playerColors.length]+(currentIndexBetting+1)+ RESET+WHITE+", you're up!"
                +" Player "+RESET+playerColors[(raiseIndex)%playerColors.length]+(raiseIndex+1)+RESET+BLUE+" raised"+RESET+WHITE+" bet to "+RESET+GREEN+(raise)+RESET+WHITE+"."+RESET);

        System.out.println(GREEN+(raise - amountPaid)+RESET+WHITE+" to Call."+RESET);

        while (true) {
            System.out.println(WHITE+"Bet raised to "+RESET+GREEN+raise+RESET+WHITE+". \n'Call', 'Fold', or Raise >= "+RESET+GREEN+raise*2+RESET+WHITE+"."+RESET);
            input = scanner.nextLine().trim();

            // Check if input is "Check" or "Fold"
            if(input.equalsIgnoreCase("Call"))
            {
                return Call(raise - amountPaid);
            }
            else if(input.equalsIgnoreCase("Fold"))
            {
                return Fold();
            }

            // Check if input is a valid integer bet
            try {
                int bet = Integer.parseInt(input);
                if (bet >= (raise - amountPaid)*2) {
                    if(bet >= stackSize) PrintAllIn(currentIndexBetting);
                    Call(raise - amountPaid);
                    return Bet(bet - raise);
                } else {
                    System.out.println(WHITE+"Bet must be a positive integer >= "+RESET+GREEN+raise*2+RESET+WHITE+"."+RESET);
                }
            } catch (NumberFormatException e) {
                System.out.println(RED+"Invalid input."+RESET+WHITE+"\nPlease enter 'Call', 'Fold', or Bet >= 2x "+RESET+GREEN+raise+RESET+WHITE+"."+RESET);
            }
        }
    }

    private int Check()
    {
        return 0;
    }

    private int Bet(int amount)
    {
        int bet = Math.min(amount, stackSize);
        withdraw(bet);
        return bet;
    }

    private int Fold()
    {
        return -1;
    }

    private int Call(int amount)
    {
        // TODO: make this not return zero to account for raising someone more than they are worth and they call
        int call = Math.min(amount, stackSize);
        withdraw(call);
        return 0;
    }

    // not needed
    private int Raise(int currentRaise)
    {
        int raise = Math.min(currentRaise*2, stackSize);
        withdraw(raise);
        return raise;
    }

    public void PrintStack(int i)
    {
        System.out.println(WHITE+"\nPlayer "+RESET+playerColors[(i)%playerColors.length]+(i+1)+ RESET+WHITE+" Stack: "+RESET+GREEN+stackSize+RESET);
    }




}
