/* ******************************************************
 * Simovies - Eurobot 2015 Robomovies Simulator.
 * Copyright (C) 2014 <Binh-Minh.Bui-Xuan@ens-lyon.org>.
 * GPL version>=3 <http://www.gnu.org/licenses/>.
 * $Id: algorithms/BrainCanevas.java 2014-10-19 buixuan.
 * ******************************************************/
package algorithms;

import robotsimulator.Brain;

import java.util.ArrayList;

import javax.jws.soap.SOAPBinding.ParameterStyle;

import characteristics.IFrontSensorResult;
import characteristics.IRadarResult;
import characteristics.Parameters;

public class BrainCanevas extends Brain {
	private boolean turnTask,turnRight,endMove,taskOne,slalom;
	private double endTaskDirection,toTheNorth,toTheSouth,toTheEast,toTheWest,lastHeading;
	private int endTaskCounter,id,latence,lastDir,shotshot;
	private static final double HEADINGPRECISION = 0.001;
	public BrainCanevas() { super(); }
	private String name;
	public void activate() {
		//---PARTIE A MODIFIER/ECRIRE---//
		shotshot = 0;
		slalom = false;
		lastDir = 3;	// 1 : West ; 2 : North ; 3 : East ; 4 : South
		latence=-1;
		turnTask=false;
		endMove=false;
		taskOne=true;
		lastHeading = -1;
		if(this.getHealth() == 100){
			this.name = "tank";
		} else {
			this.name = "adc";
		}
		endTaskDirection=getHeading()+0.25*Math.PI;
		//stepTurn(Parameters.Direction.RIGHT);
		move();
	}
	public void step() {
		//---PARTIE A MODIFIER/ECRIRE---//
		
		if (detectFront().getObjectType()==IFrontSensorResult.Types.NOTHING && !turnTask) {
			move();
		}   else if ((detectFront().getObjectType() == IFrontSensorResult.Types.WALL ||turnTask) && this.name.equals("adc")){
			turnTask = true;
			turnOver();
		} else if(detectFront().getObjectType() == IFrontSensorResult.Types.TeamMainBot 
				|| detectFront().getObjectType() == IFrontSensorResult.Types.TeamSecondaryBot
				|| radarTeam()){
			if(this.name.equals("adc")){
				stepTurn(Parameters.Direction.LEFT);
			}
		} else if(radarEnnemy() && this.name.equals("adc")){
			shotshot++;
			if(shotshot < 1100){
				System.out.println("shotshot 1 : " + shotshot);
				fireEnnemy();
			} else {
				shotshot = 0;
				move();
			}
			
		} else {
			//move();
			if(!slalom){
				stepTurn(Parameters.Direction.LEFT);
			} else {
				stepTurn(Parameters.Direction.RIGHT);
			}
		}
		if(this.name.equals("adc")){
			shotshot++;
			if(shotshot < 1100){
				System.out.println("shotshot : " + shotshot);
				fireEnnemy();
			} else {
				shotshot = 0;
				move();
			}
		}
	}

	private void turnOver(){
		//double lastHeading = this.getHeading();
		switch (lastDir){
		case 1:
			if(!isHeadingSouth()){
				stepTurn(Parameters.Direction.LEFT);
				//if(this.getHeading() == lastHeading) lastDir = 2;
			} else {
				lastDir = 4;
				turnTask = false;
			}
			break;
		case 2:
			if(!isHeadingEast()){
				stepTurn(Parameters.Direction.RIGHT);
				//if(this.getHeading() == lastHeading) lastDir = 1;
			}else {
				lastDir = 3;
				turnTask = false;
			}
			break;
		case 3:
			if(!isHeadingNorth()){
				this.stepTurn(Parameters.Direction.LEFT);
				//if(this.getHeading() == lastHeading) lastDir = 4;
			} else {
				lastDir = 2;
				turnTask = false;
			}
			break;
		case 4 :
			if(!isHeadingWest()){
				stepTurn(Parameters.Direction.RIGHT);
				//if(this.getHeading() == lastHeading) lastDir = 3;
			} else {
				lastDir = 1;
				turnTask = false;
			}
		}
	}

	private boolean isClose(){
		ArrayList<IRadarResult> radarResults = detectRadar();
		for (IRadarResult r : radarResults) {
			if (r.getObjectDistance() < 200) {
				return true;
			}
		}
		return false;
	}

	private boolean radarTeam (){
		ArrayList<IRadarResult> radarResults = detectRadar();
		for (IRadarResult r : radarResults) {
			if (r.getObjectType()==IRadarResult.Types.TeamMainBot || r.getObjectDistance() < 200) {
				return true;
			}
		}
		return false;
	}

	private boolean radarEnnemy (){
		ArrayList<IRadarResult> radarResults = detectRadar();
		for (IRadarResult r : radarResults) {
			if (r.getObjectType()==IRadarResult.Types.OpponentMainBot || r.getObjectType()==IRadarResult.Types.OpponentSecondaryBot) {
				return true;
			}
		}
		return false;
	}

	private void fireEnnemy() {
		ArrayList<IRadarResult> radarResults = detectRadar();
		if(radarResults.size() == 0 ) return;
		double lastSeenDirection = 0;
		for (IRadarResult r : radarResults) {
			if (r.getObjectType()==IRadarResult.Types.OpponentMainBot || r.getObjectType()==IRadarResult.Types.OpponentSecondaryBot) {
				fire(r.getObjectDirection());
				lastSeenDirection = r.getObjectDirection();
			}
		}
		if(lastSeenDirection != 0)
			fire(lastSeenDirection);
	}

	private boolean nothingAhead(){
		return (detectFront().getObjectType()==IFrontSensorResult.Types.NOTHING);
	}
	private boolean isHeadingSouth(){return isHeading(Parameters.SOUTH);}
	private boolean isHeadingEast(){return isHeading(Parameters.EAST);}
	private boolean isHeadingNorth(){return isHeading(Parameters.NORTH);}
	private boolean isHeadingWest(){return isHeading(Parameters.WEST);}
	private boolean isHeading(double dir){
		return Math.abs(Math.sin(getHeading()-dir))<HEADINGPRECISION;
	}
}
