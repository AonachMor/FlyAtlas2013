// aComplete.js for FlyAtlas 2013 —  16.08.2012 D.P.Leader 
// 03.11.2021 revised servlet reference

var xmlHttp;
var completeDiv;
var inputField;
var menuTable;
var menuTableBody;
var searchType;	// type of search: symbol, name, CGnum or FBgene - goTerm, goID, goFree

function createXMLHttpRequest() 
{
	if (window.ActiveXObject) 
	{
		xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
	}
	else if (window.XMLHttpRequest) 
	{
		xmlHttp = new XMLHttpRequest();                
	}
}

function initVars() 
{
	inputField = document.getElementById("inputField");            
	menuTable = document.getElementById("menuTable");
	completeDiv = document.getElementById("popup");
	menuTableBody = document.getElementById("menuTableBody");
	
	// radio button checked value for type of search
	if(document.getElementById("geneSymbol") == null && document.getElementById("goTerm") == null)	
	{
		if(document.getElementById("tissue") != null)	// Tissue page (and Top, but not relevant)
		{
			searchType = "goTerm";
		}
		else
		{
			searchType = "none";
		}
	}
	else if (document.getElementById("geneSymbol") != null)
	{
		if(document.getElementById("geneSymbol").checked == true)
		{
			searchType = "symbol";
		}
		else if(document.getElementById("geneName").checked == true)
		{
			searchType = "name";
		}
		else if(document.getElementById("CGnum").checked == true)
		{
			searchType = "cgnum";
		}
		else if(document.getElementById("FBgene").checked == true)
		{
			searchType = "fbgene";
		}
		else
		{
			searchType = "none";	
		}
	}
	else if (document.getElementById("goTerm") != null)
	{
		if(document.getElementById("goTerm").checked == true)
		{
			searchType = "goTerm";
		}
		else if(document.getElementById("goID").checked == true)
		{
			searchType = "goID";
		}
		else if(document.getElementById("goFree").checked == true)
		{
			searchType = "goFree";
		}
		else
		{
			searchType = "none";	
		}		
	}
}

function findNames(geneORgo) 
{
	initVars();
	if (inputField.value.length > 0) 
	{
		createXMLHttpRequest();
		var url = "";
		var encFieldValue = encodeURIComponent(inputField.value);
				
		if(geneORgo == "gene")
		{
			url = "/AutoComplete/index.html?gene=" + encFieldValue
				+ "&searchType=" + searchType; 
		}
		else if(geneORgo == "go")
		{
			url = "/AutoComplete/index.html?go=" + encFieldValue
				+ "&searchType=" + searchType;  		
		}
		xmlHttp.open("GET", url, true);
		xmlHttp.onreadystatechange = callback;
		xmlHttp.send(null);
	} 
	else 
	{
		clearNames();
	}
}

function callback() 
{
	if (xmlHttp.readyState == 4) 
	{
		if (xmlHttp.status == 200) 
		{
			setNames(xmlHttp.responseXML.getElementsByTagName("name"));
		} 
		else if 
		(xmlHttp.status == 204)
		{
			clearNames();
		}
	}
}

function setNames(the_names) 
{            
	clearNames();
	var size = the_names.length;
	setOffsets();

	var row, cell, txtNode;
	for (var i = 0; i < size; i++) 
	{
		var nextNode = the_names[i].firstChild.data;
		row = document.createElement("tr");
		cell = document.createElement("td");
		
		cell.onmouseout = function() {this.className='mouseOver';};
		cell.onmouseover = function() {this.className='mouseOut';};
		cell.onclick = function() { populateName(this); } ;                             

		txtNode = document.createTextNode(nextNode);
		cell.appendChild(txtNode);
		row.appendChild(cell);
		menuTableBody.appendChild(row);
	}
}

function setOffsets() 
{
	var end = inputField.offsetWidth;
	var left = calculateOffsetLeft(inputField);
	var top = calculateOffsetTop(inputField) + inputField.offsetHeight;

	completeDiv.style.border = "black 1px solid";
	completeDiv.style.left = left + "px";
	completeDiv.style.top = top + "px";
	menuTable.style.width = end + "px";
}

function calculateOffsetLeft(field) 
{
  return calculateOffset(field, "offsetLeft");
}

function calculateOffsetTop(field) 
{
  return calculateOffset(field, "offsetTop");
}

function calculateOffset(field, attr) 
{
  var offset = 0;
  while(field) 
  {
	offset += field[attr]; 
	field = field.offsetParent;
  }
  return offset;
}

function populateName(cell) 
{
	inputField.value = cell.firstChild.nodeValue;
	clearNames();
}

function clearNames() 
{
	var ind = menuTableBody.childNodes.length;
	for (var i = ind - 1; i >= 0 ; i--) 
	{
		 menuTableBody.removeChild(menuTableBody.childNodes[i]);
	}
	completeDiv.style.border = "none";
}
