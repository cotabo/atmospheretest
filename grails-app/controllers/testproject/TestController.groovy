package testproject
import org.atmosphere.cpr.BroadcasterFactory;
import org.atmosphere.cpr.DefaultBroadcaster;

import grails.converters.JSON

class TestController {

    def index = {
		render (view:'show');
	}
	
	def broadcast = {		
		broadcaster['/atmosphere/board'].broadcast(params.message)
		render 'broadcasted'
	}
	
	def broadcastBox = {
		def message = [:]				
		message.left = params.left
		message.top = params.top
		//Trying to get a broadcaster from the session				
		def bc = session.getAttribute("atmoResource").broadcaster
		//If we didn't found one or it is different from what is id 
		//of the request		
		/*	
		if (!bc || bc.getID() != 'board_'+params.boardId) {
			//Go back to the default broadcaster			
			bc =broadcaster['/atmosphere/boxmovement']
		}
		*/
		//Broadcast the message		
		bc.broadcast(message as JSON)
		render 'broadcasted'	
	}
	
	
}
