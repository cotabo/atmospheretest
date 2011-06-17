package testproject
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
		broadcaster['/atmosphere/boxmovement'].broadcast(message as JSON)
		render 'broadcasted'	
	}
	
	
}
