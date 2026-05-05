// open url in a new window of specified size with no positioning etc

	function openLinkWindow(url) 
	{ 
	   var args = 'width=900,'
	   + 'height=800,'
	   + 'toolbar=1,'
	   + 'location=1,'
	   + 'directories=1,'
	   + 'status=1,'
	   + 'menubar=1,'
	   + 'scrollbars=yes,'
	   + 'resizable=yes';
	
	   window.open(url, 'new', args);
	}
	