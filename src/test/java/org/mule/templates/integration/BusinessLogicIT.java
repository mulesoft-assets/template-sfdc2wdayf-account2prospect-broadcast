/**
 * Mule Anypoint Template
 * Copyright (c) MuleSoft, Inc.
 * All rights reserved.  http://www.mulesoft.com
 */

package org.mule.templates.integration;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mule.MessageExchangePattern;
import org.mule.api.MuleEvent;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.processor.chain.SubflowInterceptingChainLifecycleWrapper;
import org.mule.templates.builders.SfdcObjectBuilder;

import com.workday.revenue.GetProspectsResponseType;

/**
 * The objective of this class is to validate the correct behavior of the flows
 * for this Mule Template that make calls to external systems.
 */
public class BusinessLogicIT extends AbstractTemplateTestCase {
	protected static final int TIMEOUT_SEC = 60;

	private String SFDC_TEST_ACCOUNT_ID;

	private static final String PATH_TO_TEST_PROPERTIES = "./src/test/resources/mule.test.properties";

	private static SubflowInterceptingChainLifecycleWrapper retrieveProspectFromWorkdayFlow;
	private static SubflowInterceptingChainLifecycleWrapper updateAccountInSalesforceFlow;

	@BeforeClass
	public static void init() {
		System.setProperty("page.size", "200");
		System.setProperty("poll.frequencyMillis", "5000");
		System.setProperty("poll.startDelayMillis", "0");
		System.setProperty("watermark.default.expression",
				"#[groovy: new Date(System.currentTimeMillis() - 10000).format(\"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'\", TimeZone.getTimeZone('UTC'))]");
	}

	@Before
	public void setUp() throws Exception {
		final Properties props = new Properties();
		try {
			props.load(new FileInputStream(PATH_TO_TEST_PROPERTIES));
		} catch (Exception e) {
			logger.error("Error occured while reading mule.test.properties", e);
		}
		SFDC_TEST_ACCOUNT_ID = props.getProperty("sfdc.testaccount.id");

		stopFlowSchedulers(POLL_FLOW_NAME);
		registerListeners();

		getAndInitializeFlows();
	}

	/**
	 * Initialise all the test flows
	 * 
	 * @throws InitialisationException
	 */
	private void getAndInitializeFlows() throws InitialisationException {
		// Flow for retrieving prospect from Workday instance
		retrieveProspectFromWorkdayFlow = getSubFlow("retrieveProspectFlow");
		retrieveProspectFromWorkdayFlow.initialise();

		// Flow for updating account in Salesforce instance
		updateAccountInSalesforceFlow = getSubFlow("updateAccountFlow");
		updateAccountInSalesforceFlow.initialise();
	}

	/**
	 * Generates unique website based on current time.
	 * 
	 * @return String - Website
	 */
	public String generateUniqueWebsite() {
		return "http://test." + System.currentTimeMillis() + ".com";
	}

	/**
	 * Generates unique name based on current time.
	 * 
	 * @return String - name
	 */
	public String generateUniqueName() {
		return "Test" + System.currentTimeMillis();
	}

	/**
	 * Performs update on the SFDC test account.
	 * 
	 * @param name
	 *            String - new name
	 * @param website
	 *            String - new website
	 * @throws Exception
	 */
	public void editTestDataInSandbox(String name, String website) throws Exception {
		// create object to edit the test account with
		Map<String, Object> account = SfdcObjectBuilder.anAccount().with("Id", SFDC_TEST_ACCOUNT_ID).with("Name", name)
				.with("Website", website)
				.with("BillingPostalCode", "90210")
				.with("Phone", "123-4567")
				.build();
		List<Map<String, Object>> payload = new ArrayList<>();
		payload.add(account);

		// run the flow
		updateAccountInSalesforceFlow.process(getTestEvent(payload, MessageExchangePattern.REQUEST_RESPONSE));
	}

	@After
	public void tearDown() throws Exception {
		stopFlowSchedulers(POLL_FLOW_NAME);
	}

	/**
	 * Tests if update of a SFDC test Account results in Workday Prospect update
	 * 
	 * @throws Exception
	 */
	@Test
	public void testMainFlow() throws Exception {
		// edit test data
		String name = generateUniqueName();
		String website = generateUniqueWebsite();
		editTestDataInSandbox(name, website);

		// Run poll and wait for it to run
		runSchedulersOnce(POLL_FLOW_NAME);
		waitForPollToRun();

		Thread.sleep(TIMEOUT_SEC * 1000);

		// Execute retrieveProspectFlow sublow
		final MuleEvent event = retrieveProspectFromWorkdayFlow.process(getTestEvent(SFDC_TEST_ACCOUNT_ID,
				MessageExchangePattern.REQUEST_RESPONSE));
		final GetProspectsResponseType response = (GetProspectsResponseType) event.getMessage().getPayload();

		// Account previously created in Salesforce should be present in Workday
		// assertions
		assertEquals("Workday should return one result", 1, response.getResponseResults().getTotalResults().intValue());
		assertEquals("The name should be the same", response.getResponseData().getProspect().get(0).getProspectData()
				.getProspectName(), name);
		assertEquals("The website should be the same", response.getResponseData().getProspect().get(0).getProspectData()
				.getContactData().getWebAddressData().get(0).getWebAddress(), website);
		assertEquals("The phone should be the same", response.getResponseData().getProspect().get(0).getProspectData()
				.getContactData().getPhoneData().get(0).getPhoneNumber(), "123-4567");
		assertEquals("The postal code should be the same", response.getResponseData().getProspect().get(0).getProspectData()
				.getContactData().getAddressData().get(0).getPostalCode(), "90210");
	}
}
