package com.example.reindeergame;


import android.util.Log;
import java.util.Random;


public class Reindeer extends Animal {

	// Set and initialize class variables..
	private JHReindeerActivity myM;
	
	private int remainingDistance = 100;
	private int eatEnabled = 0;
	private int kickEnabled = 0;
	private int ammo = 100;
	

	// ..and constants
	// EAT_AMMO sets the amount of ammo gained by feeding at one feeding spot
	private final static int EAT_AMMO = 20;
	
	// ..and some more variables
	String name;
	int energy;
	int xPos;
	int throughs[];


	// Constructor gets the reference of ReindeerActivity class as a parameter
	public Reindeer(int[] through, int REINDEER_SPEED, JHReindeerActivity m) {
		throughs = through;
		this.myM = m;
	}


	// Method move that gets user's input as a parameter
	// Set the remaining distance at first.
	// Next, set energy to its right value and eating disabled.
	// In this method we also check if the reindeer is at some feeding spot. If yes, we want to show eat button by calling setEatButton after setting eating enabled.
	
	public int move(int value) { 
		setRemainingDistance(value);
		setAmmo(value);
		setEatDisabled();
		myM.setEatButton();


		int[] distanceToThrough = new int[4];
		for(int i = 0; i < throughs.length; i++){
			//Log.d("TEST THROUGH","" + throughs[i] );

			// Distance to through
			distanceToThrough[i] = remainingDistance - throughs[i];
			// If distance is 0 (reindeer is right at spot)
			if (distanceToThrough[i] == 0) {
				Log.d("EATING SPOT FOUND!, EATING ALLOWED", "through:" + throughs[i] + "distance" + distanceToThrough[i] );
				setEatEnabled();
				myM.setEatButton();
			} 
		}	

		return value;
	}

	
	// Returns location of the reindeer.

	public int getLocX() {
		return 100 - remainingDistance;
	}


	// Returns reindeer's distance to finish.

	public int getRemainingDistance() {
		return remainingDistance;
	}

	// Sets the distance of the reindeer. If remaining distance is zero, the reindeer is finished and we call won() method.
	
	public void setRemainingDistance(int d) {
		if((this.remainingDistance - d) > 0 ) {
			this.remainingDistance -= d;
		} else {
			this.remainingDistance = 0;
			myM.won();
		}
	}

	// Method for getting energy (ammo).

	public int getAmmo() {
		return ammo;
	}

	// Method for setting energy (ammo).
	// set a = 2, if speed is 1 or 2
	
	public void setAmmo(int a) {
		
		if(a < 2 && a > -100) { a = 2; } 

		// If the reindeer got caught by wolf (variable a has been set to -100)
		if(a == -100) { 
		Log.d("WOLF CAUGHT THE REINDEER","***" );
		// Call method wolfCaught
		myM.wolfCaught();
		} 
		// if the reindeer got caught by wolf (variable a has been set to -200)
		else if(a == -200) { 
		Log.d("BEAR CAUGHT THE REINDEER","***" ); 
		myM.bearCaught();
		}
		// else perform the move with formula given in sheets
		else {
			// let's check first if the reindeer has ammo left
			if((this.ammo - ((19/4 * a) - 30/4) > 0)) {
				// if ok, move the reindeer
				this.ammo -= ((19/4 * a) - 30/4);
			} 	
			else {
				// if the result will be 0 or less, reindeer has starved -> call method reindeerStarved()
				this.ammo = 0;
				myM.reindeerStarved();
			}
		}
	}


	// Special case: overloaded method setAmmo is called when the reindeer performed a kick.
	public void setAmmo() {
		ammo = ammo - 3;
	}

	// Getter for eatEnabled variable
	public int getEatEnabled() {
		return eatEnabled;
	}
	// Setter 1 for eatEnabled variable
	public void setEatEnabled() {
		eatEnabled = 1;

	}
	// Setter 2 for eatEnabled variable
	public void setEatDisabled() {
		eatEnabled = 0;
	}

	// Getter for kickEnabled variable
	public int getKickEnabled() {
		return kickEnabled;
	}
	
	// Setter 1 for kickEnabled variable
	public void setKickEnabled() {
		kickEnabled = 1;
	}
	// Setter 2 for kickEnabled variable
	public void setKickDisabled() {
		kickEnabled = 0;
	}
	

	// Reset() method: generate new feeding spots and set private variables to their initial states
	public void reset() { 
		// Generate new throughs for the reindeer
		myM.createThroughs();
		remainingDistance = 100;
		ammo = 100;
		eatEnabled = 0;
		kickEnabled = 0;
		
	}

	// Eat() method: if called, first adds an amount of constant EAT_AMMO to current ammo. 
	// After that calls reindeerEaten() method and sets eatDisabled and hides eat button
	public void eat() { 
		ammo = ammo + EAT_AMMO;
		myM.reindeerEaten();
		setEatDisabled();
		myM.setEatButton();
	}	


	// Add to the reindeer the capability to kick the beast: 
	// Kick is allowed, if beast is within 3 units from the reindeer. 
	// Kick should succeed with 30% probability. Successful kick makes the beast to miss one round. 

	public void kick() {
			// if getKickEnabled is 1, the wolf will miss next round, 
			// else if 0, the wolf keeps hunting
			if(getKickEnabled() > 0){
				myM.wolfMissRound();
			} else {
				// OBS! method setAmmo() is called when the reindeer performed a kick. Not in specs: it reduces ammo by 3 units (energy consumed with kicking)
				myM.wolfMissRound(0);
				
			}
	} 
	
	// tryKick() method first gets the distance of reindeer. Called before the kick() function!
	
	public boolean tryKick() {
		boolean result = false;
		// NOT IN SPECS: 
		
		
		xPos = getLocX();
		int wolfPos = myM.getWolfDistance();
		// if the absolute value of distance between the reindeer and the wolf is lesser than 3, we shall toss the dice
		// the probability for success is 30% so we draw a number between 1 and 100. If it is bigger or equal than 70, the kick will succeed
		// Otherwise, sets kickEnabled to 0.
		
		if(Math.abs(xPos - wolfPos) < 4 ) {	

			Random kickProb = new Random();
			int maxNum = 100;
			int minNum = 1;
			int range = maxNum - minNum + 1;
			int randomNum =  kickProb.nextInt(range) + minNum;

			if(randomNum < 71) {
				result = false;
				setKickDisabled();
			}
			
			else { 
				result = true;
				setKickEnabled();
				myM.setKickButton();
			}
			
		}
	
		return result;
	
	}


	// Public method status checks after every movement remaining distance and updates right values to UI by calling the methods of ReindeerActivity class.
	// After them by calling method tryKick() checks if it is possible to perform a kick at the end of current turn.
	public void status() { 
		if(getRemainingDistance() < 1){
			myM.won();
		} else {
			myM.writeDistance(getRemainingDistance());
			myM.writeAmmo(getAmmo());
			tryKick();
		}
		
	}


}