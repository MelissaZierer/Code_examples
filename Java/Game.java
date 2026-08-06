package spacebattle;

import spacebattle.battlefield.*;
import spacebattle.commander.*;
import spacebattle.ship.ShipType;


import java.util.Scanner;

import static spacebattle.battlefield.SpaceSector.getPosition;
import static spacebattle.battlefield.SpaceSector.sector;
import static spacebattle.commander.Commander.fleet;
import static spacebattle.ship.Spaceship.presentShips;

// Melissa Zierer; Matrikelnummer: 2344931; 28.01.2023

public class Game {
    public int size;
    private int coins;
    private Scanner scan = new Scanner(System.in);
    private boolean alienHit;
    private boolean continuing;

    public Game(){
        initializeGame();
        startGame();
    }


    private void initializeGame(){                                                                                            // constructor to structure the game
        System.out.println("\u001B[1m" + "Welcome to a new game of SpaceBattle!" +                            // all the sources for the following codes for color and squares are in the method description
                "\n Let's create your data first." + "\u001B[0m\"");

        Commander commander = new Commander(choseSpace(),budget());                                          // creates a new commander with the values the player chooses
        SpaceSector sectorPrint = new SpaceSector(sector.length);                                            // printed sector with the same size


        System.out.println("Now you can buy as many ships as you like as long as you still have money.");
        presentShips();                                                                                      // shows the player which ships he/she can buy

        System.out.println("Enter 1 for a BATTLESHIP \n 2 for a CARRIER \n 3 for a CRUISER " +
                "\n 4 for a CORVETTE \n and 5 for a SUBMARINE");
        commander.addShipToFleet(returnShipType());                                                        // method to start buying ships

        while (commander.continueBuying()){                                                               // continues as long as the player wants to buy more ships and still has coin
            System.out.println("Enter 1 for a BATTLESHIP \n 2 for a CARRIER \n 3 for a CRUISER " +
                    "\n 4 for a CORVETTE \n and 5 for a SUBMARINE");
            commander.addShipToFleet(returnShipType());                                                   // method to add the ships to the fleet of the commander
        }
        for (int i = 0; i < fleet.size(); i++){                                                          // should go through the whole fleet ship by ship

            System.out.println("You can rotate your ship!\nEnter a number between 0 and 3 for the amount of times you want to rotate please:");
            int rotate = scan.nextInt();
            for (int r = 0; r <= rotate; r++){
                fleet.get(i).rotating(fleet.get(i));
            }

            placingShips();
            sectorPrint.toString();
        }
        System.out.println("Those are the ships you did not place:");
        commander.getShipsToPlace();                                                   // shows the commander the ships not placed

        Alien alien = new Alien(sector.length, Alien.generateCoin(sector.length), this);     // generates a new Alien with the same size/coin as the commander
        startGame();
        commander.shoot();
        while(continuing) {                                                            // shooting continues until one of the opponents has lost
                if (commander.shoot()) {                                                                               // shoot returns 'true' when the commander hit something or tried a shot twice
                    if (alien.isDefeated()) {                                          // if the commander shot the last one of the alien ships
                        System.out.println("You won the game! Congratulations!");
                        continuing = false;
                        gameEnd(true);                                                   // end of the first game
                    } else {
                        EnemySector.enemySector.toString();                                  // if the alien is not defeated, it shows the player the situation in the enemySector after the shot
                        System.out.println("You could shoot near your last coordinates again...");
                        //commander.shoot();                                                   // commander can go again because he hit something, or tried the shot already
                        continuing = true;
                    }
                    alien.getShotCoordinates();
            }
            alien.getShotCoordinates();                                                 // if the commander hit nothing, it's the aliens turn to shoot
            if (alienHit) {                                                             // alienHit is a boolean; its value is 'true' if the alien shot a ship or tried the shot already
                if (commander.isDefeated()) {                                           // if the alien shot the last commander ship:
                    System.out.println("Sorry, the alien just shot your last ship! You lost the game.");
                    gameEnd(false);                                                // the game ends
                    continuing = false;
                }                                                                     // if the commander is not defeated:
                sector.toString();                                                    // shows the commander his own situation after the successful shot
                alien.getShotCoordinates();                                           // alien is allowed to shoot again
                continuing = true;
            }
       }
    }

    private int choseSpace(){                         // method to for the commander to select the sector size
        System.out.println("\u001B[32m" + "Enter a number between 10 and 15 for your field:" + "\u001B[0m");
        size = scan.nextInt();
        while (size < 10 || size > 15) {             // checks if the entered number is within the guideline
            System.out.println("\u001B[31m" + "The number hast to be bigger than ten and smaller than 15. Enter a new one:" + "\u001B[0m");
            size = scan.nextInt();
        }
        System.out.printf("The dimensions of your field are %dx%d\n", size, size);

        return size;
    }


    private int budget(){       // method to set the budget / amount of coins according to the size of the sector
        if (size == 10){
            coins = 25;
        }else if (size == 11){
            coins = 27;
        }else if (size == 12){
            coins = 29;
        }else if (size == 13){
            coins = 31;
        }else if (size == 14){
            coins = 33;
        }else if (size == 15){
            coins = 35;
        }
        System.out.println("Your budget is: " + coins);
        return coins;
    }

    public void placingShips(){     // method to pass the entered position of the ship
        int row;
        int col;
        for (spacebattle.ship.Spaceship spaceship : fleet) {        // goes through the whole fleet, and for every ship:
            System.out.println("This is your current ship: ");
            System.out.println("\n" + spaceship);                   // shows the ship to the commander

            System.out.println("Now please enter the row you want to place it: ");     // commander/placer enters the position
            row = scan.nextInt();
            System.out.println("And now enter the colum: ");
            col = scan.nextInt();
            if (row < sector.length && col < sector.length) {
                SpaceSector.placeShip(spaceship, row, col);       // method to place the current ship
                getPosition(spaceship);                           // returns the position of the ship if the placement was possible
                                                                  // (should return the position of the passed ship)
            } else  {
                System.out.println("\u001B[31m" + "The numbers were not within the range of the sector! Go again please:" + "\u001B[0m");
                System.out.println("Now please enter the row you want to place it: ");     // commander/player enters the position
                row = scan.nextInt();
                System.out.println("And now enter the colum: ");
                col = scan.nextInt();
            }
            SpaceSector.placeShip(spaceship, row, col);       // method to place the current ship
            getPosition(spaceship);                           // returns the position of the ship if the placement was possible
        }
    }

    private ShipType returnShipType() {             // method returns the shipType according to the number the commander enters
        int type = scan.nextInt();
        /*if (!scan.hasNextInt()){                    // checks if the entered value is an integer
            wrongType();                            // if not: following method
        }*/
            if (type == 1) {                        // if it was an integer: if query to return the according ship type
                return ShipType.BATTLESHIP;
            } else if (type == 2) {
                return ShipType.CARRIER;
            } else if (type == 3) {
                return ShipType.CRUISER;
            } else if (type == 4) {
                return ShipType.CORVETTE;
            } else
                return ShipType.SUBMARINE;
    }

    private void wrongType(){          // method to inform the user to enter an integer
        System.out.println("\u001B[31m" + "You have to enter the asked value please!" + "\u001B[0m");
        returnShipType();             // calls the previous method again
    }


    private void gameEnd(boolean win){              // method to end the game
        if (win){
            System.out.println("The alien stood no chance!\nDo you want to play again?");
        }else{
            System.out.println("Sorry that was it...but you can have another chance!");
        }
    }


    private boolean startGame(){
        continuing = true;
        return continuing;
    }

    public int getSize() {
        return size;
    }

    public void setAlienHit(boolean alienHit) {
        this.alienHit = alienHit;
    }

    public boolean isAlienHit() {
        return alienHit;
    }

    public static void main(String[] args) {
        Game game = new Game();
    }
}
