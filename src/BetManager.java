import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class BetManager
{

    public static final String RESET = "\u001B[0m";
    public static final String GREEN = "\033[1;92m";
    public static final String YELLOW = "\033[1;93m";
    public static final String RED = "\033[1;91m";
    public static final String WHITE = "\033[1;97m";    // WHITE
    public static final String MAGENTA = "\033[1;95m";
    public static final String BLUE = "\033[1;94m";
    public static final String MAGENTA_BACK = "\033[0;105m";
    String[] playerColors = new String[]{BLUE, GREEN, MAGENTA, YELLOW};

    ArrayList<Stack> stacks;
    private int defaultStackSize = 1000;
    private int dealerIndex = 0;
    private int pot;
    private final int bigBlind = 20;
    private final int smallBlind = 10;
    private ArrayList<Integer> folds;
    private ArrayList<Integer> losers;

    public BetManager(int defaultStackSize, int numPlayers)
    {
        this.stacks = new ArrayList<>();
        this.defaultStackSize = defaultStackSize;
        this.pot = 0;
        this.folds = new ArrayList<>();
        this.losers = new ArrayList<>();
        InitializeStacks(defaultStackSize, numPlayers);
    }

    public BetManager(int numPlayers)
    {
        this.stacks = new ArrayList<>();
        this.pot = 0;
        this.folds = new ArrayList<>();
        this.losers = new ArrayList<>();
        InitializeStacks(this.defaultStackSize, numPlayers);
    }

    public void SetBots(ArrayList<Integer> botI)
    {
        for(int i : botI)
        {
            stacks.get(i).setBot();
        }
    }

    public void InformBots(ArrayList<Integer> botI, ArrayList<Double> equity)
    {
        for(int i = 0; i < botI.size(); i++)
        {
            if(!(botI.get(i) > (stacks.size() - 1)) && equity.size() == stacks.size())
            {
                stacks.get(botI.get(i)).InformBot(equity.get(i));
            }
        }
    }

    private void InitializeStacks(int defaultStackSize, int numPlayers)
    {
        for(int i = 0; i < numPlayers; i++)
        {
            Stack next = new Stack(defaultStackSize);
            next.setNumPlayers(numPlayers);
            stacks.add(next);
        }
    }

    public void PreFlopBettingRound()
    {
        ResetStacksAmountPaid();
        pot += bigBlind;
        pot += smallBlind;
        boolean activeBet = false;
        if(stacks.size() == 2)
        {
            stacks.get((dealerIndex) % stacks.size()).withdraw(smallBlind);
            stacks.get((dealerIndex + 1) % stacks.size()).withdraw(bigBlind);
        }
        else {
            stacks.get((dealerIndex + 1) % stacks.size()).withdraw(smallBlind);
            stacks.get((dealerIndex + 2) % stacks.size()).withdraw(bigBlind);
        }

        for(int i = 0; i < stacks.size(); i++)
        {
            if(stacks.size() - folds.size() == 1) return;
            int currentIndexBetting = ((dealerIndex+stacks.size()-1) + i) % stacks.size(); // Under the gun goes first pre-flop (right of dealer)
            stacks.get(currentIndexBetting).setCurrentPot(pot);
            if(!activeBet && !folds.contains(currentIndexBetting) && !losers.contains(currentIndexBetting))
            {
                int result = stacks.get(currentIndexBetting).WhatWillItBe(currentIndexBetting);
                if (result < 0) {
                    System.out.println(WHITE+"\nPlayer "+RESET+playerColors[(currentIndexBetting)%playerColors.length]+(currentIndexBetting+1)+ RESET+WHITE+" : "+RESET+RED+"Fold..."+RESET);
                    folds.add(currentIndexBetting);
                    ShowPot();
                } else if (result == 0) {
                    System.out.println(WHITE+"\nPlayer "+RESET+playerColors[(currentIndexBetting)%playerColors.length]+(currentIndexBetting+1)+ RESET+WHITE+" : "+RESET+YELLOW+"Check."+RESET);
                    // TODO: need better logic for detecting who is big/small blind and how much they owe on a check/raise pre-flop
                    if(currentIndexBetting == dealerIndex)
                    {
                        if(stacks.size() - losers.size() == 2)
                        {
                            stacks.get(dealerIndex).withdraw(smallBlind);
                            pot += smallBlind;
                            stacks.get(currentIndexBetting).setCurrentPot(pot);

                        }
                        else if(stacks.size() - losers.size() > 2)
                        {
                            stacks.get(dealerIndex).withdraw(bigBlind);
                            pot += bigBlind;
                            stacks.get((dealerIndex+1)%stacks.size()).withdraw(smallBlind);
                            pot += smallBlind;
                            stacks.get(currentIndexBetting).setCurrentPot(pot);

                        }
                    }
                    ShowPot();
                } else {
                    System.out.println(WHITE+"\nPlayer "+RESET+playerColors[(currentIndexBetting)%playerColors.length]+(currentIndexBetting+1)+ RESET+WHITE+" : "+RESET+WHITE+"Bet "+RESET+GREEN+result+RESET+WHITE+"!"+RESET);
                    pot += result;
                    ShowPot();
                    stacks.get(currentIndexBetting).setAmountPaid(result);
                    stacks.get(currentIndexBetting).setCurrentPot(pot);
                    ThereWasARaise(currentIndexBetting, result);
                    activeBet = true;
                }
            }
            stacks.get(currentIndexBetting).setCurrentPot(pot);
        }
    }

    public void ThereWasARaise(int raiseIndex, int raise)
    {
        for(int i = 0; i < stacks.size(); i++)
        {
            if(stacks.size() - folds.size() == 1) return;
            int currentIndexBetting = (raiseIndex + i + 1) % stacks.size();
            stacks.get(currentIndexBetting).setCurrentPot(pot);
            if(!folds.contains(currentIndexBetting) && currentIndexBetting != raiseIndex && !losers.contains(currentIndexBetting) && !isPlayerAllIn(currentIndexBetting))
            {
                int result = stacks.get(currentIndexBetting).SomeoneRaised(raise, raiseIndex, currentIndexBetting);
                if (result < 0) {
                    System.out.println(WHITE+"\nPlayer "+RESET+playerColors[(currentIndexBetting)%playerColors.length]+(currentIndexBetting+1)+ RESET+WHITE+" : "+RESET+RED+"Fold..."+RESET);
                    folds.add(currentIndexBetting);
                    ShowPot();
                }  else if (result > 0){
                    System.out.println(WHITE+"\nPlayer "+RESET+playerColors[(currentIndexBetting)%playerColors.length]+(currentIndexBetting+1)+ RESET+WHITE+" : "+RESET+BLUE+"Raised "+RESET+GREEN+result+RESET+WHITE+"!"+RESET);
                    pot += raise - stacks.get(currentIndexBetting).getAmountPaid();
                    pot += result;
                    int totalBet = raise + result;
                    stacks.get(currentIndexBetting).setAmountPaid(totalBet);
                    stacks.get(currentIndexBetting).setCurrentPot(pot);
                    ShowPot();
                    ThereWasARaise(currentIndexBetting, totalBet);
                    return;
                } else {
                    System.out.println(WHITE+"\nPlayer "+RESET+playerColors[(currentIndexBetting)%playerColors.length]+(currentIndexBetting+1)+ RESET+WHITE+" : "+RESET+YELLOW+"Call."+RESET);
                    int amountOwed = raise - stacks.get(currentIndexBetting).getAmountPaid();
                    pot += amountOwed;
                    ShowPot();
                    stacks.get(currentIndexBetting).setAmountPaid(amountOwed);
                    stacks.get(currentIndexBetting).setCurrentPot(pot);

                }
            }
            stacks.get(currentIndexBetting).setCurrentPot(pot);
        }
    }

    private boolean isPlayerAllIn(int i)
    {
        return !folds.contains(i) && !losers.contains(i) && stacks.get(i).getStackSize() == 0;
    }

    // TODO: make function to check whether all players who arent folded are all in.
    //  if only one player not folded return true also ****

    private boolean areAllPlayersAllInOrFolded()
    {
        if(stacks.size() - folds.size() == 1) return true;
        int numPlayersAllIn = 0;
        for(int i = 0; i < stacks.size(); i++)
        {
            if(!folds.contains(i) && stacks.get(i).getStackSize() == 0)
            {
                numPlayersAllIn += 1;
            }
        }
        return stacks.size() - (folds.size() + numPlayersAllIn) <= 1;
    }

    private void ResetStacksAmountPaid()
    {
        for(Stack stack : stacks)
        {
            stack.setAmountPaid(0);
        }
    }

    public void PostFlopBettingRound()
    {
        ResetStacksAmountPaid();
        boolean activeBet = false;
        if(areAllPlayersAllInOrFolded()) return;

        for(int i = 0; i < stacks.size(); i++)
        {
            int currentIndexBetting = (dealerIndex + i + 1) % stacks.size();
            stacks.get(currentIndexBetting).setCurrentPot(pot);
            if(!activeBet && !folds.contains(currentIndexBetting) && !losers.contains(currentIndexBetting) && !isPlayerAllIn(currentIndexBetting))
            {
//                stacks.get(currentIndexBetting).PrintStack(currentIndexBetting);
                int result = stacks.get(currentIndexBetting).WhatWillItBe(currentIndexBetting);
                if (result < 0) {
                    System.out.println(WHITE+"\nPlayer "+RESET+playerColors[(currentIndexBetting)%playerColors.length]+(currentIndexBetting+1)+ RESET+WHITE+" : "+RESET+RED+"Fold..."+RESET);
                    folds.add(currentIndexBetting);
                    ShowPot();
                } else if (result == 0) {
                    System.out.println(WHITE+"\nPlayer "+RESET+playerColors[(currentIndexBetting)%playerColors.length]+(currentIndexBetting+1)+ RESET+WHITE+" : "+RESET+YELLOW+"Check."+RESET);
                    ShowPot();
                } else {
                    System.out.println(WHITE+"\nPlayer "+RESET+playerColors[(currentIndexBetting)%playerColors.length]+(currentIndexBetting+1)+ RESET+WHITE+" : "+RESET+WHITE+"Bet "+RESET+GREEN+result+RESET+WHITE+"!"+RESET);
                    pot += result;
                    stacks.get(currentIndexBetting).setAmountPaid(result);
                    stacks.get(currentIndexBetting).setCurrentPot(pot);
                    ShowPot();
                    ThereWasARaise(currentIndexBetting, result);
                    activeBet = true;
                }
            }
            stacks.get(currentIndexBetting).setCurrentPot(pot);
        }
    }


    public void RewardWinner(int i)
    {
        System.out.println(WHITE+"\nPlayer "+RESET+playerColors[(i)%playerColors.length]+(i+1)+ RESET+WHITE+" : "+RESET+GREEN+"Wins "+pot+RESET+WHITE+"!"+RESET);
        stacks.get(i).deposit(pot);
        pot = 0;
        dealerIndex++;
        dealerIndex %= stacks.size();
        folds.clear();
        CheckLosers();
        PrintWinner(i);
    }

    public void SplitWinners(ArrayList<Integer> winners)
    {
        // check to see if any of these hands folded and split between the remaining hands
        int split = pot / winners.size();
        for(int i : winners)
        {
            System.out.println(WHITE+"\nPlayer "+RESET+playerColors[(i)%playerColors.length]+(i+1)+ RESET+WHITE+" : "+RESET+YELLOW+"Splits, won "+split+RESET+WHITE+"!"+RESET);
            stacks.get(i).deposit(split);
        }
        pot = 0;
        dealerIndex++;
        folds.clear();
        CheckLosers();
    }

    public void ShowPot()
    {
        System.out.println(WHITE+"\nCurrent Pot: "+RESET+GREEN+pot+RESET);
    }

    private void CheckLosers()
    {
        for(int i = 0; i < stacks.size(); i++)
        {
            if(!losers.contains(i) && stacks.get(i).getStackSize() == 0)
            {
                losers.add(i);
            }
        }
    }

    public int getNumLosers()
    {
        return losers.size();
    }

    public void PrintWinner(int i)
    {
        if(stacks.size() - losers.size() == 1)
        {
            System.out.println(WHITE+"\nPlayer "+RESET+playerColors[(i)%playerColors.length]+(i+1)+ " : "+RESET+GREEN+"Winner"+RESET+WHITE+"!"+RESET);
        }
    }

    public ArrayList<Integer> getFolds() {
        return folds;
    }

    public ArrayList<Integer> getLosers() {
        return losers;
    }

    public void AutoFoldLoser(int i)
    {
        folds.add(i);
    }
}
