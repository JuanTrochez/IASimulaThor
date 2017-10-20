/* ******************************************************
 * Simovies - Eurobot 2015 Robomovies Simulator.
 * Copyright (C) 2014 <Binh-Minh.Bui-Xuan@ens-lyon.org>.
 * GPL version>=3 <http://www.gnu.org/licenses/>.
 * $Id: algorithms/BrainCanevas.java 2014-10-19 buixuan.
 * ******************************************************/
package algorithms;

import robotsimulator.Brain;

import java.util.ArrayList;

import characteristics.IFrontSensorResult;
import characteristics.IRadarResult;
import characteristics.Parameters;

public class BrainCanevas extends Brain {
	private static final double HEADINGPRECISION = 0.01*Math.PI;
	private static int totalInstance = 0;
	private double lastSeenDirection = Double.NaN;
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
			endTaskDirection = (instanceNumber == 5) ? Math.PI*0.5  : -Math.PI*0.5;
			moving = 401;
		}
		turnRight=(endTaskDirection>0);
		endTaskDirection+=getHeading();	
		moveTimes = 0;
	}

	public void step() {
		if (moving > 400) {
			//change direction			
			if (isHeading(endTaskDirection)) {
				moving = 0;
				moveTimes++;
				endTaskDirection= (moveTimes > 5) ? (Math.random()-0.5)*2*Math.PI : 0;
				if (moveTimes % 2 == 0) {
					switch (instanceNumber) {
					case 1:
						endTaskDirection= (Math.PI)/4;
						break;
					case 2:
						endTaskDirection= 0;						
						break;
					case 3:			
						endTaskDirection= -(Math.PI)/4;			
						break;

					default:
						break;
					}					
				} else if  (moveTimes < 4) {
					switch (instanceNumber) {
					case 1:
						endTaskDirection= -(Math.PI)/4;
						break;
					case 2:
						endTaskDirection= 0;						
						break;
					case 3:			
						endTaskDirection= Math.PI/4;			
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

	public void shot() {
		ArrayList<IRadarResult> radarResults = detectRadar(); 
		for (IRadarResult r : radarResults) {
			if (r.getObjectType()==IRadarResult.Types.OpponentMainBot 
					||r.getObjectType()==IRadarResult.Types.OpponentSecondaryBot) {
				fire(r.getObjectDirection());
			}
		}
	}

	private boolean isHeading(double dir){
		return Math.abs(Math.sin(getHeading()-dir))<HEADINGPRECISION;
	}
}
