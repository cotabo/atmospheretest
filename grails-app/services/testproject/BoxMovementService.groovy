package testproject
import org.atmosphere.cpr.BroadcasterFactory;
import org.atmosphere.cpr.DefaultBroadcaster;

import grails.converters.JSON
class BoxMovementService {

    static transactional = false

	static atmosphere = [mapping: '/atmosphere/boxmovement']

	/**
	 * We're hacking the Grails Atmosphere plugin a bit here.
	 * Normally it is intended to only have 1 atmosphere channel
	 * but we may create a new one for each boardId that arrives
	 * 
	 */
 	def onRequest = { event ->
		 println "event [$event]"
		 //We create try to get (or create a new one if not yet there) a broadcaster
		 //For the boardId that was sent as a request parameter 				 
		 def boardSpecificBroadcaster = 
		 	BroadcasterFactory.default.lookup(
				 DefaultBroadcaster.class, 
				 '/atmosphere/board_'+event.request.getParameter('boardId'),
				 true
			 )	     	
		 /**
	     //Remove outself from whatever broadcaster we were on
		 println "Before removin [${event.broadcaster}]"
	     event.broadcaster.removeAtmosphereResource(event)
		 println "After removing session from broadcaster [${event.broadcaster}]."
		 **/
		 def sessRes = event.request.session.getAttribute('atmoResource')
		 println "found in session ${sessRes}"
		 if(sessRes) {		
			 //We need to remove the AtmosphereResource from the session
			 //from its broadcaster.	 
			 sessRes.broadcaster.removeAtmosphereResource(sessRes)
			 println "removed me from ${sessRes.broadcaster}"
		 }
		 //We set the newly retrieved broadcaste for this user		 
		 event.setBroadcaster(boardSpecificBroadcaster)		 
		 println "After setting the broadcaster too [${boardSpecificBroadcaster}]"
		 //We subscribe the user to the broadcaster		 
		 boardSpecificBroadcaster.addAtmosphereResource(event)
		 println "After adding too ${boardSpecificBroadcaster}"
		 //Broadcasting a "user joined" message		 
		 boardSpecificBroadcaster.broadcast("A user joined")
		 
		 //We put this AtmosphereResource into the users session
		 //So that it can be found on the next request to the controller		 
		 event.request.session.setAttribute('atmoResource', event)	
		 //Waiting for events
		 event.suspend()		 

	}

	//This is now use                     		
	def onStateChange = { event ->
		if (!event.message) return		                     				
		if (event.isResuming() || event.isResumedOnTimeout()) {
			event.resource.response.writer.with {
				write "resuming"                
				flush()
			}
		} else {			
			println "Received message on broadcaster [${event.resource.broadcaster.getID()}]: ${event.message}"		
			event.resource.response.writer.with {				
				write "<script>parent.callback('${event.message}');</script>"				
				flush()
			}
		}
	}
}
