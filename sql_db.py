import sqlite3
from datetime import datetime, timedelta

from bot_pymessenger import *


def add_to_active(id, conn):
    found = False
    sql = "INSERT INTO active VALUES (?,?)"
    c = conn.cursor()
    c.execute("SELECT * FROM active WHERE id=?", (id,))
    already_enrolled = str(c.fetchone()) != 'None'
    if not already_enrolled:
        notify_once(id, conn)
        c.execute(sql, (id, datetime.now()))
        time = datetime.now() + timedelta(hours=2)
        if time.hour < 10:
            pad1 = '0'
        else:
            pad1 = ""

        if time.minute < 10:
            pad2 = '0'
        else:
            pad2 = ""
        bot.send_quick_reply(id, f"Successfully added to the list, you will stop receiving notifications at {pad1}{time.hour}:{pad2}{time.minute}",quick_button_1)
    else:
        bot.send_text_message(id,
                              "Hmm... You seem to already be enrolled. Please send 'stop' and 'start' if you would like to notified for 2 more hours.")
        found = True
    conn.commit()
    return found

def remove_from_active(id, conn, silent):
    sql = 'DELETE FROM active WHERE id=?'
    c = conn.cursor()
    c.execute(sql, (id,))
    if not silent:
        bot.send_text_message(id,
                              "You will no longer receive live notifications. Send 'start 2' to be notified for 2 hours.")

    conn.commit()


def notify_once(id, conn):
    c = conn.cursor()
    c.execute("SELECT status FROM status")
    stat = c.fetchone()[0]
    msg = f"The bridge is currently {stat.upper()}"
    bot.send_quick_reply(id,msg,quick_button_2)



def notify_all(message, conn):
    c = conn.cursor()
    c.execute("SELECT * FROM active")
    active_users = c.fetchall()
    now = datetime.now()

    for user in active_users:
        user_start = datetime.strptime(user[1], '%Y-%m-%d %H:%M:%S.%f')
        time_delta = now - user_start

        if time_delta.seconds > 7200 and time_delta.seconds > 0:
            remove_from_active(user[0], conn, True)
        else:
            bot.send_text_message(user[0], message)


def update_status_db(status_to_write, conn):
    c = conn.cursor()
    sql = "UPDATE status SET status = ?"
    c.execute(sql, (status_to_write,))
    conn.commit()
