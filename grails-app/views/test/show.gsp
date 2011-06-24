<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta name="layout" content="main" />
<script type="text/javascript" src="${resource(dir:'js',file:'jquery.atmosphere.js')}"></script>
<script type="text/javascript" src="${resource(dir:'js/jquery-ui', file:'jquery-ui-1.8.6.custom.min.js')}"></script>

<jq:jquery>    

    
    // Draggable stuff
    $('#box').draggable({
        stop: function(event, ui) {                        
            $.post(
                "${createLink(action:'broadcastBox')}", 
                {            
                	boardId: +$('#channel').val(),
                    left: ui.position.left,
                    top: ui.position.top                    
                }, 
                null, 
                'json');                        
        }
    });
        
        
    function callback2(response) {
        $.atmosphere.log('info', ["response.transport: " + response.transport]);
        if (response.status == 200 && response.state != 'connected' && response.state != 'closed') {
             var data = response.responseBody;                
            var positioning = $.parseJSON(data);                
            $('#box').animate(positioning);
        }
    }
    
    /**
     * Subscribes the given atmosphere channel     
     */
    var subscribeChannel = function(channelId) {
    	//Closing all active requests if there are any
    	$.atmosphere.close(); 
        $.atmosphere.subscribe('${resource(dir: '/atmosphere/boxmovement?boardId=')}'+channelId,
        	callback2,
        	//websocket doesn't work with my current Jetty - check why. Seems that it uses the wrong Servelt (comet)
        	$.atmosphere.request={transport:'websocket', fallbackTransport:'streaming'}
   		);    
    }
    
    //Register event listener for select box
    $('#channel').change(function() {
    	subscribeChannel($('#channel').val());
    });
    
    subscribeChannel(1)
    
</jq:jquery>
</head>
<body>
<!--
<form id="kran">
<input name="message" type="text" id="textbox"/>
</form>
<button id="buttonPost">post me</button>
<div id="appender">
<ul></ul>
-->
</div>
Channel: <g:select id="channel" from="${[1,2]}" name="channel"/>
<br/>
<div id="box" style="border:1px solid grey; width:150px; height:150px;background-color:#ff2200;"/>
</body>
</html>