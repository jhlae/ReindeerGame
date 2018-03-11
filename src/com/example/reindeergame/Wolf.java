package com.example.reindeergame;

import java.util.Random;

import android.util.Log;

public class Wolf extends Animal implements Beast {

	// Set and initialize class variables

	private Reindeer myReindeer; 
	private long locSeed;
	private int myX;
	private JHReindeerActivity myAct;
	private int hungerLevel = 2; 
	private int speedLevel = 2;
	private int maxNum = 59;
	private int minNum = 0;
	private int missRound = 0;


	// Constructor
	public Wolf(Reindeer r, long myseed, JHReindeerActivity m) { 
		myReindeer = r;
		locSeed = myseed;
		myAct = m;
		myX = myInitLocX(locSeed);
		myAct.writeWolf("sniff");

	}

	// Beast interface functions

	// public function setAttackMode that increases speedLevel to 6
	public int setAttackMode(int num) {
		if(num < 1){
			speedLevel = 2;
		} else {
			speedLevel = 6;
		}
		return num;
	}

	// get the initial location for wolf
	private int myInitLocX(long s){
		Random newGen = new Random(); 
		return newGen.nextInt((maxNum - minNum)) + 19;
	}
	// get new location for wolf
	private int getNewLocX(){
		Random newGen = new Random(); 
		return newGen.nextInt((maxNum - minNum)) + 19;

	}

	// Set missRound variable to 1
	public void setMissRound(){
		if(missRound < 1){
			missRound = 1; 
		}
	}

	// Overloaded function setMissRound() (used in reset the missRound parameter with zero as a parameter)
	public void setMissRound(int num){
		missRound = num;
	}

	// Method for reseting the values of wolf object. Also puts the wolf to new location by calling getNewLocX() method.
	public void reset() { 
		hungerLevel = 2;
		speedLevel = 2;
		myX = myInitLocX(getNewLocX());
		Log.d("WOLF START POSITION:","" + myX);
		myAct.writeWolf("sniff");
	}	

	// Increases hungerLevel in every round
	public void setHungerLevel() {
		hungerLevel++;
	}

	// Gets the location of the wolf
	public int getX(){
		return myX;
	}

	// Moves the wolf, if there are no misses stored in missRound parameter (missRound < 0)
	public void move() { 
		if(missRound < 1) {

			int targetX = myReindeer.getLocX(); 

			if(targetX - myX > 0) {
				myX = myX + speedLevel;
			} else {
				myX = myX - speedLevel;
			}

			Log.d("REINDEER", ""+ targetX + "WOLF"+myX);

			// Set the hunger level of the wolf
			setHungerLevel();
		} 
		// If wolf has already missed one turn, set missRound to 0.
		else {
			missRound = 0;
		}
	}

	// Checks the status of the wolf.
	// Gets the location of reindeer and checks distance to it
	// If distance (absolute value) is less than 8, set attackMode on.
	// In case of catch (absolute value 0) set Reindeer's ammo to -100 (wolf) and update wolf's message to its field.
	public void status(){
		int targetX = myReindeer.getLocX(); 
		int dx = targetX - myX;
		if (hungerLevel>7){ 
			myAct.writeWolf("I'm hungry!!");
		}
		if (Math.abs(dx)<5) {
			myAct.writeWolf("slurp!!"); 
		}
		if (Math.abs(dx)<8) {
			setAttackMode(1);
		} else {
			setAttackMode(0);	
		}
		if (Math.abs(dx)<1) { 
			myAct.writeWolf("catch!!");
			//lets set ammo to -100
			myAct.writeAmmo(0);
			myReindeer.setAmmo(-100); 
		}
	}

}