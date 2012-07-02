// General Settings
var COMMA = ',';
var COOKIE_NAME = 'news';
var numNewsToRestore = 0;
var storedNewsArr;

// News Provider
var NEWS_URI = 'http://rss.news.yahoo.com/rss/';

// Search & Page Locations
var CATEGORY = 'category';
var DESCR = 'description';
var CAT_ = 'cat_';
var _D = '_d';
var _LI = '_li';
var _A = '_a';

// News Structure
var HTML_FRG1 = '<li id="';
var HTML_FRG2 = '"><h3><a id="';
var HTML_FRG3 = '"  href="#">';
var HTML_FRG4 = '</a></h3><p id="';
var HTML_FRG5 = '"></p><a href="#" data-transition="slideup" id="';
var HTML_FRG6 = '"/></li>';

// Pages
var hdrCategoriesVar = $('#hdrCategories');
var contentCategoriesVar = $('#contentCategories');
var ftrCategoriesVar = $('#ftrCategories');

var hdrSelectVar = $('#hdrSelect');
var contentSelectVar = $('#contentSelect');
var ftrSelectVar = $('#ftrSelect');

var hdrProgressVar = $('#hdrProgress');
var contentProgressVar = $('#contentProgress');
var ftrProgressVar = $('#ftrProgress');

var hdrNewsVar = $('#hdrNews');
var contentNewsVar = $('#contentNews');
var ftrNewsVar = $('#ftrNews');

function init() {
	showProgress();
	var storedNewsTxt = $.DSt.get(COOKIE_NAME);

	if (storedNewsTxt != null && storedNewsTxt.length > 0) {
		storedNewsArr = storedNewsTxt.split(COMMA);
	} else {
		storedNewsArr = new Array();
	}
	numNewsToRestore = storedNewsArr.length;
	restore();
}

function showProgress() {
	hideCategories();
	hideSelect();
	hideNews();
	hdrProgressVar.show();
	contentProgressVar.show();
	ftrProgressVar.show();
}

function restore() {
	if (numNewsToRestore > 0) {
		getNews(storedNewsArr[--numNewsToRestore], restoreNews);
	} else {
		showCategories();
	}
}

function getNews(varCat, handler) {
	var varURI = NEWS_URI + varCat;
	$.ajax({
		type : GET,
		dataType : XML,
		url : varURI,
		success : handler
	});
	return false;
}

function restoreNews(xml) {
	populateSingleNews(xml);
	restore();
}

function populateSingleNews(xml) {
	var tmpTxt = $(xml).find(CATEGORY).first().text();
	var desc = $(xml).find(DESCR).first().text();
	var category = CAT_ + tmpTxt;
	var categoryDel = category + _D;
	var categoryLi = categoryDel + _LI;
	var categoryA = category + _A;

	var currentNewsVar = $('#currentNews');
	
	$(HTML_FRG1 + categoryLi + HTML_FRG2 + categoryA + HTML_FRG3 + desc
				+ HTML_FRG4 + category + HTML_FRG5 + categoryDel
				+ HTML_FRG6).prependTo(currentNewsVar);

}

function hideCategories() {
	hdrCategoriesVar.hide();
	contentCategoriesVar.hide();
	ftrCategoriesVar.hide();
}

function hideSelect() {
	hdrSelectVar.hide();
	contentSelectVar.hide();
	ftrSelectVar.hide();
}

function hideNews() {
	hdrNewsVar.hide();
	contentNewsVar.hide();
	ftrNewsVar.hide();
}

function hideProgress() {
	hdrProgressVar.hide();
	contentProgressVar.hide();
	ftrProgressVar.hide();
}

function showSelect() {
	hideCategories();
	hideProgress();
	hideNews();
	hdrSelectVar.show();
	contentSelectVar.show();
	ftrSelectVar.show();
}

function showNews() {
	hideCategories();
	hideSelect();
	hideProgress();
	hdrNewsVar.show();
	contentNewsVar.show();
	ftrNewsVar.show();
}

function showProgress() {
	hideCategories();
	hideSelect();
	hideNews();
	hdrProgressVar.show();
	contentProgressVar.show();
	ftrProgressVar.show();
}