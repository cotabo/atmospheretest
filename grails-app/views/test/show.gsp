<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta name="layout" content="main" />
<script type="text/javascript" src="${resource(dir:'js',file:'jquery.atmosphere.js')}"></script>
<script type="text/javascript" src="${resource(dir:'js/jquery-ui', file:'jquery-ui-1.8.6.custom.min.js')}"></script>

<jq:jquery>    

    /*
    function callback(response) {
        $.atmosphere.log('info', ["response.transport: " + response.transport]);
        if (response.status == 200 && response.state != 'connected' && response.state != 'closed') {
             var data = response.responseBody;                
            $("#appender > ul").append('<li>'+data+'</li>');                    
        }
    }
    
    $.atmosphere.subscribe('${resource(dir: '/atmosphere/board')}',
        callback,
        //websocket doesn't work with my current Jetty - check why. Seems that it uses the wrong Servelt (comet)
        $.atmosphere.request={transport:'websocket', fallbackTransport:'streaming'}
    );    
    var myResp = $.atmosphere.response
    
    $('#buttonPost').click(function() {
        var data = $.param($('#textbox'));        
        $.post('${createLink(action:'broadcast')}?' + data);                        
    });
    */
    
    
    // Draggable stuff
    $('#box').draggable({
        stop: function(event, ui) {                        
            $.post(
                "${createLink(action:'broadcastBox')}", 
                {
                    left: ui.position.left,
                    top: ui.position.top                    
                }, 
                null, 
                'json');                        
        }
    });
    
    $.atmosphere.subscribe('${resource(dir: '/atmosphere/boxmovement')}',
        callback2,
        //websocket doesn't work with my current Jetty - check why. Seems that it uses the wrong Servelt (comet)
        $.atmosphere.request={transport:'websocket', fallbackTransport:'streaming'}
    );    
        
        
    function callback2(response) {
        $.atmosphere.log('info', ["response.transport: " + response.transport]);
        if (response.status == 200 && response.state != 'connected' && response.state != 'closed') {
             var data = response.responseBody;                
            var positioning = $.parseJSON(data);                
            $('#box').animate(positioning);
        }
    }
    
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
<br/>
<div id="box" style="border:1px solid grey; width:150px; height:150px;background-color:#ff2200;"/>
</body>
</html>