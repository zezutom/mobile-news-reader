package org.zezutom.mobile.newsreader;

import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

@ManagedBean
@RequestScoped
public class RssReader {

	public static final String FEED_URL = "http://rss.news.yahoo.com/rss/";

	public static final int MAX_COUNT = 10;

	public static final String DATE_FORMAT = "EEEE MMMM dd, yyyy HH:mm:ss";
	
	public List<RssEntry> getFeeds() {
		ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
		Map<String, String> model = context.getRequestParameterMap(); 
		String category = model.get("category");
		
		return getFeeds(getFeedUrl(category), MAX_COUNT);
	}
	
	private String getFeedUrl(String category) {
		StringBuilder sb = new StringBuilder(FEED_URL);

		if (category != null && !category.isEmpty()) {
			sb.append(category);
		}

		return sb.toString();
	}

	private List<RssEntry> getFeeds(String url, int count) {
		List<RssEntry> feeds = new ArrayList<RssEntry>();

		DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

		try {
			URLConnection feedUrl = new URL(url).openConnection();
			SyndFeedInput input = new SyndFeedInput();

			// Populate data
			SyndFeed feed = input.build(new XmlReader(feedUrl));

			List feedList = feed.getEntries();
			int size = feedList.size();

			if (size > count) {
				size = count;
			}

			for (int i = 0; i < size; i++) {
				SyndEntry syndEntry = (SyndEntry) feedList.get(i);

				// Custom format
				RssEntry rssEntry = new RssEntry();

				rssEntry.setLink(syndEntry.getLink());
				rssEntry.setTitle(syndEntry.getTitle());
				rssEntry.setAuthor(syndEntry.getAuthor());
				rssEntry.setDescription(syndEntry.getDescription().getValue());

				Date published = syndEntry.getPublishedDate();
				rssEntry.setPublishedDate(dateFormat.format(published));

				feeds.add(rssEntry);
			}

		} catch (Exception ex) {
			feeds.add(RssEntry.createErrorEntry(ex));
		}
		
		return feeds;
	}

}
