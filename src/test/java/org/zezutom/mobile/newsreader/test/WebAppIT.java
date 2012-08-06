package org.zezutom.mobile.newsreader.test;

import static org.jboss.arquillian.ajocado.Graphene.jq;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.net.URL;

import org.jboss.arquillian.ajocado.Graphene;
import org.jboss.arquillian.ajocado.framework.GrapheneSelenium;
import org.jboss.arquillian.ajocado.locator.JQueryLocator;
import org.jboss.arquillian.ajocado.locator.option.OptionIdLocator;
import org.jboss.arquillian.ajocado.locator.option.OptionValueLocator;
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

	private static final JQueryLocator DEFAULT_CATEGORY = jq("li:contains('Latest News')");
	
	private static final JQueryLocator ADD_CATEGORY_LINK = jq("#add_category_link");
	
	private static final JQueryLocator HOME_PAGE = jq("#home");
	
	private static final JQueryLocator ADD_CATEGORY_PAGE = jq("#category_add");

	private static final JQueryLocator CATEGORY_LIST = jq("#category_select");
	
	private static final JQueryLocator ADD_CATEGORY_BUTTON = jq("#category_add_button");
	
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
		assertElementVisible(HOME_PAGE, "Home page should be displayed by default");
		assertElementExists(DEFAULT_CATEGORY, "Default news category should be present");
	}

	@Test
	@InSequence(2)
	public void should_open_page_to_add_category() {
		assertElementExists(ADD_CATEGORY_LINK, "Add category button should be present");
		browser.highlight(ADD_CATEGORY_LINK);
		browser.click(ADD_CATEGORY_LINK);
		browser.waitForCondition(Graphene.elementVisible.locator(ADD_CATEGORY_PAGE).getJavaScriptCondition(), 2000);
		assertElementVisible(ADD_CATEGORY_PAGE, "Add Category page should be visible when the Add button is pressed");
	}

	@Test
	@InSequence(3)	
	public void should_list_all_categories() {
		assertElementVisible(CATEGORY_LIST, "A list of categories should be available when a category is being added");
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
		browser.highlight(ADD_CATEGORY_BUTTON);
		browser.click(ADD_CATEGORY_BUTTON);
		browser.waitForCondition(Graphene.elementVisible.locator(HOME_PAGE).getJavaScriptCondition(), 2000);
		assertElementVisible(jq("li:contains('Top Stories')"), "New category should be present on the list.");		
	}
	
	private void assertElementExists(JQueryLocator element, String message) {
		assertTrue(message, Graphene.elementPresent.locator(element)
				.isTrue());
	}

	private void assertElementVisible(JQueryLocator element, String message) {
		assertTrue(message, Graphene.elementVisible.locator(element)
				.isTrue());
	}
	
}
