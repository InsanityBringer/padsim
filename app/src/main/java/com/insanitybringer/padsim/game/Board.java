package com.insanitybringer.padsim.game;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Random;

public class Board
{
    public final float BoardAspectRatio = 5.0f / 6.0f;
    private Random random = new Random();

    public Orb[][] getOrbs()
    {
        return orbs;
    }

    private Orb[][] orbs = new Orb[7][6];
    private int boardSize;

    //Needed to do swapping
    private int currentX;
    private int currentY;

    public Orb getHeldOrb()
    {
        return heldOrb;
    }

    private Orb heldOrb;

    private BoardState boardState = BoardState.Ready;
    private boolean swapped = false;
    public boolean isActive = true;

    private ArrayDeque<Match> matchQueue = new ArrayDeque<>();

    //a bit of a icky thing, hold a reference to a fading orb for timing
    private Orb activeOrb;
    //This is more icky, timer for skyfall
    private int skyfallTimer = 0;

    private int moveTimer = 0;

    private GameState gameState;

    public Board(int size, GameState state)
    {
        boardSize = size;
        gameState = state;
        for (int x = 0; x < 7; x++)
        {
            for (int y = 0; y < 6; y++)
            {
                orbs[x][y] = new Orb(x, y, random.nextInt(6), boardSize);
            }
        }
        moveTimer = getMoveTimeTicks();
        //debGenerateBoard();
        //boardFromString("HLHHHLHDLLLDHLDDDHHLDDDHHLDDDH");
    }

    private void debGenerateBoard()
    {
        //int[] orbtypes = {0, 0, 0, 1, 1, 1, 2, 2, 2, 3, 3, 3, 4, 4, 4, 5, 5, 5, 6, 6, 6, 7, 7, 7, 8, 8, 8, 9, 9, 9};
        int[] orbtypes = {0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 5, 5, 5, 5, 5, 5, 6, 6, 6, 6, 6, 6};
        int len = 30;
        int orb; int pos;
        for (int x = 0; x < 6; x++)
        {
            for (int y = 0; y < 5; y++)
            {
                pos = random.nextInt(len);
                orb = orbtypes[pos];
                len--;
                orbtypes[pos] = orbtypes[len];
                orbs[x][y] = new Orb(x, y, orb, boardSize);
            }
        }
    }

    private void boardFromString(String orbstr)
    {
        if (orbstr.length() >= 30)
        for (int x = 0; x < 6; x++)
        {
            for (int y = 0; y < 5; y++)
            {
                int orb = attrFromChar(orbstr.charAt((4-y) * 6 + x));
                orbs[x][y] = new Orb(x, y, orb, boardSize);
            }
        }
    }

    private int attrFromChar(char c)
    {
        switch (c)
        {
            case 'R':
                return 0;
            case 'G':
                return 2;
            case 'B':
                return 1;
            case 'L':
                return 3;
            case 'D':
                return 4;
            case 'H':
                return 5;
            case 'J':
                return 6;
            case 'P':
                return 7;
            case 'M':
                return 8;
            case 'X':
                return 9;
            default:
                return 6;
        }
    }

    public void update()
    {
        //orbs[0, 0].Attribute = random.Next(6);
        if (boardState == BoardState.Moving)
        {
            moveTimer--;
            //Console.WriteLine("{0}", moveTimer);
            if (moveTimer == 0)
            {
                heldOrb.clearFade();
                releaseOrb();
            }
        }
        else if (boardState == BoardState.Matching)
        {
            if (activeOrb != null && activeOrb.getAnimationState() != OrbAnimationState.Idle)
            {
            }
            else if (matchQueue.size() > 0)
            {
                Match matchInfo = matchQueue.removeFirst();
                activeOrb = orbs[matchInfo.orbs.get(0).x][matchInfo.orbs.get(0).y];
                gameState.dispatchComboEffect(matchInfo);

                //Fade all the orbs in the match
                /*foreach (OrbPosition pos in matchInfo.Orbs)
                {
                    Orbs[pos.x, pos.y].Match(pos.x, pos.y);
                }*/
                //System.out.printf("Match has %d orbs\n", matchInfo.orbs.size());
                OrbPosition pos;
                for (int i = 0; i < matchInfo.orbs.size(); i++)
                {
                    pos = matchInfo.orbs.get(i);
                    //System.out.printf("%d %d\n", pos.x, pos.y);
                    orbs[pos.x][pos.y].match(pos.x, pos.y);
                }
            }
            else //if (matchQueue.size() == 0)
            {
                boardState = BoardState.Skyfall;
                //Console.WriteLine("Skyfall starting");
                startSkyfall();
                skyfallTimer = 12;
            }
        }
        else if (boardState == BoardState.Skyfall)
        {
            skyfallTimer--;
            if (skyfallTimer == 0)
            {
                clearMatchData();
                int combos = resolveMatches();
                if (combos > 0)
                {
                    boardState = BoardState.Matching;
                }
                else
                {
                    clearMatchData();
                    //FadeOrbs();
                    boardState = BoardState.Ready;
                    gameState.endCombo();
                    skyfallTimer = 30;
                    //isActive = true;
                    moveTimer = getMoveTimeTicks();
                }
            }
        }
        for (int x = 0; x < 6; x++)
        {
            for (int y = 0; y < 5; y++)
            {
                if (orbs[x][y] != null)
                {
                    orbs[x][y].update();
                }
            }
        }
    }

    public void fadeOrbs()
    {
        for (int x = 0; x < 6; x++)
        {
            for (int y = 0; y < 5; y++)
            {
                orbs[x][y].fade();
            }
        }
    }

    public void unfadeOrbs()
    {
        for (int x = 0; x < 6; x++)
        {
            for (int y = 0; y < 5; y++)
            {
                orbs[x][y].unfade();
            }
        }
    }

    public void clearMatchData()
    {
        for (int x = 0; x < 6; x++)
        {
            for (int y = 0; y < 5; y++)
            {
                orbs[x][y].setAdded(false); orbs[x][y].setMarked(false);
            }
        }
    }

    public int getMoveTimeTicks()
    {
        return 30 * 100;
    }

    public void startSkyfall()
    {
        int[] columnHeights = new int[7];
        for (int x = 0; x < 6; x++)
        {
            for (int y = 0; y < 5; y++)
            {
                if (orbs[x][y].isAdded())
                {
                    //Delete all orbs that have been matched away
                    orbs[x][y] = null;
                }
            }
        }

        //Second cycle is to drop all orbs to their lowest point.
        int curY;
        int bestY;
        Orb curOrb;
        for (int x = 0; x < 6; x++)
        {
            for (int y = 0; y < 5; y++)
            {
                if (orbs[x][y] != null)
                {
                    curY = y;
                    bestY = y;
                    curOrb = orbs[x][y];
                    while (curY > 0)
                    {
                        curY--;
                        if (orbs[x][curY] == null)
                        {
                            if (curY < bestY)
                            {
                                bestY = curY;
                            }
                        }
                    }
                    if (bestY != y)
                    {
                        //Console.WriteLine("found destination {0} {1}", x, bestY);
                        orbs[x][y] = null;
                        orbs[x][bestY] = curOrb;
                        curOrb.fallTo(x, bestY, x, y, boardSize);
                    }
                }
            }
        }
        //Third cycle is to replenish orbs
        for (int x = 0; x < 6; x++)
        {
            for (int y = 0; y < 5; y++)
            {
                if (orbs[x][y] == null)
                {
                    orbs[x][y] = new Orb(x, y, random.nextInt(6), boardSize);
                    orbs[x][y].fallTo(x, y, x, 6 + columnHeights[x], boardSize);
                    columnHeights[x]++;
                }
            }
        }
    }

    public int sign(int input)
    {
        if (input < 0) return -1;
        if (input > 0) return 1;
        return 0;
    }

    public void swap(int destX, int destY)
    {
        Orb swapTemp;
        int diffx = destX - currentX;
        int diffy = destY - currentY;
        if (destX == currentX && destY != currentY)
        {
            if (destY > currentY) //moving orbs up
            {
                swapTemp = orbs[currentX][currentY];
                for (int i = currentY; i < destY; i++)
                {
                    orbs[currentX][i] = orbs[currentX][i + 1];
                    orbs[currentX][i].swapTo(currentX, i, boardSize, OrbAnimationState.SwapVertical);
                }
                orbs[destX][destY] = swapTemp;
                heldOrb.swapTo(destX, destY, boardSize, OrbAnimationState.SwapVertical);

            }
            else //moving orbs down
            {
                swapTemp = orbs[currentX][currentY];
                for (int i = currentY; i > destY; i--)
                {
                    orbs[currentX][i] = orbs[currentX][i - 1];
                    orbs[currentX][i].swapTo(currentX, i, boardSize, OrbAnimationState.SwapVertical);
                }
                orbs[destX][destY] = swapTemp;
                heldOrb.swapTo(destX, destY, boardSize, OrbAnimationState.SwapVertical);
            }

            currentX = destX;
            currentY = destY;
            swapped = true;
        }
        else if (destY == currentY && destX != currentX)
        {
            if (destX > currentX) //moving orbs right
            {
                swapTemp = orbs[currentX][currentY];
                for (int i = currentX; i < destX; i++)
                {
                    orbs[i][currentY] = orbs[i + 1][currentY];
                    orbs[i][currentY].swapTo(i, currentY, boardSize, OrbAnimationState.SwapHorizontal);
                }
                orbs[destX][destY] = swapTemp;
                heldOrb.swapTo(destX, destY, boardSize, OrbAnimationState.SwapHorizontal);
            }
            else //moving orbs left
            {
                swapTemp = orbs[currentX][currentY];
                for (int i = currentX; i > destX; i--)
                {
                    orbs[i][currentY] = orbs[i - 1][currentY];
                    orbs[i][currentY].swapTo(i, currentY, boardSize, OrbAnimationState.SwapHorizontal);
                }
                orbs[destX][destY] = swapTemp;
                heldOrb.swapTo(destX, destY, boardSize, OrbAnimationState.SwapVertical);
            }

            currentX = destX;
            currentY = destY;
            swapped = true;
        }
        else if (diffx != 0 && Math.abs(diffx) == Math.abs(diffy))
        {
            //Console.WriteLine("it would appear a diagonal was attempted");
            int steps = Math.abs(diffx);
            int slopeX = sign(diffx);
            int slopeY = sign(diffy);

            int posX = currentX;
            int posY = currentY;
            swapTemp = orbs[currentX][currentY];

            for (int i = 0; i < steps; i++)
            {
                orbs[posX][posY] = orbs[posX + slopeX][posY + slopeY];
                orbs[posX][posY].swapTo(posX, posY, boardSize, OrbAnimationState.SwapHorizontal);
                posX += slopeX;
                posY += slopeY;
            }
            orbs[destX][destY] = swapTemp;

            currentX = destX;
            currentY = destY;
            heldOrb.offsetTo(destX, destY, boardSize);
            swapped = true;
        }
    }

    public boolean touch(float touchX, float touchY)
    {
        if (!isActive) return false;
        //Console.WriteLine("{0}, {1}", touchPoint.X, touchPoint.Y);

        int cellX = (int)(touchX / 105.0f);
        int cellY = 4 - (int)(touchY / 105.0f);

        cellX = Math.max(0, Math.min(cellX, 5));
        cellY = Math.max(0, Math.min(cellY, 4));

        if (heldOrb == null)
        {
            //Orbs[cellX, cellY].Alpha = 0.25f;
            heldOrb = orbs[cellX][cellY];
            heldOrb.quickFade();
            currentX = cellX;
            currentY = cellY;
        }
        else
        {
            swap(cellX, cellY);
            if (swapped)
            {
                if (boardState != BoardState.Moving)
                {
                    gameState.addLock();
                    boardState = BoardState.Moving;
                    gameState.turnFlags |= GameState.GameFlags.GF_Swapped;
                }
            }
        }

        //Console.WriteLine("{0}, {1}", cellX, cellY);

        return true;
    }

    public void touchUp()
    {
        if (!isActive) return;
        if (swapped)
        {
            heldOrb.clearFade();
        }
        releaseOrb();
    }

    public void releaseOrb()
    {
        if (heldOrb != null)
        {
            heldOrb.quickUnfade();
            heldOrb.offsetTo(currentX, currentY, boardSize);
            if (swapped)
            {
                gameState.freeLock();
                isActive = false;
                int numCombos = resolveMatches();
                if (numCombos > 0)
                {
                    boardState = BoardState.Matching;
                    gameState.turnFlags |= GameState.GameFlags.GF_Matched;
                    gameState.startMatches();
                }
                swapped = false;
            }
            heldOrb = null;
        }
    }

    public int resolveMatches()
    {
        int combos = 0;
        for (int y = 0; y < 5; y++)
        {
            for (int x = 0; x < 6; x++)
            {
                if (resolveMatch(x, y, orbs[x][y].getAttribute()))
                    combos++;
            }
        }
        //Console.WriteLine("{0} Combos, match queue is {1} long", combos, matchQueue.Count);
        //System.out.printf("%d combos, match queue is %d long\n", combos, matchQueue.size());
        return combos;
    }

    public boolean resolveMatch(int x, int y, int attribute)
    {
        int results = 0;
        int[] resultsHack = new int[1];
        ArrayList<OrbPosition> leftList = new ArrayList<OrbPosition>();
        ArrayList<OrbPosition> bottomList = new ArrayList<OrbPosition>();
        orbFloodFill(x, y, attribute, resultsHack, leftList, bottomList);
        results = resultsHack[0];
        //Console.WriteLine("filled {0}, {1} leftmost orbs, {2} bottommost orbs", results, leftList.Count, bottomList.Count);
        //System.out.printf("Filled %d, %d leftmost orbs, %d bottommost orbs\n", results, leftList.size(), bottomList.size());

        int orbsMatched = 0;
        Match matchInfo = new Match(attribute);
        if (results >= 3) //can we even possibly have a match here?
        {
            OrbPosition position;
            //foreach (OrbPosition position in leftList)
            for (int i = 0; i < leftList.size(); i++)
            {
                position = leftList.get(i);
                orbsMatched += ScanOrbsLeft(position, attribute, matchInfo);
            }
            for (int i = 0; i < bottomList.size(); i++)
            {
                position = bottomList.get(i);
                orbsMatched += ScanOrbsUp(position, attribute, matchInfo);
            }
        }
        if (orbsMatched > 0)
        {
            matchInfo.centerX = (matchInfo.minX + matchInfo.maxX) / 2.0f;
            matchInfo.centerY = (matchInfo.minY + matchInfo.maxY) / 2.0f;
            //matchQueue.Enqueue(matchInfo);
            matchQueue.add(matchInfo);
           // gameState.AddMatch(matchInfo);
            return true;
        }
        return false;
    }

    public int ScanOrbsLeft(OrbPosition leftPos, int attribute, Match matchInfo)
    {
        int success = 0;
        if (leftPos.x >= (6 - 2)) //Will we run out of board space before we get a match of 3?
        {
            return 0;
        }
        if (orbs[leftPos.x + 2][leftPos.y].getAttribute() != attribute) //Before attempting to resolve, see if there's even an orb 2 spaces to the right
        {
            return 0;
        }
        OrbPosition currentPos = new OrbPosition(leftPos.x, leftPos.y);
        Orb currentOrb = orbs[leftPos.x][leftPos.y];
        while (currentOrb != null && currentOrb.getAttribute() == attribute)
        {
            success++;
            currentPos.x += 1;
            currentOrb = getOrbAt(currentPos);
        }

        if (success >= 3)
        {
            currentPos = new OrbPosition(leftPos.x, leftPos.y);
            currentOrb = orbs[leftPos.x][leftPos.y];
            while (currentOrb != null && currentOrb.getAttribute() == attribute)
            {
                if (!currentOrb.isAdded())
                {
                    OrbPosition copy = new OrbPosition(currentPos.x, currentPos.y);
                    matchInfo.minX = Math.min(matchInfo.minX, currentOrb.getPosX());
                    matchInfo.minY = Math.min(matchInfo.minY, currentOrb.getPosY());
                    matchInfo.maxX = Math.max(matchInfo.maxX, currentOrb.getPosX());
                    matchInfo.maxY = Math.max(matchInfo.maxY, currentOrb.getPosY());
                    matchInfo.orbs.add(copy);
                    currentOrb.setAdded(true);
                }
                currentPos.x += 1;
                currentOrb = getOrbAt(currentPos);
            }
            return success;
        }
        return 0;
    }

    public int ScanOrbsUp(OrbPosition bottomPos, int attribute, Match matchInfo)
    {
        int success = 0;
        if (bottomPos.y >= (5 - 2)) //Will we run out of board space before we get a match of 3?
        {
            return 0;
        }
        if (orbs[bottomPos.x][bottomPos.y + 2].getAttribute() != attribute) //Before attempting to resolve, see if there's even an orb 2 spaces above
        {
            return 0;
        }
        OrbPosition currentPos = new OrbPosition(bottomPos.x, bottomPos.y);
        Orb currentOrb = orbs[bottomPos.x][bottomPos.y];
        while (currentOrb != null && currentOrb.getAttribute() == attribute)
        {
            success++;
            currentPos.y += 1;
            currentOrb = getOrbAt(currentPos);
        }

        if (success >= 3)
        {
            currentPos = new OrbPosition(bottomPos.x, bottomPos.y);
            currentOrb = orbs[bottomPos.x][bottomPos.y];
            while (currentOrb != null && currentOrb.getAttribute() == attribute)
            {
                if (!currentOrb.isAdded())
                {
                    OrbPosition copy = new OrbPosition(currentPos.x, currentPos.y);
                    matchInfo.minX = Math.min(matchInfo.minX, currentOrb.getPosX());
                    matchInfo.minY = Math.min(matchInfo.minY, currentOrb.getPosY());
                    matchInfo.maxX = Math.max(matchInfo.maxX, currentOrb.getPosX());
                    matchInfo.maxY = Math.max(matchInfo.maxY, currentOrb.getPosY());
                    matchInfo.orbs.add(copy);
                    currentOrb.setAdded(true);
                }
                currentPos.y += 1;
                currentOrb = getOrbAt(currentPos);
            }
            return success;
        }
        return 0;
    }


    public Orb getOrbAt(OrbPosition pos)
    {
        if (pos.x >= 0 && pos.y >= 0 && pos.x < 6 && pos.y < 5)
        {
            return orbs[pos.x][pos.y];
        }
        else
        {
            return null;
        }
    }

    private boolean orbFloodFill(int x, int y, int attribute, int[] filled, ArrayList<OrbPosition> leftList, ArrayList<OrbPosition> bottomList)
    {
        if (x >= 0 && y >= 0 && x < 6 && y < 5)
        {
            if (orbs[x][y].getAttribute() != attribute)
            {
                return false;
            }
            else if (orbs[x][y].isMarked())
            {
                //Note it down anyways for bookkeeping
                return true;
            }
            else
            {
                filled[0]++;
                orbs[x][y].setMarked(true);

                orbFloodFill(x + 1, y, attribute, filled, leftList, bottomList);
                boolean orbToLeft = orbFloodFill(x - 1, y, attribute, filled, leftList, bottomList);
                orbFloodFill(x, y + 1, attribute, filled, leftList, bottomList);
                boolean orbBelow = orbFloodFill(x, y - 1, attribute, filled, leftList, bottomList);

                if (!orbToLeft)
                {
                    leftList.add(new OrbPosition(x, y));
                }

                if (!orbBelow)
                {
                    bottomList.add(new OrbPosition(x, y));
                }
            }
        }
        else
        {
            return false;
        }
        return true;
    }
}
