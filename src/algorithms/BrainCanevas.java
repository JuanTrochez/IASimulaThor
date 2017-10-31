/* ******************************************************
 * Simovies - Eurobot 2015 Robomovies Simulator.
 * Copyright (C) 2014 <Binh-Minh.Bui-Xuan@ens-lyon.org>.
 * GPL version>=3 <http://www.gnu.org/licenses/>.
 * $Id: algorithms/BrainCanevas.java 2014-10-19 buixuan.
 * ******************************************************/
package algorithms;

import robotsimulator.Brain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import characteristics.IFrontSensorResult;
import characteristics.IRadarResult;
import characteristics.Parameters;

public class BrainCanevas extends Brain {
	private static final double HEADINGPRECISION = 0.01*Math.PI;
	private static int totalInstance = 0;
	private double lastSeenDirection,fireCounter;
	private int instanceNumber, moving, mult;
	private double endTaskDirection, initialV;
	private boolean turnRight;
	private boolean shooting;
	private static HashMap<Integer,BrainCanevas> shooters = new HashMap<Integer,BrainCanevas>();
	private int moveTimes;
	private String name;
	public BrainCanevas() { super(); }

	public void activate() {
		//---PARTIE A MODIFIER/ECRIRE---//
		totalInstance++;
		this.instanceNumber = totalInstance;
		this.moving = 0;
		endTaskDirection+=getHeading();
		
		if(endTaskDirection == Math.PI){
			initialV = Math.PI;
			mult = -1;
		} else {
			initialV = 0;
			mult = 1;
		}

		if (instanceNumber != 5 && instanceNumber != 4) {
			endTaskDirection=0;
			name = "shooter " + instanceNumber;
			shooters.put(this.instanceNumber, this);
			moving = 400;
		} else {
			endTaskDirection = (instanceNumber == 5) ? mult * (Math.PI*0.5)  : mult*(-Math.PI*0.5);
			moving = 400;
		}
		turnRight=(endTaskDirection>0);
		
		moveTimes = 0;
		fireCounter = 0;
		lastSeenDirection = initialV;
	}

	public void step() {

		if (fireCounter > 0) {
			fireCounter--;
			if (detectOpponent()) {
				shot();
			} else {
				fire(lastSeenDirection);
				return;
			}
		}

//		if(instanceNumber != 5 && instanceNumber != 4){
//			for(Entry<Integer,BrainCanevas> value : shooters.entrySet()){
//				if(this.moveTimes > value.getValue().moveTimes){
//					fire(lastSeenDirection);
//					return;
//				}
//			}
//			System.out.println("Id : "+ this.name+" Value moveTimes : " + moveTimes);
//		}

		if (moving > 400) {
			//change direction if isHeading = false	
			if (isHeading(endTaskDirection)) {
				moving = 0;
				this.moveTimes++;
				endTaskDirection= (moveTimes > 15) ? (Math.random()-0.5)*2*Math.PI : 0;

				if(moveTimes == 6 && (instanceNumber == 5 || instanceNumber == 4))endTaskDirection = (Math.random()-0.5)*2*Math.PI;

				if(moveTimes == 1 && instanceNumber != 5 && instanceNumber != 4){
					endTaskDirection = fireOrTurn(mult*(-(Math.PI)/4),initialV,mult*(Math.PI/4));
				} else if (moveTimes == 1){
					if(instanceNumber == 5)endTaskDirection = mult*(-(Math.PI/2));
					if(instanceNumber == 4)endTaskDirection = mult *((Math.PI/2));
				}
				
				if(moveTimes == 2 ){
					moving = 0;
				}
				
				if(moveTimes == 3 && instanceNumber != 5 && instanceNumber != 4){
					endTaskDirection = fireOrTurn(mult*(Math.PI/4),initialV,mult*(-Math.PI/4));
					moving = 400; //Permet de rester statique
				}
				
				if(moveTimes == 4 && instanceNumber != 5 && instanceNumber != 4){
					moving = 0;
				}
				
				if(moveTimes == 5 && instanceNumber != 5 && instanceNumber != 4){
					fireCounter = 500;
					lastSeenDirection = fireOrTurn(initialV,initialV,initialV);
					moving = 400;
				}
				
				if(moveTimes == 6 && instanceNumber != 5 && instanceNumber != 4){
					fireCounter = 500;
					if(initialV == 0)lastSeenDirection = fireOrTurn(-Math.PI/4,-Math.PI/4,-Math.PI/4);
					else lastSeenDirection = fireOrTurn((5*Math.PI)/4,(5*Math.PI)/4,(5*Math.PI)/4);
					moving = 400;
				}
				
				if(moveTimes == 7 && instanceNumber != 5 && instanceNumber != 4){
					fireCounter = 500;
					if(initialV == 0)lastSeenDirection = fireOrTurn(mult*(Math.PI/4),mult*(Math.PI/4),mult*(Math.PI/4));
					else lastSeenDirection = fireOrTurn(-(5*Math.PI)/4,-(5*Math.PI)/4,-(5*Math.PI)/4);
					moving = 400;
				}
				
				if(moveTimes == 8 && instanceNumber != 5 && instanceNumber != 4){
					endTaskDirection = fireOrTurn(mult*(-Math.PI/6),initialV,mult*(Math.PI/6));
				}
				
				if(moveTimes == 10 && instanceNumber != 5 && instanceNumber != 4){
					endTaskDirection = fireOrTurn(mult*(Math.PI/6),initialV,mult*(-Math.PI/6));
				}
				
				if(moveTimes == 11 && instanceNumber != 5 && instanceNumber != 4){
					fireCounter = 500;
					lastSeenDirection = fireOrTurn(initialV,initialV,initialV);
					moving = 400;
				}
				
				if(moveTimes == 12 && instanceNumber != 5 && instanceNumber != 4){
					fireCounter = 500;
					if(initialV == 0) lastSeenDirection = fireOrTurn(mult*(-Math.PI/4),mult*(-Math.PI/4),mult*(-Math.PI/4));
					else lastSeenDirection = fireOrTurn((-5*Math.PI)/4,(-5*Math.PI)/4,(-5*Math.PI)/4);
					moving = 400;
				}
				
				if(moveTimes == 14 && instanceNumber != 5 && instanceNumber != 4){
					endTaskDirection = fireOrTurn(mult*(Math.PI/6),initialV,mult*(-Math.PI/6));
				}
				
				//TODO Demi Tour
				//
				
				turnRight=(endTaskDirection>0);
				endTaskDirection+=getHeading();	
				
				
			} else {
				if (turnRight) stepTurn(Parameters.Direction.RIGHT);
				else stepTurn(Parameters.Direction.LEFT);
			}
		} else {
			moving++;
			move();
			if((instanceNumber == 5 || instanceNumber == 4)&& moveTimes == 1 && moving < 200)moving = 200;
			if (instanceNumber != 5 && instanceNumber != 4 && moving % 2 == 0 && moveTimes >= 2 && moveTimes < 17) {
				fire(getHeading());	
			}
		}
		if (instanceNumber != 5 && instanceNumber != 4) {
			shot();
		}
	}

	public boolean detectOpponent() {
		ArrayList<IRadarResult> radarResults = detectRadar(); 
		for (IRadarResult r : radarResults) {
			if (r.getObjectType()==IRadarResult.Types.OpponentMainBot 
					||r.getObjectType()==IRadarResult.Types.OpponentSecondaryBot) {
				return true;
			}
		}
		return false;
	}

	public void shot() {
		ArrayList<IRadarResult> radarResults = detectRadar(); 
		for (IRadarResult r : radarResults) {
			if (r.getObjectType()==IRadarResult.Types.OpponentMainBot 
					||r.getObjectType()==IRadarResult.Types.OpponentSecondaryBot) {
				fire(r.getObjectDirection());
				lastSeenDirection = r.getObjectDirection();
				fireCounter = 500;
				if (moveTimes > 15) {
					endTaskDirection = lastSeenDirection;
					turnRight=(endTaskDirection>0);
					endTaskDirection+=getHeading();	
				}
			}
		}
	}

	/*
	 * @param dir = lastSeenDirection (pour shoot) || endTaskDirection (pour tourner)
	 * @param dirData = La direction vers laquelle le robot doit tourner/tirer
	 */
	public double fireOrTurn(double dirData1, double dirData2, double dirData3){
		switch (instanceNumber) {
		case 1:
			return dirData1;
		case 2:
			return dirData2;
		case 3:	
			return  dirData3;
		default:
			return 0;
		}
	}

	private boolean isHeading(double dir){
		return Math.abs(Math.sin(getHeading()-dir))<HEADINGPRECISION;
	}
}