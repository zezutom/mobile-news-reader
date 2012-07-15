var categories = new Array();
var categoryListItem = '<li><a href="news.xhtml?category=${code}">${title}</a></li>';
var defaultCategory = '<li><a href="news.xhtml">Latest News</a></li>';

function initCategories() {
	if (!_isHome()) {
		return;
	}
	
	var categoryList = $('#category_list');
	categoryList.html(null);
	categoryList.append(defaultCategory);
	
	$.each(_getCategories(), function(index, category) {
		var item = categoryListItem
		.replace('${code}', category.code)
		.replace('${title}', category.title);
		categoryList.append(item);		
	});
	categoryList.listview('refresh');	
}

function initCategoryEditor() {
	$('#category_add_button').bind('click', function() {
		var selection = $('#category_select option:selected');
		// TODO: add sanity checks
		_addCategory(selection.val(), selection.text());		
		$.mobile.changePage('index.htm');		
	});
}

function _addCategory(code, title) {
	var entry = code + ':' + title;
	var categories = _getCookie();
	
	if (categories) {
		if (new RegExp(entry).test(categories)) {
			return;
		}
		categories += ';';
	}
	else {
		categories = '';
	}
	categories += entry;
	$.cookie('categories', categories);	
}

function _getCategories() {
	var cookie = _getCookie();
	
	if (!cookie) {
		return new Array();
	}
	
	var chunks = cookie.split(';');
	var categories = new Array();
	
	$.each(chunks, function(index, chunk) {
		if (!chunk) {
			return;
		}
		var parts = chunk.split(':');
		
		if (!parts || parts.length != 2) {
			return;
		}
		
		categories.push({'code': parts[0], 'title': parts[1]});
	}); 
	return categories;
}

function _getCookie() {
	return categories = $.cookie('categories');
}
function _isHome() {
	return $(location).attr('pathname').match(/index.htm$/g);
}