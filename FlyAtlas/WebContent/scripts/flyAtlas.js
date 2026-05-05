				/* flyAtlas.js 06.07.2023 */

// Prevent being framed
	if (top.frames.length!=0)
	{
		top.location=self.document.location;
	}

// Writes start of (hidden) form setting values of options
function startForm()
{	
	var form = document.createElement("form");
	form.setAttribute("method", "get");
	form.setAttribute("action", "");
	form.setAttribute("accept-charset", "UTF-8");
	
		// include ambiguous
	if (document.getElementById('ambiguous').checked)
	{
		var hiddenAmbiguousField = document.createElement("input");
		hiddenAmbiguousField.setAttribute("type", "hidden");
		hiddenAmbiguousField.setAttribute("name", "ambiguous");
		hiddenAmbiguousField.setAttribute("value", "ambiguous");
		form.appendChild(hiddenAmbiguousField);
	}
		// include duplicates
	if (document.getElementById('duplicate').checked)
	{
		var hiddenDuplicateField = document.createElement("input");
		hiddenDuplicateField.setAttribute("type", "hidden");
		hiddenDuplicateField.setAttribute("name", "duplicate");
		hiddenDuplicateField.setAttribute("value", "duplicate");
		form.appendChild(hiddenDuplicateField);
	}
		// show SEs
	if (document.getElementById('errors').checked)
	{
		var hiddenErrorsField = document.createElement("input");
		hiddenErrorsField.setAttribute("type", "hidden");
		hiddenErrorsField.setAttribute("name", "errors");
		hiddenErrorsField.setAttribute("value", "errors");
		form.appendChild(hiddenErrorsField);
	}		
		
		// No. of hits to display
	if(document.getElementById('maxdisplayed'))
	{
		var maxdisplayed = document.getElementById('maxdisplayed').value;
		var hiddenMaxDisplayedField = document.createElement("input");
		hiddenMaxDisplayedField.setAttribute("type", "hidden");
		hiddenMaxDisplayedField.setAttribute("name", "maxdisplayed");
		hiddenMaxDisplayedField.setAttribute("value", maxdisplayed);
		form.appendChild(hiddenMaxDisplayedField);
	}
		// Should options be visible
	if(document.getElementById('hideme') && 
			document.getElementById('hideme').style.display!="none")
	{
		var hiddenShowOptionsField = document.createElement("input");
		hiddenShowOptionsField.setAttribute("type", "hidden");
		hiddenShowOptionsField.setAttribute("name", "showoptions");
		hiddenShowOptionsField.setAttribute("value", "showoptions");
		form.appendChild(hiddenShowOptionsField);
	}

	return form;
}

// only parameter is SEs for initial pages
function startToForm()
{	
	var form = document.createElement("form");
	form.setAttribute("method", "get");
	form.setAttribute("action", "");
	form.setAttribute("accept-charset", "UTF-8");
	
		// show SEs
	if (document.getElementById('errors') && 
			document.getElementById('errors').checked)
	{
		var hiddenErrorsField = document.createElement("input");
		hiddenErrorsField.setAttribute("type", "hidden");
		hiddenErrorsField.setAttribute("name", "errors");
		hiddenErrorsField.setAttribute("value", "errors");
		form.appendChild(hiddenErrorsField);
	}		
	
	return form;
}

		// Create hidden submission forms to request new initial pages //
function toGeneForm() 
{
	var form = startToForm();
	var hiddenField = document.createElement("input");
	hiddenField.setAttribute("type", "hidden");
	hiddenField.setAttribute("name", "page");	// (was initialPage)
	hiddenField.setAttribute("value", "gene");	// gene page (was 0)
	form.appendChild(hiddenField);
	document.body.appendChild(form);
	form.submit();
}

function toGOForm() 
{
	var form = startToForm();
	var hiddenField = document.createElement("input");
	hiddenField.setAttribute("type", "hidden");
	hiddenField.setAttribute("name", "page");	
	hiddenField.setAttribute("value", "go");		// GO page
	form.appendChild(hiddenField);
	document.body.appendChild(form);
	form.submit();
}

function toTissueForm() 
{
	var form = startToForm();
	var hiddenField = document.createElement("input");
	hiddenField.setAttribute("type", "hidden");
	hiddenField.setAttribute("name", "page");
	hiddenField.setAttribute("value", "tissue");	// tissue page
	form.appendChild(hiddenField);
	document.body.appendChild(form);
	form.submit();
}

function toProfileForm() 
{
	var form = startToForm();
	var hiddenField = document.createElement("input");
	hiddenField.setAttribute("type", "hidden");
	hiddenField.setAttribute("name", "page");
	hiddenField.setAttribute("value", "profile");	// profile page
	form.appendChild(hiddenField);
	document.body.appendChild(form);
	form.submit();
}

function toTopForm() 
{
	var form = startToForm();
	var hiddenField = document.createElement("input");
	hiddenField.setAttribute("type", "hidden");
	hiddenField.setAttribute("name", "page");
	hiddenField.setAttribute("value", "top");		// top page
	form.appendChild(hiddenField);
	document.body.appendChild(form);
	form.submit();
}


function toDevelopmentForm() 
{
	var form = startToForm();
	var hiddenField = document.createElement("input");
	hiddenField.setAttribute("type", "hidden");
	hiddenField.setAttribute("name", "page");
	hiddenField.setAttribute("value", "development");	// development page
	form.appendChild(hiddenField);
	document.body.appendChild(form);
	form.submit();
}

function toHomeForm() 
{
	var form = startToForm();
	var hiddenField = document.createElement("input");
	hiddenField.setAttribute("type", "hidden");
	hiddenField.setAttribute("name", "page");
	hiddenField.setAttribute("value", "home");		// home page
	form.appendChild(hiddenField);
	document.body.appendChild(form);
	form.submit();
}

function toFeedbackForm() 
{
	var form = startToForm();
	var hiddenField = document.createElement("input");
	hiddenField.setAttribute("type", "hidden");
	hiddenField.setAttribute("name", "page");
	hiddenField.setAttribute("value", "contact");	// feedback page renamed to contact as antispam
	form.appendChild(hiddenField);
	document.body.appendChild(form);
	form.submit();
}

function toDocsForm() 
{
	var form = startToForm();
	var hiddenField = document.createElement("input");
	hiddenField.setAttribute("type", "hidden");
	hiddenField.setAttribute("name", "page");
	hiddenField.setAttribute("value", "help");		// help page
	form.appendChild(hiddenField);
	document.body.appendChild(form);
	form.submit();
}


// open Help in a popup
	function toHelpForm()
	{ 
		var url = "flyHelp/index.html";
		var w = 740;
		var h = 500;
		openHelp(url, "Help", w, h);
	}

	function openHelp(url, name, w, h) 
	{ 
	   var args = 'width=' + w + ','
	   + 'height=' + h + ','
	   + 'toolbar=0,'
	   + 'location=0,'
	   + 'directories=0,'
	   + 'status=yes,'
	   + 'menubar=0,'
	   + 'scrollbars=1,'
	   + 'resizable=yes';	 
	   if (parseInt(navigator.appVersion) >= 4)
	   {
		   xposition = (screen.width - w)/2;
		   yposition = (screen.height - h)/2;  
		   args += ','
			   + 'screenx=' + xposition + ',' //NN
			   +  'screeny=' + yposition + ',' //NN
			   +  'left=' + xposition + ',' //IE
			   +  'top=' + yposition; //IE
	    }
	   window.open(url, name, args);
	}
	
	// open Links page in popup
	function loadLinks(fbgn, cg)
	{
		var url = "/FlyLinks/index.html?fbgn=" + fbgn + "&cg=" + cg;
		var w = 720;
		var h = 520;
		openHelp(url, "External Fly Links", w, h);
	}

		// Create hidden submission forms for different queries and appropriate pages //

function sendSearchGeneForm() 
{
	var gene = document.getElementById('inputField').value;

	if(gene=="")
	{
		alert("Please enter a gene identifier");
	}
	else
	{ 
		var form = startForm();
			// identifier field for gene search form
		var hiddenSearchField = document.createElement("input");
		hiddenSearchField.setAttribute("type", "hidden");
		hiddenSearchField.setAttribute("name", "search");
		hiddenSearchField.setAttribute("value", "gene");
		form.appendChild(hiddenSearchField);
			// gene name or id for text field
		var hiddenGeneField = document.createElement("input");
		hiddenGeneField.setAttribute("type", "hidden");
		hiddenGeneField.setAttribute("name", "gene");
		hiddenGeneField.setAttribute("value", gene);
		form.appendChild(hiddenGeneField);
			// radio button choice for Gene
		var radioGene = getRadioGene();
		var hiddenRadioField = document.createElement("input");
		hiddenRadioField.setAttribute("type", "hidden");
		hiddenRadioField.setAttribute("name", "radioGene");
		hiddenRadioField.setAttribute("value", radioGene);		
		form.appendChild(hiddenRadioField);
		
		document.body.appendChild(form);
		form.submit();
	}
}

function sendSearchTissueForm()
{
	var go = document.getElementById('inputField').value;

	if(go=="")
	{
		alert("Please enter a search term.");
	}
	else if(go.length==1)
	{
		alert("Please enter a search term larger than one character.");
	}
	else
	{
		var form = startForm();
			// identifier field for tissue search form		
		var hiddenSearchField = document.createElement("input");
		hiddenSearchField.setAttribute("type", "hidden");
		hiddenSearchField.setAttribute("name", "search");
		hiddenSearchField.setAttribute("value", "tissue");
		form.appendChild(hiddenSearchField);
			// gene ontology term for text field
		var hiddenGoField = document.createElement("input");
		hiddenGoField.setAttribute("type", "hidden");
		hiddenGoField.setAttribute("name", "go");
		hiddenGoField.setAttribute("value", go);
		form.appendChild(hiddenGoField);
			// order term (enrich/abund)
		var hiddenOrderField = document.createElement("input");
		var order = document.getElementById('order').value;
		hiddenOrderField.setAttribute("type", "hidden");
		hiddenOrderField.setAttribute("name", "order");
		hiddenOrderField.setAttribute("value", order);
		form.appendChild(hiddenOrderField);
			// tissue id 
		var hiddenTissueField = document.createElement("input");
		var tissue = document.getElementById('tissue').value;
		hiddenTissueField.setAttribute("type", "hidden");
		hiddenTissueField.setAttribute("name", "tissue");
		hiddenTissueField.setAttribute("value", tissue);
		form.appendChild(hiddenTissueField);

		document.body.appendChild(form);
		form.submit();
	}
}

function sendSearchTopForm()	// no text capture for this one
{
	var form = startForm();
		// identifier field for top search form
	var hiddenSearchField = document.createElement("input");
	hiddenSearchField.setAttribute("type", "hidden");
	hiddenSearchField.setAttribute("name", "search");
	hiddenSearchField.setAttribute("value", "top");
	form.appendChild(hiddenSearchField);
		// order term (enrich/abund)
	var hiddenOrderField = document.createElement("input");
	var order = document.getElementById('order').value;
	hiddenOrderField.setAttribute("type", "hidden");
	hiddenOrderField.setAttribute("name", "order");
	hiddenOrderField.setAttribute("value", order);
	form.appendChild(hiddenOrderField);
		// tissue id 
	var hiddenTissueField = document.createElement("input");
	var tissue = document.getElementById('tissue').value;
	hiddenTissueField.setAttribute("type", "hidden");
	hiddenTissueField.setAttribute("name", "tissue");
	hiddenTissueField.setAttribute("value", tissue);
	form.appendChild(hiddenTissueField);

	document.body.appendChild(form);
	form.submit();
}

function sendSearchDevelopmentForm()	// no text capture for this one
{
	var form = startForm();
		// identifier field for development search form
	var hiddenSearchField = document.createElement("input");
	hiddenSearchField.setAttribute("type", "hidden");
	hiddenSearchField.setAttribute("name", "search");
	hiddenSearchField.setAttribute("value", "development");
	form.appendChild(hiddenSearchField);
		// radio buttons for stage to be dominant
	var radioDev = getRadioDev();		
	var hiddenRadioField = document.createElement("input");
	hiddenRadioField.setAttribute("type", "hidden");
	hiddenRadioField.setAttribute("name", "radioDev");
	hiddenRadioField.setAttribute("value", radioDev);		
	form.appendChild(hiddenRadioField);
		// order term (enrich/abund)
	var hiddenOrderField = document.createElement("input");
	var order = document.getElementById('order').value;
	hiddenOrderField.setAttribute("type", "hidden");
	hiddenOrderField.setAttribute("name", "order");
	hiddenOrderField.setAttribute("value", order);
	form.appendChild(hiddenOrderField);		
		// uniTissue
	var hiddenUniTissueField = document.createElement("input");
	var uniTissue = document.getElementById('uniTissue').value;
	hiddenUniTissueField.setAttribute("type", "hidden");
	hiddenUniTissueField.setAttribute("name", "uniTissue");
	hiddenUniTissueField.setAttribute("value", uniTissue);
	form.appendChild(hiddenUniTissueField);
	
	document.body.appendChild(form);
	form.submit();
}

function sendSearchProfileForm() 
{
	var gene = document.getElementById('inputField').value;
	if(gene=="")
	{
		alert("Please enter a gene identifier.");
	}
	else
	{
		var form = startForm();
			// identifier field for profile search form
		var hiddenSearchField = document.createElement("input");
		hiddenSearchField.setAttribute("type", "hidden");
		hiddenSearchField.setAttribute("name", "search");
		hiddenSearchField.setAttribute("value", "profile");
		form.appendChild(hiddenSearchField);
			// name of gene entered in text field
		var hiddenGeneField = document.createElement("input");
		hiddenGeneField.setAttribute("type", "hidden");
		hiddenGeneField.setAttribute("name", "gene");
		hiddenGeneField.setAttribute("value", gene);
		form.appendChild(hiddenGeneField);
			// radio buttons for gene type
		var radioGene = getRadioGene();		
		var hiddenRadioField = document.createElement("input");
		hiddenRadioField.setAttribute("type", "hidden");
		hiddenRadioField.setAttribute("name", "radioGene");
		hiddenRadioField.setAttribute("value", radioGene);		
		form.appendChild(hiddenRadioField);		
			// pearson or spearman
		var hiddenPearsonField = document.createElement("input");
		hiddenPearsonField.setAttribute("type", "hidden");
		hiddenPearsonField.setAttribute("name", "correlation");
		if(document.getElementById('pearson').checked)
		{
			hiddenPearsonField.setAttribute("value", "pearson");
		}
		else
		{
			hiddenPearsonField.setAttribute("value", "spearman");
		}
		form.appendChild(hiddenPearsonField);
			// r statistic cutoff
		var rcut = document.getElementById('rcut').value;
		var rCutHidden = document.createElement("input");
		rCutHidden.setAttribute("type", "hidden");
		rCutHidden.setAttribute("name", "rcut");
		rCutHidden.setAttribute("value", rcut);
		form.appendChild(rCutHidden);

		document.body.appendChild(form);
		form.submit();
	}
}

function sendSearchGoForm() 
{	
	var go = document.getElementById('inputField').value;

	if(go=="")
	{
		alert("Please enter a search term.");
	}
	else
	{
		var form = startForm();
			// identifier field for go (gene ontology = category) search form		
		var hiddenSearchField = document.createElement("input");
		hiddenSearchField.setAttribute("type", "hidden");
		hiddenSearchField.setAttribute("name", "search");
		hiddenSearchField.setAttribute("value", "go");
		form.appendChild(hiddenSearchField);
			// gene ontology term for text field
		var hiddenGoField = document.createElement("input");
		hiddenGoField.setAttribute("type", "hidden");
		hiddenGoField.setAttribute("name", "go");
		hiddenGoField.setAttribute("value", go);
		form.appendChild(hiddenGoField);
			// radio button choice for GO
		var radioGo = getRadioGo();
		var hiddenRadioField = document.createElement("input");
		hiddenRadioField.setAttribute("type", "hidden");
		hiddenRadioField.setAttribute("name", "radioGo");
		hiddenRadioField.setAttribute("value", radioGo);		
		form.appendChild(hiddenRadioField);

		document.body.appendChild(form);
		form.submit();
	}
}

// determines checked radio button for use in hidden form fields in Gene and Profile pages
// modified so that FBgn or CG choices are automatically assigned to FBgene and CGnum, respectively
function getRadioGene()
{
	var radioGene;		// radiobutton choice
	var input = document.getElementById('inputField').value;

	if(input.substring(0,4) == "FBgn")
	{
		radioGene = "FBgene";
	}
	else if(input.substring(0,2) == "CG")
	{
		radioGene = "CGnum";
	}
	else if(document.getElementById('geneSymbol').checked)
	{
		radioGene = "geneSymbol";
	}
	else if(document.getElementById('geneName').checked)
	{
		radioGene = "geneName";
	}
	else if(document.getElementById('CGnum').checked)
	{
		radioGene = "CGnum";
	}
	else
	{
		radioGene = "FBgene";
	}	
	return radioGene;
}

//determines checked radio button for use in hidden form fields in Category page
function getRadioGo()
{
	var radioGo;		// radiobutton choice
	if(document.getElementById('goTerm').checked)
	{
		radioGo = "goTerm";
	}
	else if(document.getElementById('goID').checked)
	{
		radioGo = "goID";
	}
	else
	{
		radioGo = "goFree";
	}	
	return radioGo;
}

//determines checked radio button for use in hidden form fields in Development page
function getRadioDev()
{
	var radioDev;		// radiobutton choice
	if(document.getElementById('devAdult').checked)
	{
		radioDev = "devAdult";
	}
	else
	{
		radioDev = "devLarval";
	}	
	return radioDev;
}

// Submit forms by hitting 'enter' key (code 13)
function geneKey(e)
{
	if (e.keyCode == 13) 
	{
		sendSearchGeneForm();
	}
}
function tissueKey(e)
{
	if (e.keyCode == 13) 
	{
		sendSearchTissueForm();
	}
}
function profileKey(e)
{
	if (e.keyCode == 13) 
	{
		sendSearchProfileForm();
	}
}
function topKey(e)
{
	if (e.keyCode == 13)
	{
		sendSearchTopForm();
	}
}
function devKey(e)
{
	if (e.keyCode == 13)
	{
		sendSearchDevelopmentForm();
	}
}
function goKey(e)
{	
	if (e.keyCode == 13) 
	{
		sendSearchGoForm();
	}
}

// Create *single* hide/show link with text change - must call 'onload'
	var defLinkText = "  Options";	// default link text
	var altLinkText = "  Options";	// alternative link text - same in this case because of need to keep open
		// check these ids are actually unique!
	var visDivID = "visible";	// id of div with vis text to which link ele is added
	var hidDivID = "hideme";	// id of div with hide/show text
	var linkID = "expand";		// id for link - generated by js
	
	// creates link on line with vis text if there is div with hidden text
	function createLink()
	{
		if(document.getElementById(visDivID) && document.getElementById(hidDivID))
		{
			var visDiv = document.getElementById(visDivID);		// div to add link ele to
			var hidDiv = document.getElementById(hidDivID);		// div to hide/show
			
			// create 'a' element with js link to hideShow function and append to visible div
			var hsLink = document.createElement("a");
			hsLink.id = linkID;	// provide link with id to ref for text change 
			// construct the ahref as the js hideShow()
			hsLink.href = "javascript:hideShow('" + hsLink.id + "','" + hidDiv.id + "');";
			// add linked text to element and add element to div
			hsLink.appendChild(document.createTextNode(defLinkText));
			visDiv.appendChild(hsLink);
		}
	}
	
	// takes ids of link element and hide/show target div to do hide/show and text change
	function hideShow(link, target)
	{
		// does the hide/show stuff on the target
		theStyle = document.getElementById(target).style;
		var newText;	// name of link was 'text'
		if (theStyle.display == "block")
		{
			theStyle.display = "none";
			newText = defLinkText;
		}
		else
		{
			theStyle.display = "block";
			newText = altLinkText;
		}	
		// get the link element and change its text
		var linkEle = document.getElementById(link);
		linkEle.replaceChild(document.createTextNode(newText), linkEle.firstChild);
	}
	
	// version for image link with no change of icon
	function hideShowIS(target)
	{
		targetStyle = document.getElementById(target).style;

		if (targetStyle.display == "block")
		{
			targetStyle.display = "none";
		}
		else
		{
			targetStyle.display = "block";		
		}	
	}

	function closeIndex(target)
	{
		document.getElementById(target).style.display = "none";
	}
	
	//  takes ID of link element, hide/show target div, and default and alternative text to do hide/show and text change
	function toggleConcealed(linkID, targetID, defText, altText)
	{
		var theStyle = document.getElementById(targetID).style;
		if (theStyle.display == "block")
		{
			theStyle.display = "none";
			newText = defText;
		}
		else
		{
			theStyle.display = "block";
			newText = altText;
		}
		var linkEle = document.getElementById(linkID);
		linkEle.replaceChild(document.createTextNode(newText), linkEle.firstChild);
	}	

// This looks like JQuery stuff for toggle buttons
function toggleTable(i, m)
{
	var changeAllButton = new Boolean(0);

	var maxCount = parseInt(m);

	if(document.getElementById("t" + i).style.display=="none")
	{
		changeAllButton = true;
		var j;
		for(j = 0; j < maxCount; j++)
			if(j!=i && document.getElementById("t" + j)!=null)
				if(document.getElementById("t" + j).style.display=="none")
					changeAllButton = false;
	}
	else if(document.getElementById("t" + i).style.display!="none")
	{
		changeAllButton = true;
		var j;
		for(j = 0; j < maxCount; j++)
			if(j!=i && document.getElementById("t" + j)!=null)
				if(document.getElementById("t" + j).style.display!="none")
					changeAllButton = false;
	}

	if(changeAllButton && document.getElementById("t" + i).style.display=="none")
	{
		$("#hideAllButton").show();
		$("#showAllButton").hide();
	}

	if(changeAllButton && document.getElementById("t" + i).style.display!="none")
	{
		$("#hideAllButton").hide();
		$("#showAllButton").show();
	}

	//this deals with button itself- all of the above is for checking if the "hide/show ALL" buttons have to be changed
	$("#tB" + i).toggle();
	$("#tBHide" + i).toggle();
	$("#t" + i).slideToggle("fast");

}

function toggleGO(i, m)
{

	var changeAllButton = new Boolean(0);
	var maxCount = parseInt(m);

	if(document.getElementById("go" + i).style.display=="none")
	{
		changeAllButton = true;
		var j;
		for(j = 0; j < maxCount; j++)
		{
			if(j!=i && document.getElementById("go" + j)!=null)
			{
				if(document.getElementById("go" + j).style.display=="none")
				{
					changeAllButton = false;
				}
			}
		}
	}
	else if(document.getElementById("go" + i).style.display!="none")
	{
		changeAllButton = true;
		var j;
		for(j = 0; j < maxCount; j++)
		{
			if(j!=i && document.getElementById("go" + j)!=null)
			{
				if(document.getElementById("go" + j).style.display!="none")
				{
					changeAllButton = false;
				}
			}
		}
	}

	if(changeAllButton && document.getElementById("go" + i).style.display=="none")
	{
		$("#hideAllGOButton").show();
		$("#showAllGOButton").hide();
	}

	if(changeAllButton && document.getElementById("go" + i).style.display!="none")
	{
		$("#hideAllGOButton").hide();
		$("#showAllGOButton").show();
	}

	//this deals with button itself- all of the above is for checking if the "hide/show ALL" buttons have to be changed
	$("#tGO" + i).toggle();
	$("#tGOHide" + i).toggle();
	$("#go" + i).slideToggle("fast");
}

function showAllTables()
{
	$(".resultsWrapper").slideDown("fast");
	$("#hideAllButton").toggle();
	$("#showAllButton").toggle();
	$(".rightExpressionButton").show();
	$(".hiddenRightExpressionButton").hide();
}

function tableGraphToggle()
{
	$("#showTablesButton").toggle();
	$("#showBarChartsButton").toggle();

	var divArray = $(".geneTable").toArray();
	srToggle(divArray);

	var divArray = $(".barComparison").toArray();
	srToggle(divArray);
}

function srToggle(divArray)
{
	var i;
	for (i=0;i<divArray.length;i++)
	{
		if (divArray[i].style.display=="none")
			divArray[i].style.display="block";
		else
			divArray[i].style.display="none";
	}
}

function hideAllTables()
{
	$(".resultsWrapper").slideUp("fast");
	$("#hideAllButton").toggle();
	$("#showAllButton").toggle();
	$(".rightExpressionButton").hide();
	$(".hiddenRightExpressionButton").show();
}

function showAllTissueTables()
{
	$(".resultsWrapper").slideDown("fast");
	$("#hideAllButton").toggle();
	$("#showAllButton").toggle();
	$(".rightTissueExpressionButton").hide();
	$(".hiddenRightTissueExpressionButton").show();
}

function hideAllTissueTables()
{
	$(".resultsWrapper").slideUp("fast");
	$("#hideAllButton").toggle();
	$("#showAllButton").toggle();
	$(".rightTissueExpressionButton").show();
	$(".hiddenRightTissueExpressionButton").hide();
}

function selectProfile(f, p)
{	
	var form = startForm();
	
	var hiddenFBgn = document.createElement("input");
	hiddenFBgn.setAttribute("type", "hidden");
	hiddenFBgn.setAttribute("name", "gene");
	hiddenFBgn.setAttribute("value", f);
	form.appendChild(hiddenFBgn);
	
	var hiddenPid = document.createElement("input");
	hiddenPid.setAttribute("type", "hidden");
	hiddenPid.setAttribute("name", "pid");
	hiddenPid.setAttribute("value", p);
	form.appendChild(hiddenPid);
	
	var page = document.createElement("input");
	page.setAttribute("type", "hidden");
	page.setAttribute("name", "page");
	page.setAttribute("value", "findSet");
	form.appendChild(page);

	document.body.appendChild(form);
	form.submit();
}

// Loads linked paper in new window
function linkToPaper(url, name)	
{
	var args = "width=850,height=800,toolbar=1,scrollbars=1,resizable=yes";
	window.open(url, name, args);
}

