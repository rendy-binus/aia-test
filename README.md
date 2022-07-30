# Simple Coding Test for AIA SG Java Backend - 1

### Test Details

1. Grab the everchanging public image list
   from [Flickr public feed](https://api.flickr.com/services/feeds/photos_public.gne).
   You can check the documentation from this [API Doc](http://www.flickr.com/services/feeds/).
2. Use a public github repository to put your code. If you don't have one, just make one! Also, make use of good source
   versioning.
   Commit often, and commit meaningfully.
3. Create the endpoint that will use the data you get from that Flickr public feed API. Like, searching, or paging. Make
   sure the API Endpoint is not directly using the Flickr one(use your own API), and make sure it can be consumed from
   different URL (CORS-safe).
4. Bonus point if you can save the pulled data from Flickr periodically to your database(be it mysql, noSQL or any other
   storage) by an API Calling. So, there's one API to save the data from Flickr, and another API to pull the data, with
   different parameters.
5. Optional one (only after doing the bonus point) : Create API Endpoint that can pull the specific data (like, specific
   tags, date, etc), and other that can save the specific data from Flickr to your database only. Also, it's good if you
   are able to create one more endpoint to clear all the data pulled.
6. Expected result is JAR or WAR that can be deployed on Tomcat/JBOSS.
   Deployment is optional. If you can deploy to public/private distribution, the better.
   If you can't deploy your app, it's okay! Just make sure that you can demo it to us when we do the interview.

## Implementations

### Endpoint

| URI           | HTTP Method  |
|:--------------|:------------:|
| /api/v1/posts |     GET      |

| Key           | Mandatory | Default Value | Description                                                        |
|:--------------|:---------:|:-------------:|--------------------------------------------------------------------|
| tagMode       |    No     |      ANY      | TagMode[ALL = all tags has to be present, ANY = any available tag] |
| tags          |    No     | empty string  | comma separated values                                             |
| page          |    No     |       0       | page number with starting value of 0                               |
| size          |    No     |       5       | maximum element in a page, minimum = 5, maximum 50                 |
| sortBy        |    No     |   PUBLISHED   | PostSortField[TITLE, DATE_TAKEN, PUBLISHED]                        |
| sortDirection |    No     |      ASC      | ASC, DESC                                                          |

### Configuration Properties

<table>
<thead>
  <tr>
    <th>Prefix</th>
    <th>Property Name</th>
    <th>Default Value</th>
    <th>Description</th>
  </tr>
</thead>
<tbody>
  <tr>
    <td rowspan="2">web-client</td>
    <td>flickr.url</td>
    <td>https://www.flickr.com/services/</td>
    <td>Flickr API Service URL</td>
  </tr>
  <tr>
    <td>flickr.services.public-feeds</td>
    <td>feeds/photos_public.gne</td>
    <td>Flickr Public Feeds API Path</td>
  </tr>
  <tr>
    <td>post</td>
    <td>existing-request-before</td>
    <td>5</td>
    <td>POST_REQUEST.updated_date before x (in minutes)<br>minimum = 5, maximum = 60</td>
  </tr>
  <tr>
    <td></td>
    <td>recurring-job.fetch-feeds-and-save-post.interval</td>
    <td>PT1M</td>
    <td>Fetch Feeds and Save Post Recurring Job interval.<br>The format for the string to be parsed is "PnDTnHnMn.nS" where "nDT" means 'n' number of Days,<br>"nH" means 'n' number of Hours, "nM" means 'n' number of Minutes and "nS" means 'n' number of Seconds.<br>The format accepted are based on the ISO-8601 duration format.</td>
  </tr>
  <tr>
    <td></td>
    <td>recurring-job.fetch-feeds-and-save-post.minus-minutes</td>
    <td>15</td>
    <td>Fetch Feeds and Save Post Recurring Job to query POST_REQUEST.updated_date &lt; now() - x minutes</td>
  </tr>
  <tr>
    <td></td>
    <td>recurring-job.get-past-requests-and-delete.cron</td>
    <td>30 01 0 * * *</td>
    <td>Get Past Requests And Delete Recurring Job cron expression.<br>The cron expression is made of five fields. The following explains the values.<br><br>first * : second (0 - 59)<br>second * : minute (0 - 59)<br>third * : hour (0 - 23)<br>fourth * : day of the month (1 - 31)<br>fifth * : month (1 - 12)<br>sixth * : day of the week (0 - 6)</td>
  </tr>
  <tr>
    <td></td>
    <td>recurring-job.get-past-requests-and-delete.minus-days</td>
    <td>1</td>
    <td>Get Past Requests And Delete Recurring Job to delete POST_REQUEST by created_date before now() - x days</td>
  </tr>
  <tr>
    <td></td>
    <td>recurring-job.get-posts-and-delete.cron</td>
    <td>30 01 0 * * *</td>
    <td>Get Posts And Delete Recurring Job cron expression.</td>
  </tr>
  <tr>
    <td></td>
    <td>recurring-job.get-posts-and-delete.minus-days</td>
    <td>1</td>
    <td>Get Posts And Delete Recurring Job to delete POST by created_date before now() - x days</td>
  </tr>
</tbody>
</table>

### Details

Every single fetch to Flickr will be logged to database.
Then, the fetched results will be saved to database.
If the same request parameters is found in the database then, the system will not send a request to Flickr.
Instead, the system will query from database for the results.

There are three recurring processes.

1. Getting existing request in the database, then fetch the results and save those to database.
2. Deleting past requests that are exceeding expiration date, so the first recurring process will not process them
   again.
3. Deleting fetched data that are exceeding expiration date.

