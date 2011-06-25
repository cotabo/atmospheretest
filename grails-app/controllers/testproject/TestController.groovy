package testproject
import org.atmosphere.cpr.BroadcasterFactory;
import org.atmosphere.cpr.DefaultBroadcaster;

import grails.converters.JSON

class TestController {

    def index = {
		render (view:'show');
	}
	
	
	def broadcastBox = {	
		def bc = session.getAttribute("atmoResource").broadcaster
		if(!bc) {
			render 'no broadcster found. Please subscribe too /atmosphere/boardupdate?boardId=XX'
		}
		else {
			def message = [:]
			message.left = params.left
			message.top = params.top
			
			//Broadcast the message		
			bc.broadcast(message as JSON)
			render 'broadcasted'
		}			
	}
	
	
}
