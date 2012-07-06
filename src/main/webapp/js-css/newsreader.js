var NEWS_URL = 'news';

function init() {
	$("#add_category_button").bind('click', function() {
		
		var result = false;
		var category = $("#category_select").val();
		
		if(category != '') {
			result = getNews(category, addNews)
		}

		return result;
	});
}

function addNews(json) {
	alert('json');
	$("#home").show();
}

function getNews(category, handler) {
	var url = NEWS_URL + '?category=' category;
	$.ajax({type: 'GET', dataType: 'json', url: url, success: handler});
	return false;
}