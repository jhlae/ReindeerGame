package com.example.reindeergame;

import java.util.Random;

import android.util.Log;

public class Bear extends Animal implements Beast {

	// Set and initialize class variables

	private Reindeer myReindeer; 
	private long locSeed;
	private int myX;
	private JHReindeerActivity myAct;
	private int hungerLevel = 4; 
	private int speedLevel = 1;
	private int maxNum = 80;
	private int minNum = 50;

	// Constructor
	public Bear(Reindeer r, long myseed, JHReindeerActivity m) { 
		myReindeer = r;
		locSeed = myseed;
		myAct = m;
		myX = myInitLocX(locSeed);
		myAct.writeBear("Nuf");

	}


	// Beast interface functions
	
	// public function setAttackMode that increases speedLevel to 5
	public int setAttackMode(int num) {
		if(num < 1){
			speedLevel = 2;
		} else {
			speedLevel = 5;
		}
		return num;
	}

	// Get the initial location for bear
	private int myInitLocX(long s){
		Random newGen = new Random(); 
		return newGen.nextInt((maxNum - minNum)) + 20;
	}
	// Get new location for bear
	private int getNewLocX(){
		Random newGen = new Random(); 
		return newGen.nextInt((maxNum - minNum)) + 20;

	}
	
	// Method for reseting the values of wolf object. Also puts the bear to new location by calling getNewLocX() method.

	public void reset() { 
		hungerLevel = 4;
		speedLevel = 1;
		myX = myInitLocX(getNewLocX());
		Log.d("BEAR START POSITION:","" + myX);
		myAct.writeBear("Sniffing");
	}	

	// Increases hungerLevel in every round
	public void setHungerLevel() {
		hungerLevel++;
	}


	public void move() { 
		int targetX = myReindeer.getLocX(); 

		if(targetX - myX > 0) {
			myX = myX + speedLevel;
		} else {
			myX = myX - speedLevel;
		}

		Log.d("REINDEER", ""+ targetX + "BEAR"+myX);

		// Set the hunger level of the wolf
		setHungerLevel();
	}

	
	// Checks the status -> moves the wolf if there are no misses stored in missRound parameter (missRound < 0)
	public void status(){
		int targetX = myReindeer.getLocX(); 
		int dx = targetX - myX;
		if (hungerLevel>7){ 
			myAct.writeBear("Coming at you");
		}
		if (Math.abs(dx)<5) {
			myAct.writeBear("Slurp."); 
		}
		if (Math.abs(dx)<4) {
			setAttackMode(1);
		} else {
			setAttackMode(0);	
		}
		if (Math.abs(dx)<1) { 
			myAct.writeBear("Kiinni!");
			//lets set ammo to -200
			myAct.writeAmmo(0);
			myReindeer.setAmmo(-200); 
		}
	}

}
