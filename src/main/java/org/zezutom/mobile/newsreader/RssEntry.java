package org.zezutom.mobile.newsreader;


public class RssEntry {
	
    private String title;
    
    private String author;
    
    private String publishedDate;
    
    private String description;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getPublishedDate() {
		return publishedDate;
	}

	public void setPublishedDate(String publishedDate) {
		this.publishedDate = publishedDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public static RssEntry createErrorEntry(Exception ex) {
		RssEntry entry = new RssEntry();
		entry.setTitle("Error");
		entry.setAuthor("System");
		entry.setDescription(ex.getMessage());
		
		return entry;
	}
}
