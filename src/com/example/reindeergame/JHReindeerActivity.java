package com.example.reindeergame;

import com.example.reindeergame.R; 

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.*;
import android.util.Log;
import java.util.Random;




// Joona HeinilŠ, 1102845
// Java Basics, spring 2014
// 10.3.2014






public class JHReindeerActivity extends Activity {

	// Constant RSPEED
	static final int RSPEED = 10;

	// Constant NO_OF_THROUGHS
	static final int NO_OF_THROUGHS = 4;

	// Constant MAXSPEED
	static final int MAXSPEED = 10;

	// Constant MINSPEED
	static final int MINSPEED = 1;

	// Through array and pickedSpeed variable for user input of the speed
	int[] through;
	int pickedSpeed;
	long mySeed = 29;

	// Reference variables
	TextView distField, ammoField, wolfField, bearField;
	Button moveButton, eatButton, restartButton, kickButton;
	Reindeer myR;
	Wolf wolf;
	Bear bear;

	// Let's initialize the throughs for the reindeer (an amount of the through places constant) 
	final int[] xCoord = new int[NO_OF_THROUGHS];

	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);    

		final NumberPicker np = (NumberPicker) findViewById(R.id.speedPick);

		// Initialize fields
		distField = (TextView) findViewById(R.id.valueDistance);
		ammoField = (TextView) findViewById(R.id.valueAmmo);
		wolfField = (TextView) findViewById(R.id.valueWolfSays);
		bearField = (TextView) findViewById(R.id.valueBearSays);

		// Create reindeer, wolf and bear objects
		myR = new Reindeer(xCoord, RSPEED, this);
		wolf = new Wolf(myR, mySeed, this);
		bear = new Bear(myR, mySeed, this);

		// Create throughs
		createThroughs();

		// Create buttons
		moveButton = (Button)findViewById(R.id.button_move); 
		eatButton = (Button)findViewById(R.id.button_eat);
		restartButton = (Button)findViewById(R.id.button_restart); 
		kickButton = (Button)findViewById(R.id.button_kick); 

		// Set buttons to initial state and max/min constants to user input
		setEatButton();
		setRestartButton();
		setKickButton();
		np.setMaxValue(MAXSPEED);
		np.setMinValue(MINSPEED);
		
		// Write initial values 
		writeAmmo(myR.getAmmo());
		writeDistance(myR.getRemainingDistance());

		// Set onclick listener for move button.
		// Gets an user input
		// After that moves the reindeer, checks its status and finally checks if player can try to kick the beast (probability 30%)
		// Then moves both wolf and bear and checks their statuses after moving.
		
		moveButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				pickedSpeed = np.getValue();
				if(myR.getAmmo() > 0){
					writeAmmo(myR.getAmmo());
				} else {
					writeAmmo(0);	
				}
				// Move the reindeer and check status after that
				myR.move(pickedSpeed);
				myR.status();
				// Move the wolf and check status after that
				wolf.move();
				wolf.status();
				// Move the bear and check status after that
				bear.move();
				bear.status();

			}
		});

		// Set onclick listener for eat button. If pressed, put reindeer to eat and write correct energy level afterwards.

		eatButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				myR.eat();
				writeAmmo(myR.getAmmo());
			}
		});

		// Set onclick listener for kick button. If pressed, the reindeer first tries to kick the beast, then call for kick() method which actually performs the kick.
		// After that sets kick disabled and call setAmmo function. After that, update the Ammo field.
		kickButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				myR.tryKick();
				myR.kick();
				myR.setKickDisabled();
				myR.setAmmo();
				writeAmmo(myR.getAmmo());
				setKickButton();
			}

		});

		// Set onclick listener for restartButton. If pressed, restart the game and update distance and ammo fields.
		restartButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				resetGame();
				writeDistance(myR.getRemainingDistance());
				writeAmmo(myR.getAmmo());
			}
		});

	}


	// Method for creating throughs
	// Set amount of constant NO_OF_THROUGHS eating spots and put randomized values (10-100) to array of that size.

	public void createThroughs(){
		int count = (int)NO_OF_THROUGHS;
		Random rn = new Random();

		int maxNum = 100;
		int minNum = 10;

		for (int idx = 0; idx < count; ++idx){
			int range = maxNum - minNum + 1;
			int randomNum =  rn.nextInt(range) + minNum;
			xCoord[idx] = randomNum;
			Log.d("Generated", "" + xCoord[idx]);
		}
	}

	// Method setEatButton checks if eating is enabled (if the reindeer is at feeding spot) and either shows or hides the eating button.
	
	public void setEatButton() {
		if(myR.getEatEnabled() < 1) {
			eatButton.setVisibility(View.GONE);
		} else {
			eatButton.setVisibility(View.VISIBLE);
		}
	}

	// Method setKickButton checks if kicking is possible and shows or hides the kick button.
	public void setKickButton() {
		if(myR.getKickEnabled() < 1 && myR.getAmmo() > 0) {
			kickButton.setVisibility(View.GONE);
		} else {
			kickButton.setVisibility(View.VISIBLE);
		}
	}

	// Method setMoveButton checks if energy is 0. If not, it shows the Move button.
	public void setMoveButton() {
		if(myR.getAmmo() < 1) {
			moveButton.setVisibility(View.GONE);
			restartButton.setVisibility(View.VISIBLE);
		}
		else {
			moveButton.setVisibility(View.VISIBLE);
			restartButton.setVisibility(View.GONE);
		}
	}

	// Function for getting the distance of wolf.
	public int getWolfDistance() {
		return wolf.getX();
	}

	// Function for wolf to miss one round. Called if reindeer's kick has succeeded.
	public void wolfMissRound() {
		wolf.setMissRound();
		Log.d("***WOLF MISSES***", "Wolf misses its round");
		Toast.makeText(getApplicationContext(), "Succeeded! Wolf misses one turn!", Toast.LENGTH_SHORT).show();
	}
	
	// Function wolfMissRound is overloaded with int parameter.
	// If reindeer's kick fails, it call be called setMissRound(0), which means that the wolf doesn't miss a round
	public void wolfMissRound(int num) {
		wolf.setMissRound(num);
		Log.d("***WOLF DOESN'T MISS***", "Wolf on the move");
		Toast.makeText(getApplicationContext(), "Kick wasn't very effective", Toast.LENGTH_SHORT).show();

	}

	// Function setRestartButton sets restart button either visible or invisible.
	public void setRestartButton() {
		if(myR.getRemainingDistance() > 0 ){
			restartButton.setVisibility(View.GONE);
		}	
		else {
			restartButton.setVisibility(View.VISIBLE);
		}
	}
	
	// Function writeDistance() updates the distance field
	public void writeDistance(int dist){
		distField.setText((Integer.toString(dist)));
	}
	
	// Function writeAmmo() updates the ammo field
	public void writeAmmo(int ammo){
		ammoField.setText((Integer.toString(ammo)));
	}
	
	// Function writeWolf updates the wolf says -field
	public void writeWolf(String name){
		wolfField.setText(name);
	}
	
	// Function writeBear updates the bear says -field
	public void writeBear(String name){
		//	distanceMsg.setText(Integer.toString(dist)); 
		bearField.setText(name);
	}
	// Function won() updates the distance field, shows restart button and hides all the other buttons
	public void won(){
		Toast.makeText(getApplicationContext(), "You won!", Toast.LENGTH_SHORT).show();
		// resetGame();
		//setMoveButton();
		//resetGame();
		distField.setText("Finished!");
		moveButton.setVisibility(View.GONE);
		kickButton.setVisibility(View.GONE);
		restartButton.setVisibility(View.VISIBLE);
	}
	// Function reindeerStarved() handles the event reindeer has starved. Show it to user and then set visible only restart button
	public void reindeerStarved(){
		Toast.makeText(getApplicationContext(), "Reindeer starved!", Toast.LENGTH_SHORT).show();
		moveButton.setVisibility(View.GONE);
		restartButton.setVisibility(View.VISIBLE);
	}

	// Function resetGame() sets eating and kicking disabled, then calls for every object's reset method in order to put their variables to initial state.
	// After that, sets all the buttons to their initial states.
	public void resetGame() {
		myR.setEatDisabled();
		myR.setKickDisabled();
		myR.reset();
		wolf.reset();
		bear.reset();
		setEatButton();
		setMoveButton();
		setKickButton();
		//myR = new Reindeer(xCoord, RSPEED, this);
		//wolf = new Wolf(myR, mySeed, this);
		//bear = new Bear(myR, mySeed, this);
	}

	// Function wolfCaught() is called when wolf caughts the reindeer. Report to the player and show only restart button.
	public void wolfCaught(){
		Toast.makeText(getApplicationContext(), "Wolf caught the reindeer!", Toast.LENGTH_SHORT).show();
		moveButton.setVisibility(View.GONE);
		kickButton.setVisibility(View.GONE);
		restartButton.setVisibility(View.VISIBLE);
	}
	// Function bearCaught() is called when bear caughts the reindeer. Same as above.
	public void bearCaught(){
		Toast.makeText(getApplicationContext(), "Bear caught the reindeer!", Toast.LENGTH_SHORT).show();
		kickButton.setVisibility(View.GONE);
		moveButton.setVisibility(View.GONE);
		restartButton.setVisibility(View.VISIBLE);
		}

	// Just reporting that reindeer has eaten and gained some ammo.
	public void reindeerEaten(){
		Toast.makeText(getApplicationContext(), "Yum! Found a feeding spot and gained +20 ammo!", Toast.LENGTH_SHORT).show();
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}


}
