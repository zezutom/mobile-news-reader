var categories = new Array();
var categoryListItem = '<li><a href="news.htm?category=code">title</a><a href="#delete_category?category=code" data-rel="dialog">Delete</a></li>';
var defaultCategory = '<li><a href="news.htm">Latest News</a></li>';
var HOME_PAGE = /^#home/;
var DELETE_CATEGORY_PAGE = /^#delete_category/;
var firstTime = true;

function changePage(e, data) {
	var page = data.toPage;
	var url = $.mobile.path.parseUrl(page);
	var hash = url.hash;
	
	if (!hash || _isPage(hash, HOME_PAGE)) {
		initCategories();
	}
	else if (_isPage(hash, DELETE_CATEGORY_PAGE)) {
		_loadDeleteCategoryDialogue(url);
	}
	
	if (firstTime) {
		_initDeleteMenu();		
		firstTime = false;
	}
	_initCategoryEditor();
}

function _isPage(hash, page) {
	return hash.search(page) !== -1;
}

function _initDeleteMenu() {
	$('#delete_category').live('submit', function(e, data) {
		_deleteCategory($('#category_code').val());
		$.mobile.changePage("#home");
	});
}
function initCategories() {	
	var $page = $('#home');
	var $content = $page.children(':jqmData(role=content)');
	var ul = '<ul id="category_list" data-role="listview" data-split-icon="delete">';
	
	ul += defaultCategory;	
	$.each(_getCategories(), function(index, category) {
		var data = category.split(':');
		
		if (!data || data.length != 2) {
			return;
		}
		
		var item = categoryListItem
		.replace(/code/g, data[0])
		.replace(/title/g, data[1]);
		ul += item;				
	});	
	ul += '</ul>';
	
	$content.html(ul);
	$page.page();
	$content.find(':jqmData(role=listview)').listview();
	
}

function _initCategoryEditor() {
	$('#category_add_button').bind('click', function() {
		var selection = $('#category_select option:selected');
		// TODO: add sanity checks
		_addCategory(selection.val(), selection.text());		
		$.mobile.changePage('index.htm');		
	});
}

function _loadDeleteCategoryDialogue(url) {
	// extract category parameter
	var category = decodeURIComponent(url.hash.replace(/.*category=/, ""));
	
	// get the page requesting the deletion (home page is the only case at the moment)
	var pageId = url.hash.replace(/\?.*$/, "");
	
	// get handle of the page
	var $page = $(pageId);
	
	// access the content
	var $content = $page.children(":jqmData(role=content)");
	
	$content.find("#category_code").val(category);
	$content.find("#category_prompt").html(category);
	
	$page.page();
	
}

function _existsCategory(category) {
	var exists = false;
	var categories = _getCategories();
	
	for (var i=0; i<categories.length; i++) {
		if (categories[i] === category) {
			exists = true;
			break;
		}
	}
	return exists;
}

function _findCategory(code) {
	var index = -1;
	var categories = _getCategories();
	
	for (var i=0; i<categories.length; i++) {
		if (categories[i].match('^' + code)) {
			index = i;
			break;
		}
	}
	return index; 	
}

function _updateCategories(categories) {
	localStorage.setItem('categories', JSON.stringify(categories));
}

function _addCategory(code, title) {
	var category = code + ':' + title;
	
	if (_existsCategory(category)) {
		return;
	}
	
	var categories = _getCategories();
	categories.push(category);
	_updateCategories(categories);
}

function _deleteCategory(code) {
	var categories = _getCategories();
	var index = _findCategory(code);
	
	if (index !== -1) {
		categories.splice(index, 1);
		_updateCategories(categories);
	}
}

function _getCategories() {
	var categories = localStorage.getItem('categories');
	
	if (categories) {
		categories = JSON.parse(categories);
	}
	else {
		categories = new Array();
	}
	return categories;
}

function _getCookie() {
	return categories = $.cookie('categories');
}
function _isHome() {
	return _locationMatches(['\/$', 'index.htm$'])
}

function _isDeleteCategory() {
	return false;
}

function _locationMatches(patterns) {
	var path = $(location).attr('pathname');
	var matches = false;
	
	for (var i=0; i<patterns.length; i++) {
		if (path.match(new RegExp(patterns[i]))) {
			matches = true;
			break;
		}
	}
	
	return matches;
}