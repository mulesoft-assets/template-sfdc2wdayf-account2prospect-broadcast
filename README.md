
# Anypoint Template: Salesforce to Workday Financials Account to Prospect Broadcast	

<!-- Header (start) -->
Broadcasts new accounts or updates to existing accounts from Salesforce to Workday Financials as Prospects in real time. This template ensures that customer account information in Workday Financials module stays updated to the changes in Salesforce. The detection criteria and fields to integrate are configurable. Additional systems can be added to be notified of the changes. Real time synchronization is achieved either via rapid polling of Salesforce or Outbound Notifications to reduce the number of API calls. 

This template uses watermarking to ensure that only the most recent items are synchronized and batch to efficiently process many records at a time.

![4d81d27e-44e2-4b3e-8b13-b7ee6231c05d-image.png](https://exchange2-file-upload-service-kprod.s3.us-east-1.amazonaws.com:443/4d81d27e-44e2-4b3e-8b13-b7ee6231c05d-image.png)

<!-- Header (end) -->

# License Agreement
This template is subject to the conditions of the <a href="https://s3.amazonaws.com/templates-examples/AnypointTemplateLicense.pdf">MuleSoft License Agreement</a>. Review the terms of the license before downloading and using this template. You can use this template for free with the Mule Enterprise Edition, CloudHub, or as a trial in Anypoint Studio. 
# Use Case
<!-- Use Case (start) -->
As a Salesforce admin I want to synchronize accounts and prospects between Salesforce and Workday.

This template helps you synchronize accounts from a Salesforce instance to prospects in a Workday instance. Each time there is a new account or a change in an already existing one with type=Prospect, the integration polls for changes in the Salesforce source instance and updates the prospect in the target Workday instance.

This template provides examples and can be a starting point to adapt your integration to your requirements.

As implemented, this template leverage the Mule batch module The batch job is divided in Process and On Complete stages.

* The integration starts by a poll defined in the flow that triggers the application, queries for the newest Salesforce updates or creates that match a filter criteria, and executes the batch job.
* During the Process stage, each Salesforce account is checked to see if it has an existing matching prospect in the Workday instance.
* The last step of the Process stage groups the accounts and inserts or updates them in Workday.
* Finally during the On Complete stage the template logs output statistics data to the console.
<!-- Use Case (end) -->

# Considerations
<!-- Default Considerations (start) -->

<!-- Default Considerations (end) -->

<!-- Considerations (start) -->
To make this template run, there are certain preconditions that must be considered. All of them deal with the preparations in both source (Salesforce) and destination (Workday) systems, that must be made for the template to run smoothly. Failing to do so can lead to unexpected behavior of the template.

This template requires a Workday instance to work.
<!-- Considerations (end) -->

## Salesforce Considerations

- Where can I check that the field configuration for my Salesforce instance is the right one? See: <a href="https://help.salesforce.com/HTViewHelpDoc?id=checking_field_accessibility_for_a_particular_field.htm&language=en_US">Salesforce: Checking Field Accessibility for a Particular Field</a>.
- Can I modify the Field Access Settings? How? See: <a href="https://help.salesforce.com/HTViewHelpDoc?id=modifying_field_access_settings.htm&language=en_US">Salesforce: Modifying Field Access Settings</a>.

### As a Data Source

If a user who configures the template for the source system does not have at least *read only* permissions for the fields that are fetched, then an *InvalidFieldFault* API fault displays.

```
java.lang.RuntimeException: [InvalidFieldFault [ApiQueryFault 
[ApiFault  exceptionCode='INVALID_FIELD'
exceptionMessage='Account.Phone, Account.Rating, Account.RecordTypeId, 
Account.ShippingCity
^
ERROR at Row:1:Column:486
No such column 'RecordTypeId' on entity 'Account'. If you are 
attempting to use a custom field, be sure to append the '__c' 
after the custom field name. Reference your WSDL or the describe 
call for the appropriate names.'
]
row='1'
column='486'
]
]
```

## Workday Considerations

### As a Data Destination

There are no considerations with using Workday as a data destination.

## Workday Financials Considerations

### As a Data Destination

There are no considerations with using Workday Financials as a data destination.

# Run it!
Simple steps to get this template running.
<!-- Run it (start) -->
See below.
<!-- Run it (end) -->

## Running On Premises
In this section we help you run this template on your computer.
<!-- Running on premise (start) -->

<!-- Running on premise (end) -->

### Where to Download Anypoint Studio and the Mule Runtime
If you are new to Mule, download this software:

+ [Download Anypoint Studio](https://www.mulesoft.com/platform/studio)
+ [Download Mule runtime](https://www.mulesoft.com/lp/dl/mule-esb-enterprise)

**Note:** Anypoint Studio requires JDK 8.
<!-- Where to download (start) -->

<!-- Where to download (end) -->

### Importing a Template into Studio
In Studio, click the Exchange X icon in the upper left of the taskbar, log in with your Anypoint Platform credentials, search for the template, and click Open.
<!-- Importing into Studio (start) -->

<!-- Importing into Studio (end) -->

### Running on Studio
After you import your template into Anypoint Studio, follow these steps to run it:

1. Locate the properties file `mule.dev.properties`, in src/main/resources.
2. Complete all the properties required per the examples in the "Properties to Configure" section.
3. Right click the template project folder.
4. Hover your mouse over `Run as`.
5. Click `Mule Application (configure)`.
6. Inside the dialog, select Environment and set the variable `mule.env` to the value `dev`.
7. Click `Run`.
<!-- Running on Studio (start) -->

<!-- Running on Studio (end) -->

### Running on Mule Standalone
Update the properties in one of the property files, for example in mule.prod.properties, and run your app with a corresponding environment variable. In this example, use `mule.env=prod`. 

## Running on CloudHub
When creating your application in CloudHub, go to Runtime Manager > Manage Application > Properties to set the environment variables listed in "Properties to Configure" as well as the mule.env value.
<!-- Running on Cloudhub (start) -->
While creating your application on CloudHub (or you can do it later as a next step), you need to go to Deployment > Advanced to set all environment variables detailed in "Properties to Configure" as well as setting the mule.env value. 
After your application is set up and started, there is no need to do anything else. Each time an account is created or modified, it is automatically synchronized to your Workday instance.
<!-- Running on Cloudhub (end) -->

### Deploying a Template in CloudHub
In Studio, right click your project name in Package Explorer and select Anypoint Platform > Deploy on CloudHub.
<!-- Deploying on Cloudhub (start) -->

<!-- Deploying on Cloudhub (end) -->

## Properties to Configure
To use this template, configure properties such as credentials, configurations, etc.) in the properties file or in CloudHub from Runtime Manager > Manage Application > Properties. The sections that follow list example values.
### Application Configuration
<!-- Application Configuration (start) -->
**Application Configuration**

- page.size `200`

- scheduler.frequency= `60000`
- scheduler.start.delay= `500`
- watermark.default.expression `(now() - |PT5M|) as String` or `2018-01-02T15:53:00Z`

**Salesforce Connector Configuration**

- sfdc.username `joan.baez@orgb`
- sfdc.password `JoanBaez456`
- sfdc.securityToken `ces56arl7apQs56XTddf34X`
- sfdc.url `https://login.salesforce.com/services/Soap/u/32.0`

**Workday Connector Configuration**

- wdayf.user `wdayf_user`
- wdayf.password `wdayf_password`
- wdayf.endpoint `https://{your Workday domain}/ccx/service/{your tenant name}/Revenue_Management/v23.2`

- wdayf.country `USA`
- wdayf.state `USA-CA`
- wdayf.postalCode `90001`
- wdayf.city `San Francisco`
- wdayf.street `Main Street 123`
- wdayf.phone `123-4567`
<!-- Application Configuration (end) -->

# API Calls
<!-- API Calls (start) -->
Salesforce imposes limits on the number of API calls that can be made. However, we make API call to Salesforce only once during migration, so this is not something to worry about.
<!-- API Calls (end) -->

# Customize It!
This brief guide provides a high level understanding of how this template is built and how you can change it according to your needs. As Mule applications are based on XML files, this page describes the XML files used with this template. More files are available such as test classes and Mule application files, but to keep it simple, we focus on these XML files:

* config.xml
* businessLogic.xml
* endpoints.xml
* errorHandling.xml
<!-- Customize it (start) -->
<!-- Customize it (end) -->

## config.xml
<!-- Default Config XML (start) -->
This file provides the configuration for connectors and configuration properties. Only change this file to make core changes to the connector processing logic. Otherwise, all parameters that can be modified should instead be in a properties file, which is the recommended place to make changes.
<!-- Default Config XML (end) -->
<!-- Config XML (start) -->
<!-- Config XML (end) -->

## businessLogic.xml
<!-- Default Business Logic XML (start) -->
The functional aspect of this template is implemented in this XML file, directed by a flow that  polls for Salesforce creates or updates. The several message processors constitute four high level actions that fully implement the logic of this template:

1. The template goes to Salesforce and queries all existing accounts that match the filter criteria.
2. During the Process stage, each Salesforce Account ID is checked in an external ID field in Workday (Prospect_Reference_ID), if it has an existing matching object in Workday.
3. The template upserts the relevant prospect fields in Workday.
4. Finally during the On Complete stage, the template logs output statistics data to the console.
<!-- Default Business Logic XML (end) -->
<!-- Business Logic XML (start) -->
<!-- Business Logic XML (end) -->

## endpoints.xml
<!-- Default Endpoints XML (start) -->
This file contains a flow with a poll that periodically queries Salesforce for account updates or creates that meet the defined criteria in the query and then executes the batch job process with the query results.
<!-- Default Endpoints XML (end) -->
<!-- Endpoints XML (start) -->
<!-- Endpoints XML (end) -->

## errorHandling.xml
<!-- Default Error Handling XML (start) -->
This file handles how your integration reacts depending on the different exceptions. This file provides error handling that is referenced by the main flow in the business logic.
<!-- Default Error Handling XML (end) -->
<!-- Error Handling XML (start) -->
<!-- Error Handling XML (end) -->
<!-- Extras (start) -->
<!-- Extras (end) -->
