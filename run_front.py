import re
import sqlite3
from datetime import timedelta
from flask import Flask, request, send_file
import cronos
from sql_db import *
import requests
import cv2


help_msg = """⛵ Welcome to BoatsBeGone ⛵
This bot gives you the status of the bridge necessary for entry/exit of Parc Jean-Drapeau from the Victoria Bridge.

Send "Status" to receive a one-time notification of the bridge's status.

Send "Video" to receive footage from 511 Quebec and the Status.

Send "Image" to receive a photo of the 511 Quebec footage.

Send "Start" to be updated on the status of the bridge as stoppage occurs. You will be removed after 2 hours.

The system attempts to detect the direction of the crossing. They may not always be accurate. At the moment, the following values are used.

Downstream (towards Jacques Cartier) is the longest from observation of data. Crossings take approximately 45-60 minutes.

Upstream (towards Champlain) takes approximately 20-30 minutes.

Misc. Consists of Pleasure craft and takes approximately 20 minutes. It is the least accurate of them all as misc. is triggered via a crossing that is 2 hours early.

Send "Stop" to remove yourself from live notifications.

Send "Sched Sunday 14:37" to be pinged at that time to start.

Send "Remove all" to remove all reminders/pinging.

Send "Help" to receive this message again.

All queries are case insensitive.

GitHub repo here: https://github.com/jaybcee/BoatsBeGone
"""

app = Flask(__name__)





@app.route('/ping')
def pong():
    return 'pong'


def send_boat_img(id):
    with open('boat_vid.mp4', "wb") as file:
        # get request
        response = requests.get('http://www.quebec511.info/Carte/Fenetres/camera.ashx?id=3379&format=mp4')
        # write to file
        file.write(response.content)
        file.close()
        vidcap = cv2.VideoCapture('boat_vid.mp4')
        success, image = vidcap.read()
        if success:
            cv2.imwrite("boat_frame.jpeg", image)
            bot.send_image(id,'boat_frame.jpeg')



# We will receive messages that Facebook sends our bot at this endpoint
@app.route("/fb-endpoint", methods=['GET', 'POST'])
def receive_message():
    conn = sqlite3.connect('sql_db.db')
    if request.method == 'GET':
        """Before allowing people to message your bot, Facebook has implemented a verify token
        that confirms all requests that your bot receives came from Facebook."""
        token_sent = request.args.get("hub.verify_token")
        return verify_fb_token(token_sent)
    # if the request was not get, it must be POST and we can just proceed with sending a message back to user
    else:
        # get whatever message a user sent the bot
        output = request.get_json()
        for event in output['entry']:
            messaging = event['messaging']
            for message in messaging:
                if message.get('message'):
                    # Facebook Messenger ID for user so we know where to send response back to
                    recipient_id = message['sender']['id']
                    print(recipient_id)
                    if message['message'].get('text'):
                        msg_rec = message['message']['text'].lower()
                        if 'status' in msg_rec:
                            notify_once(recipient_id, conn)
                        elif 'start' in msg_rec:
                            add_to_active(recipient_id, conn)
                        elif 'stop' in msg_rec:
                            remove_from_active(recipient_id, conn, False)
                        elif 'help' in msg_rec:
                            bot.send_quick_reply(recipient_id,help_msg,quick_button_2)
                        elif 'video' in msg_rec:
                            bot.send_video_url(recipient_id,
                                               'http://www.quebec511.info/Carte/Fenetres/camera.ashx?id=3379&format=mp4')
                            notify_once(recipient_id, conn)
                        elif 'image' in msg_rec:
                                send_boat_img(recipient_id)
                                notify_once(recipient_id,conn)
                        elif 'remove all' in msg_rec:
                            success = cronos.remove(recipient_id)
                            if success:
                                bot.send_text_message(recipient_id, "You have cleared all reminders.")
                            else:
                                bot.send_text_message(recipient_id,
                                                      "You either had no reminders or something went wrong.")

                        elif 'sched' in msg_rec:
                            words = msg_rec.split()
                            days_of_week = ['monday', 'tuesday', 'wednesday', 'thursday', 'friday', 'saturday',
                                            'sunday']
                            valid = True
                            if words[0] != 'sched':
                                valid = False
                            if words[1] not in days_of_week:
                                valid = False
                            try:
                                time = datetime.strptime(words[2], '%H:%M')
                            except ValueError:
                                valid = False
                            if not valid:
                                bot.send_text_message(recipient_id,
                                                      "We couldn't quite understand that makes sure to send similar to the following: sched monday 09:37")
                            else:
                                exists = cronos.check_existing(recipient_id, words[1], time)
                                if exists:
                                    bot.send_text_message(recipient_id, "This reminder is already set.")
                                else:
                                    cronos.add_job(recipient_id, words[1], time)
                                    bot.send_text_message(recipient_id,
                                                          f'All set! You will be pinged every {words[1].capitalize()} at {words[2]}')

                        else:
                            bot.send_quick_reply(recipient_id,
                                                  "Sorry, we didn't understand that message, send 'help' if you need help.",quick_button_2)

                    # if user sends us a GIF, photo,video, or any other non-text item
                    if message['message'].get('attachments'):
                        response_sent_nontext = "Sorry, we can't process your attachment, try again with text. If you need help, send the message HELP "
                        bot.send_text_message(recipient_id, response_sent_nontext)
    conn.close()
    return "Message Processed"


def verify_fb_token(token_sent):
    # take token sent by facebook and verify it matches the verify token you sent
    # if they match, allow the request, else return an error
    if token_sent == VERIFY_TOKEN:
        return request.args.get("hub.challenge")
    return 'Invalid verification token'


if __name__ == "__main__":
    app.run()
