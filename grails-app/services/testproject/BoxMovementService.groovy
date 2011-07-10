package testproject

import org.atmosphere.cpr.BroadcasterFactory
import org.atmosphere.cpr.DefaultBroadcaster
import grails.converters.JSON

class BoxMovementService {

    static transactional = false

	//atmosphere mapping - every request on this path will execute the onRequest closure
	static atmosphere = [mapping: '/atmosphere/boardupdate']

	/**
	 * We're hacking the Grails Atmosphere plugin a bit here.
	 * Normally it is intended to only have 1 atmosphere channel
	 * but we may create a new one for each boardId that arrives
	 * and put this into the users session.
	 * 
	 * If we find a broadcaster in the session it means that the user switched the channel
	 * and we need to remove him from the existing broadcaster and put him
	 * onto a new one depending on the boardId request parameter.
	 * 
	 */
 	def onRequest = { event ->
		 log.debug "event [$event]"
		 //We create try to get (or create a new one if not yet there) a broadcaster
		 //For the boardId that was sent as a request parameter 				 
		 def boardSpecificBroadcaster = 
		 	BroadcasterFactory.default.lookup(
				 DefaultBroadcaster.class, 
				 '/atmosphere/boardupdate/'+event.request.getParameter('boardId'),
				 true
			 )	     	
		 def sessRes = event.request.session.getAttribute('atmoResource')
		 log.debug "found in session ${sessRes}"
		 if(sessRes) {		
			 //We need to remove the AtmosphereResource from the session
			 //from its broadcaster.	 
			 sessRes.broadcaster.removeAtmosphereResource(sessRes)
			 log.debug "removed me from ${sessRes.broadcaster}"
		 }
		 //We set the newly retrieved broadcaste for this user		 
		 event.setBroadcaster(boardSpecificBroadcaster)		 
		 log.debug "After setting the broadcaster too [${boardSpecificBroadcaster}]"
		 //We subscribe the user to the broadcaster		 
		 boardSpecificBroadcaster.addAtmosphereResource(event)
		 log.debug "After adding too ${boardSpecificBroadcaster}"
		 //Broadcasting a "user joined" message		 
		 boardSpecificBroadcaster.broadcast('{"message":"A user joined"}')
		 
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
			event.resource.response.writer.with {				
				write "<script>parent.callback('${event.message}');</script>"				
				flush()
			}
		}
	}
}
