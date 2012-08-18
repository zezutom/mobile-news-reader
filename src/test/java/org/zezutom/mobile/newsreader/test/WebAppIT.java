package org.zezutom.mobile.newsreader.test;

import static org.jboss.arquillian.ajocado.Graphene.jq;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import org.jboss.arquillian.ajocado.Graphene;
import org.jboss.arquillian.ajocado.framework.GrapheneSelenium;
import org.jboss.arquillian.ajocado.locator.JQueryLocator;
import org.jboss.arquillian.ajocado.locator.option.OptionValueLocator;
import org.jboss.arquillian.ajocado.network.NetworkTraffic;
import org.jboss.arquillian.ajocado.network.NetworkTrafficType;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class WebAppIT {

	public static final String WEBAPP_SRC = "src/main/webapp/";

	public static final String LIB_NAME = "lib/%s.jar";

	public static final String IMAGE_NAME = "target/failsafe-reports/%s.png";
	
	public static final String PNG = "PNG";
	
	public static final String ELEMENT_NOT_FOUND = "No such element found: %s";
	
	public static final String ELEMENT_NOT_REMOVED = "The element should have been removed: %s";

	// How many milliseconds are we willing to wait for something to happen
	public static final int PATIENCE = 6000;
	
	// How many news items should be displayed
	public static final int NEWS_COUNT = 10;
	
	private static final JQueryLocator DEFAULT_CATEGORY = jq("li:contains('Latest News')");
	
	private static final JQueryLocator DEFAULT_CATEGORY_LINK = jq("a:contains('Latest News')");
		
	private static final JQueryLocator ADD_CATEGORY_LINK = jq("#add_category_link");
	
	private static final JQueryLocator HOME_PAGE = jq("#home");
	
	private static final JQueryLocator NEWS_PAGE = jq("#news");
	
	private static final JQueryLocator CATEGORY_DELETE_PAGE = jq("#category_delete");
	
	private static final JQueryLocator ADD_CATEGORY_PAGE = jq("#category_add");

	private static final JQueryLocator CATEGORY_LIST = jq("#category_select");
	
	private static final JQueryLocator ADD_CATEGORY_BUTTON = jq("#category_add_button");
	
	private static final JQueryLocator DELETE_CATEGORY_FORM = jq("#delete_category");
	
	private static final JQueryLocator CANCEL_CATEGORY_DELETION_BUTTON = jq("#cancel_category_deletion");
	
	private static final JQueryLocator CONFIRM_CATEGORY_DELETION_BUTTON = jq("#confirm_category_deletion");
	
	private static final JQueryLocator TOP_STORIES_CATEGORY = jq("li:contains('Top Stories')");
	
	private static final JQueryLocator TOP_STORIES_DELETE_LINK = jq("li:contains('Top Stories') > a[href^='#category_delete']");
	
	private static final JQueryLocator NEWS_LIST = jq("#news_list");
	
	private static final JQueryLocator NEWS_ITEMS = jq("#news_list > li");
	
	private static final JQueryLocator FIRST_NEWS_ITEM = jq("#news_list li:first-child");
	
	private static final String[] CATEGORIES = {"topstories", "us", "world", "politics", "business", "stocks", "economy", "eurobiz"};
	
	@ArquillianResource
	private URL deploymentUrl;

	@Drone
	private GrapheneSelenium browser;

	@Deployment(testable = false)
	public static WebArchive deploy() {
		return ShrinkWrap.create(ZipImporter.class, "newsreader.war")
				.importFrom(new File("target/newsreader.war"))
				.as(WebArchive.class);
	}

	@Test
	@InSequence(1)
	public void should_have_default_category() {
		browser.open(deploymentUrl);
		assertElementVisible(HOME_PAGE);
		assertElementVisible(DEFAULT_CATEGORY);		
	}

	@Test
	@InSequence(2)
	public void should_open_page_to_add_category() {
		assertElementVisible(ADD_CATEGORY_LINK);
		browser.highlight(ADD_CATEGORY_LINK);
		browser.click(ADD_CATEGORY_LINK);
		browser.waitForCondition(Graphene.elementVisible.locator(ADD_CATEGORY_PAGE).getJavaScriptCondition(), 2000);
		assertElementVisible(ADD_CATEGORY_PAGE);
	}

	@Test
	@InSequence(3)	
	public void should_list_all_categories() {
		assertElementVisible(CATEGORY_LIST);
		for (String category : CATEGORIES) {
			browser.select(CATEGORY_LIST, new OptionValueLocator(category));
			browser.isSomethingSelected(CATEGORY_LIST);
			browser.isVisible(ADD_CATEGORY_BUTTON);
		}
	}

	@Test
	@InSequence(4)		
	public void should_append_new_category() {
		browser.select(CATEGORY_LIST, new OptionValueLocator(CATEGORIES[0]));
		assertElementVisible(ADD_CATEGORY_BUTTON);
		browser.click(ADD_CATEGORY_BUTTON);
		waitForPage(HOME_PAGE);
		assertElementVisible(TOP_STORIES_CATEGORY);		
	}
	
	@Test
	@InSequence(5)
	public void should_present_confirmation_dialogue_to_delete_category() {		
		assertElementVisible(TOP_STORIES_DELETE_LINK);
		browser.click(TOP_STORIES_DELETE_LINK);
		waitForPage(CATEGORY_DELETE_PAGE);
		assertElementVisible(DELETE_CATEGORY_FORM);
		
	}
	
	@Test
	@InSequence(6)
	public void should_be_able_to_cancel_category_deletion() {
		assertElementVisible(CANCEL_CATEGORY_DELETION_BUTTON);
		browser.click(CANCEL_CATEGORY_DELETION_BUTTON);
		waitForPage(HOME_PAGE);
		assertElementVisible(TOP_STORIES_CATEGORY);
	}

	@Test
	@InSequence(7)	
	public void should_be_able_to_confirm_category_deletion() {
		assertElementVisible(TOP_STORIES_DELETE_LINK);
		browser.click(TOP_STORIES_DELETE_LINK);
		waitForPage(CATEGORY_DELETE_PAGE);
		assertElementVisible(CONFIRM_CATEGORY_DELETION_BUTTON);
		browser.click(CONFIRM_CATEGORY_DELETION_BUTTON);
		waitForPage(HOME_PAGE);
		captureScreenshot("home_page_after_removal");
		assertElementNotPresent(TOP_STORIES_CATEGORY);
	}
	
	@Test
	@InSequence(8)
	public void should_show_news() {
		browser.click(DEFAULT_CATEGORY_LINK);
		waitForNewsPage();
		assertThat(browser.getCount(NEWS_ITEMS), is(NEWS_COUNT));
		assertNewsItem(FIRST_NEWS_ITEM);		
		captureScreenshot("news_page");		
	}
	
	private void assertElementVisible(JQueryLocator element) {
		assertTrue(String.format(ELEMENT_NOT_FOUND, getElementDescription(element)), Graphene.elementVisible.locator(element).isTrue());
		browser.highlight(element);
	}

	private void assertElementNotPresent(JQueryLocator element) {
		assertTrue(String.format(ELEMENT_NOT_REMOVED, getElementDescription(element)), Graphene.elementNotVisible.locator(element).isTrue());
	}
		
	private void assertNewsItem(JQueryLocator item) {
		browser.highlight(item);
		//browser.highlight(item.getChild(jq("a")));
		//browser.highlight(item.getChild(jq("div")));		
	}
	
	private void waitForPage(JQueryLocator page) {
		browser.waitForCondition(Graphene.elementVisible.locator(page).getJavaScriptCondition(), PATIENCE);		
	}

	private void waitForNewsPage() {
		browser.waitForPageToLoad(PATIENCE);
		browser.waitForCondition(Graphene.elementPresent.locator(NEWS_PAGE).getJavaScriptCondition(), PATIENCE);
		browser.waitForCondition(Graphene.elementVisible.locator(NEWS_LIST).getJavaScriptCondition(), PATIENCE);				
	}
	
	private String getElementDescription(JQueryLocator element) {
		return element.getRawLocator();
	}
	
	private void captureScreenshot(String name) {
		File output = new File(String.format(IMAGE_NAME, name));
		try {
			ImageIO.write(browser.captureScreenshot(), PNG, output);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
