<h1>⛵BoatsBeGone⛵</h1>

BoatsBeGone is a Facebook Messaging Bot that powered by Flask that aims to be reduce nuisances associated with crossing the Victoria Bridge as a pedestrian/cyclist. It works by scraping data from the Greatlakes-Seaway website. 
<h2>Getting Started</h2 

This was run on an t.2 Micro EC2 Instance with Ubuntu on AWS.

Install dependencies
```python
pip install -r requirements.txt
```
Bind flask to wsgi.py and run with gunicorn
This will receive requests from Facebook and update the database for notifications
```bash
gunicorn --bind 0.0.0.0:5000 wsgi:app
```

Start server.py to scrape and alert users
```python
python server.py
```
<h6>What it can do</h6>
<ul>
    <li>Give status of bridge in plaintext (message "status")</li>
    <li>Give video footage of bridge from public feeds (message "video")</li>
    <li>Alert users in real time of changes to the bridges state, lasting up to 2 hours (message "start")</li>
    <li>Stop receving updates (message "stop")</li>
    <li>Set-up reminders that ask if you'd like to start (message "sched sunday 13:05") <b>this feature works by enabling a cron job on linux, it needs work and could be improved via APScheduler or Celery)</b></li>
    <li> To receive all these instructions (send "help")</li>
</ul>

SQLite is used because its lightweight, if using Heroku Web and Worker model, usage of something persistent such as PostgreSQL would have been better due to the ephemeral file system. 

In order to message Boatsbegone on Facebook,one must be a developper/tester at the moment.


