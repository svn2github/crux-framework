/**
 * Page Loader function.
 */     
function loadPage(page){    
//	var _ua = navigator.userAgent.toLowerCase(); 
//	var isIOS = (_ua.indexOf('iphone') > 0 || _ua.indexOf('ipod') > 0 || _ua.indexOf('ipad') > 0);	

//	if(isIOS) {
		function replacePage() {
			setTimeout(function(){window.location.replace(page);}, 1);
			clearListeners();			
		}
	
		function addListeners(){
	        window.applicationCache.addEventListener('noupdate', replacePage, false);
	        window.applicationCache.addEventListener('updateready', replacePage, false);
	        window.applicationCache.addEventListener('error', replacePage, false);
		}
	
		function clearListeners(){
	        window.applicationCache.removeEventListener('noupdate', replacePage);
	        window.applicationCache.removeEventListener('updateready', replacePage);
	        window.applicationCache.removeEventListener('error', replacePage);
		}
	
		var checker = setInterval(function(){
        	//alert('Cache status: '+window.applicationCache.status);
			if (window.applicationCache.status !== 0) {
		        addListeners(); 
		        if (window.applicationCache.status === window.applicationCache.UPDATEREADY){
			    	replacePage();
					clearInterval(checker);
		        }else if (window.applicationCache.status === window.applicationCache.IDLE){
			        window.applicationCache.update();
					clearInterval(checker);
			    }
			} 
		}, 10);
//	} else {
//		var wrapFrame = document.createElement('iframe');
//		wrapFrame.style.border = "none";
//		wrapFrame.frameborder = 0;
//		wrapFrame.style.height = '100%';
//		wrapFrame.style.width = '100%';
//		wrapFrame.width = '100%';
//		wrapFrame.height = '100%';
//		document.getElementsByTagName('body')[0].appendChild(wrapFrame);
//		setTimeout(function(){wrapFrame.src = page;}, 1);
//	}
}
