/* ******************************************************
 * Simovies - Eurobot 2015 Robomovies Simulator.
 * Copyright (C) 2014 <Binh-Minh.Bui-Xuan@ens-lyon.org>.
 * GPL version>=3 <http://www.gnu.org/licenses/>.
 * $Id: algorithms/BrainCanevas.java 2014-10-19 buixuan.
 * ******************************************************/
package algorithms;

import robotsimulator.Brain;
import characteristics.IFrontSensorResult;
import robotsimulator.Brain;

import java.util.ArrayList;

import characteristics.IFrontSensorResult;
import characteristics.IRadarResult;
import characteristics.Parameters;


public class BrainCanevas extends Brain {
  
	private static final double HEADINGPRECISION = 0.01*Math.PI;
	private static int totalInstance = 0;
	private double lastSeenDirection,fireCounter;
	private int instanceNumber, moving;
	private double endTaskDirection;
	private boolean turnRight;
	private int moveTimes;
	public BrainCanevas() { super(); }

	public void activate() {
		//---PARTIE A MODIFIER/ECRIRE---//
		totalInstance++;
		this.instanceNumber = totalInstance;
		this.moving = 0;
		endTaskDirection=0;

		if (instanceNumber != 5 && instanceNumber != 4) {
			endTaskDirection=0;
		} else {
//			endTaskDirection = (instanceNumber == 5) ? Math.PI*0.5  : -Math.PI*0.5;
			moving = 401;
		}
		turnRight=(endTaskDirection>0);
		endTaskDirection+=getHeading();	
		moveTimes = 0;
		fireCounter = 0;
		lastSeenDirection = 0;
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
		if (moving > 400) {
			//change direction			
			if (isHeading(endTaskDirection)) {
				moving = 0;
				moveTimes++;
				endTaskDirection= (moveTimes > 5) ? (Math.random()-0.5)*2*Math.PI : 0;
				if (moveTimes == 3 || moveTimes == 4) {
					fireCounter = 500;
					switch (instanceNumber) {
					case 1:
						lastSeenDirection = (moveTimes == 3) ? 0 : (Math.PI)/8;	
						break;
					case 2:
//						lastSeenDirection = 0;							
						break;
					case 3:			
//						lastSeenDirection = 0;	
						lastSeenDirection = (moveTimes == 3) ? 0 : -(Math.PI)/8;	
						break;

					default:
						break;
					}
					
				} else if (moveTimes % 2 == 0) {
					switch (instanceNumber) {
					case 1:
						endTaskDirection = (moveTimes == 4) ? 0 : (Math.PI)/2;
						break;
					case 2:
						endTaskDirection= 0;						
						break;
					case 3:			
						endTaskDirection = (moveTimes == 4) ? 0 : -(Math.PI)/2;			
						break;

					default:
						break;
					}					
				} else if  (moveTimes < 5) {
					switch (instanceNumber) {
					case 1:
						endTaskDirection = -(Math.PI)/2;
						break;
					case 2:
						endTaskDirection= 0;						
						break;
					case 3:			
						endTaskDirection= Math.PI/2;			
						break;

					default:
						break;
					}					
				}
				turnRight=(endTaskDirection>0);
				endTaskDirection+=getHeading();	
				move();
			} else {
				if (turnRight) stepTurn(Parameters.Direction.RIGHT);
				else stepTurn(Parameters.Direction.LEFT);

			}

		} else {
			moving++;
			move();
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
				if (moveTimes > 5) {
					endTaskDirection = lastSeenDirection;
					turnRight=(endTaskDirection>0);
					endTaskDirection+=getHeading();	
					
				}
			}
		}
	}

	private boolean isHeading(double dir){
		return Math.abs(Math.sin(getHeading()-dir))<HEADINGPRECISION;
	}
	
	
  //end of class
}
