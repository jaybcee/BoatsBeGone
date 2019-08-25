import sqlite3
import requests
import time
import db
from datetime import datetime, timedelta
from bs4 import BeautifulSoup
from bot_pymessenger import *
from sql_db import *

class BoatCrossing:

    def __init__(self):
        # crawled from schedule
        self.start = None
        self.end = None
        self.timeElapsed = None
        self.ignore = False
        # crawled from boat info
        self.direction = None
        self.vessel_name = None
        self.last_loc = None
        self.next_lock = None
        self.ata = None
        self.eta = None
        # misc
        self.is_tick = True
        self.previous_status = None
        self.current_status = None

    def parse_row(self, row):
        td = row.find_all('td')
        self.vessel_name = td[0].text.strip()
        self.direction = td[3].text.strip()
        if self.direction == '⇓':
            self.direction = 'downstream'
        else:
            self.direction = 'upstream'
        self.last_loc = td[4].text.strip()
        self.ata = td[5].text.strip()
        self.eta = td[6].text.strip()
        self.next_lock = td[7].text.strip()
        now = datetime.now()

        if self.eta == "":
            self.direction = self.direction
        else:
            eta_time = datetime.strptime(self.eta, '%Y-%m-%d %H:%M')
            #if difference between NOW and ETA is greater than
            if (eta_time - now).seconds > 7000 and (eta_time - now).seconds > 0:
                self.direction = 'misc'

    def update_status(self):
        print('hi')
        last_rw_index = -1
        not_success = True
        while not_success:
            try:
                res = requests.get("http://www.greatlakes-seaway.com/R2/jsp/VT00.jsp?language=E&hiddenName=&ORDER=2305")
                src = res.content
                soup = BeautifulSoup(src, 'lxml')  # get the soup object for web page
                table = soup.find_all('table')[5]  # finds the appropriate table

                first_row = table.find_all("tr")[0]  # gets the first appropriate row
                last_row = table.find_all('tr')[last_rw_index]
                first_row_td = first_row.find_all('td')
                last_row_td = last_row.find_all('td')
                # if ETA is none, it means ship has arrived (because ATA is there) and we should consider it.
                if last_row_td[6].text.strip() == "":
                    self.parse_row(last_row)
                else:
                    self.parse_row(first_row)
                not_success = False
            # if they changed something with the "last" row try before last and last
            except IndexError as e:
                print(f"{e} at {datetime.now()}")
                last_rw_index = last_rw_index - 1
                if (last_rw_index < -2):
                    self.direction = 'misc'
                    break


    def add_db(self):
        db.add_entry(self.direction, self.start, self.end, self.timeElapsed)

    def get_delta(self):
        self.timeElapsed = (self.end - self.start).seconds / 60

def get_status(incoming_boat):
    not_success = True
    while not_success:
        try:
            result = requests.get(
                "http://www.greatlakes-seaway.com/R2/jsp/MaiBrdgStatus.jsp?language=E")

            src = result.content
            soup = BeautifulSoup(src, 'lxml')

            table_rows = soup.find_all('tr')

            bike_bridge3 = table_rows[4]
            bridge3_status = bike_bridge3.find_all('td')[2].text.strip()
            if 'Unavailable' in bridge3_status:
                bridge3_status = 'Unavailable'

            not_success = False
            return bridge3_status

        except IndexError:
            print(f"\nAn index error occurred at {datetime.now()}, likely from the server giving bad request and BS4 failing...\n")
            time.sleep(30)
        except Exception as e:
            print(e)
            print("other exception occurred... retrying in 30 seconds")
            time.sleep(60)


def main():
    conn = sqlite3.connect('sql_db.db')
    boat_crossing = BoatCrossing()
    boot_status = get_status(boat_crossing)
    update_status_db(boot_status,conn)
    conn.close()
    if boot_status == "Unavailble":
        boat_crossing.start = datetime.now()
        boat_crossing.ignore = True  # raised when booting mid crossing to prevent crashing and better analytics
    while True:
        previous = get_status(boat_crossing)
        time.sleep(60)
        current = get_status(boat_crossing)

        if previous == 'Available' and current == 'Unavailable':
            conn = sqlite3.connect('sql_db.db')
            boat_crossing.start = datetime.now()
            boat_crossing.update_status()
            if boat_crossing.direction == 'upstream':
                approx_time = 30
            elif boat_crossing.direction == 'downstream':
                approx_time = 45
            else:
                approx_time = 20

            estimated_available = boat_crossing.start + timedelta(minutes=approx_time)
            if estimated_available.hour < 10:
                pad1 = '0'
            else:
                pad1 = ""

            if estimated_available.minute < 10:
                pad2 = '0'
            else:
                pad2 = ""

            if boat_crossing.direction == 'downstream':
                helper_str = ' (towards Jacques Cartier)'
            elif boat_crossing.direction == 'upstream':
                helper_str = ' (towards Champlain)'
            else:
                helper_str = ""
            time_str_boat = f"{pad1}{estimated_available.hour}:{pad2}{estimated_available.minute}"
            message = f"The bridge is UNAVAILABLE. Incoming boat is expected to be {boat_crossing.direction.upper()}{helper_str}. Expected availability at {time_str_boat}."
            update_status_db(message,conn)
            notify_all(message, conn)
            conn.close()

        elif previous == 'Unavailable' and current == 'Available':
            conn = sqlite3.connect('sql_db.db')
            update_status_db(current,conn)
            # end the boat crossing
            boat_crossing.end = datetime.now()
            message = f"The bridge is now AVAILABLE. Happy biking! 🚴"
            notify_all(message, conn)
            conn.close()
            try:
                boat_crossing.get_delta()
                boat_crossing.add_db()
            except TypeError as e:
                print(f"Error occured: {e} at {datetime}")
            boat_crossing = BoatCrossing()  # reset


print("Crawling for boats!")
try:
    main()
except Exception as e:
    print(f"{e} @ {datetime.now()}")
