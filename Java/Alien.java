package spacebattle.commander;
import spacebattle.Game;
import spacebattle.battlefield.*;
import spacebattle.ship.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import static spacebattle.battlefield.SpaceSector.sector;

public class Alien extends Commander {

    private final Random random = new Random();
    private ArrayList<Spaceship> alienFleet;          // stores the randomly created ships for the alien
    private Spaceship[][] alienSector;
    private int alienCoin;

    private Game game;

    public Alien(int sectorSize, int coins, Game game) {
        super(sectorSize, coins);
        this.game = game;

        createAlienSector(sectorSize);
        createAlienFleet();
        placingAlienShips();
    }


    private void createAlienSector(int sectorSize) {
        alienSector = new Spaceship[sectorSize][sectorSize];

        for (int i = 0; i < alienSector.length; i++) {
            for (int j = 0; j < alienSector[0].length; j++) {
                alienSector[i][j] = null;
            }
        }
    }


    private void createAlienFleet() {
        // hier kommt dein alter Code für die Erstellung der alienFleet hinein

        alienFleet = new ArrayList<>();
        int alienFleetSize = random.nextInt(9);
        alienFleetSize += 1;

        for (int i = 0; i < alienFleetSize; i++) {                          // with the most amount of coin and the cheapest ships, it is possible for the fleet to have the length nine
            int randomShip = random.nextInt(6);                      // creates a random number for the five types of ships
            if ((randomShip == 0)) {                                       // if the number is zero, the ship type is set to the first enum - a Corvette
                randomShip += 1;
            }

            if (randomShip == 1) {                                        // adds a new Corvette to the fleet at index i
                alienFleet.add(new Corvette());
                alienCoin -= 3;                                           // reduces the amount of coin by the price of the corvette
                System.out.println("Corvette :)");
                if (alienCoin == 0) {                                    // checks if the alien used all it's coin
                    placingAlienShips();                                 // if that's the case, the completed fleet will be placed in the sector by the next method
                }else if (alienCoin < 0){                                // checks if the purchase of the ship expands the budget of the alien
                    alienCoin += 3;                                      // if that's the case, the ship cannot be bought and the coin is increased by the price
                    //alienFleet.remove(i);                                // the ship is being removed from the fleet
                    placingAlienShips();                                 // the completed fleet will be placed in the sector
                }

            } else if (randomShip == 2) {
                alienFleet.add(new Battleship());
                alienCoin -= 4;
                System.out.println("Battleship :)");
                if (alienCoin == 0) {
                    placingAlienShips();
                }else if (alienCoin < 0){
                    alienCoin += 4;
                    placingAlienShips();
                }

            } else if (randomShip == 3) {
                alienFleet.add(new Carrier());
                alienCoin -= 5;
                System.out.println("Carrier :)");
                if (alienCoin == 0) {
                    placingAlienShips();
                }else if (alienCoin < 0){
                    alienCoin += 5;
                    placingAlienShips();
                }

            } else if (randomShip == 4) {
                alienFleet.add(new Cruiser());
                alienCoin -= 6;
                System.out.println("Cruiser :)");
                if (alienCoin == 0) {
                    placingAlienShips();
                }else if (alienCoin < 0){
                    alienCoin += 6;
                    placingAlienShips();
                }

            } else {
                alienFleet.add(new Submarine());
                alienCoin -= 4;
                System.out.println("Sub");
                if (alienCoin == 0) {
                    placingAlienShips();
                }else if (alienCoin < 0){
                    alienCoin += 4;
                    placingAlienShips();
                }

            }
        }
            placingAlienShips();
    }


private void placingAlienShips(){
        if (alienFleet.size() > 0) {
            int placeShip = random.nextInt(alienFleet.size());
            int alienRow = random.nextInt(alienSector.length);
            int alienCol = random.nextInt(alienSector.length);
            int rotating = random.nextInt(4);

            if (alienRow < alienSector.length && alienCol < alienSector.length) {           // random numbers for the anker have to be within the bounds of the sector
                Spaceship ship = alienFleet.get(placeShip);
                if (SpaceSector.placingShipPossible(ship, alienRow, alienCol)) {             // checks for the random ship, if the placing is possible at the position
                    int[][] getShape = alienFleet.get(placeShip).getShape();               // new int Array for the respective shape of the ship

                    for (int k = 0; k < getShape.length; k++) {
                        for (int l = 0; l < getShape[0].length; l++) {

                            if (alienRow + k >= 0 && alienRow + k < alienSector.length
                                    && alienCol + l >= 0 && alienCol + l < alienSector[0].length) {    // checks if the shape of the ship fits into the sector
                                alienSector[alienRow + k][alienCol + l] = ship;                        // places the ship
                            }
                            for (Spaceship spaceship : alienFleet) {               // iterates through the whole alienFleet array
                                for (int r = rotating; r > 0; r--) {               // rotates every ship of the array for a random amount of times
                                    spaceship.rotate();
                                }
                            }
                        }
                    }
                }
            }
            alienSector.toString();
        }else{
            System.out.println(" The fleet is empty. No ships to place.");

        }
    }

    public static int generateCoin(int sectorSize){              // generates the amount of money for the alien, based on the sector size
        int coins = 0;
        if (sectorSize == 10){
            coins = 25;
        }else if (sectorSize == 11){
            coins = 27;
        }else if (sectorSize == 12){
            coins = 29;
        }else if (sectorSize == 13){
            coins = 31;
        }else if (sectorSize == 14){
            coins = 33;
        }else if (sectorSize == 15){
            coins = 35;
        }
        System.out.println(coins);
        return coins;
    }

    public int [] getShotCoordinates() {
        ArrayList<Integer> row = new ArrayList<>();
        ArrayList<Integer> col = new ArrayList<>();
        int[] shotCoordinates = new int[2];                           // new array for the coordinates
        int shotRow = random.nextInt(sector.length + 1);        // random number for the row of the shot, within the bounds of the sector (but sector.length - 1 because the end bound is exclusive)
        int shotCol = random.nextInt(sector.length + 1);        // same for the column as for the row

        if (shotRow < game.getSize() && shotCol < game.getSize()) {                    // checks again if the numbers are within the sector bounds
            if (sector[shotRow][shotCol] != null) {                             // if the position in the sector is not null, there might be a ship
                if (checkAlienShots(shotRow, shotCol)) {                        // checks if the alien already shot a ship or nothing at this position
                    // if it did not:
                    updateCommanderSector(shotRow, shotCol, true);           // method to update the commander sector if the alien hit
                    shotCoordinates[0] = shotRow;                              // stores the coordinates of the shot in the given array
                    shotCoordinates[1] = shotCol;
                    System.out.println("The alien shot one of your ships at position: \n");  // informs the commander - the playing person - that one of his ships has been shot
                    System.out.println(Arrays.toString(shotCoordinates));                    // informs the commander where his ship has been shot
                    row.add(shotRow);
                    col.add(shotCol);
                    game.setAlienHit(true);
                } else {                                                         // if the alien already hit that position:
                    game.setAlienHit(true);                                       // starts the method again to create new shot coordinates
                }
            }                                                                  // if the alien shot nothing at this position:
            updateCommanderSector(shotRow, shotCol, false);                  // marks that the alien shot this spot already, even though the sector was empty
        } else {
            game.setAlienHit(true);}                    // starts the method again if the previous coordinates were not within the sector

            return shotCoordinates;
        }



    private boolean checkAlienShots(int row, int col){
        return sector[row][col].getType() != ShipType.WRECK;                // returns true if the ship was not a wreck and the alien had a successful shot
    }

    private void updateCommanderSector(int row, int col, boolean hit){
        if (hit){
            sector[row][col] = new Wreck();
        }else{
            sector[row][col] = /*.getShape()[row][col] = -1*/ new Wreck();
        }
    }

}
