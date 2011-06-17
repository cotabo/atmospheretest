package testproject
import grails.converters.JSON
class BoxMovementService {

    static transactional = false

	static atmosphere = [mapping: '/atmosphere/boxmovement']

 	def onRequest = { event -> 		
		 event.suspend()		 
	}
                     		
	def onStateChange = { event ->
		if (!event.message) return
                     				
		if (event.isResuming() || event.isResumedOnTimeout()) {
			event.resource.response.writer.with {
				write "resuming"                
				flush()
			}
		} else {
			println event
			println event.message
			
			event.resource.response.writer.with {				
				write "<script>parent.callback('${event.message}');</script>"				
				flush()
			}
		}
	}
}
