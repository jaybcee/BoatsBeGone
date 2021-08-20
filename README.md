# BoatsBeGoneV2

This is the second iteration of BoatsBeGone. A simple Facebook Messenger bot that notifies users of the Victoria Bridge bike path status.
This iteration has fewer features due to the fact less data is available from the St-Lawrence Seaway System.

In order to use it, users must be added as testers due to Facebook limitations. The library used for messaging supports Slack somewhat interchangeably and hence migrating to the platform would be possible and not require a formal facebook approval process for use by the general public.


Users can send:
<ul>
<li>Status -> Retrieve the current status of the bridge.</li>
<li>Start -> Receive real time notifications about the bridges status (Available, soon to be unavailable, Unavailable) for 2 hours.</li>
<li>Stop -> Removes users from list of users to notify.</li>
</ul>

The application is written in Kotlin atop of the Spring Boot framework. It is deployed to Azure App Service.

One must set up their respective Messenger platforms by following [these instructions](https://developers.facebook.com/docs/messenger-platform/getting-started/).
Making sure to setup `Webhooks` and `Messaging`.

Environment variables of `FB_ACCESS_TOKEN` and `FB_SECRET` must be set.

In order to deploy to Azure, an App Service for Java 11 must be created.
You must set `AZURE_SUBSCRIPTION_ID`, `AZURE_RESOURCE_GROUP`, `AZURE_APP_NAME`.
More information [here](https://github.com/microsoft/azure-gradle-plugins).


