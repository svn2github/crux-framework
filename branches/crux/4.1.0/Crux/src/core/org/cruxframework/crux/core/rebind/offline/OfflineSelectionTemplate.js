function __MODULE_FUNC__() {
  var metaProps = {} ,values = [] ,providers = [] ,answers = [] ,softPermutationId = 0 ,onLoadErrorFunc, propertyErrorFunc; // end of global vars

  function unflattenKeylistIntoAnswers(propValArray, value) {
    var answer = answers;
    for (var i = 0, n = propValArray.length - 1; i < n; ++i) {
      // lazy initialize an empty object for the current key if needed
      answer = answer[propValArray[i]] || (answer[propValArray[i]] = []);
    }
    // set the final one to the value
    answer[propValArray[n]] = value;
  }

  // Computes the value of a given property.  propName must be a valid property
  // name. Used by the generated PERMUTATIONS code.
  //
  function computePropValue(propName) {
    var value = providers[propName](), allowedValuesMap = values[propName];
    if (value in allowedValuesMap) {
      return value;
    }
    var allowedValuesList = [];
    for (var k in allowedValuesMap) {
      allowedValuesList[allowedValuesMap[k]] = k;
    }
    if (propertyErrorFunc) {
      propertyErrorFunc(propName, allowedValuesList, value);
    }
    throw null;
  }

  // --------------- PROPERTY PROVIDERS --------------- 

// __PROPERTIES_BEGIN__
// __PROPERTIES_END__


  // --------------- STRAIGHT-LINE CODE ---------------
  var strongName;
  var initialHtml;

  // --------------- WINDOW ONLOAD HOOK ---------------
    try {
// __PERMUTATIONS_BEGIN__
      // Permutation logic
// __PERMUTATIONS_END__
      var idx = strongName.indexOf(':');
      if (idx != -1) {
        softPermutationId = Number(strongName.substring(idx + 1));
        strongName = strongName.substring(0, idx);
      }
      initialHtml = strongName + ".cache.html";
    } catch (e) {
      // intentionally silent on property failure
      return;
    }
//    var _ua = computePropValue('user.agent');
//    if (_ua == 'safari')
//    {
	    var wrapFrame = document.createElement('iframe');
	    wrapFrame.src = 'offlineLoader_'+initialHtml;
		wrapFrame.frameborder = 0;
		wrapFrame.style.height = '100%';
		wrapFrame.style.width = '100%';
		wrapFrame.width = '100%';
		wrapFrame.height = '100%';
	    document.getElementsByTagName('body')[0].appendChild(wrapFrame);
//	}
//	else 
//	if (window.applicationCache)
//	{
//		if (window.applicationCache.status =! 4)
//		{
//	        window.applicationCache.addEventListener('noupdate',
//	            function(event) {
//	                window.location.replace('offlineLoader_'+initialHtml);
//	            }, false);
//	        window.applicationCache.addEventListener('updateready',
//	            function(event) {
//	                window.location.replace('offlineLoader_'+initialHtml);
//	            }, false);
//	        window.applicationCache.addEventListener('error',
//	            function(event) {
//	                window.location.replace('offlineLoader_'+initialHtml);
//	            }, false);
//		} 
//		else
//		{	
//			setTimeout(function(){
//				window.location.replace('offlineLoader_'+initialHtml);
//			}, 100);
//		}
//	}
//	else
//	{
//	   window.alert('This browser does not support offline mode.');
//	}
}

__MODULE_FUNC__();
