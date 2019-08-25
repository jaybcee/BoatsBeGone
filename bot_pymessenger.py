from secrets import *
from pymessenger3.bot import Bot

quick_button_1 = [
    {
        "content_type": "text",
        "title": "Image 📷",
        "payload": "image",
    }, {
        "content_type": "text",
        "title": "Stop 🛑",
        "payload": "stop",
    },
    {
        "content_type": "text",
        "title": "Video 🎥",
        "payload": "video",
    }
]

quick_button_2 = [
    {
        "content_type": "text",
        "title": "Start ⛵",
        "payload": "start",
    }, {
        "content_type": "text",
        "title": "Stop 🛑",
        "payload": "stop",
    },
    {
        "content_type": "text",
        "title": "Help 🆘",
        "payload": "help",
    }
]

quick_button_3 = [
    {
        "content_type": "text",
        "title": "Start ⛵",
        "payload": "start",
    }, {
        "content_type": "text",
        "title": "Image 📷",
        "payload": "image",
    },
    {
        "content_type": "text",
        "title": "Video 🎥",
        "payload": "video",
    }
]


ACCESS_TOKEN = ACCESS_TOKEN
VERIFY_TOKEN = VERIFY_TOKEN
bot = Bot(ACCESS_TOKEN)
