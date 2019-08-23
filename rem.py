#!/home/ubuntu/BoatsBeGone/myprojectenv/bin/python3
from bot_pymessenger import *
import sys

if __name__ == '__main__':
	bot.send_text_message(sys.argv[1], "Reminder! Please respond 'start' to receive updates.")

